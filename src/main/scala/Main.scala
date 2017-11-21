import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import com.zendesk.maxwellsmarts.zendesk.ticketevents.TicketEvents
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

import scala.concurrent.Future
import scala.util.Try

object Main extends App with EitherOps {
  implicit val system = ActorSystem("Consume-and-produce")
  implicit val materializer = ActorMaterializer()

  val consumerSettings =
    ConsumerSettings(system, new StringDeserializer, new ByteArrayDeserializer)
      .withBootstrapServers("kafka.docker:9092")
      .withGroupId("SimpleTicketConsumer")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
      .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false")
      .withProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
      .withProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer")

  val done =
    Consumer.committableSource(consumerSettings, Subscriptions.topics("maxwellsmarts.ticket_events"))
      .mapAsync(10) { msg =>
        Future.successful(deserialize(
          msg.record.value())
          .map(t => (t.getAccountId, t.getTicketId, t.getEventsList))
        )
      }.map {
      case Left(e) => println(e)
      case Right(t) => println(t)
    }.runWith(Sink.ignore)

  private def deserialize(input: Array[Byte]): Either[Throwable, TicketEvents] = {
    Try {
      TicketEvents.parseFrom(input)
    }.toEither
  }

}
