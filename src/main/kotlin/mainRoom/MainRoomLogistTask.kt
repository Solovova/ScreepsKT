package mainRoom

import screeps.utils.toMap
import CreepTask
import mainContext.messenger
import screeps.api.*
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
    }

    // 05 Drop in future //ToDo
    val haveInTerminal05: Int = this.getResourceInTerminal() - this.constant.energyMaxTerminal
    val haveInStore05: Int = this.getResourceInStorage() - this.constant.energyMaxStorage
    if (haveInTerminal05>0 && haveInStore05 > 0)
        parent.parent.messenger("INFO",this.name,"Too many energy", COLOR_RED)


    //Mineral work
    //Storage -> Terminal this.constant.mineralMinTerminal but < this.constant.mineralAllMaxTerminal

    for (resInStorage in storage.store) {
        if (resInStorage.component1() == RESOURCE_ENERGY) continue
        val resourceStorage: ResourceConstant = resInStorage.component1()
        val quantityStorage: Int = resInStorage.component2()
        val quantityTerminal: Int = this.getResourceInTerminal(resourceStorage)
        val needInTerminal = this.constant.mineralMinTerminal - quantityTerminal
        val canMineralAllTerminal = this.constant.mineralAllMaxTerminal - (terminal.store.toMap().map { it.value }.sum()
        -this.getResourceInTerminal(RESOURCE_ENERGY))
        if (canMineralAllTerminal <= 0) parent.parent.messenger("INFO",this.name,"Terminal mineral is full", COLOR_RED)
        carry = min(min(min(needInTerminal, quantityStorage), creep.carryCapacity), canMineralAllTerminal)
        if (carry > 0) {
            parent.parent.tasks.add(creep.id, CreepTask(TypeOfTask.Transport, storage.id, storage.pos, terminal.id, terminal.pos, resourceStorage, carry))
            return
        }
    }

    //Terminal -> Storage all mineral > this.constant.mineralMinTerminal
    for (resInTerminal in terminal.store) {
        if (resInTerminal.component1() == RESOURCE_ENERGY) continue
        val resourceTerminal: ResourceConstant = resInTerminal.component1()
        val quantityTerminal: Int = resInTerminal.component2()

        val haveInTerminal = quantityTerminal - this.constant.mineralMinTerminal
        carry = min(haveInTerminal, creep.carryCapacity)
        if (carry > 0) {
            parent.parent.tasks.add(creep.id, CreepTask(TypeOfTask.Transport, terminal.id, terminal.pos, storage.id, storage.pos, resourceTerminal, carry))
            return
        }
    }
}


