var creepfn                    = require('modul.creepsfn');

var role_B_garbage = {
	run: function(creep) {
        var objRoom = Memory.Data[creep.memory.dstroom];
        if (objRoom == null) return;
        
	    //идем скинем енергию перед смертью (((
        if ((creep.carry[RESOURCE_ENERGY] != 0) & (creep.memory.timeForDie > creep.ticksToLive )){
            const storage = Game.getObjectById(creep.memory.dst);
            if(creep.transfer(storage, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                creep.moveTo(storage, {visualizePathStyle: {stroke: '#ffffff'}});
            }
            return;
        };


        if(creep.memory.work && creep.carry[RESOURCE_ENERGY] == 0) {
            creep.memory.work = false;
            creep.say('load');
        }

	    if(!creep.memory.work && creep.carry[RESOURCE_ENERGY]  == creep.carryCapacity) {
	        creep.memory.work = true;
	        creep.say('carry');
	    }

	    if(!creep.memory.work) {
            var target = creep.pos.findClosestByRange(FIND_DROPPED_RESOURCES, { filter: (o) => o.resourceType === RESOURCE_ENERGY });
            if (target) { 
                if(creep.pickup(target,RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(target, {visualizePathStyle: {stroke: '#ffffff'}});
                }
                return;
            };

            var target = creep.pos.findClosestByRange(FIND_TOMBSTONES, {
                filter: (structure) => {
                    return structure.store[RESOURCE_ENERGY] > 0;
                }
            });
                
            if (target) {
                if(creep.withdraw(target,RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) creep.moveTo(target, {visualizePathStyle: {stroke: '#ffffff'}});
            }else{
                creepfn.CreepMoveToNearestFlag(creep,COLOR_WHITE,COLOR_WHITE);
            }   
        }else{ 
            const storage = Game.getObjectById(creep.memory.dst);
            if(creep.transfer(storage, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                creep.moveTo(storage, {visualizePathStyle: {stroke: '#ffffff'}});
            }
        }
	}    
};

module.exports = role_B_garbage;