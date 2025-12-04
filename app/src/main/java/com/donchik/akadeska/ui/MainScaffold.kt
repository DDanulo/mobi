package com.donchik.akadeska.ui

import android.app.Application
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
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
import com.donchik.akadeska.presentation.chat.ChatDetailScreen
import com.donchik.akadeska.presentation.createpost.CreatePostScreen
import com.donchik.akadeska.presentation.createpost.CreatePostViewModel
import com.donchik.akadeska.presentation.createpost.CreatePostVmFactory
import com.donchik.akadeska.presentation.details.DetailsScreen
import com.donchik.akadeska.presentation.details.DetailsViewModel
import com.donchik.akadeska.presentation.details.DetailsVmFactory
import com.donchik.akadeska.presentation.home.HomeScreen
import com.donchik.akadeska.presentation.home.HomeViewModel
import com.donchik.akadeska.presentation.home.HomeVmFactory
import com.donchik.akadeska.presentation.shop.ShopScreen
import com.donchik.akadeska.presentation.shop.ShopViewModel
import com.donchik.akadeska.presentation.shop.ShopVmFactory
import kotlinx.coroutines.launch
import navigation.Screen
import com.donchik.akadeska.R
import com.donchik.akadeska.com.donchik.akadeska.presentation.drawer.DrawerViewModel
import com.donchik.akadeska.com.donchik.akadeska.presentation.drawer.DrawerVmFactory
import com.donchik.akadeska.presentation.shopItemDetail.ShopDetailsScreen
import com.donchik.akadeska.presentation.shopItemDetail.ShopDetailsViewModel
import com.donchik.akadeska.presentation.shopItemDetail.ShopDetailsVmFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

//    val adminGateVm: AdminGateViewModel = viewModel(factory = AdminGateVmFactory(AppGraph.repo))
//    val isAdmin by adminGateVm.isAdmin.collectAsState()

    // --- UPDATED VIEWMODEL ---
    val context = LocalContext.current.applicationContext as Application
    val drawerVm: DrawerViewModel = viewModel(
        factory = DrawerVmFactory(context, AppGraph.repo)
    )

    val isAdmin by drawerVm.isAdmin.collectAsState()
    val notificationsEnabled by drawerVm.areNotificationsEnabled.collectAsState()
    // -------------------------

    val fontScale by drawerVm.fontScale.collectAsState()
    val currentDensity = LocalDensity.current

    CompositionLocalProvider(
        LocalDensity provides Density(currentDensity.density, fontScale = fontScale)
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                DrawerContent(
                    isAdmin = isAdmin,
                    notificationsEnabled = notificationsEnabled,
                    fontScale = fontScale,
                    onToggleNotifications = { enabled ->
                        drawerVm.toggleNotifications(enabled)
                    },
                    onChangeFontScale = { increment ->
                        drawerVm.changeFontScale(increment)
                    },
                    onOpenAdmin = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Admin.route)
                    },
                    onOpenArchive = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Archive.route)
                    }
                )
            }
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text(stringResource(R.string.app_name)) },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = stringResource(R.string.menu_title)
                                )
                            }
                        },
                        actions = {
//                        IconButton(onClick = { AppGraph.repo.signOut() }) {
//                            Icon(Icons.Default.Person, contentDescription = "Sign out")
//                        }
                            IconButton(onClick = {
                                navController.navigate(Screen.Profile.route)
                            }) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = stringResource(R.string.profile)
                                )
                            }
                        }
                    )
                },
                floatingActionButton = {
                    if (currentRoute == Screen.Home.route || currentRoute == Screen.Shop.route) {
                        FloatingActionButton(
                            onClick = {
                                val isSignedIn = AppGraph.repo.currentUser != null
                                if (isSignedIn) navController.navigate(Screen.CreatePost.route)
                                else navController.navigate(Screen.Auth.route)
                            },
                            containerColor = Color(0xFFC62828),
                            contentColor = Color.White
                        )
                        { Icon(Icons.Default.Add, null) }
                    }
                },
                bottomBar = {
                    BottomBar(
                        currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route,
                        onSelect = { route ->
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
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
                        val vm: ShopViewModel = viewModel(factory = ShopVmFactory(AppGraph.repo))
                        ShopScreen(
                            vm = vm,
                            onOpenDetails = { id -> navController.navigate("shop_details/$id") },
                            onContactSeller = { sellerId ->
                                navController.navigate("chat_detail/$sellerId")
                            }
                        )
                    }
                    composable(Screen.ShopDetails.pattern) { entry ->
                        val postId = entry.arguments?.getString("postId")!!
                        val vm: ShopDetailsViewModel = viewModel(
                            factory = ShopDetailsVmFactory(AppGraph.repo, postId)
                        )
                        ShopDetailsScreen(
                            vm = vm,
                            onContactSeller = { sellerId ->
                                navController.navigate("chat_detail/$sellerId")
                            },
                            onBack = {
                                navController.popBackStack()
                            } // <--- Handle deletion exit
                        )
                    }
                    composable(Screen.ChatDetail.pattern) { entry ->
                        val sellerId = entry.arguments?.getString("sellerId") ?: ""

                        // We hide the bottom bar for chat usually, but for PoC simple is fine.
                        // The ChatDetailScreen handles its own UI.
                        ChatDetailScreen(
                            sellerId = sellerId,
                            onBack = { navController.popBackStack() }
                        )
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
                            val vm: CreatePostViewModel =
                                viewModel(factory = CreatePostVmFactory(AppGraph.repo))
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
                    composable(Screen.Archive.route) {
                        val vm: com.donchik.akadeska.presentation.archive.ArchiveViewModel =
                            viewModel(
                                factory = com.donchik.akadeska.presentation.archive.ArchiveVmFactory(
                                    AppGraph.repo
                                )
                            )
                        com.donchik.akadeska.presentation.archive.ArchiveScreen(
                            vm = vm,
                            onOpenDetails = { id -> navController.navigate("details/$id") }
                        )
                    }
                    composable(Screen.Profile.route) {
                        // If user is not signed in, redirect immediately
                        if (AppGraph.repo.currentUser == null) {
                            LaunchedEffect(Unit) {
                                navController.navigate(Screen.Auth.route) {
                                    popUpTo(Screen.Home.route) { inclusive = false }
                                }
                            }
                        } else {
                            val vm: com.donchik.akadeska.presentation.profile.ProfileViewModel =
                                viewModel(
                                    factory = com.donchik.akadeska.presentation.profile.ProfileVmFactory(
                                        AppGraph.repo
                                    )
                                )
                            com.donchik.akadeska.presentation.profile.ProfileScreen(
                                vm = vm,
                                onNavigateToAuth = {
                                    // Navigate to Auth and clear backstack so they can't go back
                                    navController.navigate(Screen.Auth.route) {
                                        popUpTo(0)
                                    }
                                }
                            )
                        }
                    }

                }
            }
        }
    }
}

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
        item(Screen.Home.route, Icons.Outlined.Home, stringResource(R.string.nav_home))
        item(Screen.Shop.route, Icons.Outlined.ShoppingCart, stringResource(R.string.nav_shop))
        item(
            Screen.Chat.route,
            Icons.AutoMirrored.Outlined.Chat,
            stringResource(R.string.nav_chat)
        )
    }
}
