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

    val listProduct = listOf(
        Product(
            "Mouse",
            30000
        ),
        Product(
            "Keyboard",
            60000
        ),
        Product(
            "Web Cam",
            300000
        ),
    )

}