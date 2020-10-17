package br.com.rosait.myrealm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.RealmResults
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    companion object {
        //val _database: Realm by lazy { Realm.getDefaultInstance() }
    }

    lateinit var _database: Realm
    lateinit var rvItems: RecyclerView
    var items: RealmResults<Item>? = null
    var valid = false
    var primaryKeyResult: Long = 0
    lateinit var nameResult: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Realm.init(this)
        _database = Realm.getDefaultInstance()

        rvItems = findViewById<RecyclerView>(R.id.rvItems)

        rvItems?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        val rbValid = findViewById<RadioButton>(R.id.rbValid)
        rbValid?.setOnClickListener {
            if(valid && rbValid.isChecked) {
                rbValid.isChecked = false
                valid = false
            }
            else {
                rbValid.isChecked = true
                valid = true
            }
        }

        items?.addChangeListener { results, changeSet ->
            changeSet.insertions
        }


        setupAdapter()
    }

    fun onClick(view: View) {
        val item = getItem()

        var result: Item? = null

        if(item.id != 0L) {
            _database.executeTransaction { dbRealm ->
                result = dbRealm.where(Item::class.java).equalTo("id", item.id).findFirst()
            }
        }

        if(result == null) {
            addItem(item)
        } else {
            editItem(item)
        }

        setupAdapter()

        cleanFields()
    }

    private fun setupAdapter() {
        items = readItems()

        rvItems.adapter = Adapter(items?.toMutableList() ?: mutableListOf(), ::getMyItem, ::deleteItem)
    }

    private fun getItem(): Item {
        val edtName = findViewById<EditText>(R.id.edtName)
        val edtQty = findViewById<EditText>(R.id.edtQty)
        val rbValid = findViewById<RadioButton>(R.id.rbValid)

        return Item().apply {
            id = if(primaryKeyResult != 0L) primaryKeyResult else 0
            name = edtName?.text.toString()
            qty = edtQty?.text.toString()
            isValidItem = if (rbValid?.isChecked == true) "Sim" else "Não"
        }
    }

    private fun addItem(item: Item) {
        _database.executeTransaction { dbRealm ->
            val maxId = dbRealm.where(Item::class.java).max("id")
            val defaultId = 0
            if(maxId != null)
                item.id = maxId.toLong() + 1
            else
                item.id = defaultId + 1L
            dbRealm.copyToRealm(item)
        }
    }

    private fun editItem(item: Item) {
        _database.executeTransaction { dbRealm ->
            val itemResult = dbRealm.where(Item::class.java).equalTo("id", item.id).findFirst()!!
            itemResult.name = item.name
            itemResult.qty = item.qty
            itemResult.isValidItem = item.isValidItem
        }
    }

    private fun readItems() : RealmResults<Item> {
        var results: RealmResults<Item>? = null
        _database.executeTransaction { dbRealm ->
            results = dbRealm.where(Item::class.java).findAll()
        }

        return results!!
    }

    private fun getMyItem(item: Item) {
        var myResult = Item()
        //nameResult = item.name
        primaryKeyResult = item.id
        _database.executeTransactionAsync(Realm.Transaction { dbRealm ->

            // Find a item to update.
            val itemResult = dbRealm.where(Item::class.java).equalTo("id", primaryKeyResult).findFirst()!!

            myResult.name = itemResult.name
            myResult.qty = itemResult.qty
            myResult.isValidItem = itemResult.isValidItem

        }, Realm.Transaction.OnSuccess {

            val edtName = findViewById<EditText>(R.id.edtName)
            val edtQty = findViewById<EditText>(R.id.edtQty)
            val rbValid = findViewById<RadioButton>(R.id.rbValid)

            edtName?.setText(myResult.name)
            edtQty?.setText(myResult.qty)
            rbValid?.isChecked = myResult.isValid
            //primaryKeyResult = myResult.id
        })
    }

    private fun deleteItem(item: Item) {
        //nameResult = item.name
        primaryKeyResult = item.id

        // All changes to data must happen in a transaction
        _database.executeTransactionAsync(Realm.Transaction { dbRealm ->
            val resultItem = dbRealm.where(Item::class.java).equalTo("id", primaryKeyResult).findFirst()!!
            resultItem.deleteFromRealm()

        }, Realm.Transaction.OnSuccess {
            setupAdapter()
            primaryKeyResult = 0L
        })
    }

    private fun cleanFields() {
        val edtName = findViewById<EditText>(R.id.edtName)
        val edtQty = findViewById<EditText>(R.id.edtQty)
        val rbValid = findViewById<RadioButton>(R.id.rbValid)

        edtName?.setText("")
        edtQty?.setText("")
        rbValid?.isChecked = false
        primaryKeyResult = 0L
    }
}