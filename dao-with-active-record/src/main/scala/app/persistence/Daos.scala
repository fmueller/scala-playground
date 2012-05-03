package app.persistence

import app.model.Person

trait Daos {

  implicit val personDao: Dao[Person] = new PersonDao()
  implicit val notificationDao: NotificationDao = new NotificationDao()
}

object Daos extends Daos