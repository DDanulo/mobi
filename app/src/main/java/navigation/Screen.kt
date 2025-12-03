package navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Shop : Screen("shop")
    data object Chat : Screen("chat")
    data object CreatePost : Screen("createPost")
    data object Auth : Screen("auth")
    data object Admin : Screen("admin")
    data object Archive : Screen("archive")
    data object Profile : Screen("profile")
    data class ChatDetail(val sellerId: String) : Screen("chat_detail/$sellerId") {
        companion object { const val pattern = "chat_detail/{sellerId}" }
    }
    data class Details(val postId: String) : Screen("details/$postId") {
        companion object { const val pattern = "details/{postId}" }
    }
}