package mainRoom

import screeps.api.*
import screeps.api.structures.Structure

fun MainRoom.runTower() {
    var fTask: String? = null
    var fTarget: RoomObject? = null
    var fMaxTower: Int? = null

    //0
    val hostile = this.room.find(FIND_HOSTILE_CREEPS)
    if (fTask == null) {
        val damagedStructures = this.room.find(FIND_STRUCTURES).filter {
            (it.structureType == STRUCTURE_RAMPART || it.structureType == STRUCTURE_WALL) && (it.hits < 100000)
        }

        if (damagedStructures.isNotEmpty() && hostile.isNotEmpty()) {
            var fMinHealth = 300000000
            for (i in 0 until damagedStructures.size) {
                if (damagedStructures[i].hits < fMinHealth) {
                    fTarget = damagedStructures[i]
                    fMinHealth = damagedStructures[i].hits
                }
            }
            fTask = "repair"
        }
    }

    //1
    if (fTask == null) {
        if (hostile.isNotEmpty()) {
            fTask = "attack"
            if (this.constant.TowerLastTarget == hostile[0].id && hostile.size >= 2) fTarget = hostile[1]
            else fTarget = hostile[0]

            this.constant.TowerLastTarget = fTarget.id
        } else {
            this.constant.TowerLastTarget = ""
        }
    }

    //2
    if (fTask == null) {
        val damagedCreeps = this.room.find(FIND_MY_CREEPS).filter { it.hits < it.hitsMax }
        if (damagedCreeps.isNotEmpty()) {
            fTask = "heal"
            fTarget = damagedCreeps[0]
        }
    }

    //3 Containers
    if (fTask == null){
        val damagedContainers = this.room.find(FIND_STRUCTURES).filter { (it.structureType == STRUCTURE_CONTAINER)&&(it.hits<=230000) }
        if(damagedContainers.isNotEmpty()) {
            fTask = "repair"
            fTarget = damagedContainers[0]
            fMaxTower = 1
        }
    }

    //3.1 STRUCTURE_RAMPART<5000
//    if (fTask == null){
//
//        var DamagedContainers = Game.rooms[objRoom.Name].find(FIND_STRUCTURES, {
//            filter: (structure) => (structure.structureType == STRUCTURE_RAMPART)&&(structure.hits<=5000)
//        });
//        if(DamagedContainers.length != 0) {
//            fTask = 'repair';
//            fTarget = DamagedContainers[0];
//            fMaxTower = 1;
//        }
//    }

    //4 Roads
    if (fTask == null){
        val damagedRoads =  this.room.find(FIND_STRUCTURES).filter { it.structureType == STRUCTURE_ROAD && it.hits < (it.hitsMax - 1000) }
        if(damagedRoads.isNotEmpty()) {
            fTask = "repair"
            fTarget = damagedRoads[0]
            fMaxTower = 1
        }
    }

    //5 Ramparts и Wall держим на уровне objRoom.WallHints
//    if (fTask == null){
//        var fStorage = Game.getObjectById(objRoom.Storage);
//        if (fStorage != null){
//            var fWallHints = objRoom.WallHints;
//
//            if (objRoom.BoostBuilder >= 1) fWallHints = 2000;
//            if (fStorage.store.energy>=objRoom.StorageEnergyMin){
//                var DamagedStructures = Game.rooms[objRoom.Name].find(FIND_STRUCTURES, {
//                    filter: (structure) => ((structure.structureType == STRUCTURE_RAMPART || structure.structureType == STRUCTURE_WALL) && structure.hits<fWallHints)
//                });
//                if(DamagedStructures.length != 0) {
//                    var fMinHealth = 300000000;
//                    for (let i=0;i<DamagedStructures.length;i++){
//                        if (DamagedStructures[i].hits<fMinHealth){
//                            fTarget = DamagedStructures[i];
//                            fMinHealth = DamagedStructures[i].hits;
//                        }
//                    }
//                    fTask = 'repair';
//                    fMaxTower = 2;
//                }
//            }
//        }
//    }

    //6 Ramparts ближе 6 поднимаем до objRoom.HitsRamparts
//    if (fTask == null){
//        var fStorage = Game.getObjectById(objRoom.Storage);
//        if (fStorage != null){
//            if (fStorage.store.energy>=objRoom.StorageEnergyForce){
//                var fController = Game.getObjectById(objRoom.Controller);
//                if (fController.level == 8){
//                    var fMaxRampartsHits = objRoom.HitsRamparts;
//                    var fNuker = Game.getObjectById(objRoom.Nuker);
//                    var fObserver = Game.getObjectById(objRoom.Observer);
//                    var Rampart = fStorage.pos.findClosestByRange(FIND_STRUCTURES, {
//                        filter: (structure) => (structure.structureType == STRUCTURE_RAMPART)&&(structure.hits<=fMaxRampartsHits)
//                    });
//
//                    if (Rampart!=null){
//                        var fDistance = afunc.CalcDistancePos(fStorage.pos,Rampart.pos);
//                        if (fDistance>=6 && fNuker!=null) fDistance = afunc.CalcDistancePos(fNuker.pos,Rampart.pos);
//                        if (fDistance>=6 && fObserver!=null) fDistance = afunc.CalcDistancePos(fObserver.pos,Rampart.pos);
//                        if (fDistance<6){
//                            fTask = 'repair';
//                            fTarget = Rampart;
//                            fMaxTower = 2;
//                        }
//                    }
//                }
//            }
//        }
//    }

    if (fTask == null) return

    val towers = this.structureTower.values
    if (fMaxTower == null) fMaxTower = towers.size
    if (fMaxTower > towers.size) fMaxTower = towers.size

    for ((counter, tower) in towers.withIndex()) {
        if (counter>fMaxTower) break
        when (fTask) {
            "attack" -> tower.attack(fTarget as Creep)
            "heal" -> tower.heal(fTarget as Creep)
            "repair" -> tower.repair(fTarget as Structure)
        }
    }
}
