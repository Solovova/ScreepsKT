var A_creeps                   = require('room.A_creeps');
var B_creeps                   = require('room.B_creeps');
var C_creeps                   = require('room.C_creeps');
var afunc                      = require('modul.afunc');
var buildings                  = require('modul.buildings');
var role_Tower                 = require('role.Tower');

var router = {
    CorrectionCripsBySituation: function(objRoom,objSlaveRoom){
        if (objSlaveRoom==null) {
            if (objRoom.TypeOfRoom==1) A_creeps.A_CorrectionCripsBySituation(objRoom);
            if (objRoom.TypeOfRoom==2) B_creeps.B_CorrectionCripsBySituation(objRoom);
        }else{
            if ((objSlaveRoom.TypeOfRoom==10)||(objSlaveRoom.TypeOfRoom==11)||(objSlaveRoom.TypeOfRoom==12)||(objSlaveRoom.TypeOfRoom==13)||(objSlaveRoom.TypeOfRoom==14)) C_creeps.C_CorrectionCripsBySituation(objRoom,objSlaveRoom);
        }
    },

    ShowInfoPre: function(objRoom,objSlaveRoom){
        if (objSlaveRoom==null) {
            if (objRoom.TypeOfRoom==1) A_creeps.A_ShowInfoPre(objRoom);
            if (objRoom.TypeOfRoom==2) B_creeps.B_ShowInfoPre(objRoom);
        }else{
            if ((objSlaveRoom.TypeOfRoom==10)||(objSlaveRoom.TypeOfRoom==11)||(objSlaveRoom.TypeOfRoom==12)||(objSlaveRoom.TypeOfRoom==13)|| (objSlaveRoom.TypeOfRoom==14)) C_creeps.C_ShowInfoPre(objRoom,objSlaveRoom);
        }
    },

    BuildQueue: function(objRoom,objSlaveRoom){
        if (objSlaveRoom==null) {
            if (objRoom.TypeOfRoom==1) return A_creeps.A_BuildQueue(objRoom);
            if (objRoom.TypeOfRoom==2) return B_creeps.B_BuildQueue(objRoom);    
        }else{
            if ((objSlaveRoom.TypeOfRoom==10)||(objSlaveRoom.TypeOfRoom==11)||(objSlaveRoom.TypeOfRoom==12)||(objSlaveRoom.TypeOfRoom==13)||(objSlaveRoom.TypeOfRoom==14)) return C_creeps.C_BuildQueue(objRoom,objSlaveRoom);
        }
    },

    SpawnCreeps: function(objRoom){
        if (objRoom.Queue.length == 0) return;
        if (objRoom.Queue[0][0]==objRoom.Name) {
            if (objRoom.TypeOfRoom==1) A_creeps.A_SpawnCreeps(objRoom);
            if (objRoom.TypeOfRoom==2) B_creeps.B_SpawnCreeps(objRoom);   
        }else{
            objSlaveRoom = Memory.Data[objRoom.Queue[0][0]];
            if ((objSlaveRoom.TypeOfRoom==10)||(objSlaveRoom.TypeOfRoom==11)||(objSlaveRoom.TypeOfRoom==12)||(objSlaveRoom.TypeOfRoom==13)||(objSlaveRoom.TypeOfRoom==14)) C_creeps.C_SpawnCreeps(objRoom,objSlaveRoom);
        }
    },

    ShowInfoPost: function(objRoom){
        if (objRoom.TypeOfRoom==1) A_creeps.A_ShowInfoPost(objRoom);
        if (objRoom.TypeOfRoom==2) B_creeps.B_ShowInfoPost(objRoom);
    },

    BuildAndSpawn: function(){
        for (let i = 0; i < Memory.MainRooms.length; i++){
            var objRoom = Memory.Data[Memory.MainRooms[i]];
            objRoom.CalcForQueue = objRoom.CalcRoleCreeps.slice();
        };

        for (let i = 0; i < Memory.SlaivRooms.length; i++){
            var objRoom = Memory.Data[Memory.SlaivRooms[i]];
            objRoom.CalcForQueue = objRoom.CalcRoleCreeps.slice();
        };


        for (let i = 0; i < Memory.MainRooms.length; i++){
            try{
                var objRoom = Memory.Data[Memory.MainRooms[i]];
    
                buildings.Building(objRoom);

                //var fSpawn = afunc.GetSpawnRoom(objRoom);
                //if (fSpawn==null) continue;

                router.CorrectionCripsBySituation(objRoom);
                router.ShowInfoPre(objRoom);
    
                for (let j = 0; j < objRoom.SlaivRooms.length; j++){
                    try{
                        objSlaivRoom = Memory.Data[objRoom.SlaivRooms[j]];
                        buildings.Building(objSlaivRoom);
                        router.CorrectionCripsBySituation(objRoom,objSlaivRoom);
                        router.ShowInfoPre(objRoom,objSlaivRoom);
                        if (objSlaivRoom.TypeOfRoom == 11) role_Tower.RunTower(objSlaivRoom);
                    } catch(err){
                        afunc.ErrorMessage('ERROR:Slave room error:' + Memory.MainRooms[i] + ' slave:' + objRoom.SlaivRooms[j], err);
                    }
                }
            
                afunc.Need0ToNeed(objRoom);
                router.BuildQueue(objRoom);
    
                for (let j = 0; j < objRoom.SlaivRooms.length; j++){
                    try{
                        objSlaivRoom = Memory.Data[objRoom.SlaivRooms[j]];
                        afunc.Need0ToNeed(objSlaivRoom);
                        router.BuildQueue(objRoom,objSlaivRoom);
                    } catch(err){
                        afunc.ErrorMessage('ERROR:Slave room error block 0:' + Memory.MainRooms[i] + ' slave:' + objRoom.SlaivRooms[j], err);
                    }    
                }
    
                afunc.Need1ToNeed(objRoom);
                router.BuildQueue(objRoom);
    
                for (let j = 0; j < objRoom.SlaivRooms.length; j++){
                    try{
                        objSlaivRoom = Memory.Data[objRoom.SlaivRooms[j]];
                        afunc.Need1ToNeed(objSlaivRoom);
                        router.BuildQueue(objRoom,objSlaivRoom);
                    } catch(err){
                        afunc.ErrorMessage('ERROR:Slave room error block 1:' + Memory.MainRooms[i] + ' slave:' + objRoom.SlaivRooms[j], err);
                    }    
                }

                afunc.Need2ToNeed(objRoom);
                router.BuildQueue(objRoom);
    
                for (let j = 0; j < objRoom.SlaivRooms.length; j++){
                    try{
                        objSlaveRoom = Memory.Data[objRoom.SlaivRooms[j]];
                        afunc.Need2ToNeed(objSlaveRoom);
                        router.BuildQueue(objRoom,objSlaveRoom);
                    } catch(err){
                        afunc.ErrorMessage('ERROR:Slave room error block 2:' + Memory.MainRooms[i] + ' slave:' + objRoom.SlaivRooms[j], err);
                    }     
                }
            
                router.SpawnCreeps(objRoom);
                router.ShowInfoPost(objRoom);
                
                role_Tower.RunTower(objRoom);
            } catch(err){
                afunc.ErrorMessage('ERROR:Master room error:' + Memory.MainRooms[i], err);
            }
        }
    }
};

module.exports = router;