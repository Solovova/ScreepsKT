var creepfn                          = require('modul.creepsfn');
var afunc                               = require('modul.afunc');


var role_B_boostbuildercarrier = {
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
            if (objRoom.oStorage == null) return;
            if (objRoom.oStorage.store.energy<=objRoom.StorageEnergyMin) return;
            if(creep.withdraw(objRoom.oStorage, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                creep.moveTo(objRoom.oStorage, {visualizePathStyle: {stroke: '#ffffff'}});
            }
        }else{
            try{
                var creepto = Game.getObjectById(objRoom.RoleId[221][0]);
            }catch(err){
                var creepto = null;
            }  
            //var creepto = Game.getObjectById(objRoom.idBuilderCreep);
            if (creepto!=null && !creepto.spawning){
                fDistance = afunc.CalcDistancePos(creepto.pos,creep.pos);
                if (fDistance>1){
                    creep.moveTo(creepto, {visualizePathStyle: {stroke: '#ffffff'}});
                }
                if ((creepto.carryCapacity - creepto.carry.energy)<creep.carry.energy) return;
                creep.transfer(creepto, RESOURCE_ENERGY);
            }else{
                creepfn.CreepMoveToNearestFlag(creep,COLOR_WHITE,COLOR_WHITE);
            }
        }
	}    
};

module.exports = role_B_boostbuildercarrier;