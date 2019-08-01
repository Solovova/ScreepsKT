var creepfn                    = require('modul.creepsfn');
var afunc                      = require('modul.afunc');

var role_B_smallupgrader = {
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
            var storage = Game.getObjectById(objRoom.Storage);    
            if (storage) {
                if(creep.withdraw(storage, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(storage, {visualizePathStyle: {stroke: '#ffffff'}});
                }
            }
        }else{
            var controller = Game.getObjectById(objRoom.Controller);
            if(creep.upgradeController(controller) == ERR_NOT_IN_RANGE) {
                creep.moveTo(controller, {visualizePathStyle: {stroke: '#ffffff'}});
            }
    
        }
	}    
};

module.exports = role_B_smallupgrader;