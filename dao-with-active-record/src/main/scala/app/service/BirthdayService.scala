package app.service

import app.model.{ Person, Notification }
import app.persistence.Dao

import app.persistence.ActiveRecordFacilities._

class BirthdayService(implicit personDao: Dao[Person]) {

  def celebrateBirthdayAndInviteOthers(person: Person) = {
    personDao.findAll().filter(_.id != person.id).foreach(other => {
      val notification = Notification(from = person, to = other, "user celebrates birthday")
      notification.save()
    })
    person.celebrateBirthday().save()
  }
}