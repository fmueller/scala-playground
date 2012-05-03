package app.persistence

import org.mockito.Mockito.reset
import org.scalatest.{ BeforeAndAfter, Suite }

trait MockedActiveRecordFaclities extends DaoMocks with BeforeAndAfter {

  this: Suite =>

  after {
    reset(personDao)
    reset(notificationDao)
  }
}
