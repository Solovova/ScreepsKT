var creepfn                          = require('modul.creepsfn');
var afunc                      = require('modul.afunc');

var role_C_GroupHealer = {

    run: function(creep) {
        var objRoom = Memory.Data[creep.memory.dstroom];
        if (objRoom == null) return;


        //1) если есть лидер и расстояние к нему больше 1 то идти к нему
        //2) если есть кого лечить то идем к нему если расстояние больше 1 и лечим
        //3) если нет то идем к флагу сбора

        //objRoom.AttackGroup.AttackId == null)
		//	if (Memory.Data[fRoomName].AttackGroup.HealId == null)		    Memory.Data[fRoomName].AttackGroup.HealId 		= '';
		//	if (Memory.Data[fRoomName].AttackGroup.LeaderId == null)	
        
        var creepHeal = Game.getObjectById(objRoom.AttackGroup.HealId);
        var creepLider = Game.getObjectById(objRoom.AttackGroup.LeaderId);
        if (creepHeal) {
            var fDist = afunc.CalcDistancePos(creepHeal.pos,creep.pos);
            if (fDist>1) {
                creep.moveTo(creepHeal, {visualizePathStyle: {stroke: '#ffffff'}}); 
            }else{
                if (creepLider) creep.moveTo(creepLider, {visualizePathStyle: {stroke: '#ffffff'}}); 
            }    

            creep.heal(creepHeal);
            return;
        }
        
        if (creepLider) {
            creep.moveTo(creepLider, {visualizePathStyle: {stroke: '#ffffff'}});
            return;
        }

        //2
        creepfn.CreepMoveToNearestFlag(creep,COLOR_GREY,COLOR_GREY);

        
	}
};

module.exports = role_C_GroupHealer;