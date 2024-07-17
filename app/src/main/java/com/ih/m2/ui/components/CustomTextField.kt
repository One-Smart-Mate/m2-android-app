package com.ih.m2.ui.components

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.ih.m2.R
import com.ih.m2.ui.extensions.getColor
import com.ih.m2.ui.theme.M2androidappTheme
import com.ih.m2.ui.utils.EMPTY

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    label: String,
    placeholder: String = EMPTY,
    icon: ImageVector,
    maxLines: Int = 1,
    isPassword: Boolean = false,
    onChange: (String) -> Unit,
) {

    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var text by rememberSaveable { mutableStateOf(EMPTY) }


    val leadingIcon = @Composable {
        Icon(
            icon,
            contentDescription = EMPTY,
            tint = getColor()
        )
    }

    TextField(
        value = text,
        onValueChange = {
            text = it
            onChange(it)
        },
        modifier = modifier,
        leadingIcon = leadingIcon,
        keyboardOptions = KeyboardOptions(
            autoCorrect = false,
            keyboardType = KeyboardType.Text,
        ),
        visualTransformation = if (passwordVisible || isPassword.not()) VisualTransformation.None else PasswordVisualTransformation(),
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        shape = RoundedCornerShape(25),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.primary,
            unfocusedContainerColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = getColor(),
            unfocusedTextColor = getColor(),
            focusedLabelColor = getColor(),
            unfocusedLabelColor = getColor(),
            focusedPlaceholderColor = getColor(),
            unfocusedPlaceholderColor = getColor(),
            disabledTextColor = getColor()
        ),
        maxLines = maxLines,
        trailingIcon = {
            if (isPassword) {
                val image = if (passwordVisible)
                    painterResource(id = R.drawable.ic_visibility)
                else painterResource(id = R.drawable.ic_visibility_off)

                // Please provide localized description for accessibility services
                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(painter = image, description, tint = getColor())
                }
            }
        }
    )
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
private fun LoginPreview() {
    M2androidappTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            Column {
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(R.string.email),
                    placeholder = stringResource(R.string.enter_your_email),
                    icon = Icons.Outlined.Email
                ) {}
                CustomSpacer()
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(R.string.password),
                    placeholder = stringResource(R.string.enter_your_password),
                    icon = Icons.Outlined.Lock,
                    isPassword = true
                ) {}
            }
        }
    }
}