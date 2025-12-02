package navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Shop : Screen("shop")
    data object Chat : Screen("chat")
    data object CreatePost : Screen("createPost")
    data object Auth : Screen("auth")
    data object Admin : Screen("admin")
    data class Details(val postId: String) : Screen("details/$postId") {
        companion object { const val pattern = "details/{postId}" }
    }
}