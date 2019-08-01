var creepfn                          = require('modul.creepsfn');
var afunc                      = require('modul.afunc');

var role_C_Carrier = {
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
        
        if (creepfn.AlarmMove(creep)==1) return;

        if ((objRoom.TypeOfRoom==10)||(objRoom.TypeOfRoom==12)||(objRoom.TypeOfRoom==14)){
            if (!creep.memory.work) {
                if (creep.room.name == creep.memory.dstroom) {
                    var fpos = creep.pos;
                    var target = fpos.findClosestByRange(FIND_DROPPED_RESOURCES, { filter: (o) => o.resourceType === RESOURCE_ENERGY });
                    if (target){
                        if ((afunc.CalcDistancePos(target.pos,fpos)<6)||((creep.memory.goforenergy == 1)&&(afunc.CalcDistancePos(target.pos,fpos)<9))) {
                            creep.memory.goforenergy = 1;
                            if(creep.pickup(target, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                                creep.moveTo(target, {visualizePathStyle: {stroke: '#ffffff'}});
                            }
                            return;   
                        }
                    }

                    var target = fpos.findClosestByRange(FIND_TOMBSTONES, {
                        filter: (structure) => {
                            return structure.store.energy > 0;
                        }
                    });

                    if (target){
                        if ((afunc.CalcDistancePos(target.pos,fpos)<6)||((creep.memory.goforenergy == 1)&&(afunc.CalcDistancePos(target.pos,fpos)<9))) {
                            creep.memory.goforenergy = 1;
                            if(creep.withdraw(target, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) creep.moveTo(target, {visualizePathStyle: {stroke: '#ffffff'}});
                            return;   
                        }
                    }
                }
            }

        }

        creep.memory.goforenergy = 0;

	    if(!creep.memory.work) {
            const container = Game.getObjectById(creep.memory.src);
            if (container == null) return;
            if(creep.withdraw(container, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                creepfn.CreepMoveByPathFinder(creep,container);
            }
        }else{
            var storage = null;
            // if ((objRoom.TypeOfRoom==10)||(objRoom.TypeOfRoom==12)){
            //     var iContainer = objRoom.Containers.indexOf(creep.memory.src)
            //     var storage = Game.getObjectById(objRoom.ContainersWaysToId[iContainer]);
            // }
            if (storage == null) storage = Game.getObjectById(creep.memory.dst);

            if(creep.transfer(storage, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                //build road
                if ((creep.room.name!=creep.memory.srcroom)&&(objRoom.BuildRoadByCarrier==1)) {
                    const _build = creep.room.lookForAt(LOOK_CONSTRUCTION_SITES, creep.pos);
                    if (_build[0]!=null){
                        creep.build(_build[0]);
                        return;    
                    }
                }
                
                
                //repair по дороге до стораже чиним дорогу по которой едем
                if (creep.room.name!=creep.memory.srcroom){
                    const _road = creep.room.lookForAt(LOOK_STRUCTURES, creep.pos);
                    if (_road[0]!=null){
                        if (_road[0].structureType == STRUCTURE_ROAD) {
                            if (_road[0].hits<=(_road[0].hitsMax-100)){
                                creep.repair(_road[0]);   
                            }
                            //если очень поломаная то стоим и чиним
                            if (_road[0].hits<=(_road[0].hitsMax-2000)) return;
                        }
                    }
                }
                //End-repair
                creepfn.CreepMoveByPathFinder(creep,storage);
            }
        }
	}    
};

module.exports = role_C_Carrier;