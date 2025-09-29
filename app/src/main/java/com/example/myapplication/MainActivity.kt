package com.example.myapplication

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.routes.Routes
import com.example.myapplication.ui.screen.user.AddUserScreen
import com.example.myapplication.ui.screen.user.*
import dagger.hilt.android.AndroidEntryPoint

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("UnrememberedGetBackStackEntry")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = Routes.UserList.route
            ) {
                composable(Routes.UserList.route) { backStackEntry ->
                    val userViewModel: UserViewModel = hiltViewModel(backStackEntry)

                    UserListScreen(
                        onAddClick = { navController.navigate(Routes.AddUser.route) },
                        navController = navController,
                        userViewModel = userViewModel
                    )
                }
                composable(Routes.AddUser.route) { backStackEntry ->
                    val userViewModel: UserViewModel = hiltViewModel(navController.getBackStackEntry(Routes.UserList.route))

                    AddUserScreen(
                        onSave = { name, phone ->
                            userViewModel.addUser(name, phone)
                            navController.popBackStack() // quay lại UserListScreen
                        },
                        onCancel = { navController.popBackStack() },
                        userViewModel = userViewModel
                    )
                }
                composable(Routes.UserDetail.route + "/{userId}") { backStackEntry ->
                    val userViewModel: UserViewModel =
                        hiltViewModel(navController.getBackStackEntry(Routes.UserList.route))
                    val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull()
                    val user = userViewModel.getUserById(userId)

                    if (user != null) {
                        UserDetailScreen(
                            user = user,
                            context = LocalContext.current,
                            onBack = { navController.popBackStack() },
                            userViewModel = userViewModel
                        )
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Người dùng vừa cấp quyền, bạn có thể gọi lại makePhoneCall nếu muốn
            }
        }
    }
}