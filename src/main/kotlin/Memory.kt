import screeps.api.CreepMemory
import screeps.api.RoomMemory

import screeps.utils.memory.memory


var CreepMemory.role: Int by memory {0}
var CreepMemory.srcRoom: String by memory {""}
var CreepMemory.dstRoom: String by memory {""}

var RoomMemory.TowerLastTarget: String by memory {""}


