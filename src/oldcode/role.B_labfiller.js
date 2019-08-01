var sf                      	= require('modul.smallfunc');
var logistics                   = require('modul.logistics');
var messenger                      	= require('modul.messenger');


var role_B_LabFiller = {
	run: function(creep) {
        //return;
        var objRoom = Memory.Data[creep.memory.dstroom];
        if (objRoom == null) return;
        //creep.memory.isTask = 0;

        if (creep.memory.isTask == null) creep.memory.isTask = 0;

        
        if (creep.memory.isTask == 2){
            if (_.sum(creep.carry)==0) creep.memory.isTask = 0;
        }

        if (creep.memory.isTask == 0) {
            var task = logistics.GetTaskForLabfiller(objRoom,creep.carryCapacity);
            if (task==null) return;
            creep.memory.isTask     = task.isTask;
            creep.memory.idFrom     = task.idFrom;
            creep.memory.idTo       = task.idTo;
            creep.memory.Mineral    = task.Mineral;
            creep.memory.Quantity   = task.Quantity;
            creep.memory.Task       = task.Task;

        }

        //console.log('Lab filler task:' + creep.memory.isTask + ' from:' + creep.memory.idFrom + ' to:' + creep.memory.idTo + ' mineral:' + creep.memory.Mineral + ' quantity:' + creep.memory.Quantity)
        messenger.log('LABFIL',''+creep.memory.Task+' :' + creep.memory.isTask + ' from:' + creep.memory.idFrom + ' to:' + creep.memory.idTo + ' mineral:' + creep.memory.Mineral + ' quantity:' + creep.memory.Quantity,COLOR_GREEN,objRoom.Name)
        if (creep.memory.isTask == 0) return;
        
        if (creep.memory.isTask == 1){
            if (_.sum(creep.carry)!=0) creep.memory.isTask = 2;
        }
   
	    if(creep.memory.isTask == 2) {
            var trans_to = Game.getObjectById(creep.memory.idTo);
            if (trans_to == null) return;
            if(creep.transfer(trans_to, creep.memory.Mineral, creep.memory.Quantity) == ERR_NOT_IN_RANGE) {
                creep.moveTo(trans_to);
            }
        }

        if(creep.memory.isTask == 1) {
            var trans_from = Game.getObjectById(creep.memory.idFrom);
            if (trans_from == null) return;

            if (trans_from.structureType == STRUCTURE_LAB){
                if (creep.memory.Mineral == RESOURCE_ENERGY){
                    if (trans_from.energy<creep.memory.Quantity){
                        creep.memory.isTask = 0;
                        return;   
                    }
                }else{
                    if (trans_from.mineralAmount<creep.memory.Quantity || trans_from.mineralType!=creep.memory.Mineral){
                        creep.memory.isTask = 0;
                        return;   
                    }
                }
            }

            if (trans_from.structureType == STRUCTURE_TERMINAL){
                if (sf.GetStoredResources(trans_from.store,creep.memory.Mineral)<creep.memory.Quantity){
                    creep.memory.isTask = 0;
                    return;   
                }
            }
            
            if(creep.withdraw(trans_from, creep.memory.Mineral, creep.memory.Quantity) == ERR_NOT_IN_RANGE) {
                creep.moveTo(trans_from);
            }
        }
	}    
};

module.exports = role_B_LabFiller;