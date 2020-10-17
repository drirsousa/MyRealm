package br.com.rosait.myrealm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class Item(
    @PrimaryKey
    var id: Long = 0,
    @Required
    var name: String = "",
    var qty: String = "0",
    var isValidItem: String = "Não"
) : RealmObject()