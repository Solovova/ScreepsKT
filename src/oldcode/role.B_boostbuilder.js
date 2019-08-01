var creepfn                          = require('modul.creepsfn');
var afunc                               = require('modul.afunc');
var logistics                   = require('modul.logistics');
var messenger                      	= require('modul.messenger');

var role_B_boostbuilder = {

   
    run: function(creep) {
        
        var objRoom = Memory.Data[creep.memory.dstroom];
        if (objRoom == null) return;

        if (creepfn.CreepBoorst(creep,objRoom) == 1) return;


        if (creep.memory.isTask == null) creep.memory.isTask = 0;

        if (creep.memory.Task == 'repair' && creep.memory.isTask == 1){
            objRoom.idBuilderCreep = creep.id;    
            var repair = Game.getObjectById(creep.memory.idRepair);
            if (repair == null || repair.hits>=creep.memory.RapairToHits) creep.memory.isTask = 0;
        }

        if (creep.memory.Task == 'build' && creep.memory.isTask == 1){
            objRoom.idBuilderCreep = creep.id;    
            var build = Game.getObjectById(creep.memory.idRepair);
            if (build == null) creep.memory.isTask = 0;
        }
        

        if (creep.memory.isTask == 0) {
            var task = logistics.GetTaskForBoostBuilder(objRoom,creep);
            if (task==null){
                messenger.log('ALARM',' Builder dont work in room:'+ objRoom.Name,COLOR_YELLOW,objRoom.Name);
                return;
            }
            creep.memory.isTask             = task.isTask;
            creep.memory.idRepair           = task.idRepair;
            creep.memory.RapairToHits       = task.RapairToHits;
            creep.memory.Task               = task.Task;
        }

        messenger.log('BUILDER',''+creep.memory.Task+' :' + creep.memory.isTask + ' repair:' + creep.memory.idRepair + ' to hits:' + creep.memory.RapairToHits,COLOR_GREEN,objRoom.Name)

	    if((creep.carry.energy > 0 || objRoom.CalcRoleCreeps[22]!=0) && creep.memory.Task == 'repair' && creep.memory.isTask == 1) {
            var repair = Game.getObjectById(creep.memory.idRepair);
            
            if (repair!=null){
                
                if(creep.repair(repair) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(repair, {visualizePathStyle: {stroke: '#ffffff'}});
                }    
            }
        }
        
        if((creep.carry.energy > 0 || objRoom.CalcRoleCreeps[22]!=0) && creep.memory.Task == 'build' && creep.memory.isTask == 1) {
            var build = Game.getObjectById(creep.memory.idRepair);
            //if (objRoom.Name == 'W41N7') console.log('dddddddddddddddddddddd'+ build)
            if (build!=null){
                if(creep.build(build) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(build, {visualizePathStyle: {stroke: '#ffffff'}});
                }    
            }else{
                creep.memory.isTask = 0;   
            }
        }

        if(creep.carry.energy == 0 && creep.memory.isTask == 1 && objRoom.CalcRoleCreeps[22]==0) {
            const storage = Game.getObjectById(objRoom.Storage);
            if (storage.store.energy<=objRoom.StorageEnergyMin) return;
            if(creep.withdraw(storage, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                creep.moveTo(storage, {visualizePathStyle: {stroke: '#ffffff'}});
            }
        }
	}
};

module.exports = role_B_boostbuilder;