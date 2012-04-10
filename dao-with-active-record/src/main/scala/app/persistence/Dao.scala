package app.persistence

trait Dao[T] {

  def findAll() : Seq[T]
  def save(entity: T) : T
  def update(entity: T) : T
  def delete(entity: T) : T
}