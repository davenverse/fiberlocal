package io.chrisdavenport.fiberlocal

import cats._
import cats.effect._
import cats.syntax.all._

trait GenFiberLocal[F[_]]{
  def local[A](default: A): F[FiberLocal[F, A]]
}
object GenFiberLocal {

  def apply[F[_]](implicit ev1: GenFiberLocal[F]): GenFiberLocal[F] = ev1


  // If at some point monix has support in ways that matches IOLocal then we will release
  // a new major forcing this to be explicit
  implicit def fromLiftIO[F[_]: LiftIO: Functor]: GenFiberLocal[F] = new GenFiberLocal[F] {
    def local[A](default: A): F[FiberLocal[F,A]] = LiftIO[F].liftIO(IOLocal(default)).map( local =>
      FiberLocal.fromIOLocal(local)
    )
  }

  def mapK[F[_]: Functor, G[_]](base: GenFiberLocal[F], fk: F ~> G): GenFiberLocal[G] =
    new GenFiberLocal[G] {
      def local[A](default: A): G[FiberLocal[G,A]] = 
        fk(base.local(default).map(_.mapK(fk)))
    }
}