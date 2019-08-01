var creepfn                          = require('modul.creepsfn');

var role_B_carrier = {
	run: function(creep) {
        var objRoom = Memory.Data[creep.memory.dstroom];
        if (objRoom == null) return;
        
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
            if (creep.memory.role == 206 && storage.store.energy<=objRoom.StorageEnergyMin) return;
            if(creep.withdraw(storage, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                creepfn.CreepMoveByPathFinder(creep,storage);
                //creep.moveTo(storage, {visualizePathStyle: {stroke: '#ffffff'}});
            }
        }else{
            const storage = Game.getObjectById(creep.memory.dst);
            if(creep.transfer(storage, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                creepfn.CreepMoveByPathFinder(creep,storage);
                //creep.moveTo(storage, {visualizePathStyle: {stroke: '#ffffff'}});
            }
        }
	}    
};

module.exports = role_B_carrier;