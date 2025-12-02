package com.donchik.akadeska.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.donchik.akadeska.di.AppGraph
import com.donchik.akadeska.presentation.admin.AdminGateViewModel
import com.donchik.akadeska.presentation.admin.AdminGateVmFactory
import com.donchik.akadeska.presentation.admin.AdminScreen
import com.donchik.akadeska.presentation.admin.AdminViewModel
import com.donchik.akadeska.presentation.admin.AdminVmFactory
import com.donchik.akadeska.presentation.auth.AuthScreen
import com.donchik.akadeska.presentation.auth.AuthViewModel
import com.donchik.akadeska.presentation.auth.AuthVmFactory
import com.donchik.akadeska.presentation.createpost.CreatePostScreen
import com.donchik.akadeska.presentation.createpost.CreatePostViewModel
import com.donchik.akadeska.presentation.createpost.CreatePostVmFactory
import com.donchik.akadeska.presentation.details.DetailsScreen
import com.donchik.akadeska.presentation.details.DetailsViewModel
import com.donchik.akadeska.presentation.details.DetailsVmFactory
import com.donchik.akadeska.presentation.home.HomeScreen
import com.donchik.akadeska.presentation.home.HomeViewModel
import com.donchik.akadeska.presentation.home.HomeVmFactory
import kotlinx.coroutines.launch
import navigation.Screen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // admin gate
    val adminGateVm: AdminGateViewModel = viewModel(factory = AdminGateVmFactory(AppGraph.repo))
    val isAdmin by adminGateVm.isAdmin.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                isAdmin = isAdmin,
                onOpenAdmin = {
                    scope.launch { drawerState.close() }
                    navController.navigate(Screen.Admin.route)
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("AkaDeska") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { AppGraph.repo.signOut() }) {
                            Icon(Icons.Default.Person, contentDescription = "Sign out")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    val isSignedIn = AppGraph.repo.currentUser != null
                    if (isSignedIn) navController.navigate(Screen.CreatePost.route)
                    else navController.navigate(Screen.Auth.route)
                }) { Icon(Icons.Default.Add, null) }
            },
            bottomBar = {
                BottomBar(
                    currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route,
                    onSelect = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        ) { inner ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(inner)
            ) {
                composable(Screen.Home.route) {
                    val vm: HomeViewModel = viewModel(factory = HomeVmFactory(AppGraph.repo))
                    HomeScreen(
                        vm = vm,
                        onOpenDetails = { id -> navController.navigate("details/$id") }
                    )
                }
                composable(Screen.Shop.route) {
                    ShopScreen(onOpenDetails = { id -> navController.navigate("details/$id") })
                }
                composable(Screen.Chat.route) { ChatScreen() }

                composable(Screen.Auth.route) {
                    val vm: AuthViewModel = viewModel(factory = AuthVmFactory(AppGraph.repo))
                    AuthScreen(vm = vm) {
                        navController.popBackStack()
                    }
                }
                composable(Screen.CreatePost.route) {
                    if (AppGraph.repo.currentUser == null) {
                        LaunchedEffect(Unit) { navController.navigate(Screen.Auth.route) }
                    } else {
                        val vm: CreatePostViewModel = viewModel(factory = CreatePostVmFactory(AppGraph.repo))
                        CreatePostScreen(vm = vm) { navController.popBackStack() }
                    }
                }
                composable(Screen.Admin.route) {
                    val vm: AdminViewModel = viewModel(factory = AdminVmFactory(AppGraph.repo))
                    AdminScreen(vm = vm) { navController.popBackStack() }
                }
                composable(Screen.Details.pattern) { entry ->
                    val postId = entry.arguments?.getString("postId")!!
                    val vm: DetailsViewModel = viewModel(
                        factory = DetailsVmFactory(AppGraph.repo, postId)
                    )
                    DetailsScreen(vm)
                }
            }
        }
    }
}
//@Composable
//fun NavBar(
//    selectedTab: BottomTab,
//    onTabSelected: (BottomTab) -> Unit
//) {
//    NavigationBar (containerColor = MaterialTheme.colorScheme.secondary) {
//        NavigationBarItem(
//            selected = selectedTab == BottomTab.HOME,
//            onClick = { onTabSelected(BottomTab.HOME) },
//            icon = { Icon(Icons.Outlined.Home, null) },
//            label = { Text("Home") },
//            colors = NavigationBarItemDefaults.colors(
//                indicatorColor = MaterialTheme.colorScheme.primary,
//                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
//                selectedTextColor = MaterialTheme.colorScheme.onSecondary,
//                unselectedIconColor = MaterialTheme.colorScheme.onSecondary,
//                unselectedTextColor = MaterialTheme.colorScheme.onSecondary
//            )
//        )
//        NavigationBarItem(
//            selected = selectedTab == BottomTab.SHOP,
//            onClick = { onTabSelected(BottomTab.SHOP) },
//            icon = { Icon(Icons.Outlined.ShoppingCart, null) },
//            label = { Text("Shop") },
//            colors = NavigationBarItemDefaults.colors(
//                indicatorColor = MaterialTheme.colorScheme.primary,
//                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
//                selectedTextColor = MaterialTheme.colorScheme.onSecondary,
//                unselectedIconColor = MaterialTheme.colorScheme.onSecondary,
//                unselectedTextColor = MaterialTheme.colorScheme.onSecondary
//            )
//        )
//        NavigationBarItem(
//            selected = selectedTab == BottomTab.CHAT,
//            onClick = { onTabSelected(BottomTab.CHAT) },
//            icon = { Icon(Icons.AutoMirrored.Outlined.Chat, null) },
//            label = { Text("Chat") },
//            colors = NavigationBarItemDefaults.colors(
//                indicatorColor = MaterialTheme.colorScheme.primary,
//                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
//                selectedTextColor = MaterialTheme.colorScheme.onSecondary,
//                unselectedIconColor = MaterialTheme.colorScheme.onSecondary,
//                unselectedTextColor = MaterialTheme.colorScheme.onSecondary
//            )
//        )
//    }
//}
@Composable
private fun BottomBar(currentRoute: String?, onSelect: (String) -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.secondary) {
        @Composable
        fun item(route: String, icon: ImageVector, label: String) {
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = { onSelect(route) },
                icon = { Icon(icon, null) },
                label = { Text(label) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.onSecondary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSecondary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
        item(Screen.Home.route, Icons.Outlined.Home, "Home")
        item(Screen.Shop.route, Icons.Outlined.ShoppingCart, "Shop")
        item(Screen.Chat.route, Icons.AutoMirrored.Outlined.Chat, "Chat")
    }
}
//@Preview
//@Composable
//fun NavBarPreview() {
//    NavBar(
//        selectedTab = BottomTab.HOME,
//        onTabSelected = {})
//}