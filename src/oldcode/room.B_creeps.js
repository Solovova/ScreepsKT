var afunc                      = require('modul.afunc');
var messenger                      	= require('modul.messenger');

var B_creeps = {
    B_LinkTransfer: function(objRoom) {

        fLinkUpgrader = Game.getObjectById(objRoom.Links[3]);
        if (fLinkUpgrader != null){
            var indLinkFrom = [0,1];
            for (let i=0;i<indLinkFrom.length;i++){
                
                fLinkFrom = Game.getObjectById(objRoom.Links[indLinkFrom[i]]);
                
                if (!fLinkFrom) continue;
                //console.log('link:'+fLinkFrom.energy)
                if (fLinkUpgrader.energy>600) continue; 
                if (fLinkFrom.energy<320) continue; 
                if (fLinkFrom.cooldown!=0) continue;
                var fCanSend = 800 - fLinkUpgrader.energy;
                if (fCanSend>fLinkFrom.energy) fCanSend=fLinkFrom.energy;
                if (fCanSend<300) continue;
                fLinkFrom.transferEnergy(fLinkUpgrader,fCanSend);
                return;
            }
        }

        fLinkTo = Game.getObjectById(objRoom.Links[2]);
        
        if (!fLinkTo) return;
        if (fLinkTo.energy!=0) return;
        
        var indLinkFrom = [0,1];
        for (let i=0;i<indLinkFrom.length;i++){
            
            fLinkFrom = Game.getObjectById(objRoom.Links[indLinkFrom[i]]);
            
            if (!fLinkFrom) continue;
            //console.log('link:'+fLinkFrom.energy)
            if (fLinkFrom.energy!=800) continue; 
            if (fLinkFrom.cooldown!=0) continue;
            fLinkFrom.transferEnergy(fLinkTo,800);
            break;
        }
    },

    B_GetHarvesterBody: function(objRoom) {
        return [MOVE,MOVE,MOVE,WORK,WORK,WORK,WORK,WORK,WORK,CARRY,CARRY];
    },

    B_GetLogisticBody: function(objRoom) {
        return [MOVE,MOVE,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY];
    },

    B_GetLabFillerBody: function(objRoom) {
        return [MOVE,MOVE,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY];
        //return [MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY];
    },

    B_GetHarvesterInfo: function(objRoom) {
        return 96; 
    },

    B_GetCarrierBody: function(objRoom,indContainer) {
        return objRoom.ContainersWaysCarrierNeed[indContainer].BodyCarriers;
        // if (objRoom.MaxSpawnEnergy<1800) {
        //     return [MOVE,MOVE,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY];
        // }else{
        //     return [CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE];
        // }   
    },
    
    B_GetFillerBody: function(objRoom) {
        if (objRoom.MaxSpawnEnergy>=5000) {
            return [MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY];
        }else{
            return [MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY];
        }
    },

    

    B_GetFarCarrierBody: function(objRoom) {
        if (objRoom.MaxSpawnEnergy<2250) return [MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY];
        return [MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY]
    },

    B_GetCarrierToUpgraderBody: function(objRoom) {
        if (objRoom.MaxSpawnEnergy<1800) return [MOVE,MOVE,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY];
        if (objRoom.MaxSpawnEnergy<=5600) return [CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE];
        return [MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY];
    },

    B_GetUpgraderBody: function(objRoom) {
        if (objRoom.MaxSpawnEnergy<1800) return [MOVE,MOVE,MOVE,MOVE,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,CARRY,CARRY];
        if (objRoom.MaxSpawnEnergy<3000) return [WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, CARRY, CARRY, CARRY, CARRY, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE];
        if (objRoom.MaxSpawnEnergy<=5600) return [MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY];
        return [MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY];
    },

    B_GetBuilderBody: function(objRoom) {
        return [MOVE,MOVE,MOVE,MOVE,MOVE,WORK,WORK,WORK,WORK,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY];
    },

    B_GetGarbageBody: function(objRoom) {
        if (objRoom.MaxSpawnEnergy<1800) {
            return [MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY];
        }else{
            return [MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY];
        } 
    },

    B_GetTransBody: function(objRoom) {
        return [MOVE,MOVE,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY]; 
    },

    B_GetBuilderBoorstBody: function(objRoom) {
        //if (objRoom.MaxSpawnEnergy>2300) return [MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY]; 
        return [MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY];
        
    },

    B_GetCarrierToBuilderBoorstBody: function(objRoom) {
        return [MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY]; 
    },

    B_GetDefenderBody: function(objRoom) {
        return [MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK]; 
    },
    
    

    B_CorrectionCripsBySituation: function(objRoom) {
        // if (objRoom.Name == 'W41N7') {
        //     console.log('dddddddddddddddd');
        //     objRoom.Need1[21]=1;
        //     objRoom.Need1[22]=6;
        //     //var fSpawn = afunc.GetSpawnRoom(objRoom);
        //     //afunc.SpawnCreep(fSpawn,B_creeps.B_GetBuilderBoorstBody(objRoom), 221 , null , null ,  objRoom.Name,  objRoom.Name,null ,10);
        //     //afunc.SpawnCreep(fSpawn,B_creeps.B_GetCarrierToBuilderBoorstBody(objRoom), 222 , null , null ,  objRoom.Name,  objRoom.Name,null ,10)
        // }
        
        B_creeps.B_LinkTransfer(objRoom);

        var fStorage = Game.getObjectById(objRoom.Storage);
 
        if (fStorage == null) return;
        var fEnergyInStorage = fStorage.store.energy;
        //var fController = Game.getObjectById(objRoom.Controller);
        //var fTerminal = Game.getObjectById(objRoom.Terminal);

        //костиль
        if (objRoom.Name == 'W43N4') objRoom.Need1[17] = 1;

        
        objRoom.Need1[0] = 0;                           //200 - small harvester-filler
        if (objRoom.Need1[1]==0) objRoom.Need1[1]=1;    //201 - havester source0
        if (objRoom.Need1[3]==0) objRoom.Need1[3]=1;    //203 - havester source1
        if (objRoom.Need1[5]==0) objRoom.Need1[5]=1;    //205 - filler
        if (objRoom.Need0[5]==0) objRoom.Need0[5]=1;    //205 - filler
        objRoom.Need1[9]=0;                             //209 - small filler
        if (objRoom.Need1[10]==0) objRoom.Need1[10]=1;  //210 - garbage
        
        //1 havester,carrier,filler,garbage, small harvester-filler, small filler
        //1.1 если поля не заполнены то заполняем по умолчанию
        if (objRoom.ContainersWaysCarrierNeed[0]!=null) {
            if (objRoom.Need1[2]==0) objRoom.Need1[2]=objRoom.ContainersWaysCarrierNeed[0].NeedCarriers; //202 - carrier 0
        }

        if (objRoom.ContainersWaysCarrierNeed[1]!=null) {
            if (objRoom.Need1[4]==0) objRoom.Need1[4]=objRoom.ContainersWaysCarrierNeed[1].NeedCarriers; //204 - carrier 1
        }

        //1.2 Если нет контейнеров не запускаем переносчиков
        if (objRoom.Containers[0] == null) objRoom.Need1[2]=0;
        if (objRoom.Containers[1] == null) objRoom.Need1[4]=0;
         
        //1.3 если filler = 0 и Енергии если выпускаем смалл филлер
        if ((objRoom.CalcRoleCreeps[5]==0)&&(fEnergyInStorage>2000))  objRoom.Need0[9]=1;
        if ((objRoom.CalcRoleCreeps[5]==0)&&(fEnergyInStorage<=2000)) objRoom.Need0[0]=2;

        //1.4 если один источник то обнуляем харвестера и переносчика 1
        if (objRoom.Sources.length==1) {
            objRoom.Need1[3]=0;
            objRoom.Need1[4]=0;    
        }
        //------------------------------------------------------------------------------
        
        //2 Builder
        //2.1
        objRoom.Need1[8]=0;                             //208 - Builder
        //2.2 если есть что строить то выпускаем строителя
        // if ((objRoom.IsBuild == 1)&&(fEnergyInStorage>20000)&&(objRoom.BoostBuilder != 1)) {
        //     objRoom.Need1[8]=2;
        // }

        //3 Mineral harvesting
        if ((objRoom.Extractor !='')&&(objRoom.Containers[4] != null)){
            fMineral = Game.getObjectById(objRoom.Mineral);
            fMineralsInStorage = _.sum(fStorage.store) - fStorage.store.energy;
            fHarvestMineralsInStorage = fStorage.store[fMineral.mineralType];
            if (fHarvestMineralsInStorage == null) fHarvestMineralsInStorage = 0;
            if (fMineralsInStorage<objRoom.StorageMineralAllMax && fHarvestMineralsInStorage<objRoom.StorageMineralExtractionMax && fMineral.mineralAmount!=0){
                objRoom.Need1[15]=1;
                objRoom.Need1[16]=1;  
            }
        }

        //4 Если есть Link(2) то запускаем переносчика
        if (objRoom.Links[2]!='') objRoom.Need1[14]=1;
                 
        //5 LabFiller
        if (objRoom.LabReaction != '')  objRoom.Need1[18]=1;
        


        //6 Upgrader
        if (objRoom.SentEnergyToRoom == '') {
            if (objRoom.MaxSpawnEnergy>=1800) {
                objRoom.Need1[6]=1;
                objRoom.Need1[7]=1;
                objRoom.Need2[6]=2;
                objRoom.Need2[7]=3;
                if (objRoom.Name == 'W47N3') objRoom.Need2[6]=4;     
            }else{
                objRoom.Need1[6]=2;
                objRoom.Need1[7]=2;
                objRoom.Need2[6]=1;
                objRoom.Need2[7]=2;
                
            }
        }else{
            objRoom.Need1[6]=0;
            objRoom.Need1[7]=0;
            objRoom.Need2[6]=0;
            objRoom.Need2[7]=0; 
            if (objRoom.oController < 20000) objRoom.Need1[13]=1;    
        }

        if (fEnergyInStorage<30000) {
            objRoom.Need1[6]=0;
            objRoom.Need1[7]=0;  
            objRoom.Need2[6]=0;
            objRoom.Need2[7]=0; 
        }
        
        
        if (objRoom.oController.level == 8){
            objRoom.Need1[6]=0;
            objRoom.Need1[7]=1;
            objRoom.Need2[6]=0;
            objRoom.Need2[7]=0;     
            if (objRoom.Links[3] == null) objRoom.Need1[6]=2;    
            if (objRoom.oController < 100000) objRoom.Need1[13]=1; 
        } 

        if (objRoom.Name == 'W47N3') objRoom.Need1[6]=2;

        

        if (fEnergyInStorage<objRoom.StorageEnergyForce) {
            objRoom.Need2[6]=0;
            objRoom.Need2[7]=0; 
        }


        //8 FillerTower
        if (objRoom.oController.level == 8) {
            objRoom.Need0[20]=1;    
        } 

        //if (objRoom.Name == 'W4N3') 
        //9 Logist
        objRoom.Need0[17]=1;

        //10 Far carrier
        if (fEnergyInStorage>=objRoom.StorageEnergyForce && objRoom.SentEnergyToRoom != '') {
            var TerminalTo = Game.getObjectById(Memory.Data[objRoom.SentEnergyToRoom].Terminal);
            if (TerminalTo == null) objRoom.Need1[19]=3;
        }

        //9 NukeFiller если сума G в терминале и нике >=5000 и есть енергия


        //10 Builder Boorted
        if (((objRoom.BoostBuilder == 1 && objRoom.IsBuild == 1) || (objRoom.BoostBuilder == 2)) && fEnergyInStorage>=80000){
            objRoom.Need1[21]=1;
            objRoom.Need1[22]=2;
        }
        

        //11 Defender
        //if (objRoom.Name == 'W46N7') objRoom.Need1[23]=2;

        //if (objRoom.Name == 'W6N3') objRoom.Need1[23]=2;




        //ФОРСИРОВАНИЕ ИСПОЛЬЗОВАНИЯ РЕСУРСО

        if (fEnergyInStorage>objRoom.StorageEnergyForce) {
            // 1) test скидываем ресурсы и не выпускаем мусорщика чтоб собрал
            if (objRoom.DropResources == 1){
                objRoom.Need1[10]=0;
                objRoom.Need1[11]=1;
                return;   
            }
        }
    },

    B_BuildQueue: function(objRoom) {
        fStorage = Game.getObjectById(objRoom.Storage);
        if (fStorage == null) return;
        var fEnergyInStorage = fStorage.store.energy;
        
        if (fEnergyInStorage<2000){
            var fPriorytyOfRole = [0,9,1,3,2,4,14,5,20,6,7,10,8,11,12,13,15,16,17,18,19,21,22,23];
        }else{
            var fPriorytyOfRole = [0,9,5,20,1,3,2,4,14,6,7,10,8,11,12,13,15,16,17,18,19,21,22,23];
        }
        
        for (var i = 0; i < fPriorytyOfRole.length; i++) {
            var fRole = fPriorytyOfRole[i];
            while (objRoom.CalcForQueue[fRole] < objRoom.NeedForQueue[fRole]){
                objRoom.CalcForQueue[fRole]++;
                afunc.AddToQueue(objRoom, objRoom.Name,fRole,objRoom.Describe+' '+fRole);
            }
        }
    },

    B_ShowInfoPre: function(objRoom){
        var fShowInfo = Memory.ShowInfoOnlyRoom.indexOf(objRoom.Name);
        if (fShowInfo == -1) return;

        console.log('MASTER Type2- ' + objRoom.Describe + ' (' + objRoom.Name + ') ----------');
        console.log('      :' + [0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9]);
        console.log('need 0:'+objRoom.Need0);
        console.log('need 1:'+objRoom.Need1);
        console.log('need 2:'+objRoom.Need2);
        console.log('have  :'+objRoom.CalcRoleCreeps);
    },

    B_ShowInfoPost: function(objRoom){
        var textQueue = 'Queue (' +objRoom.Describe+'):';
        var fTestSpawn = '';
        for (let i=0; i<objRoom.Spawns.length;i++){
			fSpawn = Game.getObjectById(objRoom.Spawns[i]);
			if (fSpawn.spawning != null){
                fTestSpawn = fTestSpawn + '(' + Game.creeps[fSpawn.spawning.name].memory.role + ')';	
			}
		}

        if (fTestSpawn=='') fTestSpawn='    ';

        textQueue = textQueue  + fTestSpawn+':';
        

        for (let i=0;i<objRoom.Queue.length;i++) {
            if (Memory.Data[objRoom.Queue[i][0]].TypeOfRoom<10){
                textQueue = textQueue + objRoom.Queue[i][1] + ',';
            }else{
                textQueue = textQueue + Memory.Data[objRoom.Queue[i][0]].Describe + ' ' + objRoom.Queue[i][1] + ',';
            }
        }


        messenger.log('QUEUE',textQueue)
    },

    B_SpawnCreeps: function(objRoom) {
        if (objRoom.Queue.length == 0) return;
        var fSpawn = afunc.GetSpawnRoom(objRoom);
        if (fSpawn==null) return;

        if (objRoom.Queue[0][1]==0) afunc.SpawnCreep(fSpawn,[WORK, CARRY, MOVE], 200 , objRoom.Sources[0], null,  objRoom.Name,  objRoom.Name, null, 0);
        
        if (objRoom.Queue[0][1]==1) afunc.SpawnCreep(fSpawn,B_creeps.B_GetHarvesterBody(objRoom), 201 , objRoom.Sources[0], objRoom.Containers[0], objRoom.Name,  objRoom.Name, B_creeps.B_GetHarvesterInfo(objRoom),20);
        if (objRoom.Queue[0][1]==3) afunc.SpawnCreep(fSpawn,B_creeps.B_GetHarvesterBody(objRoom), 203 , objRoom.Sources[1], objRoom.Containers[1],  objRoom.Name,  objRoom.Name, B_creeps.B_GetHarvesterInfo(objRoom),20);
        
        if (objRoom.Queue[0][1]==2) afunc.SpawnCreep(fSpawn,B_creeps.B_GetCarrierBody(objRoom,0), 202 , objRoom.Containers[0], objRoom.Storage,  objRoom.Name,  objRoom.Name,null ,80);
        if (objRoom.Queue[0][1]==4) afunc.SpawnCreep(fSpawn,B_creeps.B_GetCarrierBody(objRoom,1), 204 , objRoom.Containers[1], objRoom.Storage,  objRoom.Name,  objRoom.Name,null ,80);

        if (objRoom.Queue[0][1]==5) afunc.SpawnCreep(fSpawn,B_creeps.B_GetFillerBody(objRoom), 205 , objRoom.Storage, null ,  objRoom.Name,  objRoom.Name,null ,30);

        if (objRoom.Queue[0][1]==6) afunc.SpawnCreep(fSpawn,B_creeps.B_GetCarrierToUpgraderBody(objRoom), 206 , objRoom.Storage, objRoom.Containers[3] ,  objRoom.Name,  objRoom.Name,null ,80);

        if (objRoom.Queue[0][1]==7) afunc.SpawnCreep(fSpawn,B_creeps.B_GetUpgraderBody(objRoom), 207 , objRoom.Containers[3] , null ,  objRoom.Name,  objRoom.Name,null ,20);

        if (objRoom.Queue[0][1]==8) afunc.SpawnCreep(fSpawn,B_creeps.B_GetBuilderBody(objRoom), 208 , objRoom.Storage, null ,  objRoom.Name,  objRoom.Name,null ,50);

        if (objRoom.Queue[0][1]==9) afunc.SpawnCreep(fSpawn,[CARRY, CARRY, MOVE], 209 , objRoom.Storage, null ,  objRoom.Name,  objRoom.Name,null ,30);
        if (objRoom.Queue[0][1]==10) afunc.SpawnCreep(fSpawn,B_creeps.B_GetGarbageBody(objRoom), 210 , null , objRoom.Storage ,  objRoom.Name,  objRoom.Name,null ,40);

        if (objRoom.Queue[0][1]==11) afunc.SpawnCreep(fSpawn,B_creeps.B_GetTransBody(objRoom), 211 , objRoom.Storage , objRoom.Terminal ,  objRoom.Name,  objRoom.Name,null ,10);
        if (objRoom.Queue[0][1]==12) afunc.SpawnCreep(fSpawn,B_creeps.B_GetTransBody(objRoom), 212 , objRoom.Terminal , objRoom.Storage ,  objRoom.Name,  objRoom.Name,null ,10);
        if (objRoom.Queue[0][1]==13) afunc.SpawnCreep(fSpawn,[WORK, CARRY, MOVE], 213 , null , null,  objRoom.Name,  objRoom.Name, null, 0);
        if (objRoom.Queue[0][1]==14) afunc.SpawnCreep(fSpawn,[MOVE,CARRY,CARRY], 214 , null , null,  objRoom.Name,  objRoom.Name, null, 4);
        if (objRoom.Queue[0][1]==15) afunc.SpawnCreep(fSpawn,[MOVE,MOVE,MOVE,MOVE,MOVE,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,CARRY], 215 , null , null,  objRoom.Name,  objRoom.Name, 40 , 30);
        if (objRoom.Queue[0][1]==16) afunc.SpawnCreep(fSpawn,[MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY], 216 , null , null,  objRoom.Name,  objRoom.Name, null, 60);
        if (objRoom.Queue[0][1]==17) afunc.SpawnCreep(fSpawn,B_creeps.B_GetLogisticBody(objRoom), 217 , null , null ,  objRoom.Name,  objRoom.Name,null ,60);
        if (objRoom.Queue[0][1]==18) afunc.SpawnCreep(fSpawn,B_creeps.B_GetLabFillerBody(objRoom), 218 , null , null ,  objRoom.Name,  objRoom.Name,null ,60);

        if (objRoom.Queue[0][1]==19) afunc.SpawnCreep(fSpawn,B_creeps.B_GetFarCarrierBody(objRoom), 219 , null , null ,  objRoom.Name,  objRoom.SentEnergyToRoom,null ,60);
        if (objRoom.Queue[0][1]==20) afunc.SpawnCreep(fSpawn,[MOVE,MOVE,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY], 220 , null , null ,  objRoom.Name,  objRoom.Name,null ,10);
        if (objRoom.Queue[0][1]==21) afunc.SpawnCreep(fSpawn,B_creeps.B_GetBuilderBoorstBody(objRoom), 221 , null , null ,  objRoom.Name,  objRoom.Name,null ,10);
        if (objRoom.Queue[0][1]==22) afunc.SpawnCreep(fSpawn,B_creeps.B_GetCarrierToBuilderBoorstBody(objRoom), 222 , null , null ,  objRoom.Name,  objRoom.Name,null ,10);
        if (objRoom.Queue[0][1]==23) afunc.SpawnCreep(fSpawn,B_creeps.B_GetDefenderBody(objRoom), 223 , null , null ,  objRoom.Name,  objRoom.Name,null ,0);

        
    },
};

module.exports = B_creeps;