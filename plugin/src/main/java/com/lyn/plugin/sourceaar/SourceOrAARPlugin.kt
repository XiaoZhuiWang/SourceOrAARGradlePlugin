package com.lyn.plugin.sourceaar

import com.google.gson.Gson
import com.lyn.plugin.sourceaar.bean.ModuleDependenciesBean
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencySubstitutions
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.initialization.Settings
import java.io.File


/**
 * module源码依赖和jar包依赖切换，提升项目编译速度
 * @author lin.wang
 * @since  2024/7/25
 */
class SourceOrAARPlugin : Plugin<Any> {

    // GAV中的group
    private val group = "com.lyn.temp"

    private val fileName = "module-dependencies.json"

    //模块版本号与模块映射关系。key：模块；value：版本号
    private val versionMap = mutableMapOf<String, String?>()

    override fun apply(settings: Any) {
        if(settings !is Settings) return

        println("SourceOrAARPlugin: 开始")

        //settings对象配置完后，获取settings对象。该回调必须在Settings.gradle中设置，在build.gradle中设置不会触发回调
        settings.gradle.settingsEvaluated { settings ->
            //加载本地源码or jar包依赖配置文件
            val file = File(settings.rootDir, fileName)
            if (!file.exists()) {
                println("SourceOrAARPlugin: ${fileName}文件不存在")
                return@settingsEvaluated
            }

            val moduleDependenciesBean =
                Gson().fromJson(file.readText(), ModuleDependenciesBean::class.java)

            println("SourceOrAARPlugin: 模块数量：${moduleDependenciesBean.module.size}")
            //导入模块
            moduleDependenciesBean.module.forEach {
                includeModule(it, settings)
            }

            //project对象配置完成后，对模块进行依赖替换
            settings.gradle.projectsEvaluated {
                it.allprojects { project ->
                    project.configurations.all { configuration ->

                        configuration.allDependencies.forEach { dependency ->
                            // 对于group为com.lyn.temp的jar包依赖，保存jar包对应的的版本号。用于后面jar包和源码之间相互切换，因此在
                            // 项目中要统一声明jar包依赖去依赖模块
                            // 在Gradle中，ExternalModuleDependency是Dependency接口的一个实现， 用于表示对一个外部模块的依赖关系。
                            // 这个类负责处理对Maven、JCenter、Google Maven Repository等远程仓库中模块的依赖
                            if (dependency is ExternalModuleDependency && dependency.group == group) {
//                                println("SourceOrAARPlugin: jar包依赖：${dependency.name}：${dependency.version}")
                                versionMap[dependency.name] = dependency.version
                            }
                        }

                        configuration.resolutionStrategy.dependencySubstitution { substitution ->
                            if (moduleDependenciesBean.allSource) { //所有模块都使用源码依赖
                                moduleDependenciesBean.module.forEach { moduleName ->
                                    substitute(substitution, moduleName, true)
                                }
                            } else {
                                //源码依赖的module
                                moduleDependenciesBean.sourceModule.forEach { moduleName ->
                                    substitute(substitution, moduleName, true)
                                }
                                //除源码依赖的module后，剩下的都jar包依赖
                                moduleDependenciesBean.module.removeAll(moduleDependenciesBean.sourceModule)
                                moduleDependenciesBean.module.forEach { moduleName ->
                                    substitute(substitution, moduleName, false)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 在源码依赖和jar包依赖间切换
     *
     * @param substitution
     * @param moduleName
     * @param sourceDependency 是否是源码依赖
     */
    private fun substitute(
        substitution: DependencySubstitutions,
        moduleName: String,
        sourceDependency: Boolean
    ) {
        //jar包依赖的坐标
        val gav = substitution.module("$group:$moduleName:${versionMap[moduleName]}")
        //源码依赖的path
        val path = substitution.project(":$moduleName")
        if (sourceDependency) {//将jar包依赖替换为源码依赖
//            println("SourceOrAARPlugin: 源码依赖:$path")
            substitution.substitute(gav).using(path)
        } else {//将源码依赖替换为jar包依赖
//            println("SourceOrAARPlugin: jar包依赖:$gav")
            substitution.substitute(path).using(gav)
        }

    }

    /**
     * 导入模块
     *
     * @param it
     * @param settings
     */
    private fun includeModule(it: String, settings: Settings) {
        println("SourceOrAARPlugin: include:$it")
        if (it.contains("/")) {
            //说明模块的位置不是在项目的根目录下，而在其他的外部位置
            settings.project(":$it").projectDir = File(it)
        } else {
            //在项目根目录下
            settings.include(it)
        }
    }

    private fun registerIncreaseVersionTask(project: Project) {
        project.tasks.register("increaseVersion") {
        }
    }
}