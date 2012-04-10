package app.model

case class Person(name: String, var age: Int) extends IdEntity {

  require(name != null && name.trim().length() > 0)
  require(age >= 0)

  def this(id: Option[Long], name: String, age: Int) {
    this(name, age)
    this.id = id
  }

  def celebrateBirthday() = {
    age += 1
    this
  }
}