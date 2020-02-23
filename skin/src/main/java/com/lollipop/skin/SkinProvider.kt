package com.lollipop.skin

/**
 * @author lollipop
 * @date 2020-02-23 22:13
 * 皮肤展示器的接口
 */
interface SkinProvider<T: SkinInfo> {

    fun onSkinUpdate(info: T)

}