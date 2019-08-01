//var constants                      = require('modul.constants');
var dataNull                   = require('modul.datanull');

var constants = {
    fill: function() {
        
        if (Memory.constants == null) Memory.constants = new Object();

        //Room constants
        Memory.constants.WallHints 						= 300000;
		Memory.constants.HitsRamparts					= 5100000; //Близкие к пушкам

		Memory.constants.StorageEnergyMin      			= 20000     //Количество ниже которого не брать никому кроме филлеров
		Memory.constants.StorageEnergyMax      			= 300000     
		Memory.constants.StorageEnergyForce    			= 200000    //Количество после которого форсировать использование енергии
		Memory.constants.StorageMineralAllMax  			= 400000    //Максимальное количество минералов больше которого не ставить, чтоб не забить склад
        Memory.constants.StorageMineralExtractionMax  	= 250000    //Максимальное количество минералов которые добываем

        Memory.constants.TerminalEnergyMin      			= 20000
		Memory.constants.TerminalEnergyMax      			= 150000
		Memory.constants.TerminalMineralAllMax  			= 150000
        Memory.constants.TerminalMineralEachMin 			= 10000

        Memory.constants.DefHitsCentral       			= 5100000;
        Memory.constants.DefHitsSecondFort       		= 58000000;

        
        
        
        
        //Creep constants
        Memory.constants.UseFastTravel = 1;

        //Global constants
        //OverflowControl
        Memory.constants.TransferMinQuantity       		= 500;
        Memory.constants.OverflowOnEnergy                   = 250000; //Перетекание включаем в комнату где разница между OverflowNeedEnergy и енергией которая есть максимальная
        Memory.constants.OverflowNeedEnergy                 = 200000; //Перетекание включаем в комнату где разница между OverflowNeedEnergy и енергией которая есть максимальная
        Memory.constants.UseReactionOnBoorstLab = 1;

        //Boorted roles
        Memory.constants.Boorst = new Object();
        Memory.constants.Boorst[221] = new Object();
        Memory.constants.Boorst[221].mineral  = 'LH2O'; //21(221) - boost builder
        Memory.constants.Boorst[221].parts = WORK;
        Memory.constants.Boorst[207] = new Object();
        Memory.constants.Boorst[207].mineral  = 'GH2O'; //7 (207) - Upgrader
        Memory.constants.Boorst[207].parts = WORK;
        Memory.constants.Boorst[107] = new Object();
        Memory.constants.Boorst[107].mineral  = 'GH2O'; //7 (107) - builder+upgrader
        Memory.constants.Boorst[107].parts = WORK;
        Memory.constants.Boorst[223] = new Object();
        Memory.constants.Boorst[223].mineral  = 'UH2O';
        Memory.constants.Boorst[223].parts = ATTACK;
        

        if (Memory.logs == null) Memory.logs = new Object();
        if (Memory.logs.buyorders == null)  Memory.logs.buyorders = [];
        if (Memory.logs.sellorders == null) Memory.logs.sellorders = [];
        if (Memory.logs.errors == null)     Memory.logs.errors = [];
        if (Memory.logs.transfers == null)  Memory.logs.transfers = [];

        var fMaxLengthOfLogs = 100;
		if (Memory.logs.buyorders.length >fMaxLengthOfLogs) Memory.logs.buyorders   = [];
        if (Memory.logs.sellorders.length >fMaxLengthOfLogs) Memory.logs.sellorders = [];
        if (Memory.logs.errors.length >fMaxLengthOfLogs) Memory.logs.errors         = [];
        if (Memory.logs.transfers.length >fMaxLengthOfLogs) Memory.logs.transfers   = [];
       
    },

    resetall: function() {
        if (Memory.ResetAll == 1){
            Memory.ResetAll = 0;
            var fMemorySafeKeys  = ['accaunt','Data','creeps'];
            var fKeys = Object.keys(Memory);
            for (let i = 0;i<fKeys.length;i++){
                if (fMemorySafeKeys.indexOf(fKeys[i]) == -1) delete Memory[fKeys[i]];
            }
            
            Memory.constants = new Object();
            Memory.constants.DataSafeKeys = ['LabsReaction','MineralNeed','Labs','LabReaction','LabReactionStoping','AttackGroup','NeedBoorst','NeedBoorstId','NeedBoorstQuantity','LabReaction_Reagent0','LabReaction_Reagent1'];
            dataNull.ResetData();
        }
    },
};

module.exports = constants;