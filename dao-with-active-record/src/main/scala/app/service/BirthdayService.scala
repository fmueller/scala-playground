package app.service

import app.model.{ Person, Notification }
import app.persistence.{ Dao, NotificationDao }
import app.persistence.ActiveRecordFacilities._
import app.persistence.Daos._

class BirthdayService(implicit personDao: Dao[Person], notificationDao: NotificationDao) {

  def celebrateBirthdayAndInviteOthers(person: Person) = {
    personDao.findAll().filter(_.id != person.id).foreach(other => {
      val notification = Notification(from = person, to = other, BirthdayService.BIRTHDAY_MESSAGE)
      notification.save()
    })
    person.celebrateBirthday().save()
  }
}

object BirthdayService {

  val BIRTHDAY_MESSAGE = "user celebrates birthday"
}