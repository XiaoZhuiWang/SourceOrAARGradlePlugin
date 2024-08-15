package com.lyn.plugin.studyextension

import com.lyn.newknowledge.example.CustomExtension
import com.lyn.newknowledge.example.CustomTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 自定义gradle插件
 *
 * @constructor Create empty Custom plugin
 */
class CustomPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        //注册扩展
        project.extensions.create(CustomExtension.EXTENSION_NAME, CustomExtension::class.java)

        //注册任务
        project.tasks.register("printCustomMessage", CustomTask::class.java) {
            it.doLast {
                println("Executing printCustomMessage task!")
            }
        }
    }
}