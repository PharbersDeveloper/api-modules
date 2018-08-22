package com.pharbers

import com.pharbers.jsonapi.model.RootObject
import com.pharbers.macros.api.JsonapiConvert

package object macros {
    def formJsonapi[T: JsonapiConvert](jsonapi: RootObject): T = implicitly[JsonapiConvert[T]].fromJsonapi(jsonapi)
    def toJsonapi[T: JsonapiConvert](obj: T) : RootObject = implicitly[JsonapiConvert[T]].toJsonapi(obj)
}
