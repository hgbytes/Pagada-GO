package com.pagadasports.pagada.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pagadasports.pagada.R
import com.pagadasports.pagada.ui.theme.*
import com.pagadasports.pagada.viewmodel.AuthViewModel
import com.pagadasports.pagada.utils.InputSanitizer
import kotlinx.coroutines.delay

private fun validatePassword(password: String): List<String> {
    val errors = mutableListOf<String>()
    if (password.length < 8) {
        errors.add("be at least 8 characters")
    }
    if (!password.any { it.isDigit() }) {
        errors.add("contain a number")
    }
    if (!password.any { it.isUpperCase() }) {
        errors.add("contain an uppercase letter")
    }
    if (!password.any { it.isLowerCase() }) {
        errors.add("contain a lowercase letter")
    }
    if (password.all { it.isLetterOrDigit() }) {
        errors.add("contain a special character")
    }
    return errors
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit,
    onRegisterSuccess: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel()
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var termsAccepted by rememberSaveable { mutableStateOf(false) }
    var emailError by rememberSaveable { mutableStateOf("") }
    var animationStarted by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val authState by authViewModel.authState.collectAsState()

    // SECURITY: Enhanced validation with input sanitization
    val passwordValidation = InputSanitizer.validatePasswordStrength(password)
    val isNameValid = InputSanitizer.isValidName(name.trim())
    val isEmailValid = InputSanitizer.isValidEmail(email.trim())
    val arePasswordsMatching = password == confirmPassword && confirmPassword.isNotEmpty()
    val isPasswordValid = passwordValidation.isValid && password.isNotEmpty()
    val isFormValid = isNameValid && isEmailValid && arePasswordsMatching && isPasswordValid && termsAccepted

    LaunchedEffect(Unit) {
        delay(100)
        animationStarted = true
    }

    // Show error message when registration fails
    LaunchedEffect(authState.error) {
        authState.error?.let { error ->
            android.util.Log.d("RegisterScreen", "Auth error: $error")
            if (error.contains("already", ignoreCase = true) ||
                error.contains("exists", ignoreCase = true) ||
                error.contains("registered", ignoreCase = true) ||
                error.contains("User already registered", ignoreCase = true)) {
                emailError = "This email is already registered"
            } else {
                emailError = error
            }
        }
    }

    // Navigate on successful authentication
    LaunchedEffect(authState.isAuthenticated) {
        android.util.Log.d("RegisterScreen", "Auth state changed: isAuthenticated=${authState.isAuthenticated}")
        if (authState.isAuthenticated) {
            android.util.Log.d("RegisterScreen", "Calling onRegisterSuccess()")
            onRegisterSuccess()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF6B4CE8),
                        Color(0xFF4A148C),
                        BackgroundDark
                    ),
                    center = androidx.compose.ui.geometry.Offset(0f, 0f),
                    radius = 1500f
                )
            )
    ) {
        // Sporty Background Decorations
        SportyBackgroundDecoration()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(scrollState)
        ) {
            // Top Bar with Back Button
            AnimatedVisibility(
                visible = animationStarted,
                enter = fadeIn(tween(400)) + slideInVertically(
                    animationSpec = tween(400),
                    initialOffsetY = { -50 }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PagadaSpacing.medium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(PagadaSpacing.small))

            // Registration Form Card with Sporty Design
            AnimatedVisibility(
                visible = animationStarted,
                enter = fadeIn(tween(600, delayMillis = 200)) + slideInVertically(
                    animationSpec = tween(600, delayMillis = 200),
                    initialOffsetY = { 100 }
                )
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = PagadaSpacing.large)
                        .animateContentSize(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(28.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 16.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PagadaSpacing.extraLarge),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Pagada Logo (Circular)
                        androidx.compose.foundation.Image(
                            painter = painterResource(id = R.drawable.logo_pagada),
                            contentDescription = "Pagada Logo",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .padding(PagadaSpacing.extraSmall),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(PagadaSpacing.medium))

                        Text(
                            text = "JOIN THE TEAM",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.5.sp
                            ),
                            color = Color(0xFF1A1A3E),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Create your player account",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = Color(0xFF6B4CE8),
                            modifier = Modifier.padding(top = PagadaSpacing.extraSmall)
                        )

                        Spacer(modifier = Modifier.height(PagadaSpacing.large))

                        // Name Field with SECURITY validation
                        SportyTextField(
                            value = name,
                            onValueChange = { 
                                // SECURITY: Sanitize input in real-time
                                name = InputSanitizer.sanitizeName(it)
                            },
                            label = "Full Name",
                            leadingIcon = Icons.Default.Person,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            isError = !isNameValid && name.isNotEmpty(),
                            errorMessage = if (!isNameValid && name.isNotEmpty()) "Please enter a valid name (letters, spaces, hyphens only)" else ""
                        )

                        Spacer(modifier = Modifier.height(PagadaSpacing.medium))

                        // Email Field with SECURITY sanitization
                        SportyTextField(
                            value = email,
                            onValueChange = {
                                // SECURITY: Sanitize email input
                                email = InputSanitizer.sanitizeEmail(it)
                                emailError = ""
                                authViewModel.clearError()
                            },
                            label = "Email Address",
                            leadingIcon = Icons.Default.Email,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            isError = emailError.isNotEmpty() || (!isEmailValid && email.isNotEmpty()),
                            errorMessage = if (emailError.isNotEmpty()) emailError else if (!isEmailValid && email.isNotEmpty()) "Please enter a valid email address" else ""
                        )

                    // Show "Already have an account?" card when email exists
                    if (emailError.contains("already registered", ignoreCase = true)) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Already have an account?",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(
                                    onClick = {
                                        emailError = ""
                                        authViewModel.clearError()
                                        onLoginClick()
                                    }
                                ) {
                                    Text(
                                        "Login Here",
                                        color = AccentPink,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                        // Password Field
                        SportyTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Create Password",
                            leadingIcon = Icons.Default.Lock,
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                        tint = Color(0xFF6B4CE8)
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            isError = !passwordValidation.isValid && password.isNotEmpty(),
                            errorMessage = if (!passwordValidation.isValid && password.isNotEmpty()) "Password must:\n" + passwordValidation.errors.joinToString(separator = "\n") { "• $it" } else ""
                        )

                        Spacer(modifier = Modifier.height(PagadaSpacing.medium))

                        // Confirm Password Field
                        SportyTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "Confirm Password",
                            leadingIcon = Icons.Default.Lock,
                            trailingIcon = {
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        imageVector = if (confirmPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                        contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                        tint = Color(0xFF6B4CE8)
                                    )
                                }
                            },
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            ),
                            isError = !arePasswordsMatching && confirmPassword.isNotEmpty(),
                            errorMessage = if (!arePasswordsMatching && confirmPassword.isNotEmpty()) "Passwords do not match" else ""
                        )

                        Spacer(modifier = Modifier.height(PagadaSpacing.medium))

                    // Terms and Conditions
                    Card(
                        modifier = Modifier,
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = termsAccepted,
                                onCheckedChange = { termsAccepted = it },
                                modifier = Modifier.size(22.dp),
                                colors = CheckboxDefaults.colors(
                                    checkedColor = PrimaryPurple,
                                    uncheckedColor = TextTertiary
                                )
                            )

                            Column(modifier = Modifier.padding(start = 8.dp)) {
                                val annotatedString = buildAnnotatedString {
                                    append("I agree to the ")
                                    pushStringAnnotation(tag = "URL", annotation = "https://www.pagadasports.com/terms")
                                    withStyle(style = SpanStyle(color = PrimaryPurple, fontWeight = FontWeight.SemiBold)) {
                                        append("Terms and Conditions")
                                    }
                                    pop()
                                    append(" and ")
                                    pushStringAnnotation(tag = "PRIVACY", annotation = "https://www.pagadasports.com/privacy")
                                    withStyle(style = SpanStyle(color = PrimaryPurple, fontWeight = FontWeight.SemiBold)) {
                                        append("Privacy Policy")
                                    }
                                    pop()
                                }

                                val uriHandler = LocalUriHandler.current
                                ClickableText(
                                    text = annotatedString,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                ) { offset ->
                                    annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset).firstOrNull()?.let { uriHandler.openUri(it.item) }
                                    annotatedString.getStringAnnotations(tag = "PRIVACY", start = offset, end = offset).firstOrNull()?.let { uriHandler.openUri(it.item) }
                                }
                            }
                        }
                    }

                        Spacer(modifier = Modifier.height(PagadaSpacing.medium))

                        // Sporty Gradient Register Button
                        val buttonInteraction = remember { MutableInteractionSource() }
                        val isPressed by buttonInteraction.collectIsPressedAsState()
                        val buttonScale by animateFloatAsState(
                            targetValue = if (isPressed) 0.95f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "buttonScale"
                        )

                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                if (isFormValid) {
                                    // SECURITY: Final validation before submission
                                    val sanitizedName = InputSanitizer.sanitizeName(name)
                                    val sanitizedEmail = InputSanitizer.sanitizeEmail(email)
                                    authViewModel.signUp(sanitizedEmail, password, sanitizedName)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .graphicsLayer {
                                    scaleX = buttonScale
                                    scaleY = buttonScale
                                },
                            enabled = isFormValid && !authState.isLoading,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                            ),
                            contentPadding = PaddingValues(0.dp),
                            interactionSource = buttonInteraction
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFF6B4CE8),
                                                Color(0xFFE91E8C)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (authState.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(28.dp),
                                        color = Color.White,
                                        strokeWidth = 3.dp
                                    )
                                } else {
                                    Text(
                                        text = "CREATE ACCOUNT",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            letterSpacing = 1.5.sp
                                        ),
                                        color = Color.White,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(PagadaSpacing.large))

                        // Login Link with Sporty Style
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Already on the team? ",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color(0xFF1A1A3E)
                            )
                            TextButton(
                                onClick = onLoginClick,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = "SIGN IN",
                                    color = Color(0xFFE91E8C),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Extra bottom spacing to ensure content is visible above keyboard
            Spacer(modifier = Modifier.height(200.dp))
        }
    }
}

