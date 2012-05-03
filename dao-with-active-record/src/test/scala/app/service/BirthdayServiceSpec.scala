package app.service

import app.model.{ Notification, Person }
import app.persistence.MockedActiveRecordFaclities

import org.junit.runner.RunWith
import org.mockito.Mockito
import org.scalatest.WordSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers

import org.mockito.Matchers._
import org.mockito.Mockito._

@RunWith(classOf[JUnitRunner])
class BirthdayServiceSpec extends WordSpec with ShouldMatchers with MockedActiveRecordFaclities {

  var service: BirthdayService = _
  var dummy: Person = _
  var other: Person = _

  before {
    service = new BirthdayService()
    dummy   = new Person(id = Some(1), name = "dummy", age = 0)
    other   = new Person(id = Some(2), name = "other", age = 0)

    when(personDao.findAll()).thenReturn(Seq(dummy, other))
  }

  "A birthday service" when {

    "birthday is celebrated and others are invited" should {

      "trigger database only once to retrieve all persons" in {
        service.celebrateBirthdayAndInviteOthers(dummy)
        verify(personDao).findAll()
      }

      "notify all others about birthday" in {
        service.celebrateBirthdayAndInviteOthers(dummy)
        verify(notificationDao).save(
            Notification(from = dummy, to = other, message = BirthdayService.BIRTHDAY_MESSAGE))
      }

      "update person in database after others were informed about birthday" in {
        service.celebrateBirthdayAndInviteOthers(dummy)
        val order = inOrder(personDao)
        order.verify(personDao).findAll()
        order.verify(personDao).update(dummy)
      }
    }
  }
}