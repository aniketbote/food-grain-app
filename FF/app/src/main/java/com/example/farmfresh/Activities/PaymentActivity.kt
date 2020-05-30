package com.example.farmfresh.Activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.farmfresh.Database.CartDatabase
import com.example.farmfresh.Model.OrderList
import com.example.farmfresh.Model.PlaceOrderResponse
import com.example.farmfresh.R
import com.example.farmfresh.Payment.PaymentsUtil
import com.example.farmfresh.Retrofit.RetrofitClient
import com.example.farmfresh.Utilities.HelperUtils
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_payment.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToLong

class PaymentActivity :AppCompatActivity(){
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.d("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.d("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.d("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    private fun checkConnection(context: Context) {
        val isConnected = isOnline(context)
        Log.d("LoadingActivity", "$isConnected")

        if(!isConnected){
            Log.d("LoadingActivity", "No connection : Starting No Connection Activity")
            val noConnectionIntent = Intent(context, NoConnectionActivity::class.java)
            startActivityForResult(noConnectionIntent,999)
        }
    }

    private lateinit var paymentsClient: PaymentsClient
    private val LOAD_PAYMENT_DATA_REQUEST_CODE = 991
    private var address:String = ""
    private var price:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        checkConnection(this)
        Log.d("PaymentActivity", "Payment Activity Started")
        address = intent.getStringExtra("address") as String
        price = intent.getStringExtra("price") as String

        Log.d("PaymentActivity", "${address}  ${price}")



        paymentsClient = PaymentsUtil.createPaymentsClient(this)
        possiblyShowGooglePayButton()

        googlePayButton.setOnClickListener { requestPayment() }

    }

