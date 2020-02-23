package com.lollipop.skin

/**
 * @author lollipop
 * @date 2020-02-23 22:15
 * 皮肤的信息，用于展示器使用它进行展示
 */
interface SkinInfo {

    fun serialization(): String
    fun parse(value: String)

}