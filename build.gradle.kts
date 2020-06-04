/*
 * RRRRRR                         jjj
 * RR   RR   eee    eee               pp pp
 * RRRRRR  ee   e ee   e _____    jjj ppp  pp
 * RR  RR  eeeee  eeeee           jjj pppppp
 * RR   RR  eeeee  eeeee          jjj pp
 *                              jjjj  pp
 *
 * Copyright (c) 2020. Ree-jp.  All Rights Reserved.
 */

plugins {
    java
    maven
    kotlin("jvm") version "1.3.71"
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("net.minecrell.plugin-yml.nukkit") version "0.3.0"
}

group = "net.ree-jp"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
    maven(
        url = uri("https://repo.nukkitx.com/main/")
    )
}

dependencies {
    compileOnly("cn.nukkit:nukkit:1.0-SNAPSHOT")
    testCompileOnly("cn.nukkit:nukkit:1.0-SNAPSHOT")
    implementation(kotlin("stdlib-jdk8"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

nukkit {
    name = "SimpleBuilder"
    main = "net.ree_jp.builder.SimpleBuilderPlugin"
    api = listOf("1.0.0")
    author = "Ree-jp"
    description = "SimpleBuilderServerPlugin"
    website = "https://github.com/ReefNetwork/SimpleBuilder"
    version = "1.0.0"

    permissions.register("simplebuilder.*")

    permissions {
        "simplebuilder.*" {
            description = "SimpleBuilder permission"
            default = net.minecrell.pluginyml.nukkit.NukkitPluginDescription.Permission.Default.FALSE
            children {
                "simplebuilder.command.*" {
                    description = "SimpleBuilder command permission"
                    default = net.minecrell.pluginyml.nukkit.NukkitPluginDescription.Permission.Default.TRUE
                }
                "simplebuilder.build.*" {
                    description = "SimpleBuilder build permission"
                    default = net.minecrell.pluginyml.nukkit.NukkitPluginDescription.Permission.Default.TRUE
                    children {
                        "simplebuilder.build.16" {
                            description = "SimpleBuilder build limit permission"
                            default = net.minecrell.pluginyml.nukkit.NukkitPluginDescription.Permission.Default.FALSE
                        }
                    }
                }
            }
        }
    }
}