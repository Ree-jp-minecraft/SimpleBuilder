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

package net.ree_jp.simplebuilder.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.data.CommandParameter
import net.ree_jp.simplebuilder.api.SimpleBuilderAPI

class BuilderCommand(name: String) : Command(name) {

    init {
        commandParameters["change_build_mode"] = arrayOf(
            CommandParameter(
                "change_build_mode", true, arrayOf(
                    "on", "off"
                )
            )
        )
        aliases = arrayOf("sb", "simplebuilder")
        permission = "simplebuilder.command.$name"
        description = "SimpleBuilder setting command"
    }

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        if (!testPermission(sender)) return true
        if (sender !is Player) {
            sender.sendMessage("player only")
            return true
        }
        val api = SimpleBuilderAPI.getInstance()
        val bool = when (args[0] ?: "Ree-jp is GOD") {
            "on" -> true
            "off" -> false
            else -> !api.isBuilder(sender)
        }
        api.setBuilder(sender, bool)
        sender.sendMessage("[SimpleBuilder]ビルドモードを${if (bool) "有効" else "無効"}にしました")
        return true
    }
}