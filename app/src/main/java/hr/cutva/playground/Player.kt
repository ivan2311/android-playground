package hr.cutva.playground

data class Player(
    val id: String = "",
    val name: String,
    val modifiedDate: Long = 0,
    val deleted: Boolean = false
)