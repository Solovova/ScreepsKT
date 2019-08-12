package mainRoom

import screeps.api.Creep
import screeps.utils.toMap
import CreepTask
import screeps.api.COLOR_RED
import screeps.api.RESOURCE_ENERGY
import kotlin.math.min

fun MainRoom.setLogistTask(creep: Creep) {
    var isTask = false

    //00 Link near Storage ->
    val link = this.structureLinkNearStorage[0]
    val storage = this.structureStorage[0]
    if (link != null && link.energy != 0 && storage!=null) {
        parent.parent.tasks.add(creep.id, CreepTask(TypeOfTask.Transport, link.id, link.pos, storage.id, storage.pos, RESOURCE_ENERGY, 0))
        isTask = true
    }

    if (isTask) return

    //Ситуации
    // 01 сначала заполняем теминал до 10000 но поддерживаем в склад не меньше this.constant.energyMinStorageEmergency
    //
    // отправка енергии
    // 02 на складе поддерживаем  this.constant.energyMinStorage остальное в терминал пока не будет this.constant.energyMaxTerminal

    // стандарный режим
    // 03 на складе поддерживаем  this.constant.energyMaxStorage остальное в терминал пока не будет this.constant.energyMaxTerminal
    // если возник и this.constant.energyMaxTerminal и this.constant.energyMaxStorageEmergency то предупреждение




    val terminal = this.structureTerminal[0]
    if (terminal == null || storage == null) return




    // Terminal > 0 && Storage < this.constant.energyMinStorageEmergency
    val haveInStorage01: Int = this.getResourceInStorage() - this.constant.energyMinStorage
    if (haveInStorage01 < 0 && this.getResourceInTerminal()>0) {
        val carry = min(min(-haveInStorage01, creep.carryCapacity),this.getResourceInTerminal())
        if (carry > 0) {
            parent.parent.tasks.add(creep.id, CreepTask(TypeOfTask.Transport, terminal.id, terminal.pos, storage.id, storage.pos, RESOURCE_ENERGY, carry))
            isTask = true
        }
    }
    if (isTask) return

    //Storage > this.constant.energyMaxStorageEmergency -> Terminal < this.constant.energyMaxTerminal
    val needInTerminal02: Int = this.constant.energyMaxTerminal - this.getResourceInTerminal()
    val haveInStorage02: Int = this.getResourceInStorage() - this.constant.energyMaxStorage

    if (haveInStorage02 > 0 && needInTerminal02 > 0 && (haveInStorage02 > creep.carryCapacity || needInTerminal02 < creep.carryCapacity) ) {
        val carry = min(min(haveInStorage02, creep.carryCapacity),needInTerminal02)
        if (carry > 0) {
            parent.parent.tasks.add(creep.id, CreepTask(TypeOfTask.Transport, storage.id, storage.pos, terminal.id, terminal.pos, RESOURCE_ENERGY, carry))
            isTask = true
        }
    }else{
        if (haveInStorage02 > 0){
            //ToDo in future drop energy
            parent.parent.messenger("ERROR",this.name, "too many energy in room", COLOR_RED)
            return
        }
    }
    if (isTask) return


    //Storage > this.constant.energyMinStorageEmergency -> Terminal < this.constant.energyMinTerminal or this.constant.energyMaxTerminal if sent
    val needInTerminal01: Int = if (this.constant.sentEnergyToRoom == "") this.constant.energyMinTerminal - this.getResourceInTerminal()
    else this.constant.energyMaxTerminal - this.getResourceInTerminal()

    if (haveInStorage01 > 0 && needInTerminal01 > 0 && (haveInStorage01 > creep.carryCapacity || needInTerminal01 < creep.carryCapacity) ) {
        val carry = min(min(haveInStorage01, creep.carryCapacity),needInTerminal01)
        if (carry > 0) {
            parent.parent.tasks.add(creep.id, CreepTask(TypeOfTask.Transport, storage.id, storage.pos, terminal.id, terminal.pos, RESOURCE_ENERGY, carry))
            isTask = true
        }
    }
    if (isTask) return





    //Terminal > this.constant.energyMinTerminal or this.constant.energyMaxTerminal if sent && Storage < this.constant.energyMaxStorageEmergency
    val haveInTerminal03: Int = if (this.constant.sentEnergyToRoom == "")  this.getResourceInTerminal() - this.constant.energyMinTerminal
    else  this.getResourceInTerminal() - this.constant.energyMaxTerminal
    val needInStorage03: Int = this.constant.energyMaxStorage - this.getResourceInStorage()

    if (haveInTerminal03 > 0 && needInStorage03 > 0) {
        val carry = min(min(haveInTerminal03, creep.carryCapacity),needInStorage03)
        if (carry > 0) {
            parent.parent.tasks.add(creep.id, CreepTask(TypeOfTask.Transport, terminal.id, terminal.pos, storage.id, storage.pos, RESOURCE_ENERGY, carry))
            isTask = true
        }
    }else{
        if (haveInTerminal03 > 0){
            //ToDo in future drop energy
            parent.parent.messenger("ERROR",this.name, "too many energy in room terminal", COLOR_RED)
            return
        }
    }
    if (isTask) return


}

