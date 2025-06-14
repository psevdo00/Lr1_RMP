package com.example.lr1_rmp.view_registration_and_authorization.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.lr1_rmp.R
import com.example.lr1_rmp.database.MainDb
import com.example.lr1_rmp.view_main_page.MainPageActivity

class AuthorizationFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_authorization, container, false)

        val session: SharedPreferences
        session = requireContext().getSharedPreferences("session", Context.MODE_PRIVATE)
        val resultSession = session.getBoolean("session", false)

        if (resultSession == true) {

            GoToMainPageActivity()

        }

        val DB = MainDb.getDb(requireContext())

        val userLogin: EditText = view.findViewById(R.id.auth_login)
        val userPassword: EditText = view.findViewById(R.id.auth_password)

        val btnAuth: Button = view.findViewById(R.id.authorization)
        btnAuth.setOnClickListener(){

            val login = userLogin.text.toString().trim()
            val password = userPassword.text.toString().trim()

            if (login == "" || password == ""){

                Toast.makeText(requireContext(), "Есть пустые поля!", Toast.LENGTH_LONG).show()

            } else {

                Thread{

                    val result = DB.getDao().getUserByLogin(login)

                    if(result != null){

                        val loginDB = result.user_login
                        val passwordDB = result.user_pass

                        if (loginDB == login && passwordDB == password){

                            view.post {

                                Toast.makeText(requireContext(), "Успешная авторизация!", Toast.LENGTH_LONG).show()

                                val editor = session.edit()
                                editor.putBoolean("session", true)
                                editor.putString("current_login_user", login)
                                editor.apply()

                                //TODO (Переход в основное окно)
                                GoToMainPageActivity()

                            }

                        } else {

                            view.post {

                                Toast.makeText(requireContext(), "Неправильный логин или пароль!", Toast.LENGTH_LONG).show()

                            }

                        }

                    } else {

                        view.post {

                            Toast.makeText(requireContext(), "Пользователь с таким именем не найден!", Toast.LENGTH_LONG).show()

                        }

                    }

                }.start()

            }

        }

        val authButton: Button = view.findViewById(R.id.auth_button)

        authButton.setOnClickListener(){

            val regFragment = RegisterFragment()

            val ft = parentFragmentManager.beginTransaction()

            ft.replace(R.id.framelayout, regFragment)

            ft.commit()
        }

        return view

    }

    fun GoToMainPageActivity(){

        val intent: Intent = Intent(requireContext(), MainPageActivity::class.java)
        startActivity(intent)

    }

}