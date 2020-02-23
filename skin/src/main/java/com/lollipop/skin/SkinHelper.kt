package com.lollipop.skin

/**
 * @author lollipop
 * @date 2020-02-23 22:12
 * 皮肤管理辅助器
 */
class SkinHelper<T: SkinInfo> {

    /**
     * 呈现器的集合
     */
    private val providerList = ArrayList<SkinProvider<T>>()

    /**
     * 注册一个呈现器
     */
    fun registered(provider: SkinProvider<T>) {
        providerList.add(provider)
    }

    /**
     * 移除一个呈现器
     */
    fun unregistered(provider: SkinProvider<T>) {
        providerList.remove(provider)
    }

    /**
     * 更新皮肤
     */
    fun updateSkin(info: T) {
        providerList.forEach {
            it.onSkinUpdate(info)
        }
    }

    /**
     * 创建一个模板组
     */
    fun <T: SkinInfo, V> createGroup(run: (V, T) -> Unit) = ComponentGroup(run)

    /**
     * 模板组，批量调整一组View
     */
    class ComponentGroup<T: SkinInfo, V>(
        private val run: (V, T) -> Unit): SkinProvider<T> {

        private val componentList = ArrayList<V>()

        override fun onSkinUpdate(info: T) {
            componentList.forEach {
                run(it, info)
            }
        }

        fun add(vararg v: V) {
            componentList.addAll(v)
        }

    }

}
