package app.persistence

import app.model.Notification

object NotificationDao extends Dao[Notification] {

  def findAll() = Nil
  def save(notification: Notification) = notification
  def update(notification: Notification) = notification
  def delete(notification: Notification) = notification
}