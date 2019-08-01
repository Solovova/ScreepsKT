var creepfn                          = require('modul.creepsfn');
var afunc                      = require('modul.afunc');

var role_C_Builder = {

    /** @param {Creep} creep **/
    run: function(creep) {
        var objRoom = Memory.Data[creep.memory.dstroom];
        if (objRoom == null) return;
        var objMainRoom = Memory.Data[creep.memory.srcroom];
        if (objMainRoom == null) return;

        if (creepfn.CreepBoorst(creep,objMainRoom) == 1) return;

        // if (creep.memory.boost == null && creep.room.name == creep.memory.srcroom) {
        //     objRoomMaster = Memory.Data[creep.memory.srcroom];
        //     var fLab = Game.getObjectById(objRoomMaster.LabsReaction[0]);
        //     if (fLab!=null){
        //         fErr = fLab.boostCreep(creep);
        //         if (fErr == ERR_NOT_IN_RANGE) {
        //             creep.moveTo(fLab, {visualizePathStyle: {stroke: '#ffffff'}});
        //             return;
        //         };
        //         creep.memory.boost = fErr;
        //     }else{
        //         creep.memory.boost = 'nolab';
        //     }
        // }

        if (creep.room.name != creep.memory.dstroom) {
            if (objRoom.SafeWay!=null){
                creepfn.CreepMoveByPathToRoom(creep,objRoom.SafeWay);
                return;    
            }
        };
        
        // if (creep.room.name != creep.memory.dstroom) {
        //     creepfn.CreepMoveToDstRoom(creep);
        //     return;
        // };

        //if (creepfn.CreepMoveToDangerWay(creep,COLOR_BROWN,COLOR_BROWN,'W46N6')==1) return;
        
	    if(creep.memory.work && creep.carry.energy == 0) {
            creep.memory.work = false;
            creep.say('harvest');
	    }
	    if(!creep.memory.work && creep.carry.energy > 0) {
	        creep.memory.work = true;
	        creep.say('build');
	    }
	    if(!creep.memory.work) {
            try{
                terminal = Game.rooms[objRoom.Name].terminal;
                if (terminal.store.energy>2000) target = terminal;
            }catch(err){

            }

            if (target){
                if(creep.withdraw(target, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(target, {visualizePathStyle: {stroke: '#ffffff'}});
                }
                return;
            }

            var target = creep.pos.findClosestByRange(FIND_DROPPED_RESOURCES, { filter: (o) => o.resourceType === RESOURCE_ENERGY });
            if (target) { 
                if(creep.pickup(target,RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(target, {visualizePathStyle: {stroke: '#ffffff'}});
                }
                return;
            };

            var target = creep.pos.findClosestByRange(FIND_TOMBSTONES, {
                filter: (structure) => {
                    return structure.store.energy > 0;
                }
            });
            if (target) {
                if(creep.withdraw(target, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(target, {visualizePathStyle: {stroke: '#ffffff'}});
                }
                return;
            }

            var storage = afunc.GetNearestContainerThisMinEnergy(creep.pos,objRoom,600,3);
            if (!storage) {
                creepfn.CreepMoveToNearestFlag(creep,COLOR_WHITE,COLOR_WHITE);
                return;
            }
            if(creep.withdraw(storage, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                creep.moveTo(storage, {visualizePathStyle: {stroke: '#ffffff'}});
            }
	    } else {
            var targets = creep.pos.findClosestByRange(FIND_STRUCTURES, {
                filter: (structure) => {
                    return (structure.structureType == STRUCTURE_TOWER) && structure.energy <= (structure.energyCapacity-creep.carryCapacity);
                }
            });
            
            if(targets) {
                if(creep.transfer(targets, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(targets, {visualizePathStyle: {stroke: '#ffffff'}});
                }
                return;
            }

            var target = creep.pos.findClosestByRange(FIND_CONSTRUCTION_SITES);
            if (target == null && creep.room.name != creep.memory.dstroom) {
                if (Game.rooms[creep.memory.dstroom]!=null) {
                    targets = Game.rooms[creep.memory.dstroom].find(FIND_CONSTRUCTION_SITES);
                    if (targets.length!=0) target = targets[0];
                }
            }

            if(target) {
                if(creep.build(target) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(target, {visualizePathStyle: {stroke: '#ffffff'}});
                }
            }else{
                if (objRoom.oController!=null){
                    if( creep.upgradeController(objRoom.oController) == ERR_NOT_IN_RANGE) {
                        creep.moveTo(objRoom.oController, {visualizePathStyle: {stroke: '#ffffff'}});
                    }
                }
                //creepfn.CreepMoveToNearestFlag(creep,COLOR_WHITE,COLOR_WHITE);    
            }
	    }
	}
};

module.exports = role_C_Builder;