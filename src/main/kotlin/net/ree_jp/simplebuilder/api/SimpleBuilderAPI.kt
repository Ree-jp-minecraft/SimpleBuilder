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

package net.ree_jp.simplebuilder.api

import cn.nukkit.Player

class SimpleBuilderAPI : ISimpleBuilderAPI {

    companion object{
        const val IS_BUILD_MODE = "SimpleBuilder_is_build_mode"

        private lateinit var instance: SimpleBuilderAPI

        fun getInstance(): SimpleBuilderAPI {
            if (!::instance.isInitialized) {
                instance = SimpleBuilderAPI()
            }
            return instance
        }
    }

    override fun isBuilder(player: Player): Boolean {
        val nbt = player.namedTag
        return nbt.getBoolean(IS_BUILD_MODE)
    }

    override fun setBuilder(player: Player, bool: Boolean) {
        val nbt = player.namedTag
        nbt.putBoolean(IS_BUILD_MODE,bool)
        player.namedTag = nbt
    }
}