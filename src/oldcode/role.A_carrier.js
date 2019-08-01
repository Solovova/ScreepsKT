var creepfn                    = require('modul.creepsfn');
var afunc                      = require('modul.afunc');

var role_A_carrier = {
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
            const storage = afunc.GetNearestContainerThisMinEnergy(creep.pos,objRoom,700,2);
            if (!storage) {
                creepfn.CreepMoveToNearestFlag(creep,COLOR_WHITE,COLOR_WHITE);
                return;
            }
            if(creep.withdraw(storage, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                creep.moveTo(storage, {visualizePathStyle: {stroke: '#ffffff'}});
            }
        }else{
            const storage = Game.getObjectById(creep.memory.dst);
            if(creep.transfer(storage, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                creep.moveTo(storage, {visualizePathStyle: {stroke: '#ffffff'}});
            }
        }
	}    
};

module.exports = role_A_carrier;