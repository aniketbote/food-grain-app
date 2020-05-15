package com.example.farmfresh.Activities

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.farmfresh.Utilities.HelperUtils
import com.example.farmfresh.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private var selectedPhotoUri: Uri ?= null
    private var gender: String = ""
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
                gender = ""

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
            val loginIntent = Intent(this,
                LoginActivity::class.java)
            startActivity(loginIntent)
        }

        uploadphoto_registration.setOnClickListener {
            Log.d("RegisterActivity","Clicked Upload Photo")
            val actionIntent = Intent(Intent.ACTION_PICK)
            actionIntent.type = "image/*"
            startActivityForResult(actionIntent,0)
        }
        address_registration.setOnClickListener{
            Log.d("Registration","Clicked address")
            val addressIntent = Intent(this,
                AddAutoActivity::class.java)
            startActivityForResult(addressIntent, 12)
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





//        //Sanity Checks : Empty
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
        if (gender == ""){
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
        if (confirm_password.isEmpty()){
            if(mtoast != null) mtoast!!.cancel()
            mtoast = Toast.makeText(this,"Password field cannot be empty",Toast.LENGTH_SHORT)
            mtoast!!.show()
            return
        }


        //Sanity Checks : Email
        if(!HelperUtils.EmailValidate(email)){
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

        val emailHash = HelperUtils.generatehash(email)
        var userInfoObject = User(
            emailHash,
            "",
            name,
            email,
            phone,
            address,
            birthdate,
            gender!!,
            password,
            ""
        )

        uploadImageToFirebase(userInfoObject)

    }

    private fun uploadImageToFirebase(user_data: User){
        Log.d("RegisterActivity","Function : uploadImagetoFirebase : ${user_data.email_hash} ${selectedPhotoUri}")
        if (selectedPhotoUri == null){
            Log.d("RegisterActivity","Image not inserted")
            Toast.makeText(this,"Please insert photo",Toast.LENGTH_SHORT).show()
            return
        }

        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_bar,null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        val message = dialogView.findViewById<TextView>(R.id.text_progressBar)
        message.text = "Creating User"
        val dialog = builder.create()
        dialog.show()

        val ref = FirebaseStorage.getInstance().getReference("/images/${user_data.email_hash}")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("RegisterActivity","Image Uploaded Successfully: ${it.toString()}")
                    user_data.imageUri = it.toString()
                    uploadUserToFirebase(user_data, dialog)

                }
            }
            .addOnFailureListener {
                dialog.dismiss()
                if(mtoast != null) mtoast!!.cancel()
                mtoast = Toast.makeText(this,"Registration Failed : ${it}",Toast.LENGTH_SHORT)
                mtoast!!.show()
                Log.d("RegisterActivity","Image Upload Failed: ${it}")
                return@addOnFailureListener
            }


    }

    private fun uploadUserToFirebase(user_data: User, dialog: AlertDialog){
        Log.d("RegisterActivity","Function : uploadusertoFirebase : ${user_data.imageUri}")
        val uid = FirebaseAuth.getInstance().uid.toString()
        user_data.uid = uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/${user_data.email_hash}")
        ref.setValue(user_data)
            .addOnSuccessListener {
                Log.d("RegisterActivity","User data Inserted to Firebase")

                //Firbase Auth Object
                val auth = FirebaseAuth.getInstance()
                auth.createUserWithEmailAndPassword(user_data.email, user_data.password)
                    .addOnCompleteListener {
                        if (!it.isSuccessful){
                            return@addOnCompleteListener
                        }
                        Log.d("RegisterActivity","User created for authetication : ${it}")
                        if(mtoast != null) mtoast!!.cancel()
                        mtoast = Toast.makeText(this,"Registration Succesful",Toast.LENGTH_SHORT)
                        mtoast!!.show()
                        Log.d("RegisterActivity","Starting Login Activity")
                        val loginIntent = Intent(this,
                            LoginActivity::class.java)
                        startActivity(loginIntent)
                        dialog.dismiss()
                        finish()
                    }
                    .addOnFailureListener {
                        //delete user and image
                        val refS = FirebaseStorage.getInstance().getReference("/images/${user_data.email_hash}")
                        refS.delete()
                            .addOnSuccessListener {
                                Log.d("RegisterActivity","Failed to create user authentication : Image deleted from Firebase Storage")
                            }
                            .addOnFailureListener {
                                Log.d("RegisterActivity","Failed to create user authentication: Failed to delete image : ${it}")
                                return@addOnFailureListener
                            }

                        val refD = FirebaseDatabase.getInstance().getReference("/users/${user_data.email_hash}")
                        refD.removeValue()
                            .addOnSuccessListener {
                                Log.d("RegisterActivity","Failed to create user authentication : User deleted from Firebase Database")
                            }
                            .addOnFailureListener {
                                Log.d("RegisterActivity","Failed to create user authentication: Failed to delete user : ${it}")
                                return@addOnFailureListener
                            }
                        dialog.dismiss()
                        if(mtoast != null) mtoast!!.cancel()
                        mtoast = Toast.makeText(this,"${it.message}",Toast.LENGTH_SHORT)
                        mtoast!!.show()
                        Log.d("RegisterActivity","User could not be created for authentication : ${it.message}")
                    }

            }
            .addOnFailureListener {
                //delete image
                val ref = FirebaseStorage.getInstance().getReference("/images/${user_data.email_hash}")
                ref.delete()
                    .addOnSuccessListener {
                        Log.d("RegisterActivity","Failed to insert user data into firebase : Image deleted from Firebase Storage")
                    }
                    .addOnFailureListener {
                        Log.d("RegisterActivity","Failed to insert user data into firebase : ${it}")
                        return@addOnFailureListener
                    }
                dialog.dismiss()
                if(mtoast != null) mtoast!!.cancel()
                mtoast = Toast.makeText(this,"${it.message}",Toast.LENGTH_SHORT)
                mtoast!!.show()
                Log.d("RegisterActivity","User data could not be inserted into firebase : ${it.message}")
            }
    }

}

class User(val email_hash:String, var uid:String, val name:String, val email:String, val phone:String, val address:String, val birthdate:String, val gender:String, val password:String, var imageUri: String)