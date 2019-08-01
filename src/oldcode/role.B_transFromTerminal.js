var role_B_transFromTerminal = {
	run: function(creep) {
        var objRoom = Memory.Data[creep.memory.dstroom];
        if (objRoom == null) return;

	    const storage  = Game.getObjectById(creep.memory.dst);
	    const terminal = Game.getObjectById(creep.memory.src);
	    
        if (creep.memory.work && creep.carry.energy == 0) {
            creep.memory.work = false;
	    }
	    if(!creep.memory.work && creep.carry.energy == creep.carryCapacity) {
	        creep.memory.work = true;
	    }

	    if(!creep.memory.work) {
            if (terminal.store[RESOURCE_ENERGY]<=objRoom.StorageEnergyMin) return;
            if(creep.withdraw(terminal, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                creep.moveTo(terminal);
            }
        }else{
            
            if (storage.store.energy>=objRoom.StorageEnergyMax) return;
            if(creep.transfer(storage, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                creep.moveTo(storage);
            }
        }
	}    
};

module.exports = role_B_transFromTerminal;