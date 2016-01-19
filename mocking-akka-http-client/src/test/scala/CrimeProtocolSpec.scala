import org.scalatest.{Matchers, WordSpec}
import spray.json._

class CrimeJsonProtocolSpec extends WordSpec with Matchers with CrimeJsonProtocol {

  "CrimeJsonProtocol" should {

    "Deserialize an instance of a crime without an outcome" in {
      val jsonStr =
        """
          |{
          |  "category": "anti-social-behaviour",
          |  "persistent_id": "",
          |  "location_type": "Force",
          |  "location_subtype": "",
          |  "id": 20599642,
          |  "location": {
          |    "latitude": "52.6269479",
          |    "longitude": "-1.1121716",
          |    "street": {
          |      "id": 882380,
          |      "name": "On or near Cedar Road"
          |    }
          |  },
          |  "context": "",
          |  "month": "2013-01",
          |  "outcome_status": null
          |}
        """.stripMargin
      val jsonObj = jsonStr.parseJson
      jsonObj.convertTo[Crime] should be (Crime("anti-social-behaviour", "On or near Cedar Road", None))
    }

    "Deserialize an instance of a crime with an outcome" in {
      val jsonStr =
        """
          |{
          |  "category": "anti-social-behaviour",
          |  "persistent_id": "",
          |  "location_type": "Force",
          |  "location_subtype": "",
          |  "id": 20599642,
          |  "location": {
          |    "latitude": "52.6269479",
          |    "longitude": "-1.1121716",
          |    "street": {
          |      "id": 882380,
          |      "name": "On or near Cedar Road"
          |    }
          |  },
          |  "context": "",
          |  "month": "2013-01",
          |  "outcome_status": {
          |    "category": "Under investigation",
          |    "date": "2013-01"
          |  }
          |}
        """.stripMargin
      val jsonObj = jsonStr.parseJson
      jsonObj.convertTo[Crime] should be (Crime("anti-social-behaviour", "On or near Cedar Road", Some("Under investigation")))
    }
  }
}
