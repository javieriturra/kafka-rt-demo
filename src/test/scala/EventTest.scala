import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization.write
import org.scalatest.{FlatSpec, Matchers}
import org.json4s.jackson.JsonMethods.parse

class EventTest extends FlatSpec with Matchers {

  "parse and write" should "work" in {
    val value = "{\"value\":1, \"timestamp\":\"2020-03-25T01:02:18Z\" }"
    implicit val formats: DefaultFormats.type = DefaultFormats
    val event: Event = parse(value).extract[Event]
    val str = write(event)
    event should be(parse(str).extract[Event])
  }

}
