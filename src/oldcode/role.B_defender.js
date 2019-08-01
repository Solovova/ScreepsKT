var creepfn                          = require('modul.creepsfn');

var role_B_defender = {
    run: function(creep) {
        var objRoom = Memory.Data[creep.memory.dstroom];
        if (objRoom == null) return;

        if (creepfn.CreepBoorst(creep,objRoom) == 1) return;

        var fTargets = ['5999a1f87239d645157203aa','5999a1f2da53af70ef3b644e','5999a1ee1c6bc02300ffa021','5999a1e47edd2339fa144503','5999a1e090665139dd1045ff'];

        for (let i = 0;i<fTargets.length;i++){
            var target = Game.getObjectById(fTargets[i]);
            if (target!=null) break;
        }
        
        if(target) {
            if(creep.attack(target) == ERR_NOT_IN_RANGE) {
                creep.moveTo(target, {visualizePathStyle: {stroke: '#ffffff'}});
            }
        }else{
            creepfn.CreepMoveToNearestFlag(creep,COLOR_GREY,COLOR_GREY);
        }
	}
};

module.exports = role_B_defender;