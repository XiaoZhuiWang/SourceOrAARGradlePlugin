package com.lyn.plugin.sourceaar.bean

/**
 * 模块依赖信息
 * @author lin.wang
 * @since  2024/7/26
 */
data class ModuleDependenciesBean(
    val allSource: Boolean, //是否全部源码依赖，为true时，则忽略sourceModule
    val sourceModule: List<String>, //源码依赖的模块
    val module: MutableList<String>,//项目的所有模块
)