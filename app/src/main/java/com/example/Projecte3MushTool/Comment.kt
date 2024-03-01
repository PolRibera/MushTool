package com.example.Projecte3MushTool

data class Comment(val id: String?, val userId: String, val text: String) {
    constructor() : this("", "", "")
}