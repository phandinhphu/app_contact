package com.example.myapplication.ui.screen.user

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.activity.compose.BackHandler
import androidx.navigation.NavHostController
import com.example.myapplication.routes.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(onAddClick: () -> Unit, navController: NavHostController, userViewModel: UserViewModel) {
    val searchResult by userViewModel.searchResult.collectAsState()
    var query by remember { mutableStateOf("") }
    var searchActive by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val context = LocalContext.current

    // Lắng nghe sự kiện từ ViewModel
    LaunchedEffect(key1 = true) {
        userViewModel.uiEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // Bắt nút Back hệ thống
    BackHandler(enabled = searchActive) {
        searchActive = false
    }

    Scaffold(
        topBar = {
            if (searchActive) {
                // Giao diện TopAppBar khi đang tìm kiếm
                TopAppBar(
                    title = {
                        TextField(
                            value = query,
                            onValueChange = {
                                query = it
                                userViewModel.searchUsers(it)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                            placeholder = { Text("Tìm kiếm...") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors( // Làm cho TextField trông giống TopAppBar hơn
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            searchActive = false
                            query = ""
                            userViewModel.searchUsers("")
                            keyboardController?.hide() // Ẩn bàn phím
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Đóng tìm kiếm")
                        }
                    }
                )
            } else {
                // Giao diện TopAppBar bình thường
                CenterAlignedTopAppBar(
                    title = { Text("Danh bạ") },
                    actions = {
                        IconButton(onClick = {
                            searchActive = true // Mở ô tìm kiếm
                        }) {
                            Icon(Icons.Default.Search, contentDescription = "Tìm kiếm")
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(8.dp)
                .fillMaxSize()
        ) {
            items(searchResult) { user ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clickable {
                            navController.navigate(Routes.UserDetail.route + "/${user.id}")
                        },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = user.name, style = MaterialTheme.typography.titleMedium)
                        Text(text = user.phone, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
