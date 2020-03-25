import java.sql.Timestamp

package object model

final case class Event(value: Long, timestamp: Timestamp)
