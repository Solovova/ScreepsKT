var afunc                               = require('modul.afunc');
var creepfn                             = require('modul.creepsfn');

var role_C_DWarrior = {

    /** @param {Creep} creep **/
    run: function(creep) {
        var objRoom = Memory.Data[creep.memory.dstroom];
        if (objRoom == null) return;

        if (creep.hits<creep.hitsMax) creep.heal(creep);
   
        if (creepfn.AlarmMoveWarrior(creep)==1) return;
   
        //ведем в дальнюю комнату
	    if (creep.room.name != creep.memory.dstroom) {
            const exitDir = creep.room.findExitTo(creep.memory.dstroom);
            const texit = creep.pos.findClosestByPath(exitDir);
            creep.moveTo(texit);
            return;
        };

        if (creep.pos.y==49) {creep.moveTo(creep.pos.x,48);return}
        if (creep.pos.y==0) creep.moveTo(creep.pos.x,1);
        if (creep.pos.x==49) creep.moveTo(48,creep.pos.y);
        if (creep.pos.x==0) creep.moveTo(1,creep.pos.y);

        if ((creep.memory.role <1000) & (creep.ticksToLive < 300 )){
            creep.memory.role = creep.memory.role + 1000;
        };

        var mLair = [];
        mLair.push( Game.getObjectById(objRoom.KeperLair[0]));
        mLair.push( Game.getObjectById(objRoom.KeperLair[1]));
        mLair.push( Game.getObjectById(objRoom.KeperLair[2]));
        //ДОРАБОТАТЬ если реально добываем тогда
        if (objRoom.GatherMineralNow  == 1) mLair.push( Game.getObjectById(objRoom.KeperLair[3]));
        
        //ищем ближайшего противника который вышел из моего mLair 
        var targets = Game.rooms[creep.memory.dstroom].find(FIND_HOSTILE_CREEPS);
        //console.log('dwar:'+creep.memory.dstroom + '  ' + targets.length)
        var target = afunc.GetIndexOfNearestObjectNearObjects(creep.pos,targets,mLair)


        if (target) {
            var fDistance = afunc.CalcDistancePos(target.pos,creep.pos);
            
            if (creep.hits<creep.hitsMax){
                if(fDistance>10){
                    creep.moveTo(target, {visualizePathStyle: {stroke: '#ffffff'}});    
                };
                //стоим
                if ((fDistance<10)&&(fDistance>7)) return;
                //атакуем
                if (fDistance<7){
                    if(creep.attack(target) == ERR_NOT_IN_RANGE) {
                        creep.moveTo(target, {visualizePathStyle: {stroke: '#ffffff'}});
                    }   
                }
            }else{
                if(creep.attack(target) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(target, {visualizePathStyle: {stroke: '#ffffff'}});
                }    
            }
        }else{
            //идем к контролируему источнику у которого спавн меньше
            var fInd = -1;
            var fSpawn = 4000;
            for (var i=0;i<mLair.length;i++){
                if (mLair[i].ticksToSpawn<fSpawn){
                    fInd=i;
                    fSpawn=mLair[i].ticksToSpawn;   
                }
            }   
            creep.moveTo(mLair[fInd], {visualizePathStyle: {stroke: '#ffffff'}});   
        }
	}
};

module.exports = role_C_DWarrior;