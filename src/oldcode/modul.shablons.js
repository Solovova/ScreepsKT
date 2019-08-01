  //A type room
  //TypeOfRoom 1 role 0-19
//0 (0) - small harvester-filler
//1 (1) - havester source0
//2 (2) - filler from 0
//3 (3) - havester source1
//4 (4) - filler from 1
//5 (5) - builder-upgrader
//6 (6) - carrier to container3 
//7 (7) - garbage

  //B type room
  //TypeOfRoom 2 role 200-299

//0 (200) - small harvester-filler -  разраб 1
//1 (201) - havester source0 - разраб 2
//2 (202) - carrier 0 - разраб 4
//3 (203) - havester source1 - разраб 3
//4 (204) - carrier 1 - разраб 5
//5 (205) - filler - разраб 6
//6 (206) - carrier 2 to Upgradrader - разраб 8
//7 (207) - Upgrader - разраб 9
//8 (208) - Builder - разраб 10
//9 (209) - small filler - разраб 7 тест умереть
//10(210) - garbage
//11(211) - tarns to terminal
//12(212) - trans from terminal
//13(213) - small upgrader
//14(214) - trans from link
//15(215) - harvester mineral 
//16(216) - carrier mineral
//17(217) - logistick
//18(218) - lab filler
//19(219) - far carrier
//20(220) - filler tower
//21(221) - boost builder
//22(222) - carrier to boost builder
//23(223) - defender

  //C type room role 100-199
  //TypeOfRoom 10 - добывание, 11 - колонизация, 12 - добывание опасное, 13 - разведка
//0 (100) - warrior-
//1 (101) - havester0+
//2 (102) - carrier0-
//3 (103) - havester1+
//4 (104) - carrier1-
//5 (105) - havester2+
//6 (106) - carrier2-
//7 (107) - builder+
//8 (108) - rezerver-
//9 (109) - claimer+
//10(110) - spy+
//11(111) - updater+
//12(112) - DWarrior0
//13(113) - DWarrior1
//14(114) - groupranger
//15(115) - grouphealer
//16(116) - groupmile
//17(117) - minharvester
//18(118) - mincarrier

//для тесвов

var _Start = Game.cpu.getUsed();

//CODE

Memory._Sum = Memory._Sum + Game.cpu.getUsed()-_Start;
///at end
//Memory._Ticks = 0;
    //Memory._Sum = 0;
    Memory._Ticks++;

    console.log('Used:' + Math.trunc(Memory._Sum/Memory._Ticks*10000)/10000 + '  ticks:' + Memory._Ticks);



var creepfn                          = require('modul.creepsfn');



var person = {
    firstName:"John",
    lastName:"Doe",
    age:50,
    eyeColor:"blue"
};

var person = new Object();
person.firstName = "John";
person.lastName = "Doe";
person.age = 50;
person.eyeColor = "blue";

Game.getObjectById(creep.memory.src);


