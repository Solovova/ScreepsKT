var role_B_MinHarvester = {

    run: function(creep) {
        var objRoom = Memory.Data[creep.memory.dstroom];
        if (objRoom == null) return;

        
        if(creep.memory.work && _.sum(creep.carry) == 0) {
            creep.memory.work = false;
            creep.say('harvest');
        }
        
        var fNeedEnergy = creep.carryCapacity;
        if (creep.memory.info !=null) fNeedEnergy=creep.memory.info;
	    if(!creep.memory.work && (_.sum(creep.carry)>= (fNeedEnergy))) {
	        creep.memory.work = true;
	        creep.say('work');
	    }

	    if(!creep.memory.work) {
            const source = Game.getObjectById(objRoom.Mineral);
            if(creep.harvest(source) == ERR_NOT_IN_RANGE) {
                creep.moveTo(source, {visualizePathStyle: {stroke: '#ffaa00'}}); 
            }
        }
        else {
            const source = Game.getObjectById(objRoom.Mineral);
            var container = Game.getObjectById(objRoom.Containers[4]);
            if (container==null) return;
            if(creep.transfer(container,source.mineralType) == ERR_NOT_IN_RANGE) {
                creep.moveTo(container, {visualizePathStyle: {stroke: '#ffffff'}});
            }
        }
	}
};

module.exports = role_B_MinHarvester;