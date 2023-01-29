package hio

case class HIOValidationError(smth: String, e:Throwable = new Exception()) extends Exception(smth,e)

