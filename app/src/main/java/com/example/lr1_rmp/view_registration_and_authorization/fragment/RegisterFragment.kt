package com.example.lr1_rmp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lr1_rmp.R
import com.example.lr1_rmp.database.MainDb
import com.example.lr1_rmp.database.entities.UserEntity

class RegisterFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_register, container, false)

        val DB = MainDb.getDb(requireContext())

        val userLogin: EditText = view.findViewById(R.id.login)
        val userEmail: EditText = view.findViewById(R.id.email)
        val userPass: EditText = view.findViewById(R.id.password)
        val userRepPass: EditText = view.findViewById(R.id.repeatPassword)

        val btnReg: Button = view.findViewById(R.id.registration)
        btnReg.setOnClickListener(){

            val login = userLogin.text.toString().trim()
            val email = userEmail.text.toString().trim()
            val password = userPass.text.toString().trim()
            val repPassword = userRepPass.text.toString().trim()

            if (login == "" || email == "" || password == "" || repPassword == ""){

                Toast.makeText(requireContext(), "Есть пустые поля!", Toast.LENGTH_LONG).show()

            } else if (password != repPassword) {

                Toast.makeText(requireContext(), "Пароли не совпадают!", Toast.LENGTH_LONG).show()

            } else {

                val user = UserEntity(null, login, email, password)

                Thread{

                    val result = DB.getDao().getUserByLogin(login)
                    val boolResult: Boolean = result != null

                    if (boolResult){

                        view.post {

                            Toast.makeText(requireContext(), "Пользователь с таким именем уже существует!", Toast.LENGTH_LONG).show()

                        }

                    } else {

                        DB.getDao().insertUser(user)

                    }

                }.start()

            }

        }

        val regButton: Button = view.findViewById(R.id.button)

        regButton.setOnClickListener(){

            val authFragment = AuthorizationFragment()

            val ft = parentFragmentManager.beginTransaction()

            ft.replace(R.id.framelayout, authFragment)

            ft.commit()
        }

        // Inflate the layout for this fragment
        return view

    }

}