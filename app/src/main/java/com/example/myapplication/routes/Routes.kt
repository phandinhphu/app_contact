package com.example.myapplication.routes

sealed class Routes private constructor(val route: String) {
    object UserList : Routes("user_list")
    object AddUser : Routes("add_user")
    object UserDetail : Routes("user_detail")
}