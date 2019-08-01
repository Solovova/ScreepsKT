var creepfn                          = require('modul.creepsfn');
var afunc                            = require('modul.afunc');

var role_C_MinHarvester = {

    /** @param {Creep} creep **/
    run: function(creep) {
        var objRoom = Memory.Data[creep.memory.dstroom];
        if (objRoom == null) return;
        

       
        
        if (creepfn.AlarmMove(creep)==1) return;

        if (creep.room.name != creep.memory.dstroom) {
            creepfn.CreepMoveToDstRoom(creep);
            return;    
        };

        var fMineral = Game.getObjectById(objRoom.Mineral);
        
        if (fMineral == null) return;
        
        var fDistance = afunc.CalcDistancePos(fMineral.pos,creep.pos); 
        
        if (objRoom.MineralCarrierNear == 1 && creep.carry[fMineral.mineralType]>0 && fDistance<3){
            creep.drop(fMineral.mineralType) 
        }else{
            if (_.sum(creep.carry)>=creep.carryCapacity) return;
            if(creep.harvest(fMineral) == ERR_NOT_IN_RANGE) {
                creep.moveTo(fMineral, {visualizePathStyle: {stroke: '#ffaa00'}});
            }
        }
	}
};



module.exports = role_C_MinHarvester;