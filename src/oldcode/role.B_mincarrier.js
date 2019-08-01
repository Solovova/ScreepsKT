var role_B_mincarrier = {
	run: function(creep) {
        var objRoom = Memory.Data[creep.memory.dstroom];
        if (objRoom == null) return;
        
        if(creep.memory.work && _.sum(creep.carry) == 0) {
            creep.memory.work = false;
            creep.say('load');
	    }
	    if(!creep.memory.work && _.sum(creep.carry) == creep.carryCapacity) {
	        creep.memory.work = true;
	        creep.say('carry');
	    }
        const source = Game.getObjectById(objRoom.Mineral);
	    if(!creep.memory.work) {
            const storage = Game.getObjectById(objRoom.Containers[4]);
            
            if(creep.withdraw(storage,source.mineralType) == ERR_NOT_IN_RANGE) {
                creep.moveTo(storage, {visualizePathStyle: {stroke: '#ffffff'}});
            }
        }else{
            const storage = Game.getObjectById(objRoom.Storage);
            fHarvestMineralsInStorage = storage.store[source.mineralType];
            if (fHarvestMineralsInStorage == null) fHarvestMineralsInStorage = 0;
            if (fHarvestMineralsInStorage>=objRoom.StorageMineralExtractionMax) return;

            fMineralsInStorage = _.sum(storage.store) - storage.store.energy;
            if (fMineralsInStorage>=objRoom.StorageMineralAllMax) return;

            if(creep.transfer(storage,source.mineralType) == ERR_NOT_IN_RANGE) {
                creep.moveTo(storage, {visualizePathStyle: {stroke: '#ffffff'}});
            }
        }
	}    
};

module.exports = role_B_mincarrier;