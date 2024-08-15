package com.lyn.newknowledge.example

/**
 * 自定义扩展。通过扩展去自定义一下配置，然后在task中获取到这些配置，应用这些配置
 *
 * 注册扩展：
 * project.extensions.create(CustomExtension.EXTENSION_NAME, CustomExtension::class.java)
 *
 * 获取扩展：
 * project.extensions.findByType(CustomExtension::class.java)
 *
 * 在脚本中通过以下方式配置扩展：
 * 方式一：通过类型配置。CustomExtension为扩展类型
 * configure<CustomExtension>{
 *     message = "配置的Message 1"
 * }
 * 方式二：通过扩展名称配置。customExtension为注册扩展时的名称
 * customExtension{
 *     message = "配置的Message 2"
 * }
 *
 * @author lin.wang
 * @since  2024/8/6
 */
open class CustomExtension {
    companion object{
        //扩展名称
        const val EXTENSION_NAME = "customExtension"
    }

    var code: String = "0"
    var message: String = "Default message"
}