var creepfn                          = require('modul.creepsfn');

var role_C_Warrior = {

    /** @param {Creep} creep **/
    run: function(creep) {
        var objRoom = Memory.Data[creep.memory.dstroom];
        if (objRoom == null) return;
        
        //ведем в дальнюю комнату

        if (creep.room.name != creep.memory.dstroom) {
            creepfn.CreepMoveToDstRoom(creep);
            return;
        };

        if (creep.pos.y==49) {creep.moveTo(creep.pos.x,48);return}
        if (creep.pos.y==0) creep.moveTo(creep.pos.x,1);
        if (creep.pos.x==49) creep.moveTo(48,creep.pos.y);
        if (creep.pos.x==0) creep.moveTo(1,creep.pos.y);  
        
        
        var target = creep.pos.findClosestByRange(FIND_HOSTILE_CREEPS);
        if (creep.hits<creep.hitsMax) creep.heal(creep);    
        if(target) {
            if(creep.rangedAttack(target) == ERR_NOT_IN_RANGE) {
                creep.moveTo(target, {visualizePathStyle: {stroke: '#ffffff'}});
            }
        }else{
            creepfn.CreepMoveToNearestFlag(creep,COLOR_GREY,COLOR_GREY);
        }
	}
};

module.exports = role_C_Warrior;