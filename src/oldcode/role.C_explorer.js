var creepfn                          = require('modul.creepsfn');

var role_C_Explorer = {

    /** @param {Creep} creep **/
    run: function(creep) {
        //COLOR_GREY COLOR_RED - идем к флагу если наша комната
        //COLOR_GREY COLOR_PURPLE - идем к флагу если не наша комната
        //var objRoom = Memory.Data[creep.memory.dstroom];
        //if (objRoom == null) return;
        
	    if (creep.room.name != creep.memory.dstroom) {
            if (creepfn.CreepMoveToNearestFlag(creep,COLOR_GREY,COLOR_PURPLE) == 0){
                creepfn.CreepMoveToDstRoom(creep);
                return;
            }
            return;
        };
        
       if (creepfn.CreepMoveToNearestFlag(creep,COLOR_GREY,COLOR_RED) == 0) creep.moveTo(25,25);


        // if (creep.memory.dstroom == 'W47N3' ){
        //     var controller = Game.getObjectById('5982fbf1b097071b4adbc6f0');
        //     var fSign = 'SoloVova: Next colonise room.';
        //     creep.signController(controller, fSign)
        // }
        
    }
};

module.exports = role_C_Explorer;