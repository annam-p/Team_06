package com.team06.focuswork.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.team06.focuswork.MainActivity
import com.team06.focuswork.R
import com.team06.focuswork.databinding.ActivityLoginBinding
import com.team06.focuswork.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firstname: EditText
    private lateinit var lastname: EditText
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var login: Button
    private lateinit var register: Button
    private lateinit var loading: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindComponents()
        setUpRegisterFormState()
        setUpRegisterResult()
        setUpTextListeners()
        setUpSubmitButtons()
    }

    private fun setUpSubmitButtons() {
        register.setOnClickListener {
            loading.visibility = View.VISIBLE
            loginViewModel.register(
                firstname.text.toString(), lastname.text.toString(),
                username.text.toString(), password.text.toString()
            )
        }

        login.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setUpTextListeners() {
        username.afterTextChanged {
            loginViewModel.registerDataChanged(
                firstname.text.toString(),
                lastname.text.toString(),
                username.text.toString(),
                password.text.toString()
            )
        }
        password.afterTextChanged {
            loginViewModel.registerDataChanged(
                firstname.text.toString(),
                lastname.text.toString(),
                username.text.toString(),
                password.text.toString()
            )
        }
        password.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE ->
                    loginViewModel.register(
                        firstname.text.toString(),
                        lastname.text.toString(),
                        username.text.toString(),
                        password.text.toString()
                    )
            }
            false
        }
    }

    private fun setUpRegisterResult() {
        loginViewModel.loginResult.observe(this@RegisterActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
                return@Observer
            }
            if (loginResult.success != null) {
                updateUiWithUser()
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
        })
    }

    private fun setUpRegisterFormState() {
        loginViewModel.registerFormState.observe(this@RegisterActivity, Observer {
            val registerState = it ?: return@Observer

            // disable login button unless both username / password is valid
            register.isEnabled = registerState.isDataValid
            if (registerState.usernameError != null) {
                username.error = getString(registerState.usernameError)
            }
            if (registerState.passwordError != null) {
                password.error = getString(registerState.passwordError)
            }
            if (registerState.firstnameError != null) {
                password.error = getString(registerState.firstnameError)
            }
            if (registerState.lastnameError != null) {
                password.error = getString(registerState.lastnameError)
            }
        })
    }

    private fun bindComponents() {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        firstname = binding.firstname
        lastname = binding.lastname
        username = binding.username
        password = binding.password
        login = binding.login
        register = binding.register
        loading = binding.loading
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)
    }

    private fun updateUiWithUser() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}
