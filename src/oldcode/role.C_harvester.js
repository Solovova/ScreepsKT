var creepfn                          = require('modul.creepsfn');
var afunc                            = require('modul.afunc');

var role_C_Harvester = {

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

        //src - источник добычи
        //dst - куда скидываем
        
        //если жить осталось меньше 150 то переводим роль чтоб уже начал делатся новый
        if ((creep.memory.role <1000) && (creep.ticksToLive < 300 )){
            creep.memory.role = creep.memory.role + 1000;
        };

        if ((creep.memory.role <1000) && (creep.ticksToLive < 400 ) && creep.room.name == 'W47N3'){
            creep.memory.role = creep.memory.role + 1000;
        };

        //if (creepfn.CreepMoveToDangerWay(creep,COLOR_BROWN,COLOR_BROWN,'W46N6')==1) return;
        
        if (creepfn.AlarmMove(creep)==1) return;

        // if (creep.room.name != creep.memory.dstroom) {
        //     creepfn.CreepMoveToDstRoom(creep);
        //     return;
        // };

        if(creep.memory.work && creep.carry.energy == 0) {
            creep.memory.work = false;
            creep.say('harvest');
        }
        
        var fNeedEnergy = creep.carryCapacity;
        if (creep.memory.info !=null) fNeedEnergy=creep.memory.info;
	    if(!creep.memory.work && (creep.carry.energy >= fNeedEnergy)) {
	        creep.memory.work = true;
	        creep.say('work');
	    }

	    if(!creep.memory.work) {
            const source = Game.getObjectById(creep.memory.src);
            if(creep.harvest(source) == ERR_NOT_IN_RANGE) {
                creep.moveTo(source, {visualizePathStyle: {stroke: '#ffaa00'}});
            }
        }
        else {
            if (creep.memory.dst==null) {
                creep.memory.dst = objRoom.Containers[objRoom.Sources.indexOf(creep.memory.src)]
            }
            var container = Game.getObjectById(creep.memory.dst);

            if (!container) {
                var target = creep.pos.findClosestByRange(FIND_CONSTRUCTION_SITES);

                var fDistance = 1000;
                if (target) fDistance = afunc.CalcDistancePos(target.pos,creep.pos);
                if (fDistance>3) {
                    const source = Game.getObjectById(creep.memory.src);
                    if (afunc.CalcDistancePos(source.pos,creep.pos)==1){
                        Game.rooms[objRoom.Name].createConstructionSite(creep.pos, STRUCTURE_CONTAINER);
                        return;
                    }else{
                        //console.log('mooooooov')
                        creep.moveTo(source, {visualizePathStyle: {stroke: '#ffaa00'}});
                        return;
                    }
                }


                if(creep.build(target) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(target, {visualizePathStyle: {stroke: '#ffffff'}});
                }
                return;
            }
            
            if (container.hits > 240000) { 
                if(creep.transfer(container, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(container, {visualizePathStyle: {stroke: '#ffffff'}});
                }
            }else{
                if(creep.repair(container) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(container, {visualizePathStyle: {stroke: '#ffffff'}});
                    
                }    
            }
        }
	}
};



module.exports = role_C_Harvester;