var creepfn                          = require('modul.creepsfn');

var role_C_Claim = {

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
        
        var controller = Game.getObjectById(creep.memory.dst);
        
        if (!controller) {
            if (creep.room.name != creep.memory.dstroom) {
                creepfn.CreepMoveToDstRoom(creep);
                return;
            };
            controller = Game.rooms[creep.memory.dstroom].controller;
        }
        
        
        if(creep.claimController(controller) == ERR_NOT_IN_RANGE) {
            creep.moveTo(controller, {visualizePathStyle: {stroke: '#ffffff'}});
        }        
	}
};

module.exports = role_C_Claim;