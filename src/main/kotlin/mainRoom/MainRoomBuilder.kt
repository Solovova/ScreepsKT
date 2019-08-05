package mainRoom

import screeps.api.*
import screeps.api.structures.StructureController
import slaveRoom.building

fun MainRoom.buildStructure(fPrimeColor: ColorConstant, fSecondaryColor: ColorConstant, fWhatBuild: BuildableStructureConstant, fCount: Int): Boolean {
    //return 1 если чтото нашли и строим 0 если ничего не нашли
    var findBuild = false
    var fCount = fCount
    val flags = this.room.find(FIND_FLAGS).filter { it.color == fPrimeColor && it.secondaryColor == fSecondaryColor }
    if (flags.size < fCount) fCount = flags.size
    for (i in 0 until fCount) {
        if (this.room.createConstructionSite(flags[i].pos, fWhatBuild) == OK) {
            flags[i].remove()
            findBuild = true
        }
    }
    return findBuild
}

//T  ,   ,T
//   ,Lin,
//St ,   ,Sp
//   ,   ,
//Ter,   ,T

fun MainRoom.building() {
    //10 color COLOR_WHITE

    //secondaryColor
    //1 COLOR_RED       STRUCTURE_EXTENSION
    //2 COLOR_PURPLE    STRUCTURE_CONTAINER near controller
    //3 COLOR_BLUE      STRUCTURE_TOWER
    //4 COLOR_CYAN      STRUCTURE_ROAD after tower
    //5 COLOR_GREEN     STRUCTURE_STORAGE
    //6 COLOR_YELLOW    STRUCTURE_CONTAINER near source
    //7 COLOR_ORANGE    STRUCTURE_ROAD before storage
    //8 COLOR_BROWN     STRUCTURE_SPAWN

    for (record in this.slaveRooms)
        record.value.building() // ToDo only colonize

    //if (Math.round(Game.time/100)*100!=Game.time) return; //проверяем каждые 100 тиков
    if(this.constructionSite.isNotEmpty()) return
    val protectedStructureController: StructureController = this.structureController[0] ?: return
    if (protectedStructureController.level == 1) {
        if (this.buildStructure(COLOR_WHITE, COLOR_BROWN, STRUCTURE_SPAWN,1)) return
    }

    if (protectedStructureController.level == 2) {
        if (this.room.energyCapacityAvailable!=550) {//строим extension 5
            if (this.buildStructure(COLOR_WHITE,COLOR_RED,STRUCTURE_EXTENSION,1)) return
        }
    }

    if (protectedStructureController.level == 3) {
        if (this.room.energyCapacityAvailable!=800) {//строим extension 5
            if (this.buildStructure(COLOR_WHITE,COLOR_RED,STRUCTURE_EXTENSION,1)) return
        }
        if (this.buildStructure(COLOR_WHITE, COLOR_YELLOW, STRUCTURE_CONTAINER,1)) return
        if (this.buildStructure(COLOR_WHITE,COLOR_BLUE,STRUCTURE_TOWER,1)) return
        if (this.buildStructure(COLOR_WHITE,COLOR_CYAN,STRUCTURE_ROAD,80)) return
    }

    if (protectedStructureController.level == 4) {
        if (this.buildStructure(COLOR_WHITE, COLOR_YELLOW, STRUCTURE_CONTAINER,1)) return
        if (this.buildStructure(COLOR_WHITE,COLOR_PURPLE,STRUCTURE_CONTAINER,1)) return
        if (this.buildStructure(COLOR_WHITE,COLOR_ORANGE,STRUCTURE_ROAD,80)) return
        if (this.room.energyCapacityAvailable!=1300) {//строим extension 10
            if (this.buildStructure(COLOR_WHITE,COLOR_RED,STRUCTURE_EXTENSION,1)) return
        }
        if (this.buildStructure(COLOR_WHITE,COLOR_GREEN,STRUCTURE_STORAGE,1)) return
    }
}