package com.example.Projecte3MushTool

import java.io.Serializable

data class Post(
    var key: String,
    var uid: String,
    var imgPath: String,
    var comentario: String,
    var setaPost: String,
    var location: String,
    var userShare : String
): Serializable
{

}

