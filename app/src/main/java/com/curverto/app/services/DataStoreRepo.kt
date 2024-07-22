package com.curverto.app.services

interface DataStoreRepo {
    suspend fun putString(key: String, value: String)
    suspend fun getString(key: String): String?
    suspend fun putBoolean(key: String, value: Boolean)
    suspend fun getBoolean(key:String): Boolean
}