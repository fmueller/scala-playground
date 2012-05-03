package app.persistence

import app.model.Person
import org.mockito.Mockito.mock
import org.scalatest.mock.MockitoSugar

trait DaoMocks extends Daos with MockitoSugar {

  override implicit val personDao: Dao[Person] = mock[PersonDao]
  override implicit val notificationDao: NotificationDao = mock[NotificationDao]
}
