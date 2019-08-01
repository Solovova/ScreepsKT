var creepfn                    = require('modul.creepsfn');

var role_A_garbage = {
	run: function(creep) {
        var objRoom = Memory.Data[creep.memory.dstroom];
        if (objRoom == null) return;
        
	    //идем скинем енергию перед смертью (((
        if ((creep.carry.energy != 0) & (creep.memory.timeForDie > creep.ticksToLive )){
            var storage = creep.pos.findClosestByRange(FIND_STRUCTURES, {
                filter: (structure) => {
                    return (structure.structureType == STRUCTURE_CONTAINER) && structure.store.energy <= (structure.storeCapacity-150);
                }
            });
            if(creep.transfer(storage, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                creep.moveTo(storage, {visualizePathStyle: {stroke: '#ffffff'}});
            }
            return;
        };

        if(creep.memory.work && creep.carry.energy == 0) {
            creep.memory.work = false;
            creep.say('load');
	    }
	    if(!creep.memory.work && creep.carry.energy == creep.carryCapacity) {
	        creep.memory.work = true;
	        creep.say('carry');
	    }

	    if(!creep.memory.work) {
            var target = creep.pos.findClosestByRange(FIND_DROPPED_RESOURCES, { filter: (o) => o.resourceType === RESOURCE_ENERGY });
            if (target) { 
                if(creep.pickup(target, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(target, {visualizePathStyle: {stroke: '#ffffff'}});
                }
            }else{
                var target = creep.pos.findClosestByRange(FIND_TOMBSTONES, {
                    filter: (structure) => {
                        return structure.store.energy > 0;
                    }
                });
                
                if(creep.withdraw(target, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(target, {visualizePathStyle: {stroke: '#ffffff'}});
                }else{
                    creepfn.CreepMoveToNearestFlag(creep,COLOR_WHITE,COLOR_WHITE);
                }
            }   
        }else{
            var storage = creep.pos.findClosestByRange(FIND_STRUCTURES, {
                filter: (structure) => {
                    return (structure.structureType == STRUCTURE_CONTAINER) && structure.store.energy <= (structure.storeCapacity-150);
                }
            });
            if (storage) {
                if(creep.transfer(storage, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(storage, {visualizePathStyle: {stroke: '#ffffff'}});
                }
            }
        }
	}    
};

module.exports = role_A_garbage;