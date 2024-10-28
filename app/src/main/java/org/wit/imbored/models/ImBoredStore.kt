package org.wit.imbored.models

interface ImBoredStore {
    fun findAll(): List<ImBoredModel>
    fun create(activity: ImBoredModel)
    fun update(activity: ImBoredModel)
    fun delete(activity: ImBoredModel)
    fun findById(id: Long): ImBoredModel?
}
