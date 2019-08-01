var role_B_transToTerminal = {
	run: function(creep) {
		var objRoom = Memory.Data[creep.memory.dstroom];
        if (objRoom == null) return;

	    const storage = Game.getObjectById(creep.memory.src);
	    const terminal = Game.getObjectById(creep.memory.dst);
	    
        if (creep.memory.work && creep.carry.energy == 0) {
            creep.memory.work = false;
	    }
	    if(!creep.memory.work && creep.carry.energy == creep.carryCapacity) {
	        creep.memory.work = true;
	    }

	    if(!creep.memory.work) {
	        if (storage.store.energy<=objRoom.StorageEnergyMin) return;
            if(creep.withdraw(storage, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                creep.moveTo(storage);
            }
        }else{
			if (objRoom.DropResources == 1) {
				creep.drop(RESOURCE_ENERGY);
			}else{
				if (terminal.store.energy >= objRoom.TerminalEnergyMax) return;
             	if(creep.transfer(terminal, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                	 creep.moveTo(terminal);
				 }
			}
        }
	}    
};

module.exports = role_B_transToTerminal;