var role_A_Harvester = {

    /** @param {Creep} creep **/
    run: function(creep) {
        var objRoom = Memory.Data[creep.memory.dstroom];
        if (objRoom == null) return;
        
        //src - источник добычи
        //dst - куда скидываем
        
        //узнать есть ли контейнер
        //узнать есть ли строй площадка рядом с соурке
        //вычислить оптимальное добывание
        
        //если жить осталось меньше 150 то переводим роль чтоб уже начал делатся новый
        if ((creep.memory.role <1000) & (creep.ticksToLive < 100 )){
            creep.memory.role = creep.memory.role + 1000;
	    };
        
        if(creep.memory.work && creep.carry.energy == 0) {
            creep.memory.work = false;
            creep.say('harvest');
        }
        
        var fNeedEnergy = creep.carryCapacity;
        if (creep.memory.info !=null) fNeedEnergy=creep.memory.info;
	    if(!creep.memory.work && (creep.carry.energy >= (fNeedEnergy))) {
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
                if(creep.build(target) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(target, {visualizePathStyle: {stroke: '#ffffff'}});
                }
            }else{
                if (container.hits > 100000) { 
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
	}
};



module.exports = role_A_Harvester;