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

package net.ree_jp.simplebuilder

import cn.nukkit.plugin.PluginBase
import net.ree_jp.simplebuilder.command.BuilderCommand
import net.ree_jp.simplebuilder.event.EventListener

class SimpleBuilderPlugin : PluginBase() {

    companion object {
        lateinit var instance: SimpleBuilderPlugin
    }

    override fun onLoad() {
        instance = this
        super.onLoad()
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(EventListener(), this)
        server.commandMap.register("simplebuilder",
            BuilderCommand("builder")
        )
        super.onEnable()
    }
}