package ru.bratusev.summerschool

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.navigation.NavController

@Composable
fun LoginScreen(modifier: Modifier = Modifier, navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences =
        remember { context.getSharedPreferences("auth_data", Context.MODE_PRIVATE) }

    val loginText =
        rememberSaveable { mutableStateOf(sharedPreferences.getString("login", "") ?: "") }
    val passwordText =
        rememberSaveable { mutableStateOf(sharedPreferences.getString("password", "") ?: "") }
    val isChecked =
        rememberSaveable { mutableStateOf(sharedPreferences.getBoolean("remember_me", false)) }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sign In", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        TextField(
            value = loginText.value,
            onValueChange = {
                loginText.value = it
            },
            label = { Text("Login") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = passwordText.value,
            onValueChange = {
                passwordText.value = it
            },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (!executeLogin(
                        loginText.value,
                        passwordText.value,
                        isChecked.value,
                        sharedPreferences
                    )
                ) {
                    Toast.makeText(context, "Some field is empty", Toast.LENGTH_SHORT).show()
                } else {
                    navController.navigate(Screen.Home.route)
                }
            }
        ) {
            Text("Sign in")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isChecked.value,
                onCheckedChange = {
                    isChecked.value = it
                }
            )

            Text(
                text = "Remember me",
            )
        }
    }
}

private fun executeLogin(
    login: String,
    password: String,
    isChecked: Boolean,
    sharedPreferences: SharedPreferences
): Boolean =
    if (login.isNotEmpty() && password.isNotEmpty()) {
        if (isChecked) {
            saveAuthData(login, password, sharedPreferences)
        } else {
            saveAuthData("", "", sharedPreferences)
        }
        true
    } else false

private fun saveAuthData(login: String, password: String, sharedPreferences: SharedPreferences) {
    sharedPreferences.edit {
        putString("login", login)
        putString("password", password)
        putBoolean("remember_me", true)
    }
}
