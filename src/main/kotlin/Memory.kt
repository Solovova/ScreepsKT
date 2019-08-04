import org.w3c.dom.svg.SVGAnimatedLength
import screeps.api.BodyPartConstant
import screeps.api.CreepMemory
import screeps.api.RoomMemory

import screeps.utils.memory.memory

data class CarrierNeed (val wayLength: Int ,val needCarriers: Int, val needCapacity: Int, val timeForDeath: Int, val needBody: Array<BodyPartConstant>)

var CreepMemory.role: Int by memory {0}
var CreepMemory.slaveRoom: String by memory {""}
var CreepMemory.mainRoom: String by memory {""}

var RoomMemory.TowerLastTarget: String by memory {""}
var RoomMemory.CarrierNeeds0: CarrierNeed by memory { CarrierNeed(0,0,0, 0, arrayOf()) }
var RoomMemory.CarrierNeeds1: CarrierNeed by memory { CarrierNeed(0,0,0, 0, arrayOf()) }



