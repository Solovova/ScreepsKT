var afunc                      = require('modul.afunc');
var sf                      	= require('modul.smallfunc');

var creepfn = {
    CreepMoveByPathFinder: function(creep,fTarget) {
        if (Memory.constants.UseFastTravel != 1){
            creep.moveTo(fTarget, {visualizePathStyle: {stroke: '#ffffff'}});
            return;
        };

        var objRoom = Memory.Data[creep.memory.dstroom];
        if (objRoom == null) return;

        var iContainer = objRoom.Containers.indexOf(creep.memory.src)
        var idContainer = creep.memory.src;
        if (iContainer == -1) { //if carrier in 2 room to upgrader container
            var iContainer = objRoom.Containers.indexOf(creep.memory.dst)
            var idContainer = creep.memory.dst;
        }


        var fPath = objRoom.ContainersWaysToStorage[iContainer]
        if (fPath !=null) {
            if (fTarget.id == idContainer) {
                //идем к контейнеру
                var fAppend = -1;
                var fStart = creep.memory.currentpos;
                if (fStart==null) fStart=fPath.length-1;
                var fEnd = 1;
            }else{
                var fAppend = 1;
                var fStart = creep.memory.currentpos;
                if (fStart==null) fStart=0;
                var fEnd = fPath.length - 2;
            }
            if (creep.memory.stay == null) creep.memory.stay = 0;
        
            for (let i=fStart;(i<=fEnd && fAppend==1)||(i>=fEnd && fAppend==-1);i=i+fAppend){
                if (fPath[i].roomName!=creep.room.name) continue;
                if (fPath[i].x!=creep.pos.x) continue;
                if (fPath[i].y!=creep.pos.y) continue;
                if ((creep.memory.prevposx == creep.pos.x)&&(creep.memory.prevposy == creep.pos.y)) {
                    creep.memory.stay++;
                }else{
                    creep.memory.stay = 0;
                }
                if (creep.memory.stay>2){
                    //console.log('>>>>>>>>>>>>>move long stay room:'+ creep.room.name+ ' id:'+creep.id); 
                    if (fAppend == 1){
                        creep.memory.currentpos = 0;
                    }else{
                        creep.memory.currentpos = fPath.length-1;
                    }   
                    creep.moveTo(fTarget);
                }else{
                    creep.memory.prevposx = creep.pos.x;
                    creep.memory.prevposy = creep.pos.y;
                    creep.moveTo(fPath[i+fAppend].x, fPath[i+fAppend].y);
                    creep.memory.currentpos = (i+fAppend);
                }
                return;
            }
            //console.log('>>>>>>>>>>>>>move not in path' + creep.room.name+ ' id:'+creep.id);
            if (fAppend == 1){
                creep.memory.currentpos = 0;
            }else{
                creep.memory.currentpos = fPath.length-1;
            }
            creep.moveTo(fTarget);
        }else{
            console.log('>>>>>>>>>>>>>move havnt path room:' + creep.memory.dstroom + ' target id:' + fTarget.id);
            creep.moveTo(fTarget);
        }
    },

    CreepMoveByPathToRoom: function(creep,fPath) {
        if (fPath !=null) {
            for (let i=0;i<=fPath.length - 2;i++){
                if (fPath[i].roomName!=creep.room.name) continue;
                if (fPath[i].x!=creep.pos.x) continue;
                if (fPath[i].y!=creep.pos.y) continue;
                creep.moveTo(fPath[i+1].x, fPath[i+1].y);
                console.log('mooove ok')
                return;
            }
            creep.moveTo(fPath[0].x,fPath[0].y);
        }
    },

	CreepMoveToNearestFlag: function(creep,fColor,fSecondaryColor) {
        var target = creep.pos.findClosestByRange(FIND_FLAGS, {
            filter: (structure) => {
                return ((structure.color == fColor)&&(structure.secondaryColor == fSecondaryColor));
            }
        });
        
        if (target) {
            creep.moveTo(target);
            return 1;
        }
        return 0;
	},
	
	CreepMoveToDstRoom: function(creep) {
        if (creep.memory.dstroom =="W44N7") {
            if (creep.room.name == "W43N6") {
                const exitDir = creep.room.findExitTo("W43N7");
                const texit = creep.pos.findClosestByPath(exitDir);
                creep.moveTo(texit);
                return;
            }
        }

        const exitDir = creep.room.findExitTo(creep.memory.dstroom);
        const texit = creep.pos.findClosestByPath(exitDir);
        creep.moveTo(texit);
    },

    CreepMoveToDangerWay: function(creep,fColor,fSecondaryColor,fRoomName) {
        if (creep.memory.dstroom != 'W46N7') return 0;
        if (creep.memory.dstroom == creep.room.name) return 0;
        if (fRoomName!=creep.room.name) {
            const exitDir = creep.room.findExitTo(creep.memory.dstroom);
            const texit = creep.pos.findClosestByPath(exitDir);
            creep.moveTo(texit);
            return 1;
        }

        var target = creep.pos.findClosestByRange(FIND_FLAGS, {
            filter: (structure) => {
                return ((structure.color == fColor)&&(structure.secondaryColor == fSecondaryColor));
            }
        });
        
        if (target) {
            creep.moveTo(target);
            return 1;
        }
        return 0;
	},

    AlarmMove: function(creep) {
        var objRoom = Memory.Data[creep.memory.dstroom];
        if (objRoom == null) return;

        if (objRoom.TypeOfRoom==14){
            var objCheckpointRoom = Memory.Data[objRoom.CheckpointRoom];
            if (objCheckpointRoom.IsInvaders == 1 || objRoom.IsInvaders == 1) return 1;
        }

        if ((objRoom.TypeOfRoom!=12)&&(objRoom.TypeOfRoom!=10)) return 0;

        if (objRoom.IsInvaders == 1) {
            if (creep.memory.srcroom == creep.room.name) {
                creepfn.CreepMoveToNearestFlag(creep,COLOR_WHITE,COLOR_WHITE);
                return 1;
            } 
            const exitDir = creep.room.findExitTo(creep.memory.srcroom);
            const texit = creep.pos.findClosestByPath(exitDir);
            creep.moveTo(texit);
            return 1;   
        }

        if (objRoom.TypeOfRoom==12) {

            if (creep.memory.role == 117 || creep.memory.role == 118){
                var lair = Game.getObjectById(objRoom.KeperLair[3])
            }else{
                var source = Game.getObjectById(creep.memory.src);
                if (!source) return 1;
                var lair = afunc.FindStructureNearPos(source.pos,STRUCTURE_KEEPER_LAIR,6);  
            }
            
        
            if (lair){
                if (lair.ticksToSpawn<20) {
                    creepfn.CreepMoveToNearestFlag(creep,COLOR_WHITE,COLOR_WHITE);
                    return 1;
                }
            }

            var target = afunc.FindHostileCreepsNearPos(lair.pos,7);
            if (target){
                creepfn.CreepMoveToNearestFlag(creep,COLOR_WHITE,COLOR_WHITE); 
                return 1;   
            } 
        }
    },

    AlarmMoveWarrior: function(creep) {
        var objRoom = Memory.Data[creep.memory.dstroom];
        if (objRoom == null) return;

        if ((objRoom.TypeOfRoom!=12)&&(objRoom.TypeOfRoom!=10)) return 0;

        if (objRoom.IsInvaders == 1) {
            if (creep.memory.srcroom == creep.room.name) {
                creepfn.CreepMoveToNearestFlag(creep,COLOR_WHITE,COLOR_WHITE);
                return 1;
            } 
            const exitDir = creep.room.findExitTo(creep.memory.srcroom);
            const texit = creep.pos.findClosestByPath(exitDir);
            creep.moveTo(texit);
            return 1;   
        }
    },

    CreepBoorst: function(creep,objRoom) {
        //return 0;

        if (creep.memory.needboorst != 1)   return 0;
        if (creep.memory.boorsted != null)  return 0;
        if (creep.spawning) return 0;

        if (objRoom.NeedBoorst == ''){
            creep.memory.boorsted = 'error not need borst';
            return 0;    
        }
       
        //если нет минерала или еще не его очередь то идем к отстойнику
        var fLab = Game.getObjectById(objRoom.LabsReaction[0]);
        if (fLab == null) {
            creep.memory.boorsted = 'no lab';
            return 0;
        }

        if (objRoom.NeedBoorstId != creep.id){
            creepfn.CreepMoveToNearestFlag(creep,COLOR_WHITE,COLOR_GREY);
            return 1;
        }

        
        var fHaveMineral = sf.GetMineralInLab(fLab,objRoom.NeedBoorst);
        if (fHaveMineral<objRoom.NeedBoorstQuantity){
            creepfn.CreepMoveToNearestFlag(creep,COLOR_WHITE,COLOR_GREY);
            return 1;
        }

        fErr = fLab.boostCreep(creep);
        if (fErr == ERR_NOT_IN_RANGE) {
            creep.moveTo(fLab, {visualizePathStyle: {stroke: '#ffffff'}});
            return 1;
        };
        creep.memory.boorsted = fErr;
        objRoom.NeedBoorst = '';
        objRoom.NeedBoorstId = '';
        objRoom.NeedBoorstQuantity = 0;

    },
	
	
};

module.exports = creepfn;