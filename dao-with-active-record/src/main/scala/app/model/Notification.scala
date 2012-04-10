package app.model

case class Notification(from: Person, to: Person, message: String) extends IdEntity {

  require(from != null)
  require(to != null)
  require(message != null && message.trim().length() > 0)

  def this(id: Option[Long], from: Person, to: Person, message: String) {
    this(from, to, message)
    this.id = id
  }
}