// var probe = new Object();
    // probe.field1 = 'field111'
    // probe.field2 = 'field222'
    // probe.field3 = 'field333'

    // Memory.probe = probe;

    //Memory.probe.field3 = '5b327bafe917022bb7549ca4';
    //Memory.probe.fileld3obj = Game.getObjectById(Memory.probe.field3);
    try{
        console.log(Memory.probe.fileld3obj.mineralAmount);
    }catch(err){
        console.log('проблема');
    }





  
  			// TerminalFrom = Game.getObjectById('5b448a6440ee1f7d3d2bea3d');
			// var keysMineralNeed = Object.keys(TerminalFrom.store);
            // for (let mi=0;mi<keysMineralNeed.length;mi++){
			// 	fMineral = keysMineralNeed[mi];
			// 	fQuantity = TerminalFrom.store[fMineral];
			// 	console.log('Terminal:' + fMineral + ' q:' + fQuantity);
			// }
			// TerminalFrom = Game.getObjectById('5b448a6440ee1f7d3d2bea3d');
			// TerminalFrom.send('UH',88,'W45N7');
			//Game.market.deal('5b4b4c095bb3ee087c92da7e ', 8000, 'W43N4');
            //logistics.GetArrayOfBying(RESOURCE_ENERGY,'W43N4',ORDER_BUY); //ORDER_SELL  ORDER_BUY
            

            // var fGQuantity = logistics.GetQuantityMineral('G');
			// 	var fGQuantityMin = 10000 //Запускать если не запущено
			// 	var fGQuantityMax = 30000 //Останавливать если не запущено
			// 	if (fGQuantity<=fGQuantityMin && objRoom.LabReaction == '') logistics.LabReactionStart(objRoom,'G');
			// 	if (fGQuantity>=fGQuantityMax && objRoom.LabReaction != '' && objRoom.LabReactionStoping!=1) logistics.LabReactionStop(objRoom,'G');
			// 	if (objRoom.LabReactionStoping==1) logistics.LabReactionStop(objRoom,'G');
  
  
  
            //     Another way about it.
            //     var R1energyCapacity=Game.rooms.E57N22.energyCapacityAvailable;
            //     var R1energyAvailable =Game.rooms.E57N22.energyAvailable;
            //     console.log("Room 1 energy: "+R1energyAvailable+" out of "+R1energyCapacity+" Max");
                
            //     Change the room name to your target room ofc.
  
  
  
      // var fStorage = Game.getObjectById('5b103147474da960ab39d1db');
    // fStorage = null;
    // try{
    //     if (fStorage == null || fStorage.store['energy']>300000){
    //         console.log('>300000000000');    
    //     } 
    // }catch(err){
    //     console.log('ошиииибка');
    // }


    //Game.market.createOrder(ORDER_SELL, RESOURCE_HYDROGEN, 0.110, 10000, 'W45N7');
    // fMyRoom = 'W43N4';
    // fTargetRoom = 'W43N6';
    // fQuantity = 10000;
    
    // //fResult = Game.market.deal('5b325f8349e3bd32000350fb', fQuantity, fMyRoom); console.log('Market:' + fResult);
    // fCost = Game.market.calcTransactionCost(fQuantity, fTargetRoom, fMyRoom);console.log('Market cost:' + fCost);
    // //fResult = Game.market.deal('5b3224d6dc10892d18ae5de4', 10000, 'W43N6'); console.log('Market:' + fResult);
    // var _terminalFrom = Game.getObjectById('5b166f6d4dbe80781b3c8d15');
    // _terminalFrom.send(RESOURCE_KEANIUM,10000,'W43N6'); 
    
    //
    //var _Start = Game.cpu.getUsed();

    //Memory._Sum = Memory._Sum + Game.cpu.getUsed()-_Start;

    //Memory._Ticks = 0;
    //Memory._Sum = 0;
    //Memory._Ticks++;
    //console.log('Used:' + Math.trunc(Memory._Sum/Memory._Ticks*10000)/10000 + '  ticks:' + Memory._Ticks);
       
    
    //Memory.AutomatBuildRoute = 1;
    //tests.BuildRoadFromId1ToId2('5b2e875aac601b0df4c48281','5b2e9086eaaf6b0dfd0d958e');
    

    
    // var objRoom = Memory.Data['W4N4'];
    // var iContainer = 1;
    // var oCarrieNeed = objRoom.ContainersWaysCarrierNeed[iContainer];
    // console.log('AUTO: For room:' + objRoom.Name + ' For container:'+iContainer + ' need: ' + oCarrieNeed.NeedCarriers + ' capasity:'+ oCarrieNeed.NeedCapasity + ' timeForDeath:'+oCarrieNeed.TimeForDeath);
    // console.log('AUTO: For room:' + objRoom.Name + ' For container:'+iContainer + ' waight: ' + objRoom.ContainersWaysWaight[iContainer]);
    // console.log('AUTO: For room:' + objRoom.Name + ' Body:' + oCarrieNeed.BodyCarriers);
    //Memory.SafeWay = null;
    // if (Memory.SafeWay == null) {
    //     var fObj1 = Game.getObjectById('5b1afb272bee550ad3d96cb8');
    //     var fObj2 = Game.getObjectById('5b0f00b8c9cfc93dc5dd3f23');
    //     ret = afunc.GetWayFromPosToPosTest(fObj1.pos,fObj2.pos,1);
    //     if (!ret.incomplete) Memory.SafeWay = ret.path;
    // }
    // var objRoom = Memory.Data['W44N9'];
    // objRoom.safezone = null;
    //if (objRoom.safezone == null) afunc.CalculateSafeZoneForRoom(objRoom);
    
  
  
  
  
  
  
  
  
  //Memory.testFillStart = Game.time;
    //Memory.testFillSum = 0;
    var _start = Game.cpu.getUsed();
    





    var _end = Game.cpu.getUsed();
    Memory.testFillSum = Memory.testFillSum + _end - _start;
    var _ticks = Game.time - Memory.testFillStart;
    console.log('TEST:' + Math.round(Memory.testFillSum/_ticks*100)/100 + ' NOW:' + Math.round((_end - _start) /_ticks*100) /100); 


  // try{
    //     console.log(probe.store.energy);
    // }catch(err){
    //     console.log('пропал объект');
    //     probe = Game.getObjectById(Memory.Data['W43N6'].Storage);
    // }


    // try{
    //     var fNameRoom = 'W43N6';
    //     if (probe[fNameRoom] == null){
    //         objRoom = Memory.Data[fNameRoom];
    //         messenger.log('INIT','Пропал объект',COLOR_RED);
    //         probe[fNameRoom] = new Object();
    //         probe[fNameRoom].Controller = Game.getObjectById(objRoom.Controller);
    //         probe[fNameRoom].Storage = Game.getObjectById(objRoom.Storage);
    //         probe[fNameRoom].Terminal = Game.getObjectById(objRoom.Terminal);
    //     }
    // }catch(err){
    //     messenger.log('INIT','Ошибка',COLOR_RED);
    // }

    // var probe = new Object();
    // probe.field1 = 'field111'
    // probe.field2 = 'field222'
    // probe.field3 = 'field333'

    // Memory.probe = probe;

    // var fKeys = Object.keys(Memory.probe);
    // var fSafeKeys = ['field1','field3'];
    // for (let i = 0;i<fKeys.length;i++){
    //     if (fSafeKeys.indexOf(fKeys[i]) == -1) delete Memory.probe[fKeys[i]];
    // }