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

package net.ree_jp.simplebuilder.event

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.block.Block
import cn.nukkit.block.BlockAir
import cn.nukkit.event.EventHandler
import cn.nukkit.event.EventPriority
import cn.nukkit.event.Listener
import cn.nukkit.event.block.BlockPlaceEvent
import cn.nukkit.event.player.PlayerInteractEvent
import cn.nukkit.event.player.PlayerToggleSneakEvent
import cn.nukkit.item.Item
import cn.nukkit.level.Position
import cn.nukkit.level.particle.DustParticle
import cn.nukkit.math.BlockFace
import cn.nukkit.math.BlockVector3
import cn.nukkit.utils.BlockColor
import net.ree_jp.simplebuilder.api.SimpleBuilderAPI

class EventListener : Listener {

    private val cool = mutableListOf<String>()

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlace(ev: BlockPlaceEvent) {
        val p = ev.player
        val n = p.name
        val item = ev.item
        val bl = ev.block

        if (ev.isCancelled || cool.contains(n)) return

        val space = getBuildSpace(p, bl) ?: return

        if (item.count <= space.size) return

        var against = ev.blockAgainst
        cool.add(n)
        for (pos in space) {
            against = placeBlock(p, item, pos, against) ?: return
        }
        cool.remove(n)
    }

    @EventHandler
    fun onTap(ev: PlayerInteractEvent) {
        val p = ev.player
        val bl = p.inventory.itemInHand.block
        val view = ev.block.getSide(ev.face)
        bl.x = view.x
        bl.y = view.y
        bl.z = view.z
        val space = getBuildSpace(p, bl) ?: return

        for (pos in space) {
            sendBlockParticle(p, pos, bl.color)
        }
    }

    private fun getBuildSpace(p: Player, bl: Block): List<BlockVector3>? {
        val api = SimpleBuilderAPI.getInstance()
        val level = p.level
        val pos = bl.asBlockVector3()

        if (!p.hasPermission("simplebuilder.build") || (bl is BlockAir) || !api.isBuilder(p)) return null

        val list = mutableListOf<BlockVector3>()

        when (p.direction) {

            BlockFace.NORTH -> for (index in 1..100) {
                if (!p.hasPermission("simplebuilder.build.$index")) return null
                val checkPos = pos.north(index)
                val checkBl = level.getBlock(checkPos.asVector3())
                if (checkBl.id != Block.AIR) {
                    if (checkBl.fullId != bl.fullId) return null
                    break
                }
                list.add(checkPos)
            }

            BlockFace.SOUTH -> for (index in 1..100) {
                if (!p.hasPermission("simplebuilder.build.$index")) return null
                val checkPos = pos.south(index)
                val checkBl = level.getBlock(checkPos.asVector3())
                if (checkBl.id != Block.AIR) {
                    if (checkBl.fullId != bl.fullId) return null
                    break
                }
                list.add(checkPos)
            }

            BlockFace.EAST -> for (index in 1..100) {
                if (!p.hasPermission("simplebuilder.build.$index")) return null
                val checkPos = pos.east(index)
                val checkBl = level.getBlock(checkPos.asVector3())
                if (checkBl.id != Block.AIR) {
                    if (checkBl.fullId != bl.fullId) return null
                    break
                }
                list.add(checkPos)
            }

            BlockFace.WEST -> for (index in 1..100) {
                if (!p.hasPermission("simplebuilder.build.$index")) return null
                val checkPos = pos.west(index)
                val checkBl = level.getBlock(checkPos.asVector3())
                if (checkBl.id != Block.AIR) {
                    if (checkBl.fullId != bl.fullId) return null
                    break
                }
                list.add(checkPos)
            }

            else -> throw Exception("direction bad value")
        }

        if (list.isEmpty()) return null
        return list
    }

    private fun sendBlockParticle(p: Player, pos: BlockVector3, color: BlockColor) {
        val vec = pos.asVector3()
        (0..1).forEach { x ->
            (0..1).forEach { y ->
                (0..1).forEach { z ->
                    p.level.addParticle(DustParticle(vec.add(x.toDouble(), y.toDouble(), z.toDouble()), color), p)
                }
            }
        }
    }

    private fun placeBlock(p: Player, item: Item, pos: BlockVector3, tapBl: Block): Block? {
        if (!item.canBePlaced()) return null

        val level = p.level
        val bl = item.block
        val beforeBl = level.getBlock(pos.asVector3())

        bl.position(Position.fromObject(pos.asVector3(), level))

        if ((beforeBl.id != Block.AIR) && !(beforeBl.canBeReplaced() || (bl.id == Item.SLAB && beforeBl.id == Item.SLAB))) {
            return null
        }

        val ev = BlockPlaceEvent(p, bl, beforeBl, tapBl, item)
        Server.getInstance().pluginManager.callEvent(ev)
        if (ev.isCancelled || !reduceHandItem(p)) return null

        level.setBlock(bl, bl)

        return bl
    }

    private fun reduceHandItem(p: Player): Boolean {
        val hand = p.inventory.itemInHand
        if (hand.id == Item.AIR) return false

        hand.setCount(hand.count--)
        return p.inventory.setItemInHand(hand)
    }
}
