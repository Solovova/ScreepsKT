var role_B_transFromLink = {
	run: function(creep) {
        var objRoom = Memory.Data[creep.memory.dstroom];
        if (objRoom == null) return;

        if ((creep.memory.role <1000) & (creep.ticksToLive < 40 )){
            creep.memory.role = creep.memory.role + 1000;
	    };

	    const link    = Game.getObjectById(objRoom.Links[2]);
	    const storage = Game.getObjectById(objRoom.Storage);
	    
        if (creep.memory.work && creep.carry.energy == 0) {
            creep.memory.work = false;
	    }
	    if(!creep.memory.work && creep.carry.energy == creep.carryCapacity) {
	        creep.memory.work = true;
	    }

	    if(!creep.memory.work) {
            if(creep.withdraw(link, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                creep.moveTo(link);
            }
        }else{
            if(creep.transfer(storage, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                creep.moveTo(storage);
            }
        }
	}    
};

module.exports = role_B_transFromLink;