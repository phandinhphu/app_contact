package com.example.myapplication.ui.screen.user

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.User
import com.example.myapplication.util.PhoneUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    user: User,
    userViewModel: UserViewModel,
    context: Context,
    onBack: () -> Unit
) {
    var editableUser by remember { mutableStateOf(user) }
    var showDeleteConfirmDialog by remember { mutableStateOf<User?>(null) }

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
                    Text("Sửa")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = {
                    PhoneUtils.makePhoneCall(context, editableUser.phone)
                }) {
                    Text("Gọi")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    onClick = {
                        showDeleteConfirmDialog = editableUser
                    }
                ) {
                    Text("Xóa")
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
}