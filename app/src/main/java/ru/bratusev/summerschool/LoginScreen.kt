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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.edit


/** Верстка для экрана входа */
@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val sharedPreferences =
        remember { context.getSharedPreferences("auth_data", Context.MODE_PRIVATE) }

    // Значение отображаемое в соответствующем поле.
    // Стандартное значение берется из sharedPreference по ключам.
    val loginText =
        rememberSaveable { mutableStateOf(sharedPreferences.getString("login", "") ?: "") }
    val passwordText =
        rememberSaveable { mutableStateOf(sharedPreferences.getString("password", "") ?: "") }
    val isChecked =
        rememberSaveable { mutableStateOf(sharedPreferences.getBoolean("remember_me", false)) }


    // Вертикальный контейнер:
    // fillMaxSize - занимает всё доступное место
    // padding - отступ от всех краев экрана на 32
    // verticalArrangement - выравнивение по вертикали (центр)
    // horizontalAlignment - выравнивание по горизонтали (центр)
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Заголовок
        Text(stringResource(id = R.string.login_title), style = MaterialTheme.typography.headlineMedium)

        // Отступ высотой 32dp (можно реализовать и с использованием padding)
        Spacer(modifier = Modifier.height(32.dp))

        // Поле для ввода логина
        // value - отображаемое значение (берется из переменной, объявленной ранее)
        // onValueChange - что делать при изменении текста в поле
        // label - подсказка для ввода
        // fillMaxWidth - занимает максимум доступной ширины в рамках контейнера Column
        TextField(
            value = loginText.value,
            onValueChange = {
                loginText.value = it
            },
            label = { Text(stringResource(id = R.string.login_login_field)) },
            modifier = Modifier.fillMaxWidth()
        )

        // Отступ высотой 16dp (можно реализовать и с использованием padding)
        Spacer(modifier = Modifier.height(16.dp))

        // Поле для ввода пароля
        // value - отображаемое значение (берется из переменной, объявленной ранее)
        // onValueChange - что делать при изменении текста в поле
        // label - подсказка для ввода
        // visualTransformation - скрытие пароля (замена символов на '*')
        // keyboardOptions - смена клавиатуры (можно настроить свою клавиатуру, например только цифры)
        // fillMaxWidth - занимает максимум доступной ширины в рамках контейнера Column
        TextField(
            value = passwordText.value,
            onValueChange = {
                passwordText.value = it
            },
            label = { Text(stringResource(id = R.string.login_password_field)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        // Отступ высотой 32dp (можно реализовать и с использованием padding)
        Spacer(modifier = Modifier.height(32.dp))

        // Кнопка для запуска авторизации
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // если валидация пройдена, то вывести Toast (сообщение на экране устрйоства)
                if (!executeLogin(
                        loginText.value,
                        passwordText.value,
                        isChecked.value,
                        sharedPreferences
                    )
                ) {
                    Toast.makeText(context, context.getString(R.string.login_empty_field_toast), Toast.LENGTH_SHORT).show()
                } else {
                    // TODO Навигация на домашний экран
                }
            }
        ) {
            Text(stringResource(id = R.string.login_signin_button))
        }

        // Отступ высотой 16dp (можно реализовать и с использованием padding)
        Spacer(modifier = Modifier.height(16.dp))

        // Кастомная компонента для checkBox
        RememberMe(isChecked.value) {
            isChecked.value = it
        }
    }
}

/** Кастомная компонента чекбокса, вынесенная в отдельную функцию
 * isChecked - отображает или скрывает галочку если true и false соответственн
 * onCheckChanged - функция, принимающая новое значение для isChecked
 * */
@Composable
fun RememberMe(isChecked: Boolean, onCheckChanged: (Boolean) -> Unit) {
    // Горизонтальный контейнер
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = {
                onCheckChanged(it)
            }
        )

        Text(
            text = stringResource(id = R.string.login_remember_me),
        )
    }
}


/** Валидация данных для входа
 *
 * Возвращаемые значения:
 *   true - валидация пройдена
 *   false - валидация не пройдена
 *
 * */
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


/** Сохранение в SharedPreference данных для входа
 *
 * Ключи и соответсвующие им значения:
 * login - введеный логин
 * password - введеный пароль
 * remember_me - флаг, отвечающий за необходимость сохранения
 *
 * */
private fun saveAuthData(login: String, password: String, sharedPreferences: SharedPreferences) {
    sharedPreferences.edit {
        putString("login", login)
        putString("password", password)
        putBoolean("remember_me", true)
    }
}