@Composable
private fun SportyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { 
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            ) 
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = label,
                tint = if (isFocused) PrimaryPurple else Color.Gray
            )
        },
        trailingIcon = trailingIcon,
        modifier = modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = Color.Black,
            fontWeight = FontWeight.Medium
        ),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
        isError = isError,
        supportingText = if (errorMessage.isNotEmpty()) {
            { Text(errorMessage, color = AccentPink, fontWeight = FontWeight.Medium) }
        } else null,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            disabledTextColor = Color.Black,
            errorTextColor = Color.Black,
            focusedContainerColor = Color(0xFFF5F5FF),
            unfocusedContainerColor = Color(0xFFF8F8F8),
            focusedBorderColor = PrimaryPurple,
            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
            focusedLabelColor = Color.Black,
            unfocusedLabelColor = Color.Black,
            cursorColor = PrimaryPurple,
            focusedPlaceholderColor = Color.Gray,
            unfocusedPlaceholderColor = Color.Gray,
            errorBorderColor = Color.Red,
            errorLabelColor = Color.Red,
            errorCursorColor = Color.Red
        ),
        interactionSource = interactionSource
    )
}

// Sporty Background Decoration Component
@Composable
private fun SportyBackgroundDecoration() {
    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-100).dp, y = (-100).dp)
                .alpha(0.15f),
            shape = CircleShape,
            color = AccentPink
        ) {}
        
        Surface(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.TopEnd)
                .offset(x = 80.dp, y = (-50).dp)
                .alpha(0.1f),
            shape = CircleShape,
            color = Color(0xFFE91E8C)
        ) {}
        
        Surface(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-80).dp, y = 100.dp)
                .alpha(0.12f),
            shape = CircleShape,
            color = PrimaryPurple
        ) {}
        
        Surface(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 60.dp, y = 80.dp)
                .alpha(0.08f),
            shape = CircleShape,
            color = Color.Black
        ) {}
    }
}
