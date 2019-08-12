package mainRoom

import screeps.api.Creep
import screeps.utils.toMap
import CreepTask
import screeps.api.COLOR_RED
import screeps.api.RESOURCE_ENERGY
import kotlin.math.min

fun MainRoom.setLogistTask(creep: Creep) {
    //00 Link near Storage ->
    val link = this.structureLinkNearStorage[0]
    val storage = this.structureStorage[0]
    if (link != null && link.energy != 0 && storage != null) {
        parent.parent.tasks.add(creep.id, CreepTask(TypeOfTask.Transport, link.id, link.pos, storage.id, storage.pos, RESOURCE_ENERGY, 0))
        return
    }

    val terminal = this.structureTerminal[0]
    if (terminal == null || storage == null) return
    var carry: Int

    // 01 Terminal > 0 && Storage < this.constant.energyMinStorage
    val needInStorage01: Int = this.constant.energyMinStorage - this.getResourceInStorage()
    val haveInTerminal01: Int = this.getResourceInTerminal()
    carry = min(min(needInStorage01, haveInTerminal01), creep.carryCapacity)
    if (carry > 0) {
        parent.parent.tasks.add(creep.id, CreepTask(TypeOfTask.Transport, terminal.id, terminal.pos, storage.id, storage.pos, RESOURCE_ENERGY, carry))
        return
    }

    // 02 Storage > this.constant.energyMaxStorage -> Terminal < this.constant.energyMaxTerminal
    val needInTerminal02: Int = this.constant.energyMaxTerminal - this.getResourceInTerminal()
    val haveInStorage02: Int = this.getResourceInStorage() - this.constant.energyMaxStorage
    carry = min(min(haveInStorage02, needInTerminal02),creep.carryCapacity)

    if (carry > 0 && (carry == creep.carryCapacity || carry == needInTerminal02)) {
        parent.parent.tasks.add(creep.id, CreepTask(TypeOfTask.Transport, storage.id, storage.pos, terminal.id, terminal.pos, RESOURCE_ENERGY, carry))
        return
    }else{
        if (haveInStorage02 > 0){
            //ToDo in future drop energy
            parent.parent.messenger("ERROR",this.name, "Too many energy in room", COLOR_RED)
            return
        }
    }

    // 03 Storage > this.constant.energyMinStorage -> Terminal < this.constant.energyMinTerminal or this.constant.energyMaxTerminal if sent
    val needInTerminal03: Int = if (this.constant.sentEnergyToRoom == "") this.constant.energyMinTerminal - this.getResourceInTerminal()
    else this.constant.energyMaxTerminal - this.getResourceInTerminal()
    val haveInStorage03: Int = this.getResourceInStorage() - this.constant.energyMinStorage
    carry = min(min(needInTerminal03, haveInStorage03),creep.carryCapacity)

    if (carry > 0 && (carry == creep.carryCapacity || carry == needInTerminal03)) {
        parent.parent.tasks.add(creep.id, CreepTask(TypeOfTask.Transport, storage.id, storage.pos, terminal.id, terminal.pos, RESOURCE_ENERGY, carry))
        return
    }

    // 04 Terminal > this.constant.energyMinTerminal or this.constant.energyMaxTerminal if sent && Storage < this.constant.energyMaxStorageEmergency
    val haveInTerminal04: Int = if (this.constant.sentEnergyToRoom == "") this.getResourceInTerminal() - this.constant.energyMinTerminal
    else this.getResourceInTerminal() - this.constant.energyMaxTerminal
    val needInStorage04: Int = this.constant.energyMaxStorage - this.getResourceInStorage()
    carry = min(min(haveInTerminal04, needInStorage04), creep.carryCapacity)

    if (carry > 0) {
        parent.parent.tasks.add(creep.id, CreepTask(TypeOfTask.Transport, terminal.id, terminal.pos, storage.id, storage.pos, RESOURCE_ENERGY, carry))
        return
    } else {
        if (haveInTerminal04 > 0) {
            //ToDo in future drop energy
            parent.parent.messenger("ERROR", this.name, "Too many energy in room terminal", COLOR_RED)
            return
        }
    }


}

