var sf                      = require('modul.smallfunc');
var messenger                      	= require('modul.messenger');

var modul_Tests = {
    //Test Profit
    
    //role.C_carrier
    //main
    //modul.C_creeps
    //modul.B_creeps
    //role.B_carrier
    testRoomProfit_Eraze: function(fSetZero) {
        
        Memory.test.ProfitTestRoom = Memory.MainRooms.concat(Memory.SlaivRooms);
        
        for (var i = 0; i < Memory.test.ProfitTestRoom.length; i++) {
            if (Memory.test[Memory.test.ProfitTestRoom[i]]==null) {
                Memory.test[Memory.test.ProfitTestRoom[i]] = new Object();
                Memory.test[Memory.test.ProfitTestRoom[i]]._ProfitP = 0;
                Memory.test[Memory.test.ProfitTestRoom[i]]._ProfitM = 0;
                Memory.test[Memory.test.ProfitTestRoom[i]]._ProfitTiksStart = Game.time;
                Memory.test[Memory.test.ProfitTestRoom[i]]._ProfitGarbageP = 0;
                Memory.test[Memory.test.ProfitTestRoom[i]]._ProfitGarbageM = 0; 
                Memory.test[Memory.test.ProfitTestRoom[i]]._ProftCreepHistory = new Object();  
            }

            if (fSetZero==1) {
                Memory.test.History = new Object();
                Memory.test[Memory.test.ProfitTestRoom[i]]._ProfitP = 0;
                Memory.test[Memory.test.ProfitTestRoom[i]]._ProfitM = 0;
                Memory.test[Memory.test.ProfitTestRoom[i]]._ProfitGarbageP = 0;
                Memory.test[Memory.test.ProfitTestRoom[i]]._ProfitGarbageM = 0;
                Memory.test[Memory.test.ProfitTestRoom[i]]._ProfitTiksStart = Game.time;
                Memory.test[Memory.test.ProfitTestRoom[i]]._ProftCreepHistory = new Object(); 
            }
        };
    },
    
    testRoomProfit_Plus: function(_name,_profit) {
        if (Memory.test.ProfitTestRoom.indexOf(_name)==-1) return;
        Memory.test[_name]._ProfitP = Memory.test[_name]._ProfitP + _profit;
        Memory.test[_name]._ProfitTiksEnd   = Game.time;
    },
    
    testRoomProfit_Minus: function(_name,_profit) {
        if (Memory.test.ProfitTestRoom.indexOf(_name)==-1) return;
        Memory.test[_name]._ProfitM = Memory.test[_name]._ProfitM + _profit;
        Memory.test[_name]._ProfitTiksEnd   = Game.time;
    },

    testRoomProfitGarbage_Plus: function(_name,_profit) {
        if (Memory.test.ProfitTestRoom.indexOf(_name)==-1) return;
        Memory.test[_name]._ProfitGarbageP = Memory.test[_name]._ProfitGarbageP + _profit;
    },
    
    testRoomProfitGarbage_Minus: function(_name,_profit) {
        if (Memory.test.ProfitTestRoom.indexOf(_name)==-1) return;
        Memory.test[_name]._ProfitGarbageM = Memory.test[_name]._ProfitGarbageM + _profit;
    },

    testRoomProfit_Show: function(objRoom,objTestData,fPrefix) {
         var _log = '';
         var fEnd = Game.time;
        if (fPrefix!=null) {
            _log = _log + fPrefix;
            fEnd = fPrefix
        }
                 
        var _profit = Math.round((objTestData._ProfitP - objTestData._ProfitM)/(fEnd - objTestData._ProfitTiksStart)*1500);
        _log = _log + '-----> Profit:' + sf.padLeft(objRoom.Describe,5) + ' (' + objRoom.Name + ')  (' + sf.padLeft(''+_profit,8) + ' per. 1500 ticks)  ';
        _log = _log + '| tiks:' + sf.padLeft(''+(fEnd - objTestData._ProfitTiksStart),6);
        _log = _log + '| +:' + sf.padLeft(''+objTestData._ProfitP,8);
        _log = _log + '| -:' + sf.padLeft(''+objTestData._ProfitM,8);
        _log = _log + '| P:' + sf.padLeft(''+(objTestData._ProfitP - objTestData._ProfitM),8);
                
        if (objRoom.TypeOfRoom<10){
            _log = _log + '  Garbage: + ' + objTestData._ProfitGarbageP;
            _log = _log + ' : - ' + objTestData._ProfitGarbageM;
            _log = _log + ' : profit: ' + (objTestData._ProfitGarbageP - objTestData._ProfitGarbageM);
        }
        console.log(_log);  
    },

    testRoomProfit_Log: function(fShowHistory) {
        var fDoHistoryTicks = 6000;
        if ((Math.trunc(Game.time/fDoHistoryTicks)*fDoHistoryTicks)==Game.time){
            if (Memory.test.History == null) Memory.test.History = new Object();
            for (let i=0;i<Memory.test.ProfitTestRoom.length;i++){
                if (Memory.test.History[Memory.test.ProfitTestRoom[i]] == null) Memory.test.History[Memory.test.ProfitTestRoom[i]] = new Object();
                Memory.test.History[Memory.test.ProfitTestRoom[i]][Game.time] = new Object();
                Memory.test.History[Memory.test.ProfitTestRoom[i]][Game.time]._ProfitP = Memory.test[Memory.test.ProfitTestRoom[i]]._ProfitP;
                Memory.test.History[Memory.test.ProfitTestRoom[i]][Game.time]._ProfitM = Memory.test[Memory.test.ProfitTestRoom[i]]._ProfitM;
                Memory.test.History[Memory.test.ProfitTestRoom[i]][Game.time]._ProfitGarbageP = Memory.test[Memory.test.ProfitTestRoom[i]]._ProfitGarbageP;
                Memory.test.History[Memory.test.ProfitTestRoom[i]][Game.time]._ProfitGarbageM = Memory.test[Memory.test.ProfitTestRoom[i]]._ProfitGarbageM;
                Memory.test.History[Memory.test.ProfitTestRoom[i]][Game.time]._ProfitTiksStart = Memory.test[Memory.test.ProfitTestRoom[i]]._ProfitTiksStart;
                //обнулили
                Memory.test[Memory.test.ProfitTestRoom[i]]._ProfitP = 0;
                Memory.test[Memory.test.ProfitTestRoom[i]]._ProfitM = 0;
                Memory.test[Memory.test.ProfitTestRoom[i]]._ProfitGarbageP = 0;
                Memory.test[Memory.test.ProfitTestRoom[i]]._ProfitGarbageM = 0;
                Memory.test[Memory.test.ProfitTestRoom[i]]._ProfitTiksStart = Game.time;            
            }
        }

        if (fShowHistory==1){
            for (let i=0;i<Memory.test.ProfitTestRoom.length;i++){
                if (Memory.test.History[Memory.test.ProfitTestRoom[i]]!=null){
                    var fKeys =Object.keys(Memory.test.History[Memory.test.ProfitTestRoom[i]]);
                    for (let j=0;j<fKeys.length;j++){
                        objRoom = Memory.Data[Memory.test.ProfitTestRoom[i]]; 
                        objTestData = Memory.test.History[Memory.test.ProfitTestRoom[i]][fKeys[j]];
                        modul_Tests.testRoomProfit_Show(objRoom,objTestData,fKeys[j])     
                    }
                }
                objRoom = Memory.Data[Memory.test.ProfitTestRoom[i]]; 
                objTestData = Memory.test[Memory.test.ProfitTestRoom[i]];
                modul_Tests.testRoomProfit_Show(objRoom,objTestData)             
            }
        }else{
            for (let i=0;i<Memory.test.ProfitTestRoom.length;i++){
                objRoom = Memory.Data[Memory.test.ProfitTestRoom[i]]; 
                objTestData = Memory.test[Memory.test.ProfitTestRoom[i]];
                if (Memory.ShowInfoProfit.length != 0) {
                    fShowHistory = Memory.ShowInfoProfit.indexOf(objRoom.Name);
                }
                if (fShowHistory != -1) modul_Tests.testRoomProfit_Show(objRoom,objTestData)             
            }
        }
    },



    testRoomProfit_CreepCarry: function(creep) {
        var fTestRole = [7,210,102,104,106,202,204,214];
        if (fTestRole.indexOf(creep.memory.role) != -1) {
            if (Memory.test[creep.memory.dstroom]._ProftCreepHistory[creep.name] != null) {
                var fEnergyStart = Memory.test[creep.memory.dstroom]._ProftCreepHistory[creep.name];
                var fEnergyEnd = creep.carry.energy;
                var fTrasfer = fEnergyStart-fEnergyEnd;
                if (fEnergyStart!=fEnergyEnd) {
                    Memory.test[creep.memory.dstroom]._ProftCreepHistory[creep.name] = fEnergyEnd;
                    if ((fTrasfer>0)&&((creep.memory.role == 7)||(creep.memory.role == 210))) {
                        modul_Tests.testRoomProfitGarbage_Plus(creep.memory.dstroom,fTrasfer);
                        return
                    }
                    if (fTrasfer>3) {
                        modul_Tests.testRoomProfit_Plus(creep.memory.dstroom,fTrasfer);
                        return
                    }
                }
            }else{
                Memory.test[creep.memory.dstroom]._ProftCreepHistory[creep.name] = creep.carry.energy;
            }
        }
    },
    //END Test Profit


	StartAllTest: function(fSetZero) {
        if (Memory.test == null) Memory.test = new Object();

        modul_Tests.testRoomProfit_Eraze(fSetZero);
        if (fSetZero==1) {
            Memory.test.CpuUse_HistoryLenght = 100;
            Memory.test.CpuUse_History = new Array(Memory.test.CpuUse_HistoryLenght);
            Memory.test.CpuUse_Sum = 0;
            Memory.test.CpuUse_Tiks = 0;
        }else{
            if (Memory.test.CpuUse_HistoryLenght==null) Memory.test.CpuUse_HistoryLenght = 100;
            if (Memory.test.CpuUse_History==null) Memory.test.CpuUse_History = new Array(Memory.test.CpuUse_HistoryLenght);
            if (Memory.test.CpuUse_Sum==null) Memory.test.CpuUse_Sum = 0;
            if (Memory.test.CpuUse_Tiks==null) Memory.test.CpuUse_Tiks = 0;
        }
    }, 
    
    DoTestAtStart: function() {
        if (Memory.test == null) Memory.test = new Object();
        Memory.test.CpuUse_CPUStart = Game.cpu.getUsed();
    },

    DoTestAtEnd: function() {
        Memory.test.CpuUse_CPUEnd = Game.cpu.getUsed();
        var fUsedCPU = Memory.test.CpuUse_CPUEnd-Memory.test.CpuUse_CPUStart;
        var fShitf = Memory.test.CpuUse_History.shift();
        Memory.test.CpuUse_History.push(fUsedCPU);
        
        Memory.test.CpuUse_Tiks++;
        Memory.test.CpuUse_Sum = Memory.test.CpuUse_Sum + fUsedCPU;

        if (fShitf!=null) {
            Memory.test.CpuUse_Tiks--;
            Memory.test.CpuUse_Sum = Memory.test.CpuUse_Sum - fShitf;
        }
        
        console.log('Middle use: ' + Math.round(Memory.test.CpuUse_Sum/Memory.test.CpuUse_Tiks*100)/100);
        console.log('Now    use: ' + Math.round(fUsedCPU*100)/100); 
    },

    DoTestCode: function() {
        
        var _CpuStart=Game.cpu.getUsed();
        
        //test code
        for (var i=0;i<1000;i++) {
            var targets = Game.rooms['W4N3'].find(FIND_CONSTRUCTION_SITES);
            //var target = creep.pos.findClosestByRange(FIND_CONSTRUCTION_SITES);
        }
        //test code
        var _CpuEnd=Game.cpu.getUsed();
         
        console.log('TEST CPU:' + (_CpuEnd-_CpuStart));
    },

    CreepRoleCalcCPUObnul: function() {
        Memory.testCreepRoleCalcCPU = new Array(20);
        Memory.testCreepRoleCalcStartTicks = Game.time;
        for (var i = 0;i<Memory.testCreepRoleCalcCPU.length;i++) Memory.testCreepRoleCalcCPU[i] = 0;
    },

    CreepRoleCalcCPUStart: function() {
        Memory.testCreepRoleCalcCPUStart = Game.cpu.getUsed();
    },

    CreepRoleCalcCPUEnd: function(creep) {
        Memory.testCreepRoleCalcCPUEnd = Game.cpu.getUsed();
        if (creep.memory.role<20) {
            Memory.testCreepRoleCalcCPU[creep.memory.role] = Memory.testCreepRoleCalcCPU[creep.memory.role]  -  Memory.testCreepRoleCalcCPUStart + Memory.testCreepRoleCalcCPUEnd;   
        }
    },

    CreepRoleCalcCPUShow: function() {
        var fSum = 0;
        for (var i = 0;i<Memory.testCreepRoleCalcCPU.length;i++) {
            if (Memory.testCreepRoleCalcCPU[i] != 0) {
                fSum = fSum + Memory.testCreepRoleCalcCPU[i];
                console.log('use creep role:' + i + '  :' + Math.round(Memory.testCreepRoleCalcCPU[i]/(Game.time - Memory.testCreepRoleCalcStartTicks)*100)/100);
            }
        }
        console.log('use creep all:' + Math.round(fSum/(Game.time - Memory.testCreepRoleCalcStartTicks)*100)/100);

    },

    GetWayFromPosToPos: function(fPos1,fPos2) {
        let goals = { pos: fPos2, range: 1 };
  
        let ret = PathFinder.search(
            fPos1, goals,
            {
                // We need to set the defaults costs higher so that we
                // can set the road cost lower in `roomCallback`
                plainCost: 2,
                swampCost: 2,
                maxOps:  30000,

                roomCallback: function(roomName) {

                    let room = Game.rooms[roomName];
                    // In this example `room` will always exist, but since 
                    // PathFinder supports searches which span multiple rooms 
                    // you should be careful!
                    if (!room) return;
        
                    let costs = new PathFinder.CostMatrix;

                    room.find(FIND_STRUCTURES).forEach(function(struct) {
                        if (struct.structureType === STRUCTURE_ROAD) {
                            // Favor roads over plain tiles
                            costs.set(struct.pos.x, struct.pos.y, 1);
                        } else if (struct.structureType !== STRUCTURE_CONTAINER &&
                                (struct.structureType !== STRUCTURE_RAMPART ||
                                !struct.my)) {
                                // Can't walk through non-walkable buildings
                            costs.set(struct.pos.x, struct.pos.y, 0xff);
                        }
                    });

                    // Avoid creeps in the room
                    //room.find(FIND_CREEPS).forEach(function(creep) {
                    //  costs.set(creep.pos.x, creep.pos.y, 0xff);
                    //});

                    return costs;
                },
            }
        );
        return ret;      
    },

    GetOptimalCarrier: function(fId1,fId2) {
        var fObject1 = Game.getObjectById(fId1);
        var fObject2 = Game.getObjectById(fId2);

        if (!fObject1) {
            console.log('Не найден объект №1');
            return;
        }

        if (!fObject2) {
            console.log('Не найден объект №2');
            return;
        }

        var ret = modul_Tests.GetWayFromPosToPos(fObject1.pos,fObject2.pos);

        //console.log(ret.path);
        //console.log(ret.path.length); //длина маршрута
        //проведем расчет сколько нужно носильщиков для обеспечения доставки
        var fTwoWay = ret.path.length*2;
        var fCarry = 500;
        var fNeedCarry = 4000;
        var fNeedCarrier = Math.round(fNeedCarry/(fCarry/fTwoWay*300)*100)/100;
        console.log('На єтом маршруте нужно:' + fNeedCarrier + ' перевозчиков с объемом:' + fCarry);
        var fNeedVolume = Math.round(fNeedCarry/300*fTwoWay/2*100)/100;
        console.log('Или на єтом маршруте нужно 2 носильщика с объемом:' + fNeedVolume);
    

        //let pos = ret.path[0];
        //creep.move(creep.pos.getDirectionTo(pos));
    },

    BuildRoadFromId1ToId2: function(fId1,fId2) {
        //if (Memory.AutomatBuildRoute != 1) return;
        Memory.AutomatBuildRoute = 0;
        var fObject1 = Game.getObjectById(fId1);
        var fObject2 = Game.getObjectById(fId2);

        if (!fObject1) {
            console.log('Не найден объект №1');
            return;
        }

        if (!fObject2) {
            console.log('Не найден объект №2');
            return;
        }

        var ret = modul_Tests.GetWayFromPosToPos(fObject1.pos,fObject2.pos);

        

        for (var i=0;i<ret.path.length;i++){
            
            var fFind = Game.rooms[ret.path[i].roomName].lookForAt(LOOK_STRUCTURES,ret.path[i], {
            	filter: (structure) => {
                	return (structure.structureType == STRUCTURE_ROAD);
            	}
            });
            
            if (fFind.length == 0) {
                var fFind = Game.rooms[ret.path[i].roomName].lookForAt(LOOK_CONSTRUCTION_SITES,ret.path[i]);    
            }
            //console.log(fFind.length);
            if (fFind.length == 0) {
                console.log('строим'+ ret.path[i]);
                Game.rooms[ret.path[i].roomName].createConstructionSite(ret.path[i], STRUCTURE_ROAD);
            }
        }

        //console.log(ret.path);
        //console.log(ret.path.length); //длина маршрута
        //проведем расчет сколько нужно носильщиков для обеспечения доставки
        // var fTwoWay = ret.path.length*2;
        // var fCarry = 500;
        // var fNeedCarry = 4000;
        // var fNeedCarrier = Math.round(fNeedCarry/(fCarry/fTwoWay*300)*100)/100;
        // console.log('На єтом маршруте нужно:' + fNeedCarrier + ' перевозчиков с объемом:' + fCarry);
        // var fNeedVolume = Math.round(fNeedCarry/300*fTwoWay/2*100)/100;
        // console.log('Или на єтом маршруте нужно 2 носильщика с объемом:' + fNeedVolume);
    

        //let pos = ret.path[0];
        //creep.move(creep.pos.getDirectionTo(pos));
    },

    //новые тестировочные функции
    testShowAllWaysForRoom: function(objRoom,fShow,fCarrierCapacity, fInd){
        var fPrimeColor = COLOR_YELLOW;
        var fSecondaryColor = COLOR_YELLOW;
        for (let j=0;j<objRoom.Containers.length;j++){
            if ((fInd!=null)&&(j!=fInd)) continue;
            if (objRoom.Containers[j]==null) continue;
            if (objRoom.ContainersWaysToContainer[j]==null) continue;

            var path = objRoom.ContainersWaysToContainer[j];
            for (var i=0;i<path.length;i++){
                var fPosition = new RoomPosition(path[i].x,path[i].y,path[i].roomName);
                var flags = Game.rooms[path[i].roomName].lookForAt(LOOK_FLAGS,fPosition, {
                    filter: (structure) => {
                        return ((structure.color == fPrimeColor)&&(structure.secondaryColor == fSecondaryColor));
                    }
                });

                if (fShow==1) {
                    if (flags.length == 0) {
                        fPosition.createFlag(null,fPrimeColor,fSecondaryColor);
                    }
                }else{
                    if (flags.length > 0 ) flags[0].remove();   
                }
            }
            console.log('Need carrier:' + afunc.HowMuchCarriersNeedForContainerInd(objRoom,j,fCarrierCapacity)+ '  capacity:'+fCarrierCapacity); 
        }
    },

    testShowEnergy: function(fShowInfo){ //1 - show all room
        var fAllStorageEnergy = 0;
        var fAllTerminalEnergy = 0;
        var fText = '';
        for (let i=0;i<Memory.MainRooms.length;i++){
            var objRoom = Memory.Data[Memory.MainRooms[i]];
            if (objRoom == null) continue;
            var fStorage = Game.getObjectById(objRoom.Storage);
            var fTerminal = Game.getObjectById(objRoom.Terminal)
            var fStorageEnergy = 0;
            var fTerminalEnergy = 0;
            if (fStorage!=null) fStorageEnergy = fStorage.store.energy;
            if (fTerminal!=null) fTerminalEnergy = fTerminal.store.energy;
            if (fShowInfo == 1) fText = fText + objRoom.Name + ' S:' + fStorageEnergy + ' T:' + fTerminalEnergy;

            var fStorageMineral = 0;
            var fTerminalMineral = 0;
            if (fStorage!=null) fStorageMineral = _.sum(fStorage.store) - fStorageEnergy;
            if (fTerminal!=null) fTerminalMineral = _.sum(fTerminal.store) - fTerminalEnergy;

            var fEnergeTo = sf.GetEnergyInRoom(objRoom);
 
            if (fStorageEnergy>objRoom.StorageEnergyMax) fText = fText + '<font color="yellow"> ' + objRoom.Describe + ' (' + objRoom.Name + ') [' + fEnergeTo + '] SE;  </font>';  
            if (fStorageMineral>objRoom.StorageMineralAllMax) fText = fText + '<font color="yellow"> ' + objRoom.Describe + ' (' + objRoom.Name + ' [' + fEnergeTo+ '] SM;  </font>';  
            if (fTerminalEnergy>objRoom.TerminalEnergyMax) fText = fText + '<font color="yellow"> ' + objRoom.Describe + ' (' + objRoom.Name + ') [' + fEnergeTo +'] TE;  </font>';  
            if (fTerminalMineral>objRoom.TerminalMineralAllMax) fText = fText + '<font color="yellow"> ' + objRoom.Describe + ' (' + objRoom.Name + ') [' + fEnergeTo+ '] TM;  </font>'; 
            
            if (fStorageEnergy<(objRoom.StorageEnergyMin + 10000)) fText = fText + '<font color="red"> ' + objRoom.Describe + ' (' + objRoom.Name + ') [' + fEnergeTo + '] SE;  </font>';  
            
            
            fAllStorageEnergy = fAllStorageEnergy + fStorageEnergy;
            fAllTerminalEnergy = fAllTerminalEnergy + fTerminalEnergy;
        }

        fText = fText + 'ALL S:' + fAllStorageEnergy + ' T:' + fAllTerminalEnergy + ' A:' + (fAllStorageEnergy + fAllTerminalEnergy);
 
        messenger.log('INFO',fText);
    },


};

module.exports = modul_Tests;