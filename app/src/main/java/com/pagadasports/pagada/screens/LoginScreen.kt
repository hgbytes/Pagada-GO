package com.pagadasports.pagada.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onRegisterClick: () -> Unit,
    onBackClick: () -> Unit,
    onLoginSuccess: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel()
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var emailError by rememberSaveable { mutableStateOf("") }
    var passwordError by rememberSaveable { mutableStateOf("") }
    var animationStarted by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val authState by authViewModel.authState.collectAsState()

    // SECURITY: Enhanced validation
    val isEmailValid = InputSanitizer.isValidEmail(email.trim())
    val isPasswordValid = password.isNotEmpty() && password.length >= 6
    val isFormValid = isEmailValid && isPasswordValid

    LaunchedEffect(Unit) {
        delay(100)
        animationStarted = true
    }

    // Show error from auth state
    LaunchedEffect(authState.error) {
        authState.error?.let { error ->
            emailError = error
        }
    }

    // Navigate on successful authentication
    LaunchedEffect(authState.isAuthenticated) {
        android.util.Log.d("LoginScreen", "Auth state changed: isAuthenticated=${authState.isAuthenticated}")
        if (authState.isAuthenticated) {
            android.util.Log.d("LoginScreen", "Calling onLoginSuccess()")
            onLoginSuccess()
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
                            contentDescription = stringResource(R.string.back_content_description),
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(PagadaSpacing.small))

            // Login Form Card with Sporty Design
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
                        .padding(horizontal = PagadaSpacing.large),
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
                                .size(120.dp)
                                .clip(CircleShape)
                                .padding(PagadaSpacing.small),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(PagadaSpacing.medium))

                        // Bold Title
                        Text(
                            text = "PLAYER LOGIN",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.5.sp
                            ),
                            color = Color(0xFF1A1A3E),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Enter the game zone",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = Color(0xFF6B4CE8),
                            modifier = Modifier.padding(top = PagadaSpacing.extraSmall)
                        )

                        Spacer(modifier = Modifier.height(PagadaSpacing.extraLarge))

                        // Email Field with Sporty Style and SECURITY sanitization
                        EnhancedTextField(
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
                            isError = emailError.isNotEmpty(),
                            errorMessage = emailError
                        )

                        Spacer(modifier = Modifier.height(PagadaSpacing.medium))

                        // Password Field
                        EnhancedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                passwordError = ""
                                authViewModel.clearError()
                            },
                            label = "Password",
                            leadingIcon = Icons.Default.Lock,
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                        contentDescription = if (passwordVisible) {
                                            stringResource(R.string.hide_password)
                                        } else {
                                            stringResource(R.string.show_password)
                                        },
                                        tint = Color(0xFF6B4CE8)
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    if (isFormValid) {
                                        authViewModel.signIn(email, password)
                                    }
                                }
                            ),
                            isError = passwordError.isNotEmpty(),
                            errorMessage = passwordError
                        )

                        // Forgot Password
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = PagadaSpacing.small),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = onForgotPasswordClick
                            ) {
                                Text(
                                    "Forgot Password?",
                                    color = AccentPink,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(PagadaSpacing.medium))

                        // Sporty Gradient Login Button
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
                                if (!isEmailValid) {
                                    emailError = "Please enter a valid email address"
                                }
                                if (!isPasswordValid) {
                                    passwordError = "Password must be at least 6 characters"
                                }
                                if (isFormValid) {
                                    // SECURITY: Final sanitization before submission
                                    val sanitizedEmail = InputSanitizer.sanitizeEmail(email)
                                    authViewModel.signIn(sanitizedEmail, password)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .graphicsLayer {
                                    scaleX = buttonScale
                                    scaleY = buttonScale
                                },
                            enabled = !authState.isLoading,
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
                                        text = "LOGIN NOW",
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

                        // Divider with text
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Divider(
                                modifier = Modifier.weight(1f),
                                color = Color.Gray.copy(alpha = 0.3f),
                                thickness = 1.dp
                            )
                            Text(
                                text = "OR",
                                modifier = Modifier.padding(horizontal = 16.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                            Divider(
                                modifier = Modifier.weight(1f),
                                color = Color.Gray.copy(alpha = 0.3f),
                                thickness = 1.dp
                            )
                        }

                        Spacer(modifier = Modifier.height(PagadaSpacing.large))

                        // Sign Up Link with Sporty Style
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "New Player? ",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color(0xFF1A1A3E)
                            )
                            TextButton(
                                onClick = onRegisterClick,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = "JOIN THE TEAM",
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
private fun EnhancedTextField(
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
                    fontWeight = FontWeight.SemiBold
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
            focusedTextColor = Color(0xFF1A1A3E),
            unfocusedTextColor = Color(0xFF1A1A3E),
            focusedContainerColor = Color(0xFFF5F5FF),
            unfocusedContainerColor = Color(0xFFF8F8F8),
            focusedBorderColor = PrimaryPurple,
            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
            focusedLabelColor = PrimaryPurple,
            unfocusedLabelColor = Color.Gray,
            cursorColor = PrimaryPurple,
            errorBorderColor = AccentPink,
            errorLabelColor = AccentPink,
            errorCursorColor = AccentPink
        ),
        interactionSource = interactionSource
    )
}

// Sporty Background Decoration Component
@Composable
private fun SportyBackgroundDecoration() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Geometric sports-inspired circles/patterns
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
            color = Color.White
        ) {}
    }
}

