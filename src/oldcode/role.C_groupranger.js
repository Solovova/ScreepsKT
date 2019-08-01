var creepfn                          = require('modul.creepsfn');
var afunc                      = require('modul.afunc');

var role_C_GroupRanger = {

    run: function(creep) {
        var objRoom = Memory.Data[creep.memory.dstroom];
        if (objRoom == null) return;
        
        //ведомый
        //1) если к атакуемому расстояние меньше 7 то идек к нему и атакуем
        //2) если есть лидер и асстояние больше 1 то идем к нему
        //3) к точке сбора

        //лидер
        //1) если есть кого атаковать то идем и атакуем
        //3) к точке сбора

        //1
        var ImLider = 0;
        if (objRoom.AttackGroup.LeaderId == creep.id) ImLider =1;

        //console.log('  ' + creep.id + ' imlider:' + ImLider);

        if (ImLider == 1) {
            

            if (objRoom.AttackGroup.LeaderStop == 1) return;

            var creepAttack = Game.getObjectById(objRoom.AttackGroup.AttackId);
            if (creepAttack==null){
                if (creep.room.name != creep.memory.dstroom) {
                    creepfn.CreepMoveToDstRoom(creep);
                    return;
                };    
            }
            //console.log(creep.pos.y);
            if (creep.pos.y==49) {creep.moveTo(creep.pos.x,48);return}
            if (creep.pos.y==0) creep.moveTo(creep.pos.x,1);
            if (creep.pos.x==49) creep.moveTo(48,creep.pos.y);
            if (creep.pos.x==0) creep.moveTo(1,creep.pos.y);    
            
            //console.log('Leader attak:'+creepAttack);
            if (creepAttack) {
                var fDist = afunc.CalcDistancePos(creepAttack.pos,creep.pos);
                // if (fDist>9){
                //     creepfn.CreepMoveToNearestFlag(creep,COLOR_GREY,COLOR_GREY);
                //     return;
                // }
                if(creep.rangedAttack(creepAttack) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(creepAttack, {visualizePathStyle: {stroke: '#ffffff'}});
                }
                return;
            } 
            creepfn.CreepMoveToNearestFlag(creep,COLOR_GREY,COLOR_GREY);
        }else{
            var creepAttack = Game.getObjectById(objRoom.AttackGroup.AttackId);
            if (creepAttack) {
                var fDist = afunc.CalcDistancePos(creepAttack.pos,creep.pos);
                if (fDist<6){
                    //if (fDist>2) creep.moveTo(creepAttack, {visualizePathStyle: {stroke: '#ffffff'}});
                    if(creep.rangedAttack(creepAttack) == ERR_NOT_IN_RANGE) {
                        creep.moveTo(creepAttack, {visualizePathStyle: {stroke: '#ffffff'}});
                    }
                    return;
                }
            };
            var creepLider = Game.getObjectById(objRoom.AttackGroup.LeaderId);
            if (creepLider) {
                creep.moveTo(creepLider, {visualizePathStyle: {stroke: '#ffffff'}});
                return;
            }
            creepfn.CreepMoveToNearestFlag(creep,COLOR_GREY,COLOR_GREY);
        }
	}
};

module.exports = role_C_GroupRanger;