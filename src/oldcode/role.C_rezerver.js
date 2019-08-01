var creepfn                          = require('modul.creepsfn');

var role_C_Claim = {

    /** @param {Creep} creep **/
    run: function(creep) {
        var objRoom = Memory.Data[creep.memory.dstroom];
        if (objRoom == null) return;
        
        // if (creep.room.name != creep.memory.dstroom) {
        //     creepfn.CreepMoveToDstRoom(creep);
        //     return;
        // };


        var controller = Game.getObjectById(creep.memory.dst);
        
        if (!controller) {
            if (creep.room.name != creep.memory.dstroom) {
                creepfn.CreepMoveToDstRoom(creep);
                return;
            };
            controller = Game.rooms[creep.memory.dstroom].controller;
        }

        // if (creep.pos.y==49) {creep.moveTo(creep.pos.x,48); return;}
        // if (creep.pos.y==0) {creep.moveTo(creep.pos.x,1);return;}
        // if (creep.pos.x==49) {creep.moveTo(48,creep.pos.y);return;}
        // if (creep.pos.x==0) {creep.moveTo(1,creep.pos.y);return;}

       // if (creep.id =='5b47e1eb5a3f2a7c4f296dbc') creep.memory.path = null;
        // if(!creep.memory.path) {
        //     creep.memory.path = creep.pos.findPathTo(controller,{maxOps: 10000});
        //     //console.log('Reserver path recalculate:' + creep.memory.path.length);
        // }

        // if(creep.memory.path) {
        //     if(creep.memory.path.length <= 5) {
        //         //console.log('ddddddddddddddddddddddddddddddddddd')
        //         creep.memory.path = creep.pos.findPathTo(controller,{maxOps: 10000});
        //         //console.log('Reserver path recalculate:' + creep.memory.path.length);
        //     }
        // }
        
        
        var fErr = creep.reserveController(controller) ;
        
        if(fErr == ERR_NOT_IN_RANGE) {
            //creep.moveByPath(creep.memory.path);
            //console.log('Reserver path:' + creep.memory.path.length);
            creep.moveTo(controller, {visualizePathStyle: {stroke: '#ffffff'}});
        }

        // if(fErr == OK) {
        //     var fSign = 'SoloVova: ' + objRoom.Describe;
        //     var fNeedSign = 0;
        //     if (controller.sign == null) fNeedSign = 1;
        //     if (fNeedSign == 0 && controller.sign!=null) if (fSign != controller.sign.text) fNeedSign = 1;

        //     if (fNeedSign == 1) {
        //         if(creep.signController(controller, fSign) == ERR_NOT_IN_RANGE) {
        //             creep.moveTo(controller, {visualizePathStyle: {stroke: '#ffffff'}});
        //             return;
        //         }
        //     }
        // }
	}
};

module.exports = role_C_Claim;