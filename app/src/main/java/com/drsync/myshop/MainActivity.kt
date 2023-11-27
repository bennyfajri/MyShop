package com.drsync.myshop

import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.drsync.myshop.Constant.getArrayAdapter
import com.drsync.myshop.Constant.getToday
import com.drsync.myshop.Constant.listProduct
import com.drsync.myshop.Constant.setInputError
import com.drsync.myshop.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mAdapter: ProductAdapter

    private var qty = 0
    private var product: Product? = null
    private var productsToBuy: ArrayList<Product> = arrayListOf()

    private val sheetLayout: ConstraintLayout by lazy { findViewById(R.id.sheet_layout) }
    private val etTotalPrice: TextInputEditText by lazy { sheetLayout.findViewById(R.id.et_total) }
    private val etPay: TextInputEditText by lazy { sheetLayout.findViewById(R.id.et_pay) }
    private val etChange: TextInputEditText by lazy { sheetLayout.findViewById(R.id.et_change) }
    private val ilTotal: TextInputLayout by lazy { sheetLayout.findViewById(R.id.il_total) }
    private val ilPay: TextInputLayout by lazy { sheetLayout.findViewById(R.id.il_pay) }
    private val ilChange: TextInputLayout by lazy { sheetLayout.findViewById(R.id.il_change) }
    private val btnPay: MaterialButton by lazy { sheetLayout.findViewById(R.id.btn_pay) }
    private val sheetBehavior: BottomSheetBehavior<ConstraintLayout> by lazy {
        BottomSheetBehavior.from(
            sheetLayout
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            hideErrorInput()
            tvDate.text = getToday()

            mAdapter = ProductAdapter {
                deleteItemProduct(it)
            }
            etProduct.setAdapter(
                listProduct.map { it.name }.getArrayAdapter(this@MainActivity)
            )

            btnPlus.setOnClickListener {
                qty++
                etQty.setText(qty.toString())
            }

            btnMin.setOnClickListener {
                qty--
                etQty.setText(qty.toString())
            }

            etQty.doAfterTextChanged {
                btnMin.isEnabled = qty > 0
            }

            etProduct.doAfterTextChanged {
                if (it?.isNotEmpty() == true) {
                    val selectedProduct = listProduct.filter { product ->
                        product.name.contains(it.toString())
                    }
                    if (selectedProduct.isNotEmpty()) {
                        product = selectedProduct.first()
                        etPrice.setText(product?.price.toString())
                    }
                }
            }

            btnAdd.setOnClickListener {
                getUserInput()
            }

            etPay.doAfterTextChanged {
                if (it?.isNotEmpty() == true) {
                    etChange.setText(
                        (etPay.text.toString().toInt() - productsToBuy.getPriceTotal().toInt())
                            .toString()
                    )
                }
            }

            btnPay.setOnClickListener {
                if (productsToBuy.isNotEmpty()) {
                    if (etChange.text?.contains("-") == true) {
                        ilPay.setInputError(getString(R.string.pay_less_than_product))
                    } else {
                        cleanAllItem()
                    }
                }
            }

            btnScan.setOnClickListener {
                Intent(this@MainActivity, ScanActivity::class.java).apply {
                    launcherScanCode.launch(this)
                }
            }
        }
    }

    private val launcherScanCode = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if(result.resultCode == Constant.SCAN_RESULT_CODE) {
            //filter product by id
        }
    }

    private fun cleanAllItem() {
        MaterialAlertDialogBuilder(this@MainActivity)
            .setTitle(getString(R.string.attention))
            .setMessage(getString(R.string.payment_message, productsToBuy.first().name))
            .setPositiveButton(getString(R.string.next)) { dialog, _ ->
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.success_buying_product),
                    Toast.LENGTH_SHORT
                ).show()
                etTotalPrice.text?.clear()
                etPay.text?.clear()
                etChange.text?.clear()
                binding.etName.text?.clear()
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                productsToBuy.clear()
                mAdapter.submitList(productsToBuy)
                setRecyclerView()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteItemProduct(product: Product) {
        productsToBuy.removeIf {
            product.name == it.name && product.qty == it.qty && product.customer == it.customer
        }
        etTotalPrice.setText(productsToBuy.getPriceTotal().toString())
        mAdapter.submitList(productsToBuy)
        setRecyclerView()
    }

    private fun getUserInput() {
        binding.apply {
            val name = etName.text.toString()
            val product = etProduct.text.toString()
            val qty = etQty.text.toString()
            val price = etPrice.text.toString()

            if (validateInput(name, product, qty, price)) {
                cleanInput()
                productsToBuy.add(
                    Product(name, price.toInt(), qty.toInt(), price.toInt(), name)
                )
                etTotalPrice.setText(productsToBuy.getPriceTotal().toString())
                mAdapter.submitList(productsToBuy)
                setRecyclerView()
            }
        }
    }

    private fun List<Product>.getPriceTotal(): Number {
        return this.sumOf {
            ((it.totalPrice?.times(it.qty!!)) ?: 0)
        }
    }

    private fun cleanInput() {
        binding.apply {
            etProduct.text?.clear()
            etQty.text?.clear()
            etPrice.text?.clear()
            qty = 0
        }
    }

    private fun setRecyclerView() {
        binding.rvProductBuy.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }
    }

    private fun validateInput(name: String, product: String, qty: String, price: String): Boolean {
        binding.apply {
            if (name.isEmpty()) {
                return ilName.setInputError(getString(R.string.must_not_empty))
            }
            if (product.isEmpty()) {
                return ilProduct.setInputError(getString(R.string.choose_product))
            }
            if (qty.isEmpty()) {
                return ilQty.setInputError(getString(R.string.must_not_empty))
            }
            if (price.isEmpty()) {
                return ilProduct.setInputError(getString(R.string.choose_product))
            }

        }
        return true
    }

    private fun hideErrorInput() {
        binding.apply {
            etName.doAfterTextChanged { ilName.isErrorEnabled = false }
            etProduct.doAfterTextChanged { ilProduct.isErrorEnabled = false }
            etQty.doAfterTextChanged { ilQty.isErrorEnabled = false }
            etPrice.doAfterTextChanged { ilPrice.isErrorEnabled = false }
            etTotalPrice.doAfterTextChanged { ilTotal.isErrorEnabled = false }
            etPay.doAfterTextChanged { ilPay.isErrorEnabled = false }
            etChange.doAfterTextChanged { ilChange.isErrorEnabled = false }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is TextInputEditText || v is AutoCompleteTextView) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}