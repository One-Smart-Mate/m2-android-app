package com.ih.m2.ui.components

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.ih.m2.R
import com.ih.m2.ui.extensions.getColor
import com.ih.m2.ui.extensions.getTextColor
import com.ih.m2.ui.pages.login.LoginContent
import com.ih.m2.ui.theme.M2androidappTheme

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    label: String,
    placeholder: String = "",
    value: String,
    icon: ImageVector,
    maxLines: Int = 1,
    onChange: (String) -> Unit,
) {

    val focusManager = LocalFocusManager.current
    val leadingIcon = @Composable {
        Icon(
            icon,
            contentDescription = "",
            tint = getColor()
        )
    }

    TextField(
        value = value,
        onValueChange = onChange,
        modifier = modifier,
        leadingIcon = leadingIcon,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
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
        maxLines = maxLines
    )
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
fun LoginPreview() {
    M2androidappTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            CustomTextField(
                value = "",
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.email),
                placeholder = stringResource(R.string.enter_your_email),
                icon = Icons.Default.Email
            ) {}
        }
    }
}