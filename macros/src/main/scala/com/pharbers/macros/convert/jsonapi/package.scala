package com.pharbers.macros.convert

import com.pharbers.jsonapi.model.RootObject.ResourceObject

package object jsonapi {

    def fromResourceObject[T: ResourceObjectReader](resource: ResourceObject): T =
        implicitly[ResourceObjectReader[T]].fromResourceObject(resource)
    def toResourceObject[T: ResourceObjectReader](entity: T): ResourceObject =
        implicitly[ResourceObjectReader[T]].toResourceObject(entity)
}
