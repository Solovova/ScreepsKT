var creepfn                          = require('modul.creepsfn');
var afunc                               = require('modul.afunc');

var role_B_Updater = {

    /** @param {Creep} creep **/
    run: function(creep) {
        var objRoom = Memory.Data[creep.memory.dstroom];
        if (objRoom == null) return;

        if (creep.room.name != creep.memory.dstroom) {
            creepfn.CreepMoveToDstRoom(creep);
            return;
        };

        if (creepfn.CreepBoorst(creep,objRoom) == 1) return;

        // if (creep.memory.boost == null && objRoom.UpgraderUp == 1) {
            
        //     var fLab = Game.getObjectById(objRoom.LabsReaction[0]);
        //     if (fLab!=null){
        //         fErr = fLab.boostCreep(creep);
        //         if (fErr == ERR_NOT_IN_RANGE) {
        //             creep.moveTo(fLab, {visualizePathStyle: {stroke: '#ffffff'}});
        //             return;
        //         };
        //         creep.memory.boost = fErr;
        //     }else{
        //         creep.memory.boost = 'nolab';
        //     }
        // }

        var controller = Game.getObjectById(objRoom.Controller);
        if (controller.level ==8){
            if ((creep.memory.role <1000) & (creep.ticksToLive < 150 )){
                creep.memory.role = creep.memory.role + 1000;
            };    
        }
        
	    if(creep.memory.work && creep.carry.energy == 0) {
            creep.memory.work = false;
            creep.say('harvest');
	    }
	    if(!creep.memory.work && creep.carry.energy == creep.carryCapacity) {
	        creep.memory.work = true;
	        creep.say('build');
	    }
	    
	    if(!creep.memory.work) {
            var storage = Game.getObjectById(objRoom.Links[3]);
            if (storage == null) storage = Game.getObjectById(creep.memory.src);
            if(creep.withdraw(storage, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                creep.moveTo(storage, {visualizePathStyle: {stroke: '#ffffff'}});
            }
	    } else {
            
            
            // var fSign = 'SoloVova: ' + objRoom.Describe;
            // var fNeedSign = 0;
            // if (controller.sign == null) fNeedSign = 1;
            // if (fNeedSign == 0 && controller.sign!=null) if (fSign != controller.sign.text) fNeedSign = 1;

            // if (fNeedSign == 1) {
            //     if(creep.signController(controller, fSign) == ERR_NOT_IN_RANGE) {
            //         creep.moveTo(controller, {visualizePathStyle: {stroke: '#ffffff'}});
            //         return;
            //     }
            // }

            if( creep.upgradeController(controller) == ERR_NOT_IN_RANGE) {
                creep.moveTo(controller, {visualizePathStyle: {stroke: '#ffffff'}});
            }
	    }
	}
};

module.exports = role_B_Updater;