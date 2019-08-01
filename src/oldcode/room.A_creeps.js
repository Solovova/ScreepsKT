var afunc                      = require('modul.afunc');
var messenger                      	= require('modul.messenger');

var A_creeps = {
    A_GetHarvesterBody: function(objRoom) {
        if (objRoom.MaxSpawnEnergy < 550)  return [MOVE, WORK, WORK, CARRY];
        if (objRoom.MaxSpawnEnergy < 800)  return [MOVE,MOVE,WORK,WORK,WORK,WORK,CARRY];
        return [MOVE,MOVE,WORK,WORK,WORK,WORK,WORK,WORK,CARRY,CARRY]; 
    },

    A_GetHarvesterInfo: function(objRoom) {
        if (objRoom.MaxSpawnEnergy < 550)  return 48;
        if (objRoom.MaxSpawnEnergy < 800)  return 48;
        return 96; 
    },
    
    A_GetFillerBody: function(objRoom) {
        if (objRoom.MaxSpawnEnergy < 550)  return [MOVE,MOVE,MOVE,CARRY,CARRY,CARRY];
        if (objRoom.MaxSpawnEnergy < 800)  return [MOVE,MOVE,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY];
        return [MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY];
    },

    A_GetCarrierBody: function(objRoom) {
        if (objRoom.MaxSpawnEnergy < 550) return [MOVE,MOVE,MOVE,CARRY,CARRY,CARRY];
        if (objRoom.MaxSpawnEnergy < 800) return [MOVE,MOVE,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY];
        return [MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY];
    },

    A_GetWorkerBody: function(objRoom) {
        if (objRoom.MaxSpawnEnergy < 550)  return [MOVE,MOVE,WORK,CARRY,CARRY]; 
        if (objRoom.MaxSpawnEnergy < 800)  return [MOVE,MOVE,MOVE,MOVE,WORK,WORK,CARRY,CARRY];
        return [MOVE,MOVE,MOVE,MOVE,WORK,WORK,WORK,WORK,CARRY,CARRY,CARRY,CARRY];
    },

    A_GetGarbageBody: function(objRoom) {
        return [MOVE,MOVE,MOVE,CARRY,CARRY,CARRY]; 
    },

    A_CorrectionCripsBySituation: function(objRoom) {
        //0 (0) - small harvester-filler
        //1 (1) - havester source0
        //2 (2) - filler from 0
        //3 (3) - havester source1
        //4 (4) - filler from 1
        //5 (5) - builder-upgrader
        //6 (6) - carrier to container3 
        //7 (7) - garbage

        //0)Заполняем по умолчанию 
        var CanHarvestSource0 = objRoom.SourcesCanHarvest[0];
        var CanHarvestSource1 = 0;
        if (objRoom.Sources.length>1) CanHarvestSource1 = objRoom.SourcesCanHarvest[1];
        

        if (objRoom.MaxSpawnEnergy < 550) {
            objRoom.Need1[0] = 0;
            objRoom.Need1[1] = Math.min(3,CanHarvestSource0);
            objRoom.Need1[2] = 1;
            objRoom.Need1[3] = Math.min(3,CanHarvestSource1);
            objRoom.Need1[4] = 1;
            objRoom.Need1[5] = 7;
            objRoom.Need1[6] = 0;
            objRoom.Need1[7] = 0;
            if (objRoom.Sources.length==1){
                objRoom.Need1[5] = 4;    
            }
        }

        if ((objRoom.MaxSpawnEnergy >= 550)&&(objRoom.MaxSpawnEnergy < 800))  {
            objRoom.Need1[0] = 0;
            objRoom.Need1[1] = Math.min(2,CanHarvestSource0);
            objRoom.Need1[2] = 1;
            objRoom.Need1[3] = Math.min(2,CanHarvestSource1);
            objRoom.Need1[4] = 1;
            objRoom.Need1[5] = 3;objRoom.Need2[5] = 2;
            objRoom.Need1[6] = 2;objRoom.Need2[6] = 2;
            objRoom.Need1[7] = 1;
            if (objRoom.Sources.length==1){
                objRoom.Need1[5] = 1;objRoom.Need2[5] = 2;
                objRoom.Need1[6] = 1;objRoom.Need2[6] = 1;    
            }
        }

        if (objRoom.MaxSpawnEnergy >= 800)  {
            objRoom.Need1[0] = 0;
            objRoom.Need1[1] = Math.min(1,CanHarvestSource0);
            objRoom.Need1[2] = 1;
            objRoom.Need1[3] = Math.min(1,CanHarvestSource1);
            objRoom.Need1[4] = 1;
            objRoom.Need1[5] = 2;objRoom.Need2[5] = 2;
            objRoom.Need1[6] = 2;objRoom.Need2[6] = 1;
            objRoom.Need1[7] = 1;
            if (objRoom.Sources.length==1){
                objRoom.Need1[5] = 1;objRoom.Need2[5] = 1;
                objRoom.Need1[6] = 1;objRoom.Need2[6] = 0;   
            }
        }

        //костиль
        if (objRoom.Name == 'W44N2') {objRoom.Need1[5] = 4;objRoom.Need2[5] = 0}

        //0) если нет филлеров то SmallHarvester
        if ((objRoom.CalcRoleCreeps[2]==0)&&(objRoom.CalcRoleCreeps[4]==0)) objRoom.Need1[0]=2;
        
        //1) если нет контейнера не спавнить носильщика филлера
        if (objRoom.Containers[0]==null) objRoom.Need1[2] = 0;
        if (objRoom.Containers[1]==null) objRoom.Need1[4] = 0;
        
        //2) если нет контейнера 3 не спавнить носильщика
        if (objRoom.Containers[3]==null) {
            objRoom.Need1[6] = 0;
            objRoom.Need2[6] = 0;
        }

        //3) если нет контейнера 3 не спавнить мусорщика
        if (objRoom.Containers[3]==null) {
            objRoom.Need1[7] = 0;
            objRoom.Need2[7] = 0;
        }

        //4) если нет филлеров рабочих
        if ((objRoom.CalcRoleCreeps[2]==0)&&(objRoom.CalcRoleCreeps[4]==0))  {
            objRoom.Need1[5] = 0;
            objRoom.Need2[5] = 0;
        }
    },

    A_BuildQueue: function(objRoom) {
        var fPriorytyOfRole = [0,1,2,3,4,5,6,7]; //приоритет спавна ролей перед основными
        
        for (var i = 0; i < fPriorytyOfRole.length; i++) {
            var fRole = fPriorytyOfRole[i];
            while (objRoom.CalcForQueue[fRole] < objRoom.NeedForQueue[fRole]){
                objRoom.CalcForQueue[fRole]++;
                afunc.AddToQueue(objRoom, objRoom.Name,fRole,objRoom.Describe+' '+fRole);
            }
        }
    },

    A_ShowInfoPre: function(objRoom){
        var fShowInfo = Memory.ShowInfoOnlyRoom.indexOf(objRoom.Name);
        if (fShowInfo == -1) return;

        console.log('MASTER---- ' + objRoom.Describe + ' (' + objRoom.Name + ') ----------');
        console.log('      :' + [0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9]);
        console.log('need 0:'+objRoom.Need0);
        console.log('need 1:'+objRoom.Need1);
        console.log('need 2:'+objRoom.Need2);
        console.log('have  :'+objRoom.CalcRoleCreeps);
    },

    A_ShowInfoPost: function(objRoom){
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

    A_SpawnCreeps: function(objRoom) {    

        if (objRoom.Queue.length == 0) return;
        var fSpawn = afunc.GetSpawnRoom(objRoom);
        if (fSpawn==null) return;
    
        if (objRoom.Queue[0][1] ==0) afunc.SpawnCreep(fSpawn,[MOVE,MOVE,WORK,CARRY,CARRY], 0 , objRoom.Sources[0], null, objRoom.Name, objRoom.Name, null, 0);
        
        if (objRoom.Queue[0][1]==1) afunc.SpawnCreep(fSpawn,A_creeps.A_GetHarvesterBody(objRoom), 1 , objRoom.Sources[0], objRoom.Containers[0], objRoom.Name, objRoom.Name, A_creeps.A_GetHarvesterInfo(objRoom),20); //1
        if (objRoom.Queue[0][1]==3) afunc.SpawnCreep(fSpawn,A_creeps.A_GetHarvesterBody(objRoom), 3 , objRoom.Sources[1], objRoom.Containers[1], objRoom.Name, objRoom.Name, A_creeps.A_GetHarvesterInfo(objRoom),20); //3

        if (objRoom.Queue[0][1]==2) afunc.SpawnCreep(fSpawn,A_creeps.A_GetFillerBody(objRoom), 2 , objRoom.Containers[0], null, objRoom.Name, objRoom.Name,null ,80); //2
        if (objRoom.Queue[0][1]==4) afunc.SpawnCreep(fSpawn,A_creeps.A_GetFillerBody(objRoom), 4 , objRoom.Containers[1], null, objRoom.Name, objRoom.Name, null,80); //4

        if (objRoom.Queue[0][1]==5) afunc.SpawnCreep(fSpawn,A_creeps.A_GetWorkerBody(objRoom), 5 , null, null, objRoom.Name, objRoom.Name, null,100); //5 

        if (objRoom.Queue[0][1]==6) afunc.SpawnCreep(fSpawn,A_creeps.A_GetCarrierBody(objRoom), 6 , null, objRoom.Containers[3], objRoom.Name, objRoom.Name,null ,40); //6

        if (objRoom.Queue[0][1]==7) afunc.SpawnCreep(fSpawn,A_creeps.A_GetGarbageBody(objRoom), 7 , null, objRoom.Containers[3], objRoom.Name, objRoom.Name, null,100); //7
    },
};

module.exports = A_creeps;