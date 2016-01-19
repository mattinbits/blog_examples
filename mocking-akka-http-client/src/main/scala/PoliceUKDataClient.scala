import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpMethod, HttpRequest}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import spray.json._

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

case class Crime(category: String, street: String, outcome: Option[String])

trait CrimeJsonProtocol extends DefaultJsonProtocol {

  implicit object crimeFormat extends JsonFormat[Crime] {

    override def read(json: JsValue): Crime = {
      val cat = json.asJsObject.fields
        .getOrElse("category", throw new DeserializationException("expected category"))
        .asInstanceOf[JsString].value
      val street = (for {
        locObj <- json.asJsObject.fields.get("location")
        streetObj <- locObj.asJsObject.fields.get("street")
        nameObj <- streetObj.asJsObject.fields.get("name")
      } yield {
        nameObj.asInstanceOf[JsString].value
      }).getOrElse(throw new DeserializationException("expected street name"))
      val outcome = (for {
        outcomeStatusObj <- json.asJsObject.fields.get("outcome_status") if (outcomeStatusObj != JsNull)
        outcomeCategoryObj <- outcomeStatusObj.asJsObject.fields.get("category")
      } yield {
          outcomeCategoryObj.asInstanceOf[JsString].value
        })
      Crime(cat, street, outcome)
    }

    override def write(obj: Crime): JsValue = throw new SerializationException("Only for deserializing")
  }
}

trait HttpRequestService {

  def makeRequest(uri: String): Future[String]
}

trait PoliceUKDataService extends CrimeJsonProtocol {

  this: HttpRequestService =>

  implicit def ec: ExecutionContext

  def getData(latitude: Double, longitude: Double, month: String): Future[Seq[Crime]] = {
    val uri = s"https://data.police.uk/api/crimes-street/all-crime?lat=${latitude}&lng=${longitude}&date=${month}"
    makeRequest(uri).map(_.parseJson.convertTo[List[Crime]])
  }
}

trait AkkaHttpRequestService extends HttpRequestService {

  implicit def system: ActorSystem
  implicit def materializer: ActorMaterializer
  implicit def ec: ExecutionContext

  lazy val http = Http()

  def makeRequest(uri: String): Future[String] = {
    println(uri)
    val req = HttpRequest(HttpMethods.GET, uri)
    val resp = http.singleRequest(req)
    resp.flatMap {r => println(r); Unmarshal(r.entity).to[String] }
  }

  def shutdown() = {
    Http().shutdownAllConnectionPools().onComplete{ _ =>
      system.shutdown()
      system.awaitTermination()
    }
  }
}

object PoliceUKDataClient extends App {

  val client = new PoliceUKDataService with AkkaHttpRequestService {

    override implicit val system = ActorSystem("PoliceUKData")
    override implicit val materializer = ActorMaterializer.create(system)
    override implicit val ec: ExecutionContext = system.dispatcher
  }
  implicit val ec = client.ec
  val (lat, lon, date) = (args(0).toDouble, args(1).toDouble, args(2))
  client.getData(lat, lon, date).onComplete { res =>
    res match {
      case Success(list) => list.foreach(println)
      case Failure(ex) => println(ex.getMessage)
    }
    client.shutdown()
  }
}
