package app.persistence

import app.model.IdEntity

object ActiveRecordFacilities {

  class DeletableProvider[T <: IdEntity](dao: Dao[T], entity: T) {

    def delete() = dao.delete(entity)
  }

  class PersistableProvider[T <: IdEntity](dao: Dao[T], entity: T) {

    def save() = entity.id match {
      case Some(_) => dao.update(entity)
      case None => dao.save(entity)
    }
  }

  implicit val personDao = PersonDao
  implicit val notificationDao = NotificationDao

  implicit def idEntityToDeletableProvider[T <: IdEntity](entity: T)(implicit dao: Dao[T]) : DeletableProvider[T] = {
    new DeletableProvider[T](dao, entity)
  }

  implicit def idEntityToPersistableProvider[T <: IdEntity](entity: T)(implicit dao: Dao[T]) : PersistableProvider[T] = {
    new PersistableProvider[T](dao, entity)
  }
}