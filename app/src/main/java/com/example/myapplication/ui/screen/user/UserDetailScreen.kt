package com.example.myapplication.ui.screen.user

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.User
import com.example.myapplication.util.PhoneUtils
import com.example.myapplication.util.SmsUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    userId: Int?,
    context: Context,
    onBack: () -> Unit,
    userViewModel: UserViewModel
) {
    val userState by userViewModel.userUiState.collectAsState()
    LaunchedEffect(userId) {
        userViewModel.getUserById(userId)
    }

    when (userState) {
        is UserUiState.Loading -> CircularProgressIndicator()
        is UserUiState.Success -> {
            val user = (userState as UserUiState.Success).user

            var editableUser by remember { mutableStateOf(user) }

            var showDeleteConfirmDialog by remember { mutableStateOf<User?>(null) }
            var showSmsDialog by remember { mutableStateOf(false) }
            var smsContent by remember { mutableStateOf("") }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Chi tiết người dùng") },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                            }
                        }
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = editableUser.name,
                        onValueChange = { editableUser = editableUser.copy(name = it) },
                        label = { Text("Tên") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = editableUser.phone,
                        onValueChange = { editableUser = editableUser.copy(phone = it) },
                        label = { Text("Số điện thoại") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row {
                        Button(onClick = {
                            userViewModel.updateUser(editableUser)
                            onBack()
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Lưu")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(onClick = {
                            PhoneUtils.makePhoneCall(context, editableUser.phone)
                        }) {
                            Icon(Icons.Default.Phone, contentDescription = "Gọi điện")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(onClick = {
                            showSmsDialog = true
                        }) {
                            Icon(Icons.Default.Sms, contentDescription = "Gửi Sms")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            onClick = {
                                showDeleteConfirmDialog = editableUser
                            }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Xóa")
                        }
                    }
                }
            }

            if (showDeleteConfirmDialog != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirmDialog = null },
                    title = { Text("Xác nhận xóa") },
                    text = { Text("Bạn có chắc chắn muốn xóa người dùng '${showDeleteConfirmDialog?.name}' không?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteConfirmDialog?.let { userToDelete ->
                                    userViewModel.deleteUser(userToDelete)
                                }
                                showDeleteConfirmDialog = null
                                onBack()
                            }
                        ) {
                            Text("Xóa", color = Color.Red)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteConfirmDialog = null }) {
                            Text("Hủy")
                        }
                    }
                )
            }

            if (showSmsDialog) {
                AlertDialog(
                    onDismissRequest = { showSmsDialog = false },
                    title = { Text("Gửi SMS") },
                    text = {
                        OutlinedTextField(
                            value = smsContent,
                            onValueChange = { smsContent = it },
                            label = { Text("Nội dung tin nhắn") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val activity = context as? Activity
                            activity?.let {
                                SmsUtils.sendSms(it, editableUser.phone, smsContent)
                            }
                            showSmsDialog = false
                            smsContent = "" // reset
                        }) {
                            Text("Gửi")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSmsDialog = false }) {
                            Text("Hủy")
                        }
                    }
                )
            }
        }
        is UserUiState.Error -> Text((userState as UserUiState.Error).message)
    }
}