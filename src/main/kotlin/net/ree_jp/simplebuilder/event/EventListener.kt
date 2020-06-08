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
import cn.nukkit.item.Item
import cn.nukkit.level.Position
import cn.nukkit.level.particle.DustParticle
import cn.nukkit.math.BlockFace
import cn.nukkit.math.BlockVector3
import cn.nukkit.scheduler.TaskHandler
import cn.nukkit.utils.BlockColor
import net.ree_jp.simplebuilder.SimpleBuilderPlugin
import net.ree_jp.simplebuilder.api.SimpleBuilderAPI

class EventListener : Listener {

    private val cool = mutableMapOf<String, TaskHandler>()

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlace(ev: BlockPlaceEvent) {
        val p = ev.player
        val n = p.name
        val item = ev.item
        val bl = ev.block

        if (ev.isCancelled || cool.contains(n)) return

        val space = getBuildSpace(p, bl) ?: return

        if (item.count <= space.size) return

        val iterator = space.iterator()
        var against: Block = ev.blockAgainst
        val handler = Server.getInstance().scheduler.scheduleRepeatingTask(
            SimpleBuilderPlugin.instance,
            {
                if (iterator.hasNext() && (against.id != Block.AIR)) {
                    val pos = iterator.next()
                    against = placeBlock(p, item, pos, against) ?: Block.get(Block.AIR)
                } else {
                    cool[n]?.cancel()
                    cool.remove(n)
                }
            },
            3
        )
        cool[n] = handler
    }

    @EventHandler
    fun onTap(ev: PlayerInteractEvent) {
        val p = ev.player
        val bl = p.inventory.itemInHand.block
        val view = ev.block.getSide(ev.face)
        bl.setVec(view)

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

        (1..100).forEach { index ->
            val checkPos = nextPos(pos, index, p.direction)
            val checkBl = level.getBlock(checkPos.asVector3())
            if (!p.hasPermission("simplebuilder.build.$index")) return null
            if (!isCanPlace(checkBl, bl)) {
                if ((checkBl.fullId != bl.fullId) || list.isEmpty()) return null
                return list
            }
            list.add(checkPos)
        }
        throw Exception("limit is 100")
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

        if (!isCanPlace(beforeBl, bl)) {
            return null
        }

        val ev = BlockPlaceEvent(p, bl, beforeBl, tapBl, item)
        Server.getInstance().pluginManager.callEvent(ev)
        if (ev.isCancelled || !reduceHandItem(p)) return null

        level.setBlock(bl, bl)

        return bl
    }

    private fun nextPos(pos: BlockVector3, int: Int, face: BlockFace): BlockVector3 {
        return when (face) {

            BlockFace.NORTH -> pos.north(int)
            BlockFace.SOUTH -> pos.south(int)
            BlockFace.EAST -> pos.east(int)
            BlockFace.WEST -> pos.west(int)

            else -> throw Exception("direction bad value")
        }
    }

    private fun isCanPlace(before: Block, after: Block): Boolean {
        return before.canBeReplaced() || ((after.id == Item.SLAB) && (before.id == Item.SLAB))
    }

    private fun reduceHandItem(p: Player): Boolean {
        val hand = p.inventory.itemInHand
        if (hand.id == Item.AIR) return false

        hand.setCount(hand.count - 1)
        return p.inventory.setItemInHand(hand)
    }

    private fun Block.setVec(bl: Block) {
        x = bl.x
        y = bl.y
        z = bl.z
    }
}
