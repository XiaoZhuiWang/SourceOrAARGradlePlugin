package com.lyn.newknowledge.example

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * 自定义task
 * @author lin.wang
 * @since  2024/8/6
 */
open class CustomTask : DefaultTask() {
    init {
        //设置任务的分组，便于在gradle task窗口列表中查看
        group = "custom"
        description = "A custom task"
    }

    /**
     * TaskAction将方法标记为执行任务时要运行的操作
     */
    @TaskAction
    fun printMessage(){
        //获取自定义扩展对象
        val customExtension = project.extensions.findByType(CustomExtension::class.java)
        //使用扩展
        println("customExtension = ${customExtension?.code} : ${customExtension?.message}")
    }
}