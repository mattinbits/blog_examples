import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, WordSpec}
import scala.concurrent._
import scala.concurrent.duration._

class PoliceUKDataClientSpec extends WordSpec with Matchers with MockFactory {

  trait MockHttpRequestService extends HttpRequestService {

    val mock = mockFunction[String, Future[String]]

    override def makeRequest(uri: String): Future[String] = mock(uri)
  }

  "PoliceUKDataClient" should {

    "Return a list of crimes from a uri" in {

      val client = new PoliceUKDataService with MockHttpRequestService {
        override def ec = scala.concurrent.ExecutionContext.Implicits.global
      }
      client.mock
        .expects("https://data.police.uk/api/crimes-street/all-crime?lat=51.5&lng=0.13&date=2013-01")
        .returning(Future.successful(
          """
            |[
            |  {
            |    "category":"anti-social-behaviour",
            |    "location_type":"Force",
            |    "location":{
            |      "latitude":"52.624424",
            |      "street":{"id":882275,"name":"On or near Stuart Street"},
            |      "longitude":"-1.150485"
            |    },"context":"",
            |    "outcome_status":null,
            |    "persistent_id":"",
            |    "id":20597953,
            |    "location_subtype":"",
            |    "month":"2013-01"
            |  },
            |  {
            |    "category":"burglary",
            |    "location_type":"Force",
            |    "location":{
            |      "latitude":"52.627058",
            |      "street":{"id":882207,"name":"On or near Beaconsfield Road"},
            |      "longitude":"-1.154260"
            |    },
            |    "context":"",
            |    "outcome_status":{"category":"Investigation complete; no suspect identified","date":"2013-03"},
            |    "persistent_id":"",
            |    "id":20600818,
            |    "location_subtype":"",
            |    "month":"2013-01"
            |  }
            |]
          """.stripMargin))
      val crimesF = client.getData(51.5, 0.13, "2013-01")
      val crimes = Await.result(crimesF, 1 second)
      crimes should be (
        List(
          Crime("anti-social-behaviour", "On or near Stuart Street", None ),
          Crime("burglary", "On or near Beaconsfield Road", Some("Investigation complete; no suspect identified"))))
    }
  }
}
