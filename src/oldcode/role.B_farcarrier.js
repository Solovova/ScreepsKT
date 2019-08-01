var creepfn                          = require('modul.creepsfn');

var role_B_farcarrier = {
	run: function(creep) {
        var objRoomDst = Memory.Data[creep.memory.srcroom];
        if (objRoomDst == null) return;
        
        var objRoomSrc = Memory.Data[creep.memory.dstroom];
        if (objRoomSrc == null) return;
        
        if(creep.memory.work && creep.carry.energy == 0) {
            creep.memory.work = false;
            creep.say('load');
	    }
	    if(!creep.memory.work && creep.carry.energy == creep.carryCapacity) {
	        creep.memory.work = true;
	        creep.say('carry');
	    }

	    if(!creep.memory.work) {
            var storage = Game.getObjectById(objRoomSrc.Storage);
            if (storage == null) return;
            if (storage.store.energy<=objRoomSrc.StorageEnergyMin) return;
            if(creep.withdraw(storage, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                //creepfn.CreepMoveByPathFinder(creep,storage);
                creep.moveTo(storage, {visualizePathStyle: {stroke: '#ffffff'}});
            }
        }else{
            var storage = Game.getObjectById(objRoomDst.Storage);
            if (storage == null) return;
            if(creep.transfer(storage, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                //creepfn.CreepMoveByPathFinder(creep,storage);
                creep.moveTo(storage, {visualizePathStyle: {stroke: '#ffffff'}});
            }
        }
	}    
};

module.exports = role_B_farcarrier;