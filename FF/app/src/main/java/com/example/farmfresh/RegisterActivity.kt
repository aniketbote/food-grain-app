package com.example.farmfresh

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private var selectedPhotoUri: Uri ?= null
    private var gender: String ?= null
    private var mtoast: Toast ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val genderArray = resources.getStringArray(R.array.gender)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderArray)
        gender_registration.adapter = adapter

        gender_registration.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                //To change body of created functions use File | Settings | File Templates.
                gender = null

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //To change body of created functions use File | Settings | File Templates.
                gender = genderArray[p2]
                Log.d("RegisterActivity","$gender")
            }

        }



        register_registration.setOnClickListener {
            Log.d("RegisterActivity","Clicked Registration Button")
            perfromRegistration()
        }

        login_registration.setOnClickListener {
            Log.d( "RegisterActivity","Clicked Login Button")
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

        uploadphoto_registration.setOnClickListener {
            Log.d("RegisterActivity","Clicked Upload Photo")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
        address_registration.setOnClickListener{
            Log.d("Registration","Clicked address")
            val intent = Intent(this,AddAutoActivity::class.java)
            startActivityForResult(intent, 12)
        }

        birthdate_registration.setOnClickListener {
            Log.d("RegisterActivity","Clicked Birthdate")
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, myear, mmonth, mday ->

                // Display Selected date in textbox
                birthdate_registration.setText("" + mday + "/" + mmonth + "/" + myear)
            }, year, month, day)
            dpd.show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            Log.d("RegisterActivity", "Photo Selected Successfully")
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            showphoto_registration.setImageBitmap(bitmap)
            uploadphoto_registration.alpha = 0f

        }

        if(requestCode == 12 && resultCode == Activity.RESULT_OK && data != null){
            Log.d("RegisterActivity","Address received Successsfully")
            val addressHolder = data.getSerializableExtra("address")
            val address_reg:EditText = findViewById(R.id.address_registration)
            address_reg.setText("${addressHolder}")
            Log.d("RegisterActivity","${addressHolder}")
        }

    }


    private fun perfromRegistration(){
        Log.d("RegisterActivity","Function : performRegistration")
        val name = name_registration.text.toString()
        val email = email_registration.text.toString()
        val phone = phone_registration.text.toString()
        val address = address_registration.text.toString()
        val birthdate = birthdate_registration.text.toString()
        val password = password_registration.text.toString()
        val confirm_password = confirmpassword_registration.text.toString()




        //Sanity Checks : Empty
        if (name.isEmpty()){
            if(mtoast != null) mtoast!!.cancel()
            mtoast = Toast.makeText(this,"Name field cannot be empty",Toast.LENGTH_SHORT)
            mtoast!!.show()
            return
        }
        if (email.isEmpty()){
            if(mtoast != null) mtoast!!.cancel()
            mtoast = Toast.makeText(this,"Email field cannot be empty",Toast.LENGTH_SHORT)
            mtoast!!.show()
            return
        }
        if (phone.isEmpty()){
            if(mtoast != null) mtoast!!.cancel()
            mtoast = Toast.makeText(this,"Phone field cannot be empty",Toast.LENGTH_SHORT)
            mtoast!!.show()
            return

        }
        if (address.isEmpty()){
            if(mtoast != null) mtoast!!.cancel()
            mtoast = Toast.makeText(this,"Address field cannot be empty",Toast.LENGTH_SHORT)
            mtoast!!.show()
            return
        }
        if (birthdate.isEmpty()){
            if(mtoast != null) mtoast!!.cancel()
            mtoast = Toast.makeText(this,"Birthdate field cannot be empty",Toast.LENGTH_SHORT)
            mtoast!!.show()
            return
        }
        if (gender!!.isEmpty()){
            if(mtoast != null) mtoast!!.cancel()
            mtoast = Toast.makeText(this,"Please select gender",Toast.LENGTH_SHORT)
            mtoast!!.show()
            return
        }
        if (password.isEmpty()){
            if(mtoast != null) mtoast!!.cancel()
            mtoast = Toast.makeText(this,"Password field cannot be empty",Toast.LENGTH_SHORT)
            mtoast!!.show()
            return
        }


        //Sanity Checks : Email
        if(!EmailValidate(email)){
            if(mtoast != null) mtoast!!.cancel()
            mtoast = Toast.makeText(this,"Enter a valid Email",Toast.LENGTH_SHORT)
            mtoast!!.show()
            return
        }

        //Sanity Checks : Password Length
        if (password.length < 6 || password.length > 20){
            if(mtoast != null) mtoast!!.cancel()
            mtoast = Toast.makeText(this,"Password should contain 6 to 20 characters",Toast.LENGTH_SHORT)
            mtoast!!.show()
            return
        }

        //Sanity Checks : Phone Length
        if (phone.length != 10){
            if(mtoast != null) mtoast!!.cancel()
            mtoast = Toast.makeText(this,"Enter Valid Phone Number",Toast.LENGTH_SHORT)
            mtoast!!.show()
            return
        }

        //Sanity Check : password == confirm password

        if (password != confirm_password){
            if(mtoast != null) mtoast!!.cancel()
            mtoast = Toast.makeText(this,"Password and Confirm password are not same",Toast.LENGTH_SHORT)
            mtoast!!.show()
            return
        }

        uploadImageToFirebase(name)


    }

    private fun uploadImageToFirebase(filename : String){
        Log.d("RegisterActivity","Function : uploadImagetoFirebase : ${filename} ${selectedPhotoUri}")
        if (selectedPhotoUri == null){
            Log.d("RegisterActivity","Image not inserted")
            Toast.makeText(this,"Please insert photo",Toast.LENGTH_SHORT).show()
            return
        }
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("RegisterActivity","Image Uploaded Successfully: ${it.toString()}")
                    uploadUserToFirebase(it.toString())

                }
            }
            .addOnFailureListener {
                Log.d("RegisterActivity","Image Upload Failed: ${it}")
            }


    }

    private fun uploadUserToFirebase(imageUri:String){
        Log.d("RegisterActivity","Function : uploadusertoFirebase : ${imageUri}")
        val uid = FirebaseAuth.getInstance().uid.toString()
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, name_registration.text.toString(), email_registration.text.toString(), phone_registration.text.toString(), address_registration.text.toString(), birthdate_registration.text.toString(), gender!!, password_registration.text.toString(), imageUri)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity","User data Inserted to Firebase")

                //Firbase Auth Object
                val auth = FirebaseAuth.getInstance()
                auth.createUserWithEmailAndPassword(email_registration.text.toString(), password_registration.text.toString())
                    .addOnCompleteListener {
                        if (!it.isSuccessful){
                            return@addOnCompleteListener
                        }
                        Log.d("RegisterActivity","User created for authetication : ${it}")

                    }
                    .addOnFailureListener {
                        if(mtoast != null) mtoast!!.cancel()
                        mtoast = Toast.makeText(this,"${it.message}",Toast.LENGTH_SHORT)
                        mtoast!!.show()
                        Log.d("RegisterActivity","User could not be created for authentication : ${it.message}")
                    }



                if(mtoast != null) mtoast!!.cancel()
                mtoast = Toast.makeText(this,"Registration Succesful",Toast.LENGTH_SHORT)
                mtoast!!.show()
                Log.d("RegisterActivity","Starting Login Activity")
                val intent = Intent(this,LoginActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                if(mtoast != null) mtoast!!.cancel()
                mtoast = Toast.makeText(this,"${it.message}",Toast.LENGTH_SHORT)
                mtoast!!.show()
                Log.d("RegisterActivity","User data could not be inserted into firebase : ${it.message}")
            }
    }


    private fun EmailValidate(email: String): Boolean {
        Log.d("RegisterActivity","Funtion : email validate")
        return Patterns.EMAIL_ADDRESS.toRegex().matches(email)
        }
}

class User(val uid:String, val name:String, val email:String, val phone:String, val address:String, val birthdate:String, val gender:String, val password:String, val imageUri: String)
