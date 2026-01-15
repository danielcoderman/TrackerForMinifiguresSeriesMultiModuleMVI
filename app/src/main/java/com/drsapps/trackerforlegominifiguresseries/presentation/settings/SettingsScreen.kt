package com.drsapps.trackerforlegominifiguresseries.presentation.settings

//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.consumeWindowInsets
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.example.trackerforlegominifiguresseries.presentation.util.Screen
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SettingsScreen(
//    viewModel: SettingsViewModel = hiltViewModel()
//) {
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(Screen.Settings.title!!)
//                }
//            )
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .padding(paddingValues)
//                .consumeWindowInsets(paddingValues)
//        ) {
//            Text("Welcome to the ${Screen.Settings.title!!} screen!")
//        }
//    }
//
//}