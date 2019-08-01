var role_B_Harvester = {

    /** @param {Creep} creep **/
    run: function(creep) {
        var objRoom = Memory.Data[creep.memory.dstroom];
        if (objRoom == null) return;

        if ((creep.memory.role <1000) & (creep.ticksToLive < 150 )){
            creep.memory.role = creep.memory.role + 1000;
	    };
        
        if(creep.memory.work && creep.carry.energy == 0) {
            creep.memory.work = false;
            creep.say('harvest');
        }
        
        var fNeedEnergy = creep.carryCapacity;
        if (creep.memory.info !=null) fNeedEnergy=creep.memory.info;
	    if(!creep.memory.work && (creep.carry.energy >= (fNeedEnergy))) {
	        creep.memory.work = true;
	        creep.say('work');
	    }

	    if(!creep.memory.work) {
            const source = Game.getObjectById(creep.memory.src);
            if(creep.harvest(source) == ERR_NOT_IN_RANGE) {
                creep.moveTo(source, {visualizePathStyle: {stroke: '#ffaa00'}});
                //console.log('harvester:' + creep.id + ' move to Source'); 
            }
        }
        else {
            var fLink = Game.getObjectById(objRoom.Links[objRoom.Sources.indexOf(creep.memory.src)]);
            if (fLink) {
                if(creep.transfer(fLink, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                    //console.log('harvester:' + creep.id + ' move to Link');
                    creep.moveTo(fLink, {visualizePathStyle: {stroke: '#ffffff'}});
                }
            }else{
                var container = Game.getObjectById(creep.memory.dst);
                if (container==null) return;
                if (container.hits > 240000) { 
                    if(creep.transfer(container, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                        //console.log('harvester:' + creep.id + ' move to Conteiner');
                        creep.moveTo(container, {visualizePathStyle: {stroke: '#ffffff'}});
                    }
                }else{
                    if(creep.repair(container) == ERR_NOT_IN_RANGE) {
                        //console.log('harvester:' + creep.id + ' move to Conteiner');
                        creep.moveTo(container, {visualizePathStyle: {stroke: '#ffffff'}});
                    }    
                }
            }
        }
	}
};

module.exports = role_B_Harvester;