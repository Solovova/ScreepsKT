var tests                       = require('modul.tests');
var sf                          = require('modul.smallfunc');
var messenger                      	= require('modul.messenger');


var afunc = {
    padLeft: function (input, totalWidth){
        var result = input;
        if (result.length < totalWidth){
            for(var i = result.length; i < totalWidth; i++){
                result = ' ' + result; 
            }       
        }
        return result;
    },

	GetMaxSpawnEnergy: function(objRoom) { 
        if ((objRoom.TypeOfRoom<10)||(objRoom.Controller!='')||(objRoom.Spawns.length == 0)) {
			var fController = Game.getObjectById(objRoom.Controller);
			var fExtension = Game.rooms[objRoom.Name].find(FIND_STRUCTURES, {
            	filter: (structure) => {
              		return (structure.structureType == STRUCTURE_EXTENSION)
			 	}
			});
			return fExtension.length*EXTENSION_ENERGY_CAPACITY[fController.level] + 300*objRoom.Spawns.length;
		}else{
            return 0;
        }    
    },
    
    GetInvaders: function(objRoom) {
        var targets = Game.rooms[objRoom.Name].find(FIND_HOSTILE_CREEPS);

        if (objRoom.TypeOfRoom==12) {
            var haveInvaders = 0;    
            for (var i=0;i<targets.length;i++){
                if (targets[i].body.length<40) {
                    haveInvaders = 1;    
                    break;
                }
            }
            return haveInvaders;
        }else{
            if (targets.length>0) return 1;
            return 0;
        }
    },

    CalcDistanceXY: function(_x1, _y1, _x2, _y2) { 
		return Math.max(Math.abs(_x1-_x2),Math.abs(_y1-_y2));
    },

    CalcDistancePos: function(_pos1,_pos2) { 
		return Math.max(Math.abs(_pos1.x-_pos2.x),Math.abs(_pos1.y-_pos2.y));
    },

    //функция myPos,targets,около объектов array
    GetIndexOfNearestObjectNearObjects: function(myPos,findInObjects,Objects) {
        var fMinDistance = 1000;
        var fTarget = null;
        for (let i=0;i<findInObjects.length;i++){
            var isNearMyObject = 0;
            for (let j=0;j<Objects.length;j++){
                if (afunc.CalcDistancePos(findInObjects[i].pos,Objects[j].pos)<10){
                    isNearMyObject = 1;
                    break;
                } 
            }
            if (isNearMyObject == 1){
                fDistance = afunc.CalcDistancePos(findInObjects[i].pos,myPos);
                if (fDistance<fMinDistance) {
                    fMinDistance = fDistance;
                    fTarget = findInObjects[i];     
                }
            }

        }
        return fTarget;
    },

    GetWayFromPosToPos: function(fPos1,fPos2) {
        let goals = { pos: fPos2, range: 1 };
  
        let ret = PathFinder.search(
            fPos1, goals,
            {
                plainCost: 2,
                swampCost: 10,
                maxOps:  3000,
                roomCallback: function(roomName) {
                    let room = Game.rooms[roomName];
                    if (!room) return;        
                    let costs = new PathFinder.CostMatrix;
                    room.find(FIND_STRUCTURES).forEach(function(struct) {
                        if (struct.structureType === STRUCTURE_ROAD) {
                            costs.set(struct.pos.x, struct.pos.y, 1);
                        } else if (struct.structureType !== STRUCTURE_CONTAINER &&
                                (struct.structureType !== STRUCTURE_RAMPART ||
                                !struct.my)) {
                            costs.set(struct.pos.x, struct.pos.y, 0xff);
                        }
                    });
                    return costs;
                },
            }
        );
        return ret;      
    },

    CalculateWayForRoom: function(objRoom, fNeedRecalculate){
        if (fNeedRecalculate==null) fNeedRecalculate=0;
        
        if (objRoom.TypeOfRoom<10) {
            var objMasterRoom = objRoom;
        }else{
            var objMasterRoom = Memory.Data[objRoom.MasterRoom];
        }
        if (objMasterRoom ==null ) return;
        var fStorage = Game.getObjectById(objMasterRoom.Storage);
        if (!fStorage) return;
        for (let i=0;i<objRoom.Containers.length;i++){
            if ((objRoom.ContainersWaysToStorage[i]!=null)&&(fNeedRecalculate==0)) continue;
            if (objRoom.Containers[i]==null) continue;
            var fContainer = Game.getObjectById(objRoom.Containers[i]);
            
            if (fContainer==null) continue;
            
            var ret = afunc.GetWayFromPosToPos(fContainer.pos,fStorage.pos);
            if (!ret.incomplete) objRoom.ContainersWaysToStorage[i] = ret.path;
            
            var fEnergyCapacity = SOURCE_ENERGY_CAPACITY+500;
            if (objRoom.TypeOfRoom == 12 || objRoom.TypeOfRoom == 14) fEnergyCapacity = SOURCE_ENERGY_KEEPER_CAPACITY + 2000; //800 - from dead invader every 300 ticks
            if (!ret.incomplete) objRoom.ContainersWaysWaight[i] = fEnergyCapacity*ret.path.length*2/SPAWN_ENERGY_CAPACITY;
            if (!ret.incomplete) objRoom.ContainersWaysCarrierNeed[i] = afunc.GetInfoCarierNeed(objRoom,i); 
        } 

    },

    GetInfoCarierNeed: function(objRoom, IndContainer){
        var oCarrieNeed = new Object();
        if (objRoom.ContainersWaysToStorage[IndContainer] == null){  //если не определен маршрут то
            console.log('ALARM!!! Way for room:'+ objRoom.Name + '  container:' + IndContainer + ' not calculated!');
            if (objRoom.TypeOfRoom<10) {
                oCarrieNeed.NeedCarriers = 1;
                oCarrieNeed.NeedCapasity = 500;
                oCarrieNeed.TimeForDeath = 50;
                oCarrieNeed.BodyCarriers = [MOVE,MOVE,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY];
            }else{
                oCarrieNeed.NeedCarriers = 2;
                oCarrieNeed.NeedCapasity = 750;
                oCarrieNeed.TimeForDeath = 100;
                oCarrieNeed.BodyCarriers = [MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,WORK,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY];
            }
            return oCarrieNeed;
        };
        
        //Определение минимального количества перевозчиков и их нужный объем для обеспечения маршрута
        //1) Делим WayWeight на MaxCapasity получаем минимальное количество перевозчиков
        //2) NeedCarriers = truc(WayWeight/MaxCapasity) + 1   - получаем нужное количество перевозчиков, теперь определяем нужную емкость
        //3) NeedCapasity = trunc(WayWeight/NeedCarriers/100)*100 + 100  - грузоподъемность кратная 100
        //4) TimeForDeath = WayLenght*2+20 - время когда нужно прибить носильщика иначе не хватит времени пройти маршрут
        var fWayWaight = objRoom.ContainersWaysWaight[IndContainer]*1.1;
        if (objRoom.TypeOfRoom<10) { //its MasterRoom
            var fMaxCapasity = Math.min(Math.trunc(objRoom.MaxSpawnEnergy/150)*100,1600);
            oCarrieNeed.NeedCarriers = Math.trunc(fWayWaight/fMaxCapasity) + 1;
            oCarrieNeed.NeedCapasity = Math.trunc(fWayWaight/oCarrieNeed.NeedCarriers/100)*100 + 100;
            oCarrieNeed.TimeForDeath = objRoom.ContainersWaysToStorage[IndContainer].length*2 + 20;
            var fBody = [];
            afunc.AddToBody(fBody,(oCarrieNeed.NeedCapasity)/50,CARRY);
            afunc.AddToBody(fBody,(oCarrieNeed.NeedCapasity)/50/2,MOVE);
            oCarrieNeed.BodyCarriers = fBody; 
        }else{
            var objMasterRoom = Memory.Data[objRoom.MasterRoom];
            var fMaxCapasity = Math.min(Math.trunc((objMasterRoom.MaxSpawnEnergy-200)/150)*100+50,1550); //Start body MOVE,CARRY,WORK
            oCarrieNeed.NeedCarriers = Math.trunc(fWayWaight/fMaxCapasity) + 1;
            oCarrieNeed.NeedCapasity = Math.trunc(fWayWaight/oCarrieNeed.NeedCarriers/50)*50 + 50;
            if ((Math.trunc(oCarrieNeed.NeedCapasity/100)*100)==oCarrieNeed.NeedCapasity) oCarrieNeed.NeedCapasity = oCarrieNeed.NeedCapasity + 50;
            if (oCarrieNeed.NeedCapasity>1550) oCarrieNeed.NeedCapasity = 1550;
            oCarrieNeed.TimeForDeath = objRoom.ContainersWaysToStorage[IndContainer].length*2 + 20;
            var fBody = [WORK,CARRY,MOVE];
            afunc.AddToBody(fBody,(oCarrieNeed.NeedCapasity-50)/50,CARRY);
            afunc.AddToBody(fBody,(oCarrieNeed.NeedCapasity-50)/50/2,MOVE);
            oCarrieNeed.BodyCarriers = fBody;
        } 
        return oCarrieNeed;
    },

    AddToBody: function(fBody, fCount, fPart){
        for (let i=0;i<fCount;i++){
            fBody.push(fPart);
        }
    },

    NeedBorstForCreep_markCreep: function(creep,objRoom) {
        //1) Ищем крипов которые должны бурстится но не помеченные
             //считаем чем буртить, сколько нужно
        if (!creep.spawning) return;
        if (Memory.constants.Boorst[creep.memory.role] == null) return;
        if (objRoom.UpgraderUp == 0 && creep.memory.role == 207) return;
        if (objRoom.BuilderUp == 0 &&  creep.memory.role == 221) return;
        if (creep.memory.needboorst == 1) return;

        creep.memory.needboorst = 1
        creep.memory.boorstmineral = Memory.constants.Boorst[creep.memory.role].mineral;
        var partsCount = sf.GetBodyCountParts(creep.body);
        creep.memory.boorstmineralquantity = partsCount[Memory.constants.Boorst[creep.memory.role].parts]*30;
    },

    NeedBorstForCreep_setQueue: function(creep,objRoom) {
        if (creep.memory.needboorst != 1) return;
        if (creep.memory.boorsted != null) return;
        if (creep.spawning) return;

        if (objRoom.NeedBoorst != '') return;
        if (sf.isTerminalAndStorage(objRoom)==0) return;

        var fLab = Game.getObjectById(objRoom.LabsReaction[0]);
        var fHaveMineral = sf.GetMineralInLab(fLab,creep.memory.boorstmineral) + sf.GetStoredResources(objRoom.oTerminal.store,creep.memory.boorstmineral);

        if (fHaveMineral<creep.memory.boorstmineralquantity){
            creep.memory.boorsted = 'no mineral';
        }else{
            objRoom.NeedBoorst			  = creep.memory.boorstmineral;
            objRoom.NeedBoorstQuantity	  = creep.memory.boorstmineralquantity;
            objRoom.NeedBoorstId	  	  = creep.id;
        }
    },

    NeedBorstForCreep: function(creep,objRoom) {
        if (creep.memory.role == 107){
            objMainRoom = Memory.Data[creep.memory.srcroom];
            afunc.NeedBorstForCreep_markCreep(creep,objMainRoom);
            afunc.NeedBorstForCreep_setQueue(creep,objMainRoom);      
        }else{
            afunc.NeedBorstForCreep_markCreep(creep,objRoom);
            afunc.NeedBorstForCreep_setQueue(creep,objRoom);      
        }
    },

    CalcCreeps: function(creep) {
        objRoom = Memory.Data[creep.memory.dstroom];
        if (objRoom==null) return;

        //Занисим id некоторых крипов
        var fRoleIdToMemory = [221];
        if (fRoleIdToMemory.indexOf(creep.memory.role) != -1){
            if (objRoom.RoleId == null) objRoom.RoleId = new Object();
            if (objRoom.RoleId[creep.memory.role] == null) objRoom.RoleId[creep.memory.role] = [];
            objRoom.RoleId[creep.memory.role].push(creep.id)
        }

        afunc.NeedBorstForCreep(creep,objRoom);

        

        var calcCreepsByRole   = objRoom.CalcRoleCreeps;

		if (objRoom.TypeOfRoom==1){
			if (creep.memory.role>99) return;
			calcCreepsByRole[creep.memory.role]++;
		}

		if (objRoom.TypeOfRoom==2){
			//если есть крипы с ролью меньше 20 то они с типа комнаты 1 нужно переназначить роль
			if  (creep.memory.role<100) afunc.ChangeRoleFromType1ToType2(creep);

			if  ((creep.memory.role<200)||(creep.memory.role>299)) return;
			calcCreepsByRole[creep.memory.role-200]++;
		}

		if ((objRoom.TypeOfRoom==10)||(objRoom.TypeOfRoom==11)||(objRoom.TypeOfRoom==12)||(objRoom.TypeOfRoom==13)||(objRoom.TypeOfRoom==14)){
			if ((creep.memory.role<100)||(creep.memory.role>199)) return;
			calcCreepsByRole[creep.memory.role-100]++;
		}
    },
    
    ChangeRoleFromType1ToType2: function(creep) {
        var objRoom = Memory.Data[creep.memory.dstroom];
		if (creep.memory.role==0) creep.suicide();

		if (creep.memory.role==1) {
			creep.memory.role = 201;
			creep.memory.src = objRoom.Sources[0];
			creep.memory.dst = objRoom.Containers[0];
		};

		if (creep.memory.role==2) {
			creep.memory.role = 202;
			creep.memory.src = objRoom.Containers[0];
			creep.memory.dst = objRoom.Storage;
		};

		if (creep.memory.role==3) {
			creep.memory.role = 203;
			creep.memory.src = objRoom.Sources[1];
			creep.memory.dst = objRoom.Containers[1];
		};

		if (creep.memory.role==4) {
			creep.memory.role = 204;
			creep.memory.src = objRoom.Containers[1];
			creep.memory.dst = objRoom.Storage;
		};

		if (creep.memory.role==5) creep.suicide();
		if (creep.memory.role==6) creep.suicide();
		if (creep.memory.role==7) {
			creep.memory.role = 210;
			creep.memory.dst = objRoom.Storage;
		}
    },
    
    KillOldEmptyCreep: function(creep) { 
		if ( (_.sum(creep.carry) == 0) & (creep.memory.timeForDie > creep.ticksToLive ) ){
			creep.suicide();
		}
    },
    
    utiliteClearMemoryFromDeathCreep: function() {
        for(var i in Memory.creeps) {
            if(!Game.creeps[i]) {
                delete Memory.creeps[i];
            }
        }   
    },
    
	AddToQueue: function(objRoom,_room,_role,_desk) {
		objRoom.Queue.push([_room,_role,_desk]);
    },
    
	Need0ToNeed: function(objRoom) {
		for (let i=0;i<objRoom.Need0.length;i++){
			objRoom.NeedForQueue[i]=objRoom.Need0[i];
		}
    },
    
    Need1ToNeed: function(objRoom) {
		for (let i=0;i<objRoom.Need0.length;i++){
			objRoom.NeedForQueue[i]=objRoom.Need0[i]+objRoom.Need1[i];
		}
    },
    
    Need2ToNeed: function(objRoom) {
		for (let i=0;i<objRoom.Need0.length;i++){
			objRoom.NeedForQueue[i]=objRoom.Need0[i]+objRoom.Need1[i]+objRoom.Need2[i];
		}
    },
    
    GetSpawnRoom: function(objRoom) {
		for (let i=0; i<objRoom.Spawns.length;i++){
			fSpawn = Game.getObjectById(objRoom.Spawns[i]);
			if (fSpawn.spawning == null){
				return fSpawn;	
			}
		}
		return null;
    },
    
    SpawnCreep: function(_spawn,_body,_role, _src, _dst, _dstroom, _srcroom, _info , _timetodie) {
        var fSwawnIsStart = 0;
		if (_spawn.canCreateCreep(_body, 'mst' + Game.time) == OK) {
            fSwawnIsStart = 1;
			_spawn.createCreep(_body, 'mst' + Game.time, {role: _role, src: _src, dst: _dst, dstroom: _dstroom, srcroom: _srcroom,info: _info, timeForDie: _timetodie});
        };
        
        //Тестировчные функции
        if (fSwawnIsStart == 1){
            objRoom = Memory.Data[_dstroom];
            if (objRoom.TypeOfRoom==1){
                tests.testRoomProfit_Minus(_dstroom, afunc.GetCostOfCreep(_body));
                if (_role == 7) tests.testRoomProfitGarbage_Minus(_dstroom, afunc.GetCostOfCreep(_body));
            };

            if (objRoom.TypeOfRoom==2){
                var fRole = [201,202,203,204,214];
                tests.testRoomProfit_Minus(_dstroom, afunc.GetCostOfCreep(_body));
                if (_role == 210) tests.testRoomProfitGarbage_Minus(_dstroom, afunc.GetCostOfCreep(_body));
                if (fRole.indexOf(_role)!=-1) tests.testRoomProfit_Minus(_dstroom, afunc.GetCostOfCreep(_body));
            };

            if ((objRoom.TypeOfRoom==10)||(objRoom.TypeOfRoom==12)||(objRoom.TypeOfRoom==14)){
                tests.testRoomProfit_Minus(_dstroom, afunc.GetCostOfCreep(_body));
            };
        }
    },

    GetCostOfCreep: function(fBody){
		var fBodyPart = [MOVE,WORK,CARRY,ATTACK,RANGED_ATTACK,HEAL,CLAIM,TOUGH];
		var fBodyCost = [  50, 100,   50,    80,          150, 250,  600,   10];
		var fCost = 0;
		for (var i=0;i<fBody.length;i++) {
			var fInd = fBodyPart.indexOf(fBody[i]);
			if (fInd!=-1) fCost = fCost + fBodyCost[fInd];
		}
		return (fCost); 
	},
    
    GetNearestContainerThisMinEnergy: function(fPos,objRoom,minEnergy,maxIndContainer){
        if (maxIndContainer == null) maxIndContainer = objRoom.Containers.length; 
        var fMinDistance = 10000;
        var fContainer = null;
        for (let i=0;i<maxIndContainer;i++){
            if (objRoom.Containers[i]==null) continue;
            fTmpContainer = Game.getObjectById(objRoom.Containers[i]);
            if (fTmpContainer.store.energy<minEnergy) continue;
            fTmpDistance = afunc.CalcDistancePos(fPos,fTmpContainer.pos);
            if (fTmpDistance<fMinDistance) {
                fMinDistance = fTmpDistance;
                fContainer = fTmpContainer;   
            }
        }
        return fContainer;
    },

    FindStructureNearPos: function(fPos,fTypeOfStructure,fMaxDistance) {
        var struct = fPos.findClosestByRange(FIND_STRUCTURES, {
            filter: (structure) => {
                return (structure.structureType == fTypeOfStructure);
            }
        });
        if (fMaxDistance==null) return struct;
        if (struct==null) return null; 
        if (afunc.CalcDistancePos(struct.pos,fPos)>fMaxDistance) return null;
        return struct;
	},

	FindHostileCreepsNearPos: function(fPos,fMaxDistance) {
        var target = fPos.findClosestByRange(FIND_HOSTILE_CREEPS);
        if (fMaxDistance==null) return target;
        if (target==null) return null; 
        if (afunc.CalcDistancePos(target.pos,fPos)>fMaxDistance) return null;
        return target;
    },
    
    RecalculateAllWays: function(fRecalculate){
        if (fRecalculate==1) {
            for (let i=0;i<Memory.MainRooms.length;i++){
                var objRoomMain = Memory.Data[Memory.MainRooms[i]];
                if (objRoomMain.TypeOfRoom == 1) continue;
                afunc.CalculateWayForRoom(objRoomMain,1);
                messenger.log('WAY','RECALCULATE 1 WAYS ROOM:' + objRoomMain.Describe,COLOR_YELLOW);
                objRoomMain.ContainersWaysNeedRecalculate == 0
                for (let j=0;j<objRoomMain.SlaivRooms.length;j++){
                    var objRoomSlaiv = Memory.Data[objRoomMain.SlaivRooms[j]];
                    if (objRoomSlaiv.TypeOfRoom == 11 || objRoomSlaiv.TypeOfRoom == 13) continue;
                    afunc.CalculateWayForRoom(objRoomSlaiv,1);
                    messenger.log('WAY','RECALCULATE 1 WAYS ROOM:' + objRoomSlaiv.Describe,COLOR_YELLOW);
                    objRoomSlaiv.ContainersWaysNeedRecalculate == 0
                }
            }
            return;   
        }

        for (let i=0;i<Memory.MainRooms.length;i++){
            try{
                var objRoomMain = Memory.Data[Memory.MainRooms[i]];
                if (objRoomMain.ContainersWaysNeedRecalculate == 0) continue;
                if (objRoomMain.TypeOfRoom == 1) continue;
                afunc.CalculateWayForRoom(objRoomMain,1);
                messenger.log('WAY','RECALCULATE WAYS ROOM:' + objRoomMain.Describe,COLOR_YELLOW);
                objRoomMain.ContainersWaysNeedRecalculate = 0
			    for (let j=0;j<objRoomMain.SlaivRooms.length;j++){
                    var objRoomSlaiv = Memory.Data[objRoomMain.SlaivRooms[j]];
                    if (objRoomSlaiv.ContainersWaysNeedRecalculate == 0) continue;
                    if (objRoomSlaiv.TypeOfRoom == 11 || objRoomSlaiv.TypeOfRoom == 13) continue;
                    afunc.CalculateWayForRoom(objRoomSlaiv,1);
                    messenger.log('WAY','RECALCULATE WAYS ROOM:' + objRoomSlaiv.Describe.COLOR_YELLOW);
                    objRoomSlaiv.ContainersWaysNeedRecalculate = 0
                }
            }catch(err){
                afunc.ErrorMessage('ERROR!!! Recalculate ways:' + Memory.MainRooms[i], err);
            }
        }

		for (let j=0;j<Memory.SlaivRooms.length;j++){
            try{
                var objRoomSlaiv = Memory.Data[Memory.SlaivRooms[j]];
                if (objRoomSlaiv.ContainersWaysNeedRecalculate == 0) continue;
                if (objRoomSlaiv.TypeOfRoom == 11 || objRoomSlaiv.TypeOfRoom == 13) continue;
                afunc.CalculateWayForRoom(objRoomSlaiv,1);
                messenger.log('WAY','RECALCULATE WAYS ROOM:' + objRoomSlaiv.Describe,COLOR_YELLOW);
                objRoomSlaiv.ContainersWaysNeedRecalculate = 0
            }catch(err){
                afunc.ErrorMessage('ERROR!!! Recalculate ways:' + Memory.SlaivRooms[j], err);
            }
        }
        
    },

    TerminalTransferEnergy: function(){
        const TransferQuantity = 50000;
        const TransferWhenWeHave = 70000;
        var TerminalFrom = null;
        var objRoomFrom = null;
        var TerminalTo = null;
        var objRoomTo = null;

        //сначала комнаты которые делают прямые поставки
        for (let i=0;i<Memory.MainRooms.length;i++) {
            objRoomFrom = Memory.Data[Memory.MainRooms[i]];
            if (objRoomFrom.SentEnergyToRoom == '') continue;
            objRoomTo = Memory.Data[objRoomFrom.SentEnergyToRoom];
            TerminalFrom = Game.getObjectById(objRoomFrom.Terminal);
            TerminalTo = Game.getObjectById(objRoomTo.Terminal);
            if (TerminalTo == null || TerminalFrom == null) continue;
            if (TerminalFrom.store[RESOURCE_ENERGY]>=(TransferWhenWeHave) && _.sum(TerminalTo.store)<(TERMINAL_CAPACITY - TransferQuantity) ){
                if ((TerminalFrom.cooldown==0)&&(TerminalTo.cooldown==0)){
                    messenger.log('TRANS','Direct Transfer from:' + objRoomFrom.Name + ' to:' + objRoomTo.Name + ' qantity:' + TransferQuantity);
                    TerminalFrom.send(RESOURCE_ENERGY,TransferQuantity,objRoomTo.Name);	
                }
            }
        }

        var TerminalFrom = null;
        var objRoomFrom = null;
        var TerminalTo = null;
        var objRoomTo = null;

        for (let i=0;i<Memory.MainRooms.length;i++) {
            var objRoom = Memory.Data[Memory.MainRooms[i]];
            var fController = Game.getObjectById(objRoom.Controller);
            var fTerminal = Game.getObjectById(objRoom.Terminal);
            var fStorage = Game.getObjectById(objRoom.Storage);
            if ((fController == null)||(fTerminal == null)||(fStorage == null)) continue;
            if (fTerminal.store[RESOURCE_ENERGY]>=(TransferWhenWeHave) && fStorage.store[RESOURCE_ENERGY] >= objRoom.StorageEnergyMax){
                TerminalFrom = fTerminal;
                objRoomFrom = objRoom;
                break;	
            }
        }

        if (TerminalFrom!=null){
            messenger.log('TRANS','Ready for transfer from room:' + objRoomFrom.Name);
            var CostTransfer = TransferQuantity;
            var fMinMinerals = 1300000;
            var fToRoom = null;
            for (let i=0;i<Memory.MainRooms.length;i++) {
                var objRoom = Memory.Data[Memory.MainRooms[i]];
                var fController = Game.getObjectById(objRoom.Controller);
                var fTerminal = Game.getObjectById(objRoom.Terminal);
                var fStorage = Game.getObjectById(objRoom.Storage);
                if ((fController == null)||(fTerminal == null)||(fStorage == null)) continue;

                //Ищем комнату где минимум минералов


                if ((_.sum(fTerminal.store)<(TERMINAL_CAPACITY - TransferQuantity))&&(_.sum(fStorage.store)<700000) && fStorage.store.energy<=objRoom.StorageEnergyMax){
                    var fMinerals = fStorage.store.energy + fTerminal.store.energy;
                    if (fMinerals<fMinMinerals){
                        fMinMinerals = fMinerals;
                        fToRoom = objRoom;
                    }
                }
            }

            if (fToRoom != null){
                var fTerminal = Game.getObjectById(fToRoom.Terminal);
                TerminalTo = fTerminal;
                objRoomTo = fToRoom;
            }
        }

        if ((TerminalFrom!=null)&&(objRoomTo!=null)){
            messenger.log('TRANS','Ready for transfer to room:' + objRoomTo.Name + ' cost:' + CostTransfer);
            if ((TerminalFrom.cooldown==0)&&(TerminalTo.cooldown==0)){
                var fTransferText = 'Transfer from:' + objRoomFrom.Name + ' to:' + objRoomTo.Name + ' qantity:' + TransferQuantity;
                messenger.log('TRANS',fTransferText);
                Memory.logs.transfers.push(fTransferText);
                TerminalFrom.send(RESOURCE_ENERGY,TransferQuantity,objRoomTo.Name);	
            }
        }      
    },

    TerminalTransferMinerals: function(){
        const TransferMinQuantity = Memory.constants.TransferMinQuantity;
        const TransferMaxQuantity = 10000;
        
        var TerminalFrom = null;
        var objRoomFrom = null;
        var TerminalTo = null;
        var objRoomTo = null;

        for (let i=0;i<Memory.MainRooms.length;i++) {
            var objRoomTo = Memory.Data[Memory.MainRooms[i]];
            if (objRoomTo.MineralNeed == null) continue;
            var fTerminalTo = Game.getObjectById(objRoomTo.Terminal);
            var fStorageTo = Game.getObjectById(objRoomTo.Storage);
            if ((fTerminalTo == null)||(fStorageTo == null)) continue;

            var keysMineralNeed = Object.keys(objRoomTo.MineralNeed);
            for (let mi=0;mi<keysMineralNeed.length;mi++){
                var fMineral = keysMineralNeed[mi];
                var fNeed = objRoomTo.MineralNeed[fMineral];
                var fHave = sf.GetStoredResources(fTerminalTo.store,fMineral) + sf.GetStoredResources(fStorageTo.store,fMineral);
                var fNeedTransfer = fNeed - fHave;
                if (fNeedTransfer<=0) continue;
                if (fNeedTransfer>TransferMaxQuantity) fNeedTransfer=TransferMaxQuantity;
                if (fNeedTransfer >= TransferMinQuantity) {
                    
                    messenger.log('TRANS','Transfer room:' + objRoomTo.Name + ' mineral:' + fMineral + ' need:' + fNeed + ' have:' + fHave + ' transfer:'+ fNeedTransfer);
                    
                    //Ищем откуда трансферить на входе fNeedTransfer,fMineral,TerminalTo,objRoomTo
                    for (let ifrom=0;ifrom<Memory.MainRooms.length;ifrom++) {
                        if (Memory.MainRooms[ifrom] == objRoomTo.Name) continue;
                        var objRoomFrom = Memory.Data[Memory.MainRooms[ifrom]];
                        var fTerminalFrom = Game.getObjectById(objRoomFrom.Terminal);
                        var fStorageFrom = Game.getObjectById(objRoomFrom.Storage);
                        if ((fTerminalFrom == null)||(fStorageFrom == null)) continue;
                        var fMineralHaveFrom = sf.GetStoredResources(fTerminalFrom.store,fMineral) + sf.GetStoredResources(fStorageFrom.store,fMineral);
                        if (fMineralHaveFrom<TransferMinQuantity) continue;

                        
                        var fMineralNeedFrom = 0;
                        if (objRoomFrom.MineralNeed != null) {
                            if (objRoomFrom.MineralNeed[fMineral]!=null) fMineralNeedFrom = objRoomFrom.MineralNeed[fMineral];
                        }
                        
                        
                        var fCanTransfer = fMineralHaveFrom - fMineralNeedFrom;
                        if (fCanTransfer<0) fCanTransfer = 0;
                        if (fCanTransfer>fNeedTransfer) fCanTransfer=fNeedTransfer;
                        if (fCanTransfer>TransferMaxQuantity) fCanTransfer=TransferMaxQuantity;
                        if (fCanTransfer == 0) continue;
                        
                        //console.log('Transfer from room:' + objRoomFrom.Name + ' mineral:' + fMineral + ' quantity:' + fCanTransfer);
                        if ((fTerminalFrom.cooldown==0)&&(fTerminalTo.cooldown==0)){
                            var fTransferText = 'Transfer from:' + objRoomFrom.Name + ' to:' + objRoomTo.Name + ' qantity:' + fCanTransfer + ' mineral:' + fMineral;
        
                            
                            var fErr = fTerminalFrom.send(fMineral,fCanTransfer,objRoomTo.Name);
                            messenger.log('TRANS','TRY:'+fTransferText);
                            //if (objRoomFrom.Name == 'W46N7' && fMineral == 'UH') console.log('sssssssssssssssssssssssss' + fTerminalFrom)
                            if (fErr == OK){
                                messenger.log('TRANS',fTransferText);
                                fNeedTransfer = fNeedTransfer - fCanTransfer;
                                Memory.logs.transfers.push(fTransferText);
                                return; // один трансфер за тик
                            }
                        }
                        if (fNeedTransfer<=0) break;
                    }
                
                
                
                }
            }
        }

        

    },

    SetCostMatrixLairs(costs,roomName){
        let room = Game.rooms[roomName];
        if (!room) return;
        var fRadius = 4;
        // var lair = Game.rooms[roomName].find(FIND_STRUCTURES, {
        //     filter: (structure) => {
        //         return (structure.structureType == STRUCTURE_KEEPER_LAIR);
        //     }
        // });
        
        // if (lair.length == 0) return;
        
        // for (let i=0;i<lair.length;i++){
        //     fLair = lair[i];
        //     if (fLair == null) continue;
        //     for (let dx=(fLair.pos.x-fRadius); dx<=(fLair.pos.x+fRadius);dx++){
        //         if (dx<0 || dx>49) continue;
        //         for (let dy=(fLair.pos.y-fRadius); dy<=(fLair.pos.y+fRadius);dy++){
        //             if (dy<0 || dy>49) continue;
        //             //console.log('room:' + roomName +' x:' + dx + ' y:' + dy)
        //             costs.set(dx, dy, 0xff);
        //         }
        //     }
        // } 
        
        var lair = Game.rooms[roomName].find(FIND_HOSTILE_CREEPS);

        if (lair.length == 0) return;
        
        for (let i=0;i<lair.length;i++){
            fLair = lair[i];
            if (fLair == null) continue;
            for (let dx=(fLair.pos.x-fRadius); dx<=(fLair.pos.x+fRadius);dx++){
                if (dx<0 || dx>49) continue;
                for (let dy=(fLair.pos.y-fRadius); dy<=(fLair.pos.y+fRadius);dy++){
                    if (dy<0 || dy>49) continue;
                    //console.log('room:' + roomName +' x:' + dx + ' y:' + dy)
                    costs.set(dx, dy, 0xff);
                }
            }
        } 
    },

    GetWayFromPosToPosDange: function(fPos1,fPos2,useLairsDange) {
        let goals = { pos: fPos2, range: 1 };
  
        let ret = PathFinder.search(
            fPos1, goals,
            {
                plainCost: 1,
                swampCost: 5,
                maxOps:  10000,
                roomCallback: function(roomName) {
                    let room = Game.rooms[roomName];
                    if (!room) return;        
                    let costs = new PathFinder.CostMatrix;
                    room.find(FIND_STRUCTURES).forEach(function(struct) {
                        if (struct.structureType === STRUCTURE_ROAD) {
                            costs.set(struct.pos.x, struct.pos.y, 1);
                        } else if (struct.structureType !== STRUCTURE_CONTAINER &&
                                (struct.structureType !== STRUCTURE_RAMPART ||
                                !struct.my)) {
                            costs.set(struct.pos.x, struct.pos.y, 0xff);
                        }
                    });
                    if (useLairsDange == 1) afunc.SetCostMatrixLairs(costs,roomName);
                    return costs;
                },
            }
        );
        return ret;      
    },

    ErrorMessage: function(fText,err) {
        var fAllText = fText + ' Cause error:' + err.name + '  stack:' + err.stack;
        Memory.logs.errors.push(fAllText)
        console.log('<font color="yellow">'+fAllText + '</font>');
    },

    BuildSafeWay: function(FromObjRoom,ToObjRoom,idFrom,idTo) {
        if (ToObjRoom.SafeWay != null) {console.log('way to room:'+ ToObjRoom.Name + ' is already build!'); return;}
        objFrom = Game.getObjectById(idFrom); if (objFrom == null){ console.log('cant find id from:'+ idFrom); return;}
        objTo = Game.getObjectById(idTo); if (objTo == null){ console.log('cant find id to:'+ idTo); return;}
        var ret = afunc.GetWayFromPosToPosDange(objFrom.pos,objTo.pos,1);
        if (ret.incomplete) {console.log('cant build safe way'); return;}
        console.log('build safe way');
        ToObjRoom.SafeWay = ret.path;
    },

    CalculateSafeZoneForRoom(objRoom){
        let room = Game.rooms[objRoom.Name];
        if (!room) return;        
        let costs = new PathFinder.CostMatrix;
        room.find(FIND_STRUCTURES).forEach(function(struct) {
            if (struct.structureType === STRUCTURE_ROAD) {
                costs.set(struct.pos.x, struct.pos.y, 1);
            } else if (struct.structureType !== STRUCTURE_CONTAINER && (struct.structureType !== STRUCTURE_RAMPART || !struct.my)) {
                costs.set(struct.pos.x, struct.pos.y, 0xff);
            }
        });
        
        objRoom.safezone = costs.serialize();
    },
};

module.exports = afunc;