    private fun possiblyShowGooglePayButton() {

        val isReadyToPayJson = PaymentsUtil.isReadyToPayRequest() ?: return
        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString()) ?: return

        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        val task = paymentsClient.isReadyToPay(request)
        task.addOnCompleteListener { completedTask ->
            try {
                completedTask.getResult(ApiException::class.java)?.let(::setGooglePayAvailable)
            } catch (exception: ApiException) {
                // Process error
                Log.d("PaymentActivity", "isReadyToPay failed : ${exception}")
            }
        }
    }
    private fun setGooglePayAvailable(available: Boolean) {
        if (available) {
            googlePayButton.visibility = View.VISIBLE
        } else {
            Toast.makeText(
                this,
                "Unfortunately, Google Pay is not available on this device",
                Toast.LENGTH_LONG).show();
        }
    }
    private fun requestPayment() {

        // Disables the button to prevent multiple clicks.
        googlePayButton.isClickable = false

        // The price provided to the API should include taxes and shipping.
        // This price is not displayed to the user.
        val price = (1).toString()

        val paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(price)
        if (paymentDataRequestJson == null) {
            Log.d("PaymentActivity", "RequestPayment : Can't fetch payment data request")
            return
        }
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())

        // Since loadPaymentData may show the UI asking the user to select a payment method, we use
        // AutoResolveHelper to wait for the user interacting with it. Once completed,
        // onActivityResult will be called with the result.
        if (request != null) {
            AutoResolveHelper.resolveTask(
                paymentsClient.loadPaymentData(request), this, LOAD_PAYMENT_DATA_REQUEST_CODE)

        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            // value passed in AutoResolveHelper
            LOAD_PAYMENT_DATA_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK ->
                        data?.let { intent ->
                            PaymentData.getFromIntent(intent)?.let(::handlePaymentSuccess)
                        }
                    Activity.RESULT_CANCELED -> {
                        // Nothing to do here normally - the user simply cancelled without selecting a
                        // payment method.
                    }

                    AutoResolveHelper.RESULT_ERROR -> {
                        AutoResolveHelper.getStatusFromIntent(data)?.let {
                            handleError(it.statusCode)
                        }
                    }
                }
                // Re-enables the Google Pay payment button.
                googlePayButton.isClickable = true
            }
        }
    }
    private fun handleError(statusCode: Int) {
        Log.d("PaymentActivity", "loadPaymentData failed : ${String.format("Error code: %d", statusCode)}")
    }
    private fun handlePaymentSuccess(paymentData: PaymentData) {
        val paymentInformation = paymentData.toJson() ?: return

        try {
            // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
            val paymentMethodData = JSONObject(paymentInformation).getJSONObject("paymentMethodData")

            // If the gateway is set to "example", no payment information is returned - instead, the
            // token will only consist of "examplePaymentMethodToken".

            val billingName = paymentMethodData.getJSONObject("info")
                .getJSONObject("billingAddress").getString("name")
            Log.d("PaymentActivity", "BillingName : $billingName")

            Toast.makeText(this, getString(R.string.payments_show_name, billingName), Toast.LENGTH_LONG).show()

            // Logging token string.
            Log.d("PaymentActivity", "GooglePaymentToken : ${paymentMethodData.getJSONObject("tokenizationData").getString("token")}")
            createOrder()

        } catch (e: JSONException) {
            Log.d("PaymentActivity", "handlePaymentSuccess : Error: " + e.toString())
        }

    }
    fun createOrder(){
        //progress bar
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_bar,null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        val message = dialogView.findViewById<TextView>(R.id.text_progressBar)
        message.text = "Please Wait while we place your order"
        val dialog = builder.create()
        dialog.show()

        val token = getSharedPreferences("UserSharedPreferences", Context.MODE_PRIVATE)
        val emailHash = token.getString("EMAILHASH", "").toString()

        val db = CartDatabase(this)
        val cartJsonObj = db.readDataJson()
        val cartList = db.readData()
        Log.d("PaymentActivity","$cartJsonObj")

        RetrofitClient.instance.placeorder(cartJsonObj, emailHash,address)
            .enqueue(object : Callback<PlaceOrderResponse> {
                override fun onFailure(call: Call<PlaceOrderResponse>, t: Throwable) {
                    dialog.dismiss()
                    Toast.makeText(
                        this@PaymentActivity,
                        "Failed : ${t.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("PlaceOrderActivity", "Failed : ${t.message}")
                }

                override fun onResponse(
                    call: Call<PlaceOrderResponse>,
                    response: Response<PlaceOrderResponse>
                ) {
                    Log.d("PlaceOrderActivity", "Successful : ${response.body()?.message}")
                    if (response.body() == null) {
                        dialog.dismiss()
                        Toast.makeText(
                            this@PaymentActivity,
                            "Some Error Occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    if (response.body()?.errorCode == 0) {
                        Log.d("PlaceOrderActivity", "Setting shared preferences")
                        val pref = this@PaymentActivity.getSharedPreferences(
                            "$emailHash",
                            Context.MODE_PRIVATE
                        )
                        val editor = pref.edit()
                        editor.putString("pendingOrder", true.toString())
                        editor.putString("previousOrderAddress", address)
                        editor.commit()

                        for (i in 0 until cartList.size) {
                            db.deleteData(cartList[i].name)
                        }
                        cartCount = 0
                        itemText.visibility = View.INVISIBLE

                        val currentRef = FirebaseDatabase.getInstance()
                            .getReference("all_orders/$emailHashGlobal/current")
                        currentRef.addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                Log.d("PlaceOrderActivity", "Error occured: ${p0}")
                                return
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                val orderList =
                                    HelperUtils.getOrderList(
                                        p0
                                    )
                                val orderListObj = OrderList(orderList)
                                Log.d("PlaceOrderActivity", "${orderList}")
                                val currentOrdersIntent =
                                    Intent(this@PaymentActivity, CurrentOrdersActivity::class.java)
                                currentOrdersIntent.putExtra("orderListObj", orderListObj)
                                currentOrdersIntent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                                Toast.makeText(
                                    this@PaymentActivity, "${response.body()?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(currentOrdersIntent)
                                dialog.dismiss()
                                finish()
                            }
                        })

                    }

                }
            })
    }

    override fun onBackPressed() {
        val placeOrderIntent = Intent(this, PlaceOrderActivity::class.java)
        startActivity(placeOrderIntent)
    }
}
