// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // 在Gradle 6.4及以后就不用再添加gradleApi()来配置Plugin的依赖啥的了，直接一个java-gradle-plugin插件搞定，
    // 它会自动把java、gradleApi()依赖添加到项目中。
    // 并且不需要像以前在src/main/resources/META-INF/gradle-plugins/xxx.properties中来配置你的implementation-class了，
    // 直接一个gradlePlugin{ }配置搞定，Gradle会自动生成META-INF描述文件
    id("java-gradle-plugin") //使用`kotlin-dsl`也可以
//    `kotlin-dsl`
    id( "maven-publish")
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.google.code.gson:gson:2.8.5")
}


//gradle插件配置
gradlePlugin{
    plugins{
        // 注册插件，插件名随便填
        register("CustomPlugin"){
            id = "custom-plugin" // 插件id，脚本引用插件的时候会用到
            implementationClass = "com.lyn.plugin.studyextension.CustomPlugin"
        }

        // 注册插件，插件名随便填
        register("SourceOrAARPlugin"){
            id = "com.lyn.plugin.SourceOrAARPlugin"
            implementationClass = "com.lyn.plugin.sourceaar.SourceOrAARPlugin"
        }
    }

}

publishing{
    publications{
        register<MavenPublication>("maven"){
            groupId = "com.lyn.plugin"
            artifactId = "SourceOrAARPlugin"
            version = "1.0.1-SNAPSHOT"
            from(components["java"])
        }
    }

    repositories {
        maven {
            //仓库名，随便填。最好跟远程仓库名保持一致，例如这里是将制品发布到maven-module仓库
            name = "gradle-plugin"
            //发布到本地仓库
//            url = uri("file://${project.rootDir}/maven")
            //发布到远程仓库
            url = uri("http://localhost:8081/repository/gradle-plugin/")
            //允许不安全的http协议
            isAllowInsecureProtocol = true
            credentials {
                username = "admin"
                password = "admin123"
            }
        }
    }
}
