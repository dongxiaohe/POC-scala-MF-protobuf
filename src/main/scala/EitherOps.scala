import scala.util.{Failure, Success, Try}

trait EitherOps {
  implicit class TryOps[T](t: Try[T]) {
    def toEither(): Either[Throwable, T] = {
      t match {
        case Success(t) => Right(t)
        case Failure(e) => Left(e)
      }
    }
  }
}
