package com.drsync.myshop

import android.content.Context
import android.widget.ArrayAdapter
import com.google.android.material.textfield.TextInputLayout
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object Constant {
    const val SCAN_RESULT_CODE = 101
    const val SCAN_RESULT = "ScanResult"

    fun getToday(): String {
        val calendar = Calendar.getInstance().time
        val df = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        return df.format(calendar)
    }

    fun List<Any>.getArrayAdapter(context: Context) = ArrayAdapter(
        context,
        R.layout.dropdown_menu_popup,
        this
    )

    fun TextInputLayout.setInputError(message: String): Boolean {
        this.isErrorEnabled = true
        this.error = message
        this.requestFocus()
        return false
    }

    fun Number.formatRupiah(): String {
        val rupiahsFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        return rupiahsFormat.format(this)
    }

    fun List<Product>.getPriceTotal(): Number {
        return this.sumOf {
            it.totalPrice ?: 0
        }
    }

    val listProduct = listOf(
        Product(
            1,
            "Mouse",
            30000
        ),
        Product(
            2,
            "Keyboard",
            60000
        ),
        Product(
            3,
            "Web Cam",
            300000
        ),
        Product(
            8996006856197,
            "TehBotol Kotak Less Sugar",
            3500
        ),
        Product(
            8992759184013,
            "Tissue Jolly",
            4500
        ),
    )

}