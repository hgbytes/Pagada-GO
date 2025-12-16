package com.pagadasports.pagada.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pagadasports.pagada.ui.theme.*
import com.pagadasports.pagada.utils.InputSanitizer
import com.pagadasports.pagada.viewmodel.PasswordResetState
import com.pagadasports.pagada.viewmodel.PasswordResetStep
import com.pagadasports.pagada.viewmodel.PasswordResetViewModel
import kotlinx.coroutines.delay

@Composable
fun ForgotPasswordScreen(
    onBackClick: () -> Unit,
    onSuccessNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PasswordResetViewModel = viewModel()
) {
    val resetState by viewModel.resetState.collectAsState()
    
    // Navigate to login after success
    LaunchedEffect(resetState.currentStep) {
        if (resetState.currentStep is PasswordResetStep.Success) {
            delay(3000) // Show success message for 3 seconds
            viewModel.resetFlow()
            onSuccessNavigateToLogin()
        }
    }

    when (resetState.currentStep) {
        is PasswordResetStep.EnterEmail -> {
            ForgotPasswordEmailScreen(
                state = resetState,
                onBackClick = onBackClick,
                onSendResetEmail = { email ->
                    viewModel.sendResetEmail(email)
                },
                onClearError = { viewModel.clearError() },
                modifier = modifier
            )
        }
        is PasswordResetStep.Success -> {
            SuccessScreen(
                message = resetState.successMessage,
                onNavigateToLogin = {
                    viewModel.resetFlow()
                    onSuccessNavigateToLogin()
                },
                modifier = modifier
            )
        }
        else -> {
            // Future: Handle OTP and Reset Password steps
            ForgotPasswordEmailScreen(
                state = resetState,
                onBackClick = onBackClick,
                onSendResetEmail = { email ->
                    viewModel.sendResetEmail(email)
                },
                onClearError = { viewModel.clearError() },
                modifier = modifier
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ForgotPasswordEmailScreen(
    state: PasswordResetState,
    onBackClick: () -> Unit,
    onSendResetEmail: (String) -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by rememberSaveable { mutableStateOf("") }
    var animationStarted by remember { mutableStateOf(false) }
    
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    
    // Validation
    val isEmailValid = InputSanitizer.isValidEmail(email.trim())
    
    // Continuous pulsing animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    LaunchedEffect(Unit) {
        delay(100)
        animationStarted = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF6B4CE8),
                        Color(0xFF3D2A7D),
                        Color(0xFF1A0F3E),
                        BackgroundDark
                    ),
                    center = androidx.compose.ui.geometry.Offset(0.5f, 0.3f),
                    radius = 1800f
                )
            )
    ) {
        // Dynamic background decorations
        PasswordResetBackgroundDecorations(pulseAlpha, rotationAngle)
        
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
                        .padding(16.dp),
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

            Spacer(modifier = Modifier.height(40.dp))

            // Main Content Card
            AnimatedVisibility(
                visible = animationStarted,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    initialScale = 0.8f
                ) + fadeIn(tween(600))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
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
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Icon with pulsing effect
                        Surface(
                            modifier = Modifier
                                .size(100.dp)
                                .graphicsLayer {
                                    scaleX = 1f + (pulseAlpha - 0.45f) * 0.2f
                                    scaleY = 1f + (pulseAlpha - 0.45f) * 0.2f
                                },
                            shape = CircleShape,
                            color = AccentPink.copy(alpha = 0.1f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = AccentPink
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Forgot Password?",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            ),
                            color = Color(0xFF1A1A3E),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "No worries! Enter your registered email and we'll send you a link to reset your password.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                lineHeight = 22.sp
                            ),
                            color = Color(0xFF6B4CE8),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Email Field
                        SecureTextField(
                            value = email,
                            onValueChange = {
                                email = InputSanitizer.sanitizeEmail(it)
                                onClearError()
                            },
                            label = "Email Address",
                            leadingIcon = Icons.Default.Email,
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done,
                            onImeAction = {
                                focusManager.clearFocus()
                                if (isEmailValid && !state.isLoading) {
                                    onSendResetEmail(email.trim())
                                }
                            },
                            isError = state.error != null || (!isEmailValid && email.isNotEmpty()),
                            errorMessage = state.error ?: if (!isEmailValid && email.isNotEmpty()) "Please enter a valid email address" else ""
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Send Reset Link Button
                        val buttonInteraction = remember { MutableInteractionSource() }
                        val isPressed by buttonInteraction.collectIsPressedAsState()
                        val buttonScale by animateFloatAsState(
                            targetValue = if (isPressed) 0.94f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "buttonScale"
                        )

                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                if (isEmailValid) {
                                    onSendResetEmail(email.trim())
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .graphicsLayer {
                                    scaleX = buttonScale
                                    scaleY = buttonScale
                                },
                            enabled = isEmailValid && !state.isLoading,
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
                                if (state.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(28.dp),
                                        color = Color.White,
                                        strokeWidth = 3.dp
                                    )
                                } else {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Send,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "SEND RESET LINK",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                letterSpacing = 1.sp
                                            ),
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Back to Login
                        TextButton(
                            onClick = onBackClick,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    tint = Color(0xFF6B4CE8),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Back to Login",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = Color(0xFF6B4CE8)
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
private fun SuccessScreen(
    message: String,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var animationStarted by remember { mutableStateOf(false) }
    
    val infiniteTransition = rememberInfiniteTransition(label = "success")
    val successScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        delay(100)
        animationStarted = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF6B4CE8),
                        Color(0xFF3D2A7D),
                        Color(0xFF1A0F3E),
                        BackgroundDark
                    ),
                    center = androidx.compose.ui.geometry.Offset(0.5f, 0.5f),
                    radius = 1500f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = animationStarted,
            enter = scaleIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                initialScale = 0.5f
            ) + fadeIn(tween(600))
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 24.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Success Icon with animation
                    Surface(
                        modifier = Modifier
                            .size(120.dp)
                            .graphicsLayer {
                                scaleX = successScale
                                scaleY = successScale
                            },
                        shape = CircleShape,
                        color = Color(0xFF4CAF50).copy(alpha = 0.15f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Success",
                                modifier = Modifier.size(64.dp),
                                tint = Color(0xFF4CAF50)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Check Your Email!",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color(0xFF1A1A3E),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            lineHeight = 24.sp
                        ),
                        color = Color(0xFF6B4CE8),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Click the link in the email to reset your password.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = onNavigateToLogin,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(0.dp)
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
                            Text(
                                text = "BACK TO LOGIN",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                ),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PasswordResetBackgroundDecorations(pulseAlpha: Float, rotationAngle: Float) {
    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-100).dp, y = (-80).dp)
                .alpha(pulseAlpha * 0.4f)
                .rotate(rotationAngle * 0.3f),
            shape = CircleShape,
            color = AccentPink
        ) {}
        
        Surface(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.TopEnd)
                .offset(x = 80.dp, y = (-40).dp)
                .alpha(pulseAlpha * 0.3f)
                .rotate(-rotationAngle * 0.2f),
            shape = CircleShape,
            color = Color(0xFF9C27B0)
        ) {}
        
        Surface(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-80).dp, y = 100.dp)
                .alpha(pulseAlpha * 0.35f)
                .rotate(rotationAngle * 0.4f),
            shape = CircleShape,
            color = PrimaryPurple
        ) {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SecureTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    onImeAction: () -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    Column(modifier = modifier.fillMaxWidth()) {
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
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = Color.Black,
                fontWeight = FontWeight.Medium
            ),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                onDone = { onImeAction() }
            ),
            singleLine = true,
            isError = isError,
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
        
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}
