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
    var upiInput by remember { mutableStateOf("") }
    var loginIdentifierInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var withdrawalPasswordInput by remember { mutableStateOf("") }
    var inviteCodeInput by remember { mutableStateOf(incomingReferralCode ?: "") }

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
                        OutlinedTextField(
                            value = upiInput,
                            onValueChange = { upiInput = it.trim() },
                            label = { Text("Your UPI ID (VPA)") },
                            placeholder = { Text("e.g. yourname@okaxis", color = Color(0xFF6B7D82), fontSize = 13.sp) },
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
                                text = "Binds your UPI ID for payouts & withdrawals",
                                color = DasaniTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    } else {
                        OutlinedTextField(
                            value = loginIdentifierInput,
                            onValueChange = { loginIdentifierInput = it.trim() },
                            label = { Text("UPI ID or Account ID") },
                            placeholder = { Text("e.g. yourname@okaxis", color = Color(0xFF6B7D82), fontSize = 13.sp) },
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

                    Button(
                        onClick = {
                            if (isRegisterTab) {
                                val res = viewModel.register(
                                    phoneInput = upiInput,
                                    pass1 = passwordInput,
                                    pass2 = confirmPasswordInput,
                                    withdrawPass = withdrawalPasswordInput,
                                    invite = inviteCodeInput,
                                    upiIdInput = upiInput
                                )
                                if (res.first) {
                                    onAuthSuccess()
                                } else {
                                    viewModel.showToast(res.second)
                                }
                            } else {
                                val success = viewModel.login(
                                    phoneInput = loginIdentifierInput,
                                    passInput = passwordInput
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
                }
            }
        }
    }
}
