package io.chrisdavenport.fiberlocal

import cats._
import cats.effect._

trait FiberLocal[F[_], A]{

  def get: F[A]

  def set(value: A): F[Unit]

  def reset: F[Unit]

  def update(f: A => A): F[Unit]

  def modify[B](f: A => (A, B)): F[B]

  def getAndSet(value: A): F[A]

  def getAndReset: F[A]

  def mapK[G[_]](fk: F ~> G): FiberLocal[G, A] = new FiberLocal.MapKFiberLocal(this, fk)
}


object FiberLocal {
  def fromIOLocal[F[_]: LiftIO, A](local: IOLocal[A]): FiberLocal[F, A] = new FiberLocal[F, A] {
    def get: F[A] = LiftIO[F].liftIO(local.get)
    
    def set(value: A): F[Unit] = LiftIO[F].liftIO(local.set(value))
    
    def reset: F[Unit] = LiftIO[F].liftIO(local.reset)
    
    def update(f: A => A): F[Unit] = LiftIO[F].liftIO(local.update(f))
    
    def modify[B](f: A => (A, B)): F[B] = LiftIO[F].liftIO(local.modify(f))
    
    def getAndSet(value: A): F[A] = LiftIO[F].liftIO(local.getAndSet(value))
    
    def getAndReset: F[A] = LiftIO[F].liftIO(local.getAndReset)
    
  }

  private class MapKFiberLocal[F[_], G[_], A](
    local: FiberLocal[F, A], fk: F ~> G
  ) extends FiberLocal[G, A]{
    def get: G[A] = fk(local.get)
    
    def set(value: A): G[Unit] = fk(local.set(value))
    
    def reset: G[Unit] = fk(local.reset)
    
    def update(f: A => A): G[Unit] = fk(local.update(f))
    
    def modify[B](f: A => (A, B)): G[B] = fk(local.modify(f))
    
    def getAndSet(value: A): G[A] = fk(local.getAndSet(value))
    
    def getAndReset: G[A] = fk(local.getAndReset)
    
  }
}
