package app.persistence

import app.model.{ Notification, Person }

class NotificationDao extends Dao[Notification] {

  def findAll() = Nil
  def save(notification: Notification) = notification
  def update(notification: Notification) = notification
  def delete(notification: Notification) = notification

  def findAllFor(person: Person) = Nil
}
