package ui

data class CommunityPost(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)