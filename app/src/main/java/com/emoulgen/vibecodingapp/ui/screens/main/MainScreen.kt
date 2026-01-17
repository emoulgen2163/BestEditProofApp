package com.emoulgen.vibecodingapp.ui.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.emoulgen.vibecodingapp.R
import com.emoulgen.vibecodingapp.ui.theme.BackgroundDark
import com.emoulgen.vibecodingapp.ui.theme.TealLight
import com.emoulgen.vibecodingapp.ui.theme.TealPrimary
import com.emoulgen.vibecodingapp.ui.theme.TextSecondary
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController = rememberNavController()) {
    var selectedTab by remember { mutableStateOf(0) }
    Scaffold(
        topBar = {
            AppHeader(navController)
        },
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onNavigateToProfile = {  }
            )
        }
    ) {
        Column(Modifier.fillMaxSize().padding(it)) {
            when(selectedTab){
                0 -> DashboardScreen(navController = navController)
                1 -> ProfileScreen(navController = navController)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AppHeader(navController: NavController = rememberNavController()) {
    TopAppBar(
        title = {},
        navigationIcon = {
            Image(
                painter = painterResource(id = R.drawable.best_edit_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(100.dp)
            )
        },
        actions = {
            IconButton(onClick = { navController.navigate("profile") }) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    tint = TealPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(onClick = {
                val auth = FirebaseAuth.getInstance()
                auth.signOut()
                navController.navigate("splash")
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Profile",
                    tint = TealPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BackgroundDark
        )
    )
}


@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    NavigationBar(
        containerColor = BackgroundDark,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Dashboard",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Dashboard",
                    fontSize = 12.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TealPrimary,
                selectedTextColor = TealPrimary,
                indicatorColor = TealLight.copy(alpha = 0.2f),
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
            )
        )

        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = {
                onTabSelected(1)
                onNavigateToProfile()
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Profile",
                    fontSize = 12.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TealPrimary,
                selectedTextColor = TealPrimary,
                indicatorColor = TealLight.copy(alpha = 0.2f),
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}