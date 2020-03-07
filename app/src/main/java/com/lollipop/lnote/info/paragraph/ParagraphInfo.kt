package com.lollipop.lnote.info.paragraph

/**
 * @author lollipop
 * @date 2020/3/7 23:32
 * 段落信息
 */
interface ParagraphInfo {

    /**
     * 序列化
     */
    fun serialization(): String

    /**
     * 解析
     */
    fun parse(value: String)

    /**
     * 摘要信息（用于预览
     */
    fun summary(): String

}