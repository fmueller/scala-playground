package app.model

import app.persistence.NotificationDao

case class Person(name: String, var age: Int)(implicit private[this] val notificationDao: NotificationDao) extends IdEntity {

  require(name != null && name.trim().length() > 0)
  require(age >= 0)

  def this(id: Option[Long], name: String, age: Int)(implicit notificationDao: NotificationDao) {
    this(name, age)(notificationDao)
    this.id = id
  }

  def celebrateBirthday() = {
    age += 1
    this
  }

  def notifications = notificationDao.findAllFor(this)
}