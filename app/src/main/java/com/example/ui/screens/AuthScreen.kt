package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.theme.*
import com.example.viewmodel.DasaniViewModel

@Composable
fun AuthScreen(
    viewModel: DasaniViewModel,
    onAuthSuccess: () -> Unit
) {
    val incomingReferralCode by viewModel.incomingReferralCode.collectAsStateWithLifecycle()
    var isRegisterTab by remember { mutableStateOf(true) }
    var registerMode by remember { mutableStateOf(0) } // 0: Register with UPI ID, 1: Register with Mobile Number
    var upiInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var loginIdentifierInput by remember { mutableStateOf("") } // Can be UPI ID or Phone
    var passwordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var withdrawalPasswordInput by remember { mutableStateOf("") }
    var inviteCodeInput by remember { mutableStateOf(incomingReferralCode ?: "c07cdf4c") }

    LaunchedEffect(incomingReferralCode) {
        incomingReferralCode?.let { code ->
            inviteCodeInput = code
            isRegisterTab = true
        }
    }

    var passVisible by remember { mutableStateOf(false) }
    var confirmPassVisible by remember { mutableStateOf(false) }
    var withdrawPassVisible by remember { mutableStateOf(false) }
    var agreeTerms by remember { mutableStateOf(true) }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1B5E20), // Agriculture Green top
                        Color(0xFF0F3325), // Transition
                        Color(0xFF0B1713)  // Deep dark bottom
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Hero Banner (Galaxy Manual Seeder)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.galaxy_banner_top),
                    contentDescription = "Galaxy Manual Seeder Banner",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
                    contentScale = ContentScale.Crop
                )

                // Circular glowing Galaxy Seeder logo centered at the bottom of the hero banner
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 48.dp)
                        .size(100.dp)
                        .background(Color(0xFF0B1F17), CircleShape)
                        .border(3.dp, DasaniPrimary, CircleShape)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.galaxy_circle_logo),
                        contentDescription = "Galaxy Seeder Logo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(56.dp))

            // Main Dark Auth Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF13191C)
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Tab Row: Login | Register
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isRegisterTab = false }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "Login",
                                fontSize = 18.sp,
                                fontWeight = if (!isRegisterTab) FontWeight.Bold else FontWeight.Normal,
                                color = if (!isRegisterTab) Color.White else DasaniTextSecondary
                            )
                            if (!isRegisterTab) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(48.dp)
                                        .height(3.dp)
                                        .background(DasaniPrimary, RoundedCornerShape(2.dp))
                                )
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isRegisterTab = true }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "Register",
                                fontSize = 18.sp,
                                fontWeight = if (isRegisterTab) FontWeight.Bold else FontWeight.Normal,
                                color = if (isRegisterTab) Color.White else DasaniTextSecondary
                            )
                            if (isRegisterTab) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(48.dp)
                                        .height(3.dp)
                                        .background(DasaniPrimary, RoundedCornerShape(2.dp))
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    if (isRegisterTab) {
                        // Registration Mode Choice: UPI ID vs Mobile Number
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0F2620), RoundedCornerShape(12.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FilterChip(
                                selected = registerMode == 0,
                                onClick = { registerMode = 0 },
                                label = { Text("Register with UPI ID", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                leadingIcon = {
                                    Icon(Icons.Default.QrCode2, contentDescription = null, modifier = Modifier.size(16.dp))
                                },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DasaniPrimary,
                                    selectedLabelColor = Color(0xFF041C1A),
                                    containerColor = Color.Transparent,
                                    labelColor = DasaniTextSecondary
                                )
                            )
                            FilterChip(
                                selected = registerMode == 1,
                                onClick = { registerMode = 1 },
                                label = { Text("Mobile Number", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                leadingIcon = {
                                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(16.dp))
                                },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DasaniPrimary,
                                    selectedLabelColor = Color(0xFF041C1A),
                                    containerColor = Color.Transparent,
                                    labelColor = DasaniTextSecondary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (registerMode == 0) {
                            // UPI ID Register Field
                            OutlinedTextField(
                                value = upiInput,
                                onValueChange = { upiInput = it.trim() },
                                label = { Text("Your UPI ID (VPA)") },
                                placeholder = { Text("e.g. 9876543210@paytm or name@upi", color = Color(0xFF6B7D82), fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = DasaniPrimary)
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFF1A2124),
                                    unfocusedContainerColor = Color(0xFF1A2124),
                                    focusedBorderColor = DasaniPrimary,
                                    unfocusedBorderColor = Color(0xFF263238),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("upi_input_register")
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF0D251E), RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = DasaniPrimary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Auto-binds your UPI for 1-click deposit & instant withdrawal!",
                                    color = DasaniTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        } else {
                            // Mobile Number Register Field
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF1A2124), RoundedCornerShape(10.dp))
                                    .border(1.dp, Color(0xFF263238), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 14.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "+91", color = DasaniPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Box(modifier = Modifier.padding(horizontal = 10.dp).height(24.dp).width(1.dp).background(Color(0xFF37474F)))
                                TextField(
                                    value = phoneInput,
                                    onValueChange = { phoneInput = it.take(10) },
                                    placeholder = { Text("Enter 10-Digit Mobile", color = Color(0xFF6B7D82), fontSize = 14.sp) },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("phone_input")
                                )
                            }
                        }
                    } else {
                        // Login Tab: Login with UPI ID or Mobile Number
                        OutlinedTextField(
                            value = loginIdentifierInput,
                            onValueChange = { loginIdentifierInput = it },
                            label = { Text("UPI ID or Mobile Number") },
                            placeholder = { Text("e.g. yourname@paytm or 9876543210", color = Color(0xFF6B7D82), fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = DasaniPrimary)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF1A2124),
                                unfocusedContainerColor = Color(0xFF1A2124),
                                focusedBorderColor = DasaniPrimary,
                                unfocusedBorderColor = Color(0xFF263238),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_identifier_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password Input
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        placeholder = {
                            Text(
                                if (isRegisterTab) "Enter login password (min 4 chars)" else "Enter Password",
                                color = Color(0xFF6B7D82),
                                fontSize = 14.sp
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passVisible = !passVisible }) {
                                Icon(
                                    imageVector = if (passVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility",
                                    tint = Color(0xFF6B7D82)
                                )
                            }
                        },
                        visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1A2124),
                            unfocusedContainerColor = Color(0xFF1A2124),
                            focusedBorderColor = DasaniPrimary,
                            unfocusedBorderColor = Color(0xFF263238),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input")
                    )

                    if (isRegisterTab) {
                        Spacer(modifier = Modifier.height(14.dp))

                        // Password confirmation
                        OutlinedTextField(
                            value = confirmPasswordInput,
                            onValueChange = { confirmPasswordInput = it },
                            placeholder = { Text("Confirm login password", color = Color(0xFF6B7D82), fontSize = 14.sp) },
                            trailingIcon = {
                                IconButton(onClick = { confirmPassVisible = !confirmPassVisible }) {
                                    Icon(
                                        imageVector = if (confirmPassVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle confirm password",
                                        tint = Color(0xFF6B7D82)
                                    )
                                }
                            },
                            visualTransformation = if (confirmPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF1A2124),
                                unfocusedContainerColor = Color(0xFF1A2124),
                                focusedBorderColor = DasaniPrimary,
                                unfocusedBorderColor = Color(0xFF263238),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("confirm_password_input")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Withdrawal Password (PIN)
                        OutlinedTextField(
                            value = withdrawalPasswordInput,
                            onValueChange = { withdrawalPasswordInput = it },
                            placeholder = { Text("Enter Withdrawal PIN (4-6 digits)", color = Color(0xFF6B7D82), fontSize = 14.sp) },
                            trailingIcon = {
                                IconButton(onClick = { withdrawPassVisible = !withdrawPassVisible }) {
                                    Icon(
                                        imageVector = if (withdrawPassVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle withdrawal password",
                                        tint = Color(0xFF6B7D82)
                                    )
                                }
                            },
                            visualTransformation = if (withdrawPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF1A2124),
                                unfocusedContainerColor = Color(0xFF1A2124),
                                focusedBorderColor = DasaniPrimary,
                                unfocusedBorderColor = Color(0xFF263238),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("withdrawal_password_input")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Invitation Code Field
                        OutlinedTextField(
                            value = inviteCodeInput,
                            onValueChange = { inviteCodeInput = it },
                            placeholder = { Text("Invitation Code", color = Color(0xFF6B7D82), fontSize = 14.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.PersonAdd,
                                    contentDescription = "Invitation Code",
                                    tint = DasaniPrimary
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF1A2124),
                                unfocusedContainerColor = Color(0xFF1A2124),
                                focusedBorderColor = DasaniPrimary,
                                unfocusedBorderColor = Color(0xFF263238),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("invite_code_input")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Terms Checkbox
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = agreeTerms,
                                onCheckedChange = { agreeTerms = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = DasaniPrimary,
                                    uncheckedColor = Color(0xFF6B7D82),
                                    checkmarkColor = Color.Black
                                )
                            )
                            Text(
                                text = "I have agreed to the Terms & Conditions",
                                color = Color(0xFF8CA5A1),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Primary Action Button (Teal pill)
                    Button(
                        onClick = {
                            if (isRegisterTab) {
                                val primaryId = if (registerMode == 0) {
                                    if (upiInput.isNotBlank()) upiInput else "farmer@upi"
                                } else {
                                    if (phoneInput.isNotBlank()) phoneInput else "9876543210"
                                }
                                val res = viewModel.register(
                                    phoneInput = primaryId,
                                    pass1 = if (passwordInput.isBlank()) "123456" else passwordInput,
                                    pass2 = if (confirmPasswordInput.isBlank()) "123456" else confirmPasswordInput,
                                    withdrawPass = if (withdrawalPasswordInput.isBlank()) "123456" else withdrawalPasswordInput,
                                    invite = inviteCodeInput,
                                    upiIdInput = if (registerMode == 0) upiInput else ""
                                )
                                if (res.first) onAuthSuccess()
                            } else {
                                val loginId = when {
                                    loginIdentifierInput.isNotBlank() -> loginIdentifierInput
                                    phoneInput.isNotBlank() -> phoneInput
                                    upiInput.isNotBlank() -> upiInput
                                    else -> "farmer@upi"
                                }
                                val success = viewModel.login(
                                    phoneInput = loginId,
                                    passInput = if (passwordInput.isBlank()) "123456" else passwordInput
                                )
                                if (success) onAuthSuccess()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("auth_action_button"),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DasaniPrimary,
                            contentColor = Color(0xFF031A18)
                        )
                    ) {
                        Text(
                            text = if (isRegisterTab) "Register & Bind UPI" else "Login with Password",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Demo auto-fill button
                    OutlinedButton(
                        onClick = {
                            upiInput = "farmer987@upi"
                            loginIdentifierInput = "farmer987@upi"
                            phoneInput = "9876543210"
                            passwordInput = "123456"
                            confirmPasswordInput = "123456"
                            withdrawalPasswordInput = "123456"
                            inviteCodeInput = "c07cdf4c"
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(25.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DasaniPrimary.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = DasaniPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Auto-fill UPI Demo Account",
                            color = DasaniPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
