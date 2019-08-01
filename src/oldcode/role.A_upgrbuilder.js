var creepfn                    = require('modul.creepsfn');
var afunc                      = require('modul.afunc');

var role_B_upgrbuilder = {
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
            if (objRoom.Containers[3]==null){
                var container = afunc.GetNearestContainerThisMinEnergy(creep.pos,objRoom,800,2);
            }else{
                var container = Game.getObjectById(objRoom.Containers[3]);    
            }

            if (container) {
                if(creep.withdraw(container, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(container, {visualizePathStyle: {stroke: '#ffffff'}});
                }
            }else{
                creepfn.CreepMoveToNearestFlag(creep,COLOR_WHITE,COLOR_WHITE);
            }
        }else{
            if (objRoom.oController.ticksToDowngrade>1000) {
                var target = creep.pos.findClosestByRange(FIND_CONSTRUCTION_SITES);
                if(target!=null) {
                    if(creep.build(target) == ERR_NOT_IN_RANGE) {
                        creep.moveTo(target, {visualizePathStyle: {stroke: '#ffffff'}});
                    };
                    return;
                }
            } 
            
            if(creep.upgradeController(controller) == ERR_NOT_IN_RANGE) {
                creep.moveTo(controller, {visualizePathStyle: {stroke: '#ffffff'}});
            }
    
        }
	}    
};

module.exports = role_B_upgrbuilder;