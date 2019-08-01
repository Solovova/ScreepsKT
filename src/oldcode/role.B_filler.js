var creepfn                          = require('modul.creepsfn');

var role_B_Filler = {
	run: function(creep) {
        var objRoom = Memory.Data[creep.memory.dstroom];
        if (objRoom == null) return;
        
	    //идем скинем енергию перед смертью (((
        if ((creep.carry.energy != 0) & (creep.memory.timeForDie > creep.ticksToLive )){
            const storage = Game.getObjectById(creep.memory.src);
            if(creep.transfer(storage, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                creep.moveTo(storage, {visualizePathStyle: {stroke: '#ffffff'}});
            }
            return;
        };

        if ((creep.memory.role <1000) & (creep.ticksToLive < 100 )){
            creep.memory.role = creep.memory.role + 1000;
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
            const storage = Game.getObjectById(creep.memory.src);
            var fError = creep.withdraw(storage, RESOURCE_ENERGY);
            if(fError == ERR_NOT_IN_RANGE) {
                creep.moveTo(storage, {visualizePathStyle: {stroke: '#ffffff'}});
            }else{
                if (fError!=OK) creepfn.CreepMoveToNearestFlag(creep,COLOR_WHITE,COLOR_WHITE);
            }
        }else{
            var fController = Game.getObjectById(objRoom.Controller);
            if (fController.level != 8) {
                var targets = creep.pos.findClosestByRange(FIND_STRUCTURES, {
                    filter: (structure) => {
                        return (structure.structureType == STRUCTURE_TOWER) && structure.energy < 500;
                    }
                });
            }
            
            if(!targets) {
                var targets = creep.pos.findClosestByRange(FIND_STRUCTURES, {
                    filter: (structure) => {
                        return ((structure.structureType == STRUCTURE_EXTENSION)||(structure.structureType == STRUCTURE_SPAWN)) && structure.energy < structure.energyCapacity;
                    }
                });
            }
            
            if(targets) {
                if(creep.transfer(targets, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(targets, {visualizePathStyle: {stroke: '#ffffff'}});
                }
            }else{
                creepfn.CreepMoveToNearestFlag(creep,COLOR_WHITE,COLOR_WHITE);
            } 
        }
	}    
};

module.exports = role_B_Filler;