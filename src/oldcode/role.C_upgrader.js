var creepfn                          = require('modul.creepsfn');
var afunc                      = require('modul.afunc');

var role_C_Updater = {

    /** @param {Creep} creep **/
    run: function(creep) {
        var objRoom = Memory.Data[creep.memory.dstroom];
        if (objRoom == null) return;

        if (creep.room.name != creep.memory.dstroom) {
            if (objRoom.SafeWay!=null){
                creepfn.CreepMoveByPathToRoom(creep,objRoom.SafeWay);
                return;    
            }
        };

        //if (creepfn.CreepMoveToDangerWay(creep,COLOR_BROWN,COLOR_BROWN,'W46N6')==1) return;

        if (creep.room.name != creep.memory.dstroom) {
            creepfn.CreepMoveToDstRoom(creep);
            return;
        };
        
	    if(creep.memory.work && creep.carry.energy == 0) {
            creep.memory.work = false;
            creep.say('harvest');
	    }
	    if(!creep.memory.work && creep.carry.energy == creep.carryCapacity) {
	        creep.memory.work = true;
	        creep.say('build');
	    }
	    
	    if(!creep.memory.work) {
            const storage = afunc.GetNearestContainerThisMinEnergy(creep.pos,objRoom,200,3);
            if (!storage) {
                creepfn.CreepMoveToNearestFlag(creep,COLOR_WHITE,COLOR_WHITE);
                return;
            }
            if(creep.withdraw(storage, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                creep.moveTo(storage, {visualizePathStyle: {stroke: '#ffffff'}});
            }
	    } else {
	        var controller = Game.getObjectById(objRoom.Controller);
            if(creep.upgradeController(controller) == ERR_NOT_IN_RANGE) {
                creep.moveTo(controller, {visualizePathStyle: {stroke: '#ffffff'}});
            }
	    }
	}
};

module.exports = role_C_Updater;