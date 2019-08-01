var afunc                      = require('modul.afunc');
var messenger                      	= require('modul.messenger');

var role_C_groupranger         = require('role.C_groupranger');              //34
var role_C_grouphealer        = require('role.C_grouphealer');               //35
var role_C_groupmile          = require('role.C_groupmile');                 //36

var modul_groupattack = {

    isHealerBost: function(fBody,fNeedBoost) {
        if (fNeedBoost == 1) {
            for (var j=0;j<fBody.length;j++) {
                if ((fBody[j].type == HEAL)&&(fBody[j].boost!=null)) return 1;
            };
        }else{
            for (var j=0;j<fBody.length;j++) {
                if ((fBody[j].type == HEAL)&&(fBody[j].boost==null)) return 1;
            };
        }
        return 0;
    },

    isHealer: function(fBody) {
        
        for (var j=0;j<fBody.length;j++) {
            if (fBody[j].type == HEAL) return 1;
        };
       
        return 0;
    },

    GetTargetHealer: function(objRoom,fMinDistance){
        //сначала ищем таргет хилера без бустов
        var creepLider = Game.getObjectById(objRoom.AttackGroup.LeaderId);
        fTargetsHeal = Game.rooms[objRoom.Name].find(FIND_HOSTILE_CREEPS);

        //Сначала ищем хилера без бурстов
        fNearestHeal = 1000;
        fTargetHeal = null;
        for (var i=0;i<fTargetsHeal.length;i++){
            if (modul_groupattack.isHealerBost(fTargetsHeal[i].body,0) == 0) continue;
            var fTmpDistance = afunc.CalcDistancePos(fTargetsHeal[i].pos,creepLider.pos);
            if (fTmpDistance<fNearestHeal) {
                fNearestHeal = fTmpDistance;
                fTargetHeal = fTargetsHeal[i];    
            } 
        }
        if (fNearestHeal<=fMinDistance) return fTargetHeal;
        //Сначала ищем хилера с бурстом
        fNearestHeal = 1000;
        fTargetHeal = null;
        for (var i=0;i<fTargetsHeal.length;i++){
            if (modul_groupattack.isHealerBost(fTargetsHeal[i].body,1) == 0) continue;
            var fTmpDistance = afunc.CalcDistancePos(fTargetsHeal[i].pos,creepLider.pos);
            if (fTmpDistance<fNearestHeal) {
                fNearestHeal = fTmpDistance;
                fTargetHeal = fTargetsHeal[i];    
            } 
        }
        if (fNearestHeal<=fMinDistance) return fTargetHeal;
        return null;
    },

    GetTargetLair: function(objRoom,fMinDistance){
        //если рядом защитник то его в первую очередь
        var creepLider = Game.getObjectById(objRoom.AttackGroup.LeaderId);
        fTargets = Game.rooms[objRoom.Name].find(FIND_HOSTILE_CREEPS);

        fNearest = 1000;
        fTarget = null;
        for (var i=0;i<fTargets.length;i++){
            if (fTargets[i].body.length != 50) continue;
            var fTmpDistance = afunc.CalcDistancePos(fTargets[i].pos,creepLider.pos);
            if (fTmpDistance<fNearest) {
                fNearest = fTmpDistance;
                fTarget = fTargets[i];    
            } 
        }
        if (fNearest<=fMinDistance) return fTarget;
        return null;
    },

    setTarget: function(objRoom) {
        
        // if (objRoom.Name == 'W44N4') {
        //     objRoom.AttackGroup.AttackId = '5b373503ce40365aa627c1cd';
        //     return;
        // }


        
        var creepLider = Game.getObjectById(objRoom.AttackGroup.LeaderId);
        if (!creepLider) return;
        if (creepLider.room.name != creepLider.memory.dstroom) return;

        ///вібор минени
        var fNowTarget = Game.getObjectById(objRoom.AttackGroup.AttackId);
        
        //если рядом есть LairKeper то сначала его
        var fLairKeeper = modul_groupattack.GetTargetLair(objRoom,4);
        if (fLairKeeper!=null) {
            objRoom.AttackGroup.AttackId = fLairKeeper.id;
            messenger.log('ATTACK','target nearest lair');
            return;
        }

        if (fNowTarget) {
            if (modul_groupattack.isHealer(fNowTarget.body)==0){
                var targetHealer = modul_groupattack.GetTargetHealer(objRoom,10);
                if (targetHealer!=null) {
                    objRoom.AttackGroup.AttackId = targetHealer.id;
                    messenger.log('ATTACK','target nearest healer');
                    return;       
                } 
            }else{
                return;
            }
        }

        var fTarget = creepLider.pos.findClosestByRange(FIND_HOSTILE_CREEPS);
        messenger.log('ATTACK','target nearest');  
        objRoom.AttackGroup.AttackId = fTarget.id; 
    },


    startinitialisation: function(objRoom) {
        //логика следующая если видим состояние Need ==1 что в комнате нужна атакующая группа, оно может быть установлено в любом месте
        //Flag єтой переменной управляєм мы в этом модуле, если 0 то ничего не делалось, если 1 то группа в производстве, если 2 то группа сделана и атакуем
        //NeedRanged, NeedHealer, NeedMile выставляем тут когда
        messenger.log('ATTACK','ATTACK need (' + objRoom.Describe + ') :' +  objRoom.AttackGroup.Need + ' ready:' + objRoom.AttackGroup.Flag); 
        
        objRoom.AttackGroup.NeedHealer = 0;
        objRoom.AttackGroup.NeedRanger = 0;
        objRoom.AttackGroup.NeedMile = 0;

        objRoom.AttackGroup.LeaderStop = 0;

        if ((objRoom.AttackGroup.Need == 1)&&(objRoom.AttackGroup.Flag == 0)) {
            objRoom.AttackGroup.NeedHealer = 1;
            objRoom.AttackGroup.NeedRanger = 2; 
            objRoom.AttackGroup.AllNeed = objRoom.AttackGroup.NeedHealer+objRoom.AttackGroup.NeedRanger+objRoom.AttackGroup.NeedMile;   
        }

        if ((objRoom.AttackGroup.Flag==0)&&(objRoom.AttackGroup.AllNeed == objRoom.AttackGroup.Have.length)&&(objRoom.AttackGroup.Need == 1)) {
            var fMaxDistance = 0;
            for (var i=1;i<objRoom.AttackGroup.Have.length;i++) {
                var creep0 = Game.getObjectById(objRoom.AttackGroup.Have[i]);
                if (creep0 == null) continue;
                var creep1 = Game.getObjectById(objRoom.AttackGroup.Have[i-1]);
                if (creep1 == null) continue;
                var fDist = afunc.CalcDistancePos(creep0.pos,creep1.pos);
                if (fDist>fMaxDistance) fMaxDistance = fDist;
            }
            //console.log(fMaxDistance);
            //все собрались
            if ((fMaxDistance<4)&&(objRoom.AttackGroup.Have.length>0)) {
                objRoom.AttackGroup.Flag=1;
                for (var i=1;i<objRoom.AttackGroup.Have.length;i++) {
                    var creep = Game.getObjectById(objRoom.AttackGroup.Have[i]);
                    if (creep == null) continue;
                    if ((creep.memory.role==114)||(creep.memory.role==116)) {
                        objRoom.AttackGroup.LeaderId = creep.id;
                        break;    
                    }
                }

            }
        }

        
        //ждем когда все сделаются
        if ((objRoom.AttackGroup.Need == 1)&&(objRoom.AttackGroup.Flag == 1)) {
            //если не все значит когото убили
            if (objRoom.AttackGroup.AllNeed != objRoom.AttackGroup.Have.length){
                objRoom.AttackGroup.Flag = 0;
                return;    
            }
            //сделались
            messenger.log('ATTACK','IN ATTTTAK');
            //AttackId
            if (Game.rooms[objRoom.Name]) {
                modul_groupattack.setTarget(objRoom);
                messenger.log('ATTACK','TARGET:' + objRoom.AttackGroup.AttackId);
            }

            //HealId
            var tId = '';
            var tMinHeal = 0;
            for (var i=0;i<objRoom.AttackGroup.Have.length;i++) {
                var creep = Game.getObjectById(objRoom.AttackGroup.Have[i]);
                if (creep == null) continue;
                if (creep.hits<creep.hitsMax) {
                    if (tMinHeal<(creep.hitsMax-creep.hits)) {
                        tMinHeal=(creep.hitsMax-creep.hits);
                        tId = creep.id;
                    }
                }    
            }
            objRoom.AttackGroup.HealId = tId;
            messenger.log('ATTACK','HEAL:' + objRoom.AttackGroup.HealId);
        }
        
        //turn
        
        for (var i=0;i<objRoom.AttackGroup.Have.length;i++) {
            var creep = Game.getObjectById(objRoom.AttackGroup.Have[i]);
            if (creep == null) continue;
            if (creep.memory.role  == 114) {role_C_groupranger.run(creep)}
            if (creep.memory.role  == 116) {role_C_groupmile.run(creep)}
        }

        for (var i=0;i<objRoom.AttackGroup.Have.length;i++) {
            var creep = Game.getObjectById(objRoom.AttackGroup.Have[i]);
            if (creep == null) continue;
            if (creep.memory.role  == 115) {role_C_grouphealer.run(creep)}
        }
    },

    CalcAllCreeps: function(objRoom){
        if (objRoom.AttackGroup.Need!=1) return;
        objRoom.AttackGroup.Have = [];
        for(var name in Game.creeps) {
            var creep = Game.creeps[name];
            if (creep.memory.dstroom!=objRoom.Name) continue;
            if ((creep.memory.role >= 114)&&(creep.memory.role <= 116)) {
                objRoom.AttackGroup.Have.push(creep.id);
            }
        }
    },

    SetNeed: function(objRoom){
        fNeed = objRoom.IsInvaders;
        //fNeed = 1;

        if ((fNeed==0)&&(objRoom.AttackGroup.Need==1)) {
            //отбились
            objRoom.AttackGroup.Need = fNeed;
            objRoom.AttackGroup.Flag = 0;
            objRoom.AttackGroup.AttackId = '';
            objRoom.AttackGroup.HealId = '';
            objRoom.AttackGroup.LeaderId = ''; 
            
            for (var i=0;i<objRoom.AttackGroup.Have.length;i++) {
                var creep = Game.getObjectById(objRoom.AttackGroup.Have[i]);
                if (creep == null) continue;
                creep.suicide();
            }
        };

        if ((fNeed==1)&&(objRoom.AttackGroup.Need==0)) {
            //напали
            objRoom.AttackGroup.Need = fNeed;
            objRoom.AttackGroup.Flag = 0;
            objRoom.AttackGroup.AttackId = '';
            objRoom.AttackGroup.HealId = '';
            objRoom.AttackGroup.LeaderId = '';    
        };

    },

    GroupAttack: function(){
        
        for (var i=0;i<Memory.SlaivRooms.length;i++) {
            var fRoomName = Memory.SlaivRooms[i];
            
            var objRoom = Memory.Data[fRoomName];
            if (objRoom == null) continue;
            
            if (objRoom.TypeOfRoom != 12) continue;
            try {
                modul_groupattack.CalcAllCreeps(objRoom);
                modul_groupattack.SetNeed(objRoom);
                modul_groupattack.startinitialisation(objRoom);
            }catch(err){
                afunc.ErrorMessage('Group attack:' + objRoom.Name, err);
            }
        }
    },



};

module.exports = modul_groupattack;