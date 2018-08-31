package com.pharbers.macros.api

import com.pharbers.jsonapi.model.RootObject

trait JsonapiConvert[T] {
    def fromJsonapi(jsonapi: RootObject, package_local: String): T
    def toJsonapi(obj: T): RootObject
}