package com.github.mutoxu_n.idsreversiapp

data class ReversiConfig(
    val name: String,
    val height: Int,
    val width: Int,
    val board: List<Int>)