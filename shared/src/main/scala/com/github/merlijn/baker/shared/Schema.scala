package com.github.merlijn.baker.shared

case class Schema(`type`: String, properties: Option[Map[String, Schema]]) {

//  def toType: types.Type = `type` match {
//    case "string"  => CharArray
//    case "integer" => Int64
//    case "boolean" => Bool
//    case "object" => {
//
//      val fields = properties.get.map {
//        case (name, schema) => RecordField(name, schema.toType)
//      }.toSeq
//
//      RecordType(fields)
//    }
//  }
}