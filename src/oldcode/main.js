var dataMain                   = require('modul.datamain');
var afunc                      = require('modul.afunc');
var tests                      = require('modul.tests');
var sf                      	= require('modul.smallfunc');

var router                     = require('room.router');
var modul_groupattack          = require('modul.groupattack');
var messenger                  = require('modul.messenger');
var constants                  = require('modul.constants');
var logistics                  = require('modul.logistics');

var role_A_SmallHarvester      = require('role.A_smallharvester');           //0
var role_A_Harvester           = require('role.A_harvester');                //1,3
var role_A_Filler              = require('role.A_filler');                   //2,4
var role_A_upgrbuilder         = require('role.A_upgrbuilder');              //5
var role_A_carrier             = require('role.A_carrier');                  //6
var role_A_garbage             = require('role.A_garbage');                  //7
                                                                             
var role_C_Claimer             = require('role.C_claimer');                  //109
var role_C_Explorer            = require('role.C_explorer');                 //110
var role_C_harvester           = require('role.C_harvester');                //101,103,105
var role_C_Builder             = require('role.C_builder');                  //117
var role_C_Updater             = require('role.C_upgrader');                 //111
var role_C_carrier             = require('role.C_carrier');                  //102,104,106
var role_C_rezerver            = require('role.C_rezerver');                 //108
var role_C_warrior             = require('role.C_warrior');                  //100
var role_C_dwarrior            = require('role.C_dwarrior');                 //112,113,1112,1113
var role_C_minharvester        = require('role.C_minharvester');             //117
var role_C_mincarrier          = require('role.C_mincarrier');               //118



var role_B_smallharvester      = require('role.B_smallharvester');           //200
var role_B_harvester           = require('role.B_harvester');                //201,203,1201,1203
var role_B_carrier             = require('role.B_carrier');                  //202,204,206
var role_B_filler              = require('role.B_filler');                   //205,209,1205       
var role_B_upgrader            = require('role.B_upgrader');                 //207
var role_B_builder             = require('role.B_builder');                  //208
var role_B_garbage             = require('role.B_garbage');                  //210
var role_B_transToTerminal     = require('role.B_transToTerminal');          //211
var role_B_transFromTerminal   = require('role.B_transFromTerminal');        //212
var role_B_smallupgrader       = require('role.B_smallupgrader');            //213
var role_B_transFromLink       = require('role.B_transFromLink');            //214,1214
var role_B_minharvester        = require('role.B_minharvester');             //215
var role_B_mincarrier          = require('role.B_mincarrier');               //216
var role_B_logistic            = require('role.B_logistic');                 //217
var role_B_labfiller           = require('role.B_labfiller');                //218
var role_B_farcarrier          = require('role.B_farcarrier');               //219
var role_B_fillertw            = require('role.B_fillertw');                 //220,1220
var role_B_boostbuilder        = require('role.B_boostbuilder');             //221
var role_B_boostbuildercarrier = require('role.B_boostbuildercarrier');      //222
var role_B_defender            = require('role.B_defender');                 //223



//const profiler = require('screeps-profiler');
//profiler.enable();
//Game.profiler.profile(100)
function wrapLoop(fn) {
    let memory;
    let tick;
    
    return () => {
		if (tick && tick + 1 === Game.time && memory) {
			// this line is required to disable the default Memory deserialization
			delete global.Memory;
            Memory = memory;
            console.log('ok memory');
		} else {
            memory = Memory;
            console.log('reset memory');
		}
		
        tick = Game.time;
        
        fn();
        
		// there are two ways of saving Memory with different advantages and disadvantages
		// 1. RawMemory.set(JSON.stringify(Memory));
		// + ability to use custom serialization method
		// - you have to pay for serialization
		// - unable to edit Memory via Memory watcher or console		
		// 2. RawMemory._parsed = Memory;
		// - undocumented functionality, could get removed at any time
		// + the server will take care of serialization, it doesn't cost any CPU on your site
		// + maintain full functionality including Memory watcher and console
		
		// this implementation uses the official way of saving Memory
        RawMemory.set(JSON.stringify(Memory));
    };
}

//module.exports.loop = wrapLoop(function() {
module.exports.loop = function() {
    //profiler.wrap(function() {
    
        
    console.log(''+Game.time + '_____________________________________________________________________');
    messenger.log('CPU','-------------------- 1:' +  Game.cpu.getUsed());
    //console.log('' + tick)
    let heap = Game.cpu.getHeapStatistics();
    console.log(`Used ${heap.total_heap_size} / ${heap.heap_size_limit}`);
    
    
    
    //Memory.ResetAll = 1;
    constants.resetall();
    
    constants.fill();
    
    
    
    
    tests.DoTestAtStart();

    
    
    dataMain.RefillMainData();

   

    tests.StartAllTest(); //if attribute 1- reset all tests

    tests.testShowEnergy();
    modul_groupattack.GroupAttack();
    
    afunc.utiliteClearMemoryFromDeathCreep();

    

    
    messenger.log('CPU','-------------------- 2:' +  Game.cpu.getUsed());
    for(var name in Game.creeps) {
        var creep = Game.creeps[name];
        try {
            afunc.CalcCreeps(creep);
            afunc.KillOldEmptyCreep(creep);
            //creep.suicide();
        }catch(err){
            afunc.ErrorMessage('ERROR!!! Calc Creep Id:' + creep.id + ' Role:' + creep.memory.role  + ' Room:' + creep.memory.dstroom, err);
        }
    }

    
    
    
    messenger.log('CPU','-------------------- 3:' +  Game.cpu.getUsed());
    //рекции запускаем тут, так как реакции  где нужен бурст крипа пропускаем лабу 0
    try{
        logistics.allReactionControl();	
    }catch(err){
        afunc.ErrorMessage('ERROR!!! Reactions', err);
    }
    
    messenger.log('CPU','-------------------- 4:' +  Game.cpu.getUsed());
    var fCreepCpu = new Object();
    for(var name in Game.creeps) {
        var creep = Game.creeps[name];
        try {
            var fCreepCpuStart = Game.cpu.getUsed();
            if (creep.memory.role == 0) {role_A_SmallHarvester.run(creep)}
            if((creep.memory.role == 1)||(creep.memory.role == 1001)) {role_A_Harvester.run(creep)}
            if((creep.memory.role == 3)||(creep.memory.role == 1003)) {role_A_Harvester.run(creep)}
            if((creep.memory.role == 2)||(creep.memory.role == 1002)) {role_A_Filler.run(creep)}
            if((creep.memory.role == 4)||(creep.memory.role == 1004)) {role_A_Filler.run(creep)}
            if (creep.memory.role == 5) {role_A_upgrbuilder.run(creep)}
            if (creep.memory.role == 6) {role_A_carrier.run(creep)}
            if (creep.memory.role == 7) {role_A_garbage.run(creep)}

           

           
            
            if (creep.memory.role  == 109) {role_C_Claimer.run(creep)}
            if (creep.memory.role  == 110) {role_C_Explorer.run(creep)}
            if ((creep.memory.role == 101)||(creep.memory.role == 1101)) {role_C_harvester.run(creep)}
            if ((creep.memory.role == 103)||(creep.memory.role == 1103)) {role_C_harvester.run(creep)}
            if ((creep.memory.role == 105)||(creep.memory.role == 1105)) {role_C_harvester.run(creep)}
            if (creep.memory.role  == 107) {role_C_Builder.run(creep)}
            if (creep.memory.role  == 111) {role_C_Updater.run(creep)}
            
            
            if (creep.memory.role  == 102) {role_C_carrier.run(creep)}
            if (creep.memory.role  == 104) {role_C_carrier.run(creep)}
            if (creep.memory.role  == 106) {role_C_carrier.run(creep)}
            
            
            if (creep.memory.role  == 118) {role_C_mincarrier.run(creep)}
            if (creep.memory.role  == 117) {role_C_minharvester.run(creep)}
            
            if (creep.memory.role  == 108) {role_C_rezerver.run(creep)}
            if ((creep.memory.role == 112)||(creep.memory.role == 1112)) {role_C_dwarrior.run(creep)}
            if ((creep.memory.role == 113)||(creep.memory.role == 1113)) {role_C_dwarrior.run(creep)}
            if (creep.memory.role  == 100) {role_C_warrior.run(creep)}
            
            
            
            if (creep.memory.role  == 200) {role_B_smallharvester.run(creep)}
            
            if ((creep.memory.role == 201)||(creep.memory.role == 1201)) {role_B_harvester.run(creep)}
            if ((creep.memory.role == 203)||(creep.memory.role == 1203)) {role_B_harvester.run(creep)}
            
        
            if (creep.memory.role  == 202) {role_B_carrier.run(creep)}
            if (creep.memory.role  == 204) {role_B_carrier.run(creep)}
            if (creep.memory.role  == 206) {role_B_carrier.run(creep)}
            
            if ((creep.memory.role == 205)||(creep.memory.role == 1205)) {role_B_filler.run(creep)}
            
            if (creep.memory.role  == 209) {role_B_filler.run(creep)}
            if ((creep.memory.role == 207)||(creep.memory.role == 1207)) {role_B_upgrader.run(creep)}
            if (creep.memory.role  == 208) {role_B_builder.run(creep)}
            if (creep.memory.role  == 210) {role_B_garbage.run(creep)}
            if (creep.memory.role  == 211) {role_B_transToTerminal.run(creep)}
            if (creep.memory.role  == 212) {role_B_transFromTerminal.run(creep)}
            if (creep.memory.role  == 213) {role_B_smallupgrader.run(creep)}
            if ((creep.memory.role == 214)||(creep.memory.role == 1214)) {role_B_transFromLink.run(creep)}
            if (creep.memory.role  == 215) {role_B_minharvester.run(creep)}
            if (creep.memory.role  == 216) {role_B_mincarrier.run(creep)}
            
            if (creep.memory.role  == 217 || creep.memory.role  == 1217) {role_B_logistic.run(creep)}
            
            if (creep.memory.role  == 218) {role_B_labfiller.run(creep)}
            if (creep.memory.role  == 219) {role_B_farcarrier.run(creep)}
            if ((creep.memory.role == 220)||(creep.memory.role == 1220)) {role_B_fillertw.run(creep)}

            
            if (creep.memory.role  == 221) {role_B_boostbuilder.run(creep)}
            if (creep.memory.role  == 222) {role_B_boostbuildercarrier.run(creep)}
            
           
            if (creep.memory.role  == 223) {role_B_defender.run(creep)}
            
            var fCreepCpuEnd = Game.cpu.getUsed();
            var ftKey = ''+creep.memory.role + creep.memory.dstroom + creep.id;
            if (fCreepCpu[creep.memory.role]==null) fCreepCpu[ftKey] = 0;
            fCreepCpu[ftKey] = fCreepCpu[ftKey] + fCreepCpuEnd -fCreepCpuStart;
            
        }catch(err){
            afunc.ErrorMessage('ERROR!!! Creep Id:' + creep.id + ' Role:' + creep.memory.role  + ' Room:' + creep.memory.dstroom, err);
        }
        

        tests.testRoomProfit_CreepCarry(creep);
    }

    

    var fText = '';
    //Memory.oldfCreepCpu=null;
    if (Memory.oldfCreepCpu!=null){
        var fKeys = Object.keys(fCreepCpu);
        for (let i = 0;i<fKeys.length;i++){
            var fOldCpu = Memory.oldfCreepCpu[fKeys[i]];
            var fNewCpu = fCreepCpu[fKeys[i]];
            if (fOldCpu == null) fOldCpu = 0;
            if (fNewCpu == null) fNewCpu = 0;
            if ((fNewCpu - fOldCpu) > 5) {
                fText = fText + ' | ' + fKeys[i] + ':' + (fNewCpu - fOldCpu);
            }
            
        }
    }
    Memory.oldfCreepCpu = fCreepCpu;
    messenger.log('CPU',fText);
    messenger.log('CPU','-------------------- 5:' +  Game.cpu.getUsed());

    
    
    
    
    
    

    

    router.BuildAndSpawn();
    
    tests.testRoomProfit_Log(-1); //0 - simple, 1 - show hystory - -1 dontshow если 0 то показывает только комнаты в Memory.ShowInfoProfit 
    tests.DoTestAtEnd();

    //Memory.tst = 1;

    //  if (Memory.tst == 1){
    //         console.log('dddddddddddddddddddddddddddddddddddddd')
    //      Memory.tst = 0;
    //      tests.BuildRoadFromId1ToId2('5b58ae037705994afedfa7ae','5982fbfdb097071b4adbc889');
    //  } 
    
//});  
}
  
//});