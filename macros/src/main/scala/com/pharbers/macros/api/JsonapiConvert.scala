package com.pharbers.macros.api

import com.pharbers.jsonapi.model.RootObject
import com.pharbers.jsonapi.model.RootObject.ResourceObject
import com.pharbers.macros.convert.jsonapi.ResourceObjectReader

trait JsonapiConvert[T] {
    def fromJsonapi(jsonapi: RootObject): T
    def toJsonapi(obj: T): RootObject

    def fromResourceObject[T: ResourceObjectReader](resource: ResourceObject): T =
        implicitly[ResourceObjectReader[T]].fromResourceObject(resource)
    def toResourceObject[T: ResourceObjectReader](entity: T): ResourceObject =
        implicitly[ResourceObjectReader[T]].toResourceObject(entity)
}