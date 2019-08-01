var creepfn                    = require('modul.creepsfn');
var afunc                      = require('modul.afunc');

var role_C_Carrier = {
	run: function(creep) {
        var objRoom = Memory.Data[creep.memory.dstroom];
        if (objRoom == null) return;
        objRoom.MineralCarrierNear = 0; 

        var objRoomMaster = Memory.Data[creep.memory.srcroom];
        if (objRoomMaster == null) return;
        
        if(creep.memory.work && _.sum(creep.carry) == 0) {
            creep.memory.work = false;
            creep.say('load');
	    }
	    if(!creep.memory.work && _.sum(creep.carry)>0) {
	        creep.memory.work = true;
	        creep.say('carry');
        }
        
        if (creepfn.AlarmMove(creep)==1) return;

	    if(!creep.memory.work) {
            if (creep.room.name != creep.memory.dstroom) {
                creepfn.CreepMoveToDstRoom(creep);
                return;    
            };
            var fMineral = Game.getObjectById(objRoom.Mineral);
            if (fMineral == null) return;
            var fDistance = afunc.CalcDistancePos(fMineral.pos,creep.pos);
            if (fDistance>4){
                creep.moveTo(fMineral, {visualizePathStyle: {stroke: '#ffaa00'}}); 
                return;  
            } 
            objRoom.MineralCarrierNear = 1;

            var target = creep.pos.findClosestByRange(FIND_DROPPED_RESOURCES, { filter: (o) => o.resourceType === fMineral.mineralType });
            if (target){
                if (afunc.CalcDistancePos(target.pos,creep.pos)<6){
                    if(creep.pickup(target, fMineral.mineralType) == ERR_NOT_IN_RANGE) {
                        creep.moveTo(target, {visualizePathStyle: {stroke: '#ffffff'}});
                    }
                    return;   
                }
            }
        }else{
            var fMineral = Game.getObjectById(objRoom.Mineral);
            if (fMineral == null) return;
            var storage = Game.getObjectById(objRoomMaster.Storage);
            if (storage == null) return;
            if(creep.transfer(storage, fMineral.mineralType) == ERR_NOT_IN_RANGE) {
                creep.moveTo(storage, {visualizePathStyle: {stroke: '#ffaa00'}});
            }
        }
	}    
};

module.exports = role_C_Carrier;