package com.example.farmfresh

import android.media.Image
import kotlin.properties.Delegates

public class Model(image: Int, title: String, desc: String) {
    private var image = image
    private var title = title
    private var desc = desc

    public fun getImage(): Int {
        return this.image
    }

    public fun setImage(image: Int){
        this.image = image
    }

    public fun getTitle(): String {
        return this.title
    }

    public fun setTitle(title: String){
        this.title = title
    }

    public fun getDesc(): String {
        return this.desc
    }

    public fun setDesc(desc: String){
        this.desc = desc
    }

}