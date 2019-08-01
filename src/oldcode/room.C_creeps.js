var afunc                      = require('modul.afunc');

var C_creeps = {
    C_GetHarvesterBody: function(objMasterRoom,objSlaveRoom) {
        if (objSlaveRoom.TypeOfRoom == 12) {
            return [MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,CARRY,CARRY,CARRY,CARRY];    
        }
        if (objMasterRoom.MaxSpawnEnergy >= 1000) return [WORK, WORK, WORK, WORK, WORK, WORK, WORK, CARRY, CARRY, MOVE, MOVE, MOVE, MOVE];
        if (objMasterRoom.MaxSpawnEnergy >= 800) return [MOVE,MOVE,WORK,WORK,WORK,WORK,WORK,WORK,CARRY,CARRY];
    },

    C_GetHarvesterInfo: function(objMasterRoom,objSlaveRoom) {
        if (objSlaveRoom.TypeOfRoom  == 12) {
            return 196;    
        }
        if (objMasterRoom.MaxSpawnEnergy >= 1000) return 98;
        if (objMasterRoom.MaxSpawnEnergy >= 800) return 96;
    },

    C_GetWorkerBody: function(objMasterRoom,objSlaveRoom) {
        if (objMasterRoom.MaxSpawnEnergy  >= 2100) return [MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,WORK,WORK,WORK,WORK,WORK,WORK,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY]
        return [WORK, WORK, WORK, CARRY, CARRY, CARRY, CARRY, MOVE, MOVE, MOVE, MOVE]; 
        
        
    },
    
    C_GetCarrierBody: function(objMasterRoom,objSlaveRoom,indContainer) {
        return objSlaveRoom.ContainersWaysCarrierNeed[indContainer].BodyCarriers;
    },

    C_GetWarriorBody: function(objMasterRoom,objSlaveRoom) {
        return [MOVE,MOVE,MOVE,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,HEAL]
    },

    C_GetDWarriorBody: function(objMasterRoom,objSlaveRoom) {
        return [MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,HEAL,HEAL,HEAL,HEAL,HEAL];
    },

    C_GetGroupRangerBody: function(objMasterRoom,objSlaveRoom) {
        return [TOUGH,TOUGH,TOUGH,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK];
    },

    C_GetGroupHealerBody: function(objMasterRoom,objSlaveRoom) {
        return [MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,HEAL,HEAL,HEAL,HEAL,HEAL,HEAL,HEAL,HEAL,HEAL,HEAL,HEAL,HEAL,HEAL,HEAL,HEAL]
    },

    C_GetGroupMileBody: function(objMasterRoom,objSlaveRoom) {
        return [TOUGH,TOUGH,TOUGH,TOUGH,TOUGH,TOUGH,TOUGH,TOUGH,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK];
    },

    C_GetMinHarvesterBody: function(objMasterRoom,objSlaveRoom) {
        return [MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY];
    },

    C_GetMinCarrierBody: function(objMasterRoom,objSlaveRoom) {
        return [MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY];
    },


    C_CorrectionCripsBySituation: function(objMasterRoom,objSlaveRoom) {
        if (objSlaveRoom.TypeOfRoom==13) {
            objSlaveRoom.Need1[10] = 1;
        }
       
        if (objSlaveRoom.TypeOfRoom==11) {
            if (objSlaveRoom.NotColonise == 1) {
                objSlaveRoom.Need1[10] = 1;
                return;
            }
            
            if (Game.rooms[objSlaveRoom.Name]==null) {
                //больше ничего не выпускаем потому что комнаты не видно
                for (let i=0;i<objSlaveRoom.Need1.length;i++) objSlaveRoom.Need1[i]=0;
                objSlaveRoom.Need1[9] = 1;
                return;
            };

            //1) если видим комнату но она не наша то нужен колонизатор
            if (!Game.rooms[objSlaveRoom.Name].controller.my) objSlaveRoom.Need1[9] = 1;

            //2) если комната по колонизации то нисильшики не нужны
            objSlaveRoom.Need1[2] = 0;
            objSlaveRoom.Need1[4] = 0;
            objSlaveRoom.Need1[6] = 0;

            //3) проверяем сколько источников для добычи столько выпускаем добытчиков
            if (objSlaveRoom.Sources.length>=1) objSlaveRoom.Need1[1] = 1;
            if (objSlaveRoom.Sources.length>=2) objSlaveRoom.Need1[3] = 1;
            if (objSlaveRoom.Sources.length>=3) objSlaveRoom.Need1[5] = 1;

            //4) выпускаем строителей в два раза больше чем добытчиков
            objSlaveRoom.Need1[7] = (objSlaveRoom.Need1[1]+objSlaveRoom.Need1[3]+objSlaveRoom.Need1[5])*3;
            
            //5) если нет ниодного контейнера то не выпускаем строителей
            if ((objSlaveRoom.Containers[0]==null)&& (objSlaveRoom.Containers[1]==null)&&(objSlaveRoom.Containers[2]==null)) objSlaveRoom.Need1[7]=0;

            //6) если есть строители то выпускаем одного апдейтреа
            //if (objSlaveRoom.Need1[7]!=0) objSlaveRoom.Need1[11]=1;

            //7) воина проверяем
            var Hostile = Game.rooms[objSlaveRoom.Name].find(FIND_HOSTILE_CREEPS);
            if (Hostile.length > 0) objSlaveRoom.Need1[0] = 1;
        };

        if (objSlaveRoom.TypeOfRoom==10) {
            //7) воина проверяем
            if (objSlaveRoom.IsInvaders == 1) { 
                for (let i=1;i<objSlaveRoom.Need0.length;i++) {
                    objSlaveRoom.Need0[i]=0;
                    objSlaveRoom.Need1[i]=0;
                    objSlaveRoom.Need2[i]=0;
                };
                objSlaveRoom.Need0[0] = 1;
                return;
            }


            if (Game.rooms[objSlaveRoom.Name]==null) {
                //больше ничего не выпускаем потому что комнаты не видно
                for (let i=0;i<objSlaveRoom.Need1.length;i++) objSlaveRoom.Need1[i]=0;
                objSlaveRoom.Need1[8] = 1;
                objSlaveRoom.Need1[10] = 1;
                return;
            };



            //1) если резервация меньше то резервируем дальше
            if (!Game.rooms[objSlaveRoom.Name].controller.reservation){
                objSlaveRoom.Need1[8] = 1;
            }
            if (Game.rooms[objSlaveRoom.Name].controller.reservation){
                if (Game.rooms[objSlaveRoom.Name].controller.reservation.ticksToEnd<3000) {
                    objSlaveRoom.Need1[8] = 1;
                }
            }

            //2) проверяем сколько источников для добычи столько выпускаем добытчиков
            if (objSlaveRoom.Sources.length>=1) objSlaveRoom.Need1[1] = 1;
            if (objSlaveRoom.Sources.length>=2) objSlaveRoom.Need1[3] = 1;
            if (objSlaveRoom.Sources.length>=3) objSlaveRoom.Need1[5] = 1;

            //3) если чтото там строится то выпускаем строителей в два раза больше чем добытчиков
            if ((objSlaveRoom.IsBuild == 1)&&(objSlaveRoom.BuildRoadByCarrier!=1)) {
                var fNeedBuilder = ( objSlaveRoom.Need1[1]+ objSlaveRoom.Need1[3]+ objSlaveRoom.Need1[5])*2;
                objSlaveRoom.Need1[7] = fNeedBuilder;
            };
            
            //4) если нет ниодного контейнера то не выпускаем строителей
            if ((objSlaveRoom.Containers[0]==null)&& (objSlaveRoom.Containers[1]==null)&&(objSlaveRoom.Containers[2]==null)) objSlaveRoom.Need1[7]=0;

            //5 если контейнера есть то запускаем носильщиков
            if (objSlaveRoom.Containers[0]==null) objSlaveRoom.Need1[2] = 0;
            if (objSlaveRoom.Containers[1]==null) objSlaveRoom.Need1[4] = 0;
            if (objSlaveRoom.Containers[2]==null) objSlaveRoom.Need1[6] = 0;

            if ((objSlaveRoom.Containers[0]!=null) &&(objSlaveRoom.Need1[2]==0)) objSlaveRoom.Need1[2] = objSlaveRoom.ContainersWaysCarrierNeed[0].NeedCarriers;
            if ((objSlaveRoom.Containers[1]!=null) &&(objSlaveRoom.Need1[4]==0)) objSlaveRoom.Need1[4] = objSlaveRoom.ContainersWaysCarrierNeed[1].NeedCarriers;
            if ((objSlaveRoom.Containers[2]!=null) &&(objSlaveRoom.Need1[6]==0)) objSlaveRoom.Need1[6] = objSlaveRoom.ContainersWaysCarrierNeed[2].NeedCarriers;
            
            //6) если чтото строим то носильщиков не пускаем
            if ((objSlaveRoom.IsBuild == 1)&&(objSlaveRoom.BuildRoadByCarrier!=1)) {
                objSlaveRoom.Need1[2] = 0;
                objSlaveRoom.Need1[4] = 0;
                objSlaveRoom.Need1[6] = 0;
            };



        };


        if (objSlaveRoom.TypeOfRoom==12) {
            
            if (objSlaveRoom.AttackGroup.Need == 1) {
                for (let i=0;i<objSlaveRoom.Need0.length;i++) objSlaveRoom.Need0[i]=0;
                for (let i=0;i<objSlaveRoom.Need1.length;i++) objSlaveRoom.Need1[i]=0;
                for (let i=0;i<objSlaveRoom.Need2.length;i++) objSlaveRoom.Need2[i]=0;
                objSlaveRoom.Need0[14] = objSlaveRoom.AttackGroup.NeedRanger;
                objSlaveRoom.Need0[15] = objSlaveRoom.AttackGroup.NeedHealer;
                objSlaveRoom.Need0[16] = objSlaveRoom.AttackGroup.NeedMile;
                return;
            }
            
            if (Game.rooms[objSlaveRoom.Name]==null) {
                //больше ничего не выпускаем потому что комнаты не видно
                for (let i=0;i<objSlaveRoom.Need1.length;i++) objSlaveRoom.Need1[i]=0;
                objSlaveRoom.Need1[10] = 1;
                return;
            };

            // //0) инвадеры но не атакуем, никого не выпускаем
            
            if (objSlaveRoom.IsInvaders == 1) {
                for (let i=0;i<objSlaveRoom.Need0.length;i++) objSlaveRoom.Need0[i]=0;
                for (let i=0;i<objSlaveRoom.Need1.length;i++) objSlaveRoom.Need1[i]=0;
                for (let i=0;i<objSlaveRoom.Need2.length;i++) objSlaveRoom.Need2[i]=0;
                objSlaveRoom.Need1[10] = 1;
                return;
            }

            //1) всегда там держим 2-х воинов
            objSlaveRoom.Need1[12] = 1;
            objSlaveRoom.Need1[13] = 0;

            //2) проверяем сколько источников для добычи столько выпускаем добытчиков                        
            if (objSlaveRoom.Sources.length>=1) objSlaveRoom.Need1[1] = 1;
            if (objSlaveRoom.Sources.length>=2) objSlaveRoom.Need1[3] = 1;
            if (objSlaveRoom.Sources.length>=3) objSlaveRoom.Need1[5] = 1;

            //3) если чтото там строится то выпускаем строителей в два раза больше чем добытчиков
            
            if ((objSlaveRoom.IsBuild == 1)&&(objSlaveRoom.BuildRoadByCarrier!=1)) {
                var fNeedBuilder = ( objSlaveRoom.Need1[1]+ objSlaveRoom.Need1[3]+ objSlaveRoom.Need1[5])*2;
                objSlaveRoom.Need1[7] = fNeedBuilder;
            };
            
            //4) если нет ниодного контейнера то не выпускаем строителей
            if ((objSlaveRoom.Containers[0]==null)&& (objSlaveRoom.Containers[1]==null)&&(objSlaveRoom.Containers[2]==null)) objSlaveRoom.Need1[7]=0;

            //5 если контейнера есть то запускаем носильщиков
            if (objSlaveRoom.Containers[0]==null) objSlaveRoom.Need1[2] = 0;
            if (objSlaveRoom.Containers[1]==null) objSlaveRoom.Need1[4] = 0;
            if (objSlaveRoom.Containers[2]==null) objSlaveRoom.Need1[6] = 0;

            if ((objSlaveRoom.Containers[0]!=null) &&(objSlaveRoom.Need1[2]==0)) objSlaveRoom.Need1[2] = objSlaveRoom.ContainersWaysCarrierNeed[0].NeedCarriers;
            if ((objSlaveRoom.Containers[1]!=null) &&(objSlaveRoom.Need1[4]==0)) objSlaveRoom.Need1[4] = objSlaveRoom.ContainersWaysCarrierNeed[1].NeedCarriers;
            if ((objSlaveRoom.Containers[2]!=null) &&(objSlaveRoom.Need1[6]==0)) objSlaveRoom.Need1[6] = objSlaveRoom.ContainersWaysCarrierNeed[2].NeedCarriers;
            
            //6) если чтото строим то носильщиков не пускаем
            if ((objSlaveRoom.IsBuild == 1)&&(objSlaveRoom.BuildRoadByCarrier!=1)) {
                objSlaveRoom.Need1[2] = 0;
                objSlaveRoom.Need1[4] = 0;
                objSlaveRoom.Need1[6] = 0;
            };

            //7) Mineral
            objSlaveRoom.GatherMineralNow = 0;
            if ((objSlaveRoom.Extractor !='' && objSlaveRoom.GatherMineral == 1 && objSlaveRoom.Mineral !='' && objMasterRoom.Storage !='')){
                
                fMineral = Game.getObjectById(objSlaveRoom.Mineral);
                fStorage = Game.getObjectById(objMasterRoom.Storage);
                fMineralsInStorage = _.sum(fStorage.store) - fStorage.store.energy;
                fHarvestMineralsInStorage = fStorage.store[fMineral.mineralType];
                
                if (fHarvestMineralsInStorage == null) fHarvestMineralsInStorage = 0;
                if (fMineralsInStorage<objRoom.StorageMineralAllMax && fHarvestMineralsInStorage<objRoom.StorageMineralExtractionMax && fMineral.mineralAmount!=0){
                    objSlaveRoom.GatherMineralNow = 1;
                    objSlaveRoom.Need1[17]=1;
                    objSlaveRoom.Need1[18]=1;  
                }
            }
        };

        if (objSlaveRoom.TypeOfRoom==14) {
            
            if (Game.rooms[objSlaveRoom.Name]==null) {
                for (let i=0;i<objSlaveRoom.Need1.length;i++) objSlaveRoom.Need1[i]=0;
                objSlaveRoom.Need1[10] = 1;
                return;
            };

            //0) инвадеры но не атакуем, никого не выпускаем
            var objCheckpointRoom = Memory.Data[objSlaveRoom.CheckpointRoom];
            if (objSlaveRoom.IsInvaders == 1 || objCheckpointRoom.IsInvaders == 1) {
                for (let i=0;i<objSlaveRoom.Need0.length;i++) objSlaveRoom.Need0[i]=0;
                for (let i=0;i<objSlaveRoom.Need1.length;i++) objSlaveRoom.Need1[i]=0;
                for (let i=0;i<objSlaveRoom.Need2.length;i++) objSlaveRoom.Need2[i]=0;
                objSlaveRoom.Need1[10] = 1;
                return;
            }

             //2) проверяем сколько источников для добычи столько выпускаем добытчиков                        
            if (objSlaveRoom.Sources.length>=1) objSlaveRoom.Need1[1] = 1;
            if (objSlaveRoom.Sources.length>=2) objSlaveRoom.Need1[3] = 1;
            if (objSlaveRoom.Sources.length>=3) objSlaveRoom.Need1[5] = 1;

            //3) если чтото там строится то выпускаем строителей в два раза больше чем добытчиков
            
            if ((objSlaveRoom.IsBuild == 1)&&(objSlaveRoom.BuildRoadByCarrier!=1)) {
                var fNeedBuilder = ( objSlaveRoom.Need1[1]+ objSlaveRoom.Need1[3]+ objSlaveRoom.Need1[5])*2;
                objSlaveRoom.Need1[7] = fNeedBuilder;
            };
            
            //4) если нет ниодного контейнера то не выпускаем строителей
            if ((objSlaveRoom.Containers[0]==null)&& (objSlaveRoom.Containers[1]==null)&&(objSlaveRoom.Containers[2]==null)) objSlaveRoom.Need1[7]=0;

            //5 если контейнера есть то запускаем носильщиков
            if (objSlaveRoom.Containers[0]==null) objSlaveRoom.Need1[2] = 0;
            if (objSlaveRoom.Containers[1]==null) objSlaveRoom.Need1[4] = 0;
            if (objSlaveRoom.Containers[2]==null) objSlaveRoom.Need1[6] = 0;

            if ((objSlaveRoom.Containers[0]!=null) &&(objSlaveRoom.Need1[2]==0)) objSlaveRoom.Need1[2] = objSlaveRoom.ContainersWaysCarrierNeed[0].NeedCarriers;
            if ((objSlaveRoom.Containers[1]!=null) &&(objSlaveRoom.Need1[4]==0)) objSlaveRoom.Need1[4] = objSlaveRoom.ContainersWaysCarrierNeed[1].NeedCarriers;
            if ((objSlaveRoom.Containers[2]!=null) &&(objSlaveRoom.Need1[6]==0)) objSlaveRoom.Need1[6] = objSlaveRoom.ContainersWaysCarrierNeed[2].NeedCarriers;
            
            //6) если чтото строим то носильщиков не пускаем
            if ((objSlaveRoom.IsBuild == 1)&&(objSlaveRoom.BuildRoadByCarrier!=1)) {
                objSlaveRoom.Need1[2] = 0;
                objSlaveRoom.Need1[4] = 0;
                objSlaveRoom.Need1[6] = 0;
            };
        };
    },

    C_BuildQueue: function(objMasterRoom,objSlaveRoom) {
        var fPriorytyOfRole = [15,14,16,12,13,0,10,8,9,1,3,5,2,4,6,7,11,17,18];
        
        for (var i = 0; i < fPriorytyOfRole.length; i++) {
            var fRole = fPriorytyOfRole[i];
            while (objSlaveRoom.CalcForQueue[fRole] < objSlaveRoom.NeedForQueue[fRole]){
                objSlaveRoom.CalcForQueue[fRole]++;
                afunc.AddToQueue(objMasterRoom, objSlaveRoom.Name,fRole,objSlaveRoom.Describe+' '+fRole);
            }
        }
    },

    C_ShowInfoPre: function(objMasterRoom,objSlaveRoom){
        var fShowInfo = Memory.ShowInfoOnlyRoom.indexOf(objSlaveRoom.Name);
        if (fShowInfo == -1) return;

        console.log('SLAVE---- ' + objSlaveRoom.Describe + ' (' + objSlaveRoom.Name + ') --- MASTER:' + objMasterRoom.Describe );
        console.log('      :' + [0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9]);
        console.log('need 0:'+objSlaveRoom.Need0);
        console.log('need 1:'+objSlaveRoom.Need1);
        console.log('need 2:'+objSlaveRoom.Need2);
        console.log('have  :'+objSlaveRoom.CalcRoleCreeps);
    },

    C_ShowInfoPost: function(objMasterRoom,objSlaveRoom){

    },

    C_SpawnCreeps: function(objMasterRoom,objSlaveRoom) {
        
        if (objMasterRoom.Queue.length == 0) return;
        var fSpawn = afunc.GetSpawnRoom(objMasterRoom);
        if (fSpawn==null) return;
        

        if (objMasterRoom.Queue[0][0] != objSlaveRoom.Name) return; //тут спавним только крипов нашей комнаты
        
        if (objMasterRoom.Queue[0][1]==10) afunc.SpawnCreep(fSpawn,[MOVE], 110 , null , null, objSlaveRoom.Name, objMasterRoom.Name, 0);
        if (objMasterRoom.Queue[0][1]==9)  afunc.SpawnCreep(fSpawn,[CLAIM, MOVE], 109 , null , objSlaveRoom.Controller, objSlaveRoom.Name, objMasterRoom.Name, null,0);
        if (objMasterRoom.Queue[0][1]==8)  afunc.SpawnCreep(fSpawn,[CLAIM, CLAIM, MOVE, MOVE], 108 , null , objSlaveRoom.Controller, objSlaveRoom.Name, objMasterRoom.Name, null,0);
        
        if (objMasterRoom.Queue[0][1]==1)  afunc.SpawnCreep(fSpawn,C_creeps.C_GetHarvesterBody(objMasterRoom,objSlaveRoom), 101 , objSlaveRoom.Sources[0], objSlaveRoom.Containers[0] ,objSlaveRoom.Name , objMasterRoom.Name, C_creeps.C_GetHarvesterInfo(objMasterRoom,objSlaveRoom), 150);
        if (objMasterRoom.Queue[0][1]==3)  afunc.SpawnCreep(fSpawn,C_creeps.C_GetHarvesterBody(objMasterRoom,objSlaveRoom), 103 , objSlaveRoom.Sources[1], objSlaveRoom.Containers[1] ,objSlaveRoom.Name , objMasterRoom.Name, C_creeps.C_GetHarvesterInfo(objMasterRoom,objSlaveRoom), 150);
        if (objMasterRoom.Queue[0][1]==5)  afunc.SpawnCreep(fSpawn,C_creeps.C_GetHarvesterBody(objMasterRoom,objSlaveRoom), 105 , objSlaveRoom.Sources[2], objSlaveRoom.Containers[2] ,objSlaveRoom.Name , objMasterRoom.Name, C_creeps.C_GetHarvesterInfo(objMasterRoom,objSlaveRoom), 150);
        
        if (objMasterRoom.Queue[0][1]==7)  afunc.SpawnCreep(fSpawn,C_creeps.C_GetWorkerBody(objMasterRoom,objSlaveRoom), 107 , null, null ,objSlaveRoom.Name  , objMasterRoom.Name, null , 0);
        if (objMasterRoom.Queue[0][1]==11) afunc.SpawnCreep(fSpawn,C_creeps.C_GetWorkerBody(objMasterRoom,objSlaveRoom), 111 , null, null ,objSlaveRoom.Name  , objMasterRoom.Name, null , 0);

        if (objMasterRoom.Queue[0][1]==2) afunc.SpawnCreep(fSpawn,C_creeps.C_GetCarrierBody(objMasterRoom,objSlaveRoom,0), 102 , objSlaveRoom.Containers[0], objMasterRoom.Storage ,objSlaveRoom.Name , objMasterRoom.Name, null , 0);
        if (objMasterRoom.Queue[0][1]==4) afunc.SpawnCreep(fSpawn,C_creeps.C_GetCarrierBody(objMasterRoom,objSlaveRoom,1), 104 , objSlaveRoom.Containers[1], objMasterRoom.Storage ,objSlaveRoom.Name , objMasterRoom.Name, null , 0);
        if (objMasterRoom.Queue[0][1]==6) afunc.SpawnCreep(fSpawn,C_creeps.C_GetCarrierBody(objMasterRoom,objSlaveRoom,2), 106 , objSlaveRoom.Containers[2], objMasterRoom.Storage ,objSlaveRoom.Name , objMasterRoom.Name, null , 0);

        if (objMasterRoom.Queue[0][1]==0) afunc.SpawnCreep(fSpawn,C_creeps.C_GetWarriorBody(objMasterRoom,objSlaveRoom), 100 , null, null ,objSlaveRoom.Name , objMasterRoom.Name, null , 0);

        if (objMasterRoom.Queue[0][1]==12) afunc.SpawnCreep(fSpawn,C_creeps.C_GetDWarriorBody(objMasterRoom,objSlaveRoom), 112 , null, null ,objSlaveRoom.Name , objMasterRoom.Name, null , 0);
        if (objMasterRoom.Queue[0][1]==13) afunc.SpawnCreep(fSpawn,C_creeps.C_GetDWarriorBody(objMasterRoom,objSlaveRoom), 113 , null, null ,objSlaveRoom.Name , objMasterRoom.Name, null , 0);

        if (objMasterRoom.Queue[0][1]==14) afunc.SpawnCreep(fSpawn,C_creeps.C_GetGroupRangerBody(objMasterRoom,objSlaveRoom), 114 , null, null ,objSlaveRoom.Name , objMasterRoom.Name, null , 0);
        if (objMasterRoom.Queue[0][1]==15) afunc.SpawnCreep(fSpawn,C_creeps.C_GetGroupHealerBody(objMasterRoom,objSlaveRoom), 115 , null, null ,objSlaveRoom.Name , objMasterRoom.Name, null , 0);
        if (objMasterRoom.Queue[0][1]==16) afunc.SpawnCreep(fSpawn,C_creeps.C_GetGroupMileBody(objMasterRoom,objSlaveRoom), 116 , null, null ,objSlaveRoom.Name , objMasterRoom.Name, null , 0);

        if (objMasterRoom.Queue[0][1]==17) afunc.SpawnCreep(fSpawn,C_creeps.C_GetMinHarvesterBody(objMasterRoom,objSlaveRoom), 117 , null, null ,objSlaveRoom.Name , objMasterRoom.Name, null , 150)
        if (objMasterRoom.Queue[0][1]==18) afunc.SpawnCreep(fSpawn,C_creeps.C_GetMinCarrierBody(objMasterRoom,objSlaveRoom), 118 , null, null ,objSlaveRoom.Name , objMasterRoom.Name, null , 150)
    },
};

module.exports = C_creeps;