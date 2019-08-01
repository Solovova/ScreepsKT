var creepfn                    = require('modul.creepsfn');
var afunc                      = require('modul.afunc');

var roleHarvesterSmall = {

    /** @param {Creep} creep **/
    run: function(creep) {
        var objRoom = Memory.Data[creep.memory.dstroom];
        if (objRoom == null) return;
        
        if(creep.memory.work && creep.carry.energy == 0) {
            creep.memory.work = false;
            creep.say('harvest');
	    }
	    if(!creep.memory.work && creep.carry.energy == creep.carryCapacity) {
	        creep.memory.work = true;
	        creep.say('work');
	    }
	    
	    if(!creep.memory.work) {
            var container = afunc.GetNearestContainerThisMinEnergy(creep.pos,objRoom,100,3);
            if (container) {
                if(creep.withdraw(container, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(container, {visualizePathStyle: {stroke: '#ffffff'}});
                }
                return;
            }

            const source = Game.getObjectById(creep.memory.src);
            var fError = creep.harvest(source);
            if(fError == ERR_NOT_IN_RANGE) {
                var fError = creep.moveTo(source, {visualizePathStyle: {stroke: '#ffaa00'}});
                if (fError == ERR_NO_PATH) {  //меняем источник тут все занято
                    if (objRoom.Sources.length>1){
                        if (creep.memory.src == objRoom.Sources[0]){
                            creep.memory.src = objRoom.Sources[1]
                        }else{
                            creep.memory.src = objRoom.Sources[0]
                        }
                    }
                }
            }else{
                if (fError!=OK) creepfn.CreepMoveToNearestFlag(creep,COLOR_WHITE,COLOR_WHITE);
            }
        } else {
            var target = creep.pos.findClosestByRange(FIND_STRUCTURES, {
                filter: (structure) => {
                    return ((structure.structureType == STRUCTURE_SPAWN)||(structure.structureType == STRUCTURE_EXTENSION) ) && structure.energy < structure.energyCapacity;
                }
            });
  
            if(target) {
                if(creep.transfer(target, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(target, {visualizePathStyle: {stroke: '#ffffff'}});
                }
            }else{
                var controller = Game.getObjectById(objRoom.Controller);
                if(creep.upgradeController(controller) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(controller, {visualizePathStyle: {stroke: '#ffffff'}});
                }


            } 
        }
	}
};

module.exports = roleHarvesterSmall;