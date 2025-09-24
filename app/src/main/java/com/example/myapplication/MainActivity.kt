package com.example.myapplication

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.repository.UserRepositoryImpl
import com.example.myapplication.domain.usecase.user.*
import com.example.myapplication.routes.Routes
import com.example.myapplication.ui.screen.user.AddUserScreen
import com.example.myapplication.ui.screen.user.*

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            // khởi tạo các usecase và repository
            val repository = UserRepositoryImpl()
            val getUsersUseCase = GetUsersUseCase(repository)
            val addUserUseCase = AddUserUseCase(repository)
            val updateUserUseCase = UpdateUserUseCase(repository)
            val deleteUserUseCase = DeleteUserUseCase(repository)

            // khởi tạo ViewModel
            val userViewModel: UserViewModel = viewModel(
                factory = UserViewModelFactory(
                    getUsersUseCase,
                    addUserUseCase,
                    updateUserUseCase,
                    deleteUserUseCase,
                    ValidateUserNameUseCase(),
                    ValidatePhoneNumberUseCase()
                )
            )

            NavHost(
                navController = navController,
                startDestination = Routes.UserList.route
            ) {
                composable(Routes.UserList.route) {
                    UserListScreen(
                        onAddClick = { navController.navigate(Routes.AddUser.route) },
                        navController = navController,
                        userViewModel = userViewModel
                    )
                }
                composable(Routes.AddUser.route) {
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
                    val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull()
                    val user = userViewModel.getUserById(userId)
                    if (user != null) {
                        UserDetailScreen(
                            user = user,
                            userViewModel = userViewModel,
                            context = LocalContext.current,
                            onBack = { navController.popBackStack() }
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