var dataNull                   = require('modul.datanull');
var modul_auto                 = require('modul.auto');
var afunc                      = require('modul.afunc');
var logistics                  = require('modul.logistics');
var sf                      	= require('modul.smallfunc');

var dataMain = {
	RefillMainData: function() {
		//sv
		//Memory.accaunt = 'test';
		//Memory.accaunt = 'sv';
		//Memory.accaunt = 'testop';
		

		
		
		//test
		if (Memory.accaunt == 'test') {
			Memory.ShowInfoAutoFillOnlyRoom 	= 'recalculated';
    		Memory.ShowInfoOnlyRoom         	= [];
    		Memory.ShowInfoProfit           	= ['W4N4','W6N4','W5N4'];
			
			Memory.MainRooms  = ['W4N3','W5N3','W5N1','W6N3','W7N4','W7N3','W8N3'];
			
			//Memory.Data['W8N3'] = new Object();
			dataNull.RefillNullMain(); 					//If argument = 1 - reset all Data

			var fAll8lvlUpgraderUp = 0;

			//M0 L8
			var objRoom = Memory.Data['W4N3'];
			objRoom.SlaivRooms 		= ['W4N4','W4N2'];
			if (fAll8lvlUpgraderUp==1) objRoom.MineralNeed['GH2O'] = 1000;
			objRoom.MineralNeed['LH2O'] = 1000;
			objRoom.BoostBuilder  		= 2;
			objRoom.BuilderUp  		= 1;
			objRoom.UpgraderUp  		= fAll8lvlUpgraderUp;
			
			
			
			//M1 L8
			var objRoom = Memory.Data['W5N3'];
			objRoom.SlaivRooms 		= ['W5N4','W5N2'];
			if (fAll8lvlUpgraderUp==1) objRoom.MineralNeed['GH2O'] = 1000;
			objRoom.MineralNeed['LH2O'] = 1000;
			objRoom.BoostBuilder  		= 2;
			objRoom.BuilderUp  		= 1;
			objRoom.UpgraderUp  		= fAll8lvlUpgraderUp;
			objRoom.DefHitsSecondFort = 100000000;
			
			
			
			//M2 L8
			var objRoom = Memory.Data['W5N1'];
			objRoom.SlaivRooms 		= ['W4N1'];
			if (fAll8lvlUpgraderUp==1) objRoom.MineralNeed['GH2O'] = 1000;
			objRoom.MineralNeed['LH2O'] = 1000;
			objRoom.BoostBuilder  		= 2;
			objRoom.BuilderUp  		= 1;
			objRoom.UpgraderUp  		= fAll8lvlUpgraderUp;
			objRoom.DefHitsSecondFort = 100000000;
			
			
			//M3 L5
			var objRoom = Memory.Data['W6N3'];
			objRoom.SlaivRooms 		= [];
			if (fAll8lvlUpgraderUp==1) objRoom.MineralNeed['GH2O'] = 1000;
			objRoom.MineralNeed['LH2O'] = 1000;
			objRoom.BoostBuilder  		= 2;
			objRoom.BuilderUp  		= 1;
			objRoom.UpgraderUp  		= fAll8lvlUpgraderUp;
			objRoom.DefHitsSecondFort = 100000000;

			

			//M4 L8
			var objRoom = Memory.Data['W7N4'];
			objRoom.SlaivRooms 		= ['W6N4','W8N4'];
			if (fAll8lvlUpgraderUp==1) objRoom.MineralNeed['GH2O'] = 1000;
			objRoom.MineralNeed['LH2O'] = 1000;
			objRoom.BoostBuilder  		= 2;
			objRoom.BuilderUp  		= 1;
			objRoom.UpgraderUp  		= fAll8lvlUpgraderUp;
			objRoom.DefHitsSecondFort = 100000000;

			//M5 L5
			var objRoom = Memory.Data['W7N3'];
			objRoom.SlaivRooms 		= [];
			if (fAll8lvlUpgraderUp==1) objRoom.MineralNeed['GH2O'] = 1000;
			objRoom.MineralNeed['LH2O'] = 1000;
			objRoom.BoostBuilder  		= 2;
			objRoom.BuilderUp  		= 1;
			objRoom.UpgraderUp  		= fAll8lvlUpgraderUp;
			objRoom.DefHitsSecondFort = 100000000;
			
			
			//M6 L8
			var objRoom = Memory.Data['W8N3'];
			objRoom.SlaivRooms 		= ['W8N2'];
			if (fAll8lvlUpgraderUp==1) objRoom.MineralNeed['GH2O'] = 1000;
			objRoom.MineralNeed['LH2O'] = 1000;
			objRoom.BoostBuilder  		= 2;
			objRoom.BuilderUp  		= 1;
			objRoom.UpgraderUp  		= fAll8lvlUpgraderUp;
			objRoom.DefHitsSecondFort = 100000000;

			//Labs
			Memory.Data['W4N3'].AutoReaction = 'GH2O'; //M0
			Memory.Data['W5N3'].AutoReaction = 'GH2O'; //M1
			Memory.Data['W5N1'].AutoReaction = 'OH'; //M2
			Memory.Data['W6N3'].AutoReaction = 'OH'; //M3
			Memory.Data['W7N4'].AutoReaction = 'GH'; //M4
			Memory.Data['W7N3'].AutoReaction = 'OH'; //M5
			Memory.Data['W8N3'].AutoReaction = 'GH'; //M6


			dataNull.FillSlaveRooms();
			dataNull.RefillNullSlaiv();
			dataNull.ErazeOldNotUseRoom();

			dataNull.RefillConstantsMain();			   	//If argument = 1 - Refil Describe
			
			for (let i=0;i<Memory.SlaivRooms.length;i++) Memory.Data[Memory.SlaivRooms[i]].TypeOfRoom = 10;
			for (let i=0;i<Memory.SlaivRooms.length;i++) Memory.Data[Memory.SlaivRooms[i]].BuildRoadByCarrier = 1;
			
			Memory.Data['W4N4'].TypeOfRoom = 12;
			Memory.Data['W4N4'].GatherMineral = 0;
			Memory.Data['W6N4'].TypeOfRoom = 12;
			Memory.Data['W6N4'].GatherMineral = 0;
			Memory.Data['W5N4'].TypeOfRoom = 12;
			Memory.Data['W5N4'].GatherMineral = 0;
			

			dataNull.RefillConstantsSlaiv();			//If argument = 1 - Refil Describe
		}

		//testop
		if (Memory.accaunt == 'testop') {
			Memory.ShowInfoAutoFillOnlyRoom 	= 'recalculated';
    		Memory.ShowInfoOnlyRoom         	= [];
    		Memory.ShowInfoProfit           	= ['W7N1','W6N1'];
			
			Memory.MainRooms  = ['W7N2'];
			
			//Memory.Data['W8N3'] = new Object();
			dataNull.RefillNullMain(); 					//If argument = 1 - reset all Data

			//M0 L8
			var objRoom = Memory.Data['W7N2'];
			objRoom.SlaivRooms 		= ['W7N1','W6N1'];
			

			dataNull.FillSlaveRooms();
			dataNull.RefillNullSlaiv();
			dataNull.ErazeOldNotUseRoom();

			dataNull.RefillConstantsMain();			   	//If argument = 1 - Refil Describe
			
			for (let i=0;i<Memory.SlaivRooms.length;i++) Memory.Data[Memory.SlaivRooms[i]].TypeOfRoom = 10;
			for (let i=0;i<Memory.SlaivRooms.length;i++) Memory.Data[Memory.SlaivRooms[i]].BuildRoadByCarrier = 1;
			
			dataNull.RefillConstantsSlaiv();			//If argument = 1 - Refil Describe
		}
		
		if (Memory.accaunt == 'sv') {
			Memory.ShowInfoAutoFillOnlyRoom = 'recalculated';
    		Memory.ShowInfoOnlyRoom         = [];
			Memory.ShowInfoProfit           = ['W44N4','W44N6','W45N6','W45N8','W44N5','W46N6'];
	
			// old Memory.MainRooms  = ['W43N6','W43N5','W43N4','W41N3','W45N7','W41N7','W44N2','W46N7','W44N9','W47N3','W46N9','W42N9'];
			Memory.MainRooms  = ['W43N6','W43N5','W43N4','W41N3','W45N7','W41N7','W44N2','W44N9','W46N9','W42N9'];
			//Memory.Data['W42N9'] = new Object();
			
			dataNull.RefillNullMain();

			
			
			var fAll8lvlUpgraderUp = 1;

			//M0 Nuke 1  L8
			var objRoom = Memory.Data['W43N6']
			objRoom.SlaivRooms = ['W44N6','W44N7','W43N7'];
			if (fAll8lvlUpgraderUp==1) objRoom.MineralNeed['GH2O'] = 1000;
			objRoom.BoostBuilder  		= 1;
			objRoom.UpgraderUp  		= fAll8lvlUpgraderUp;

			objRoom.MineralNeed['XKHO2'] = 10000;
			objRoom.MineralNeed['XGHO2'] = 10000;
			objRoom.MineralNeed['XGH2O'] = 20000;
			
			//M1 Nuke 1  L8
			var objRoom = Memory.Data['W43N5']
			objRoom.SlaivRooms = ['W44N5','W42N6','W42N5'];
			if (fAll8lvlUpgraderUp==1) objRoom.MineralNeed['GH2O'] = 1000;
			objRoom.BoostBuilder  		= 1;
			objRoom.UpgraderUp  		= fAll8lvlUpgraderUp;
			
			
			//M2 Nuke 1  L8
			var objRoom = Memory.Data['W43N4']
			objRoom.SlaivRooms = ['W44N4','W42N4'];
			if (fAll8lvlUpgraderUp==1) objRoom.MineralNeed['GH2O'] = 1000;
			objRoom.BoostBuilder  		= 1;
			objRoom.UpgraderUp  		= fAll8lvlUpgraderUp;
			
			
			//M3 Nuke 1  L8
			var objRoom = Memory.Data['W41N3']
			objRoom.SlaivRooms = ['W41N4','W42N3'];
			if (fAll8lvlUpgraderUp==1) objRoom.MineralNeed['GH2O'] = 1000;
			objRoom.BoostBuilder  		= 2;
			objRoom.UpgraderUp  		= fAll8lvlUpgraderUp;
			
			
			//M4 Nuke 0  L8
			var objRoom = Memory.Data['W45N7']
			objRoom.SlaivRooms = ['W45N6','W45N8'];
			if (fAll8lvlUpgraderUp==1) objRoom.MineralNeed['GH2O'] = 1000;
			objRoom.BoostBuilder  		= 2;
			
			objRoom.UpgraderUp  		= fAll8lvlUpgraderUp;
			

			//M5 Nuke 0  L8
			var objRoom = Memory.Data['W41N7']
			objRoom.SlaivRooms = ['W41N8','W42N8'];
			if (fAll8lvlUpgraderUp==1) objRoom.MineralNeed['GH2O'] = 1000;
			objRoom.BoostBuilder  		= 2;
			
			objRoom.UpgraderUp  		= fAll8lvlUpgraderUp;
			

			//M6 Nuke 0  L6
			var objRoom = Memory.Data['W44N2']
			objRoom.SlaivRooms = ['W45N2','W45N3'];
			if (fAll8lvlUpgraderUp==1) objRoom.MineralNeed['GH2O'] = 1000;
			objRoom.BoostBuilder  		= 2;
			objRoom.LabFillNuke 		= 1;
			objRoom.MineralNeed['G'] = 1000;
			objRoom.UpgraderUp  		= fAll8lvlUpgraderUp;
			
			//M7 Nuke 0 L6
// 			var objRoom = Memory.Data['W46N7']
// 			objRoom.SlaivRooms = ['W46N6'];
// 			if (fAll8lvlUpgraderUp==1) objRoom.MineralNeed['GH2O'] = 1000;
// 			objRoom.BoostBuilder  		= 2;
			
// 			objRoom.UpgraderUp  		= fAll8lvlUpgraderUp;
			
			//M8 Nuke 0 L0
			var objRoom = Memory.Data['W44N9']
			objRoom.SlaivRooms = ['W44N8','W43N9','W45N9'];
			if (fAll8lvlUpgraderUp==1) objRoom.MineralNeed['GH2O'] = 1000;
			objRoom.BoostBuilder  		= 2;
			objRoom.UpgraderUp  		= fAll8lvlUpgraderUp;
			
			//M9 Nuke 0 L0
// 			var objRoom = Memory.Data['W47N3']
// 			objRoom.SlaivRooms = ['W46N3'];
// 			if (fAll8lvlUpgraderUp==1) objRoom.MineralNeed['GH2O'] = 1000;
// 			objRoom.BoostBuilder  		= 2;
// 			objRoom.LabFillNuke 		= 1;
// 			objRoom.MineralNeed['G'] = 1000;
// 			objRoom.UpgraderUp  		= fAll8lvlUpgraderUp;

			//M10 Nuke 0 L0
			var objRoom = Memory.Data['W42N9']
			objRoom.SlaivRooms = [];
			objRoom.BoostBuilder  		= 2;
			
			

			//M10
			var objRoom = Memory.Data['W46N9']
			objRoom.SlaivRooms = [];
			objRoom.MineralNeed['GH2O'] = 1000;
			objRoom.MineralNeed['LH2O'] = 1000;
			objRoom.BoostBuilder  		= 2;
			objRoom.BuilderUp  			= 1;
			objRoom.UpgraderUp  		= 1;
			objRoom.OverflowOnEnergy 	= 500000;
			objRoom.OverflowNeedEnergy 	= 400000;

			

			
			//Labs
			
			Memory.Data['W43N6'].AutoReaction = 'GH2O';				//M0
			Memory.Data['W43N5'].AutoReaction = 'GH2O';				//M1
			Memory.Data['W43N4'].AutoReaction = 'GH';				//M2
			Memory.Data['W41N3'].AutoReaction = 'OH';			    //M3
			Memory.Data['W45N7'].AutoReaction = 'OH';				//M4
			Memory.Data['W41N7'].AutoReaction = 'GH';				//M5
			Memory.Data['W44N2'].AutoReaction = 'OH';				//M6
// 			Memory.Data['W46N7'].AutoReaction = 'G';				//M7
			Memory.Data['W44N9'].AutoReaction = 'ZK';			    //M8
// 			Memory.Data['W47N3'].AutoReaction = 'UL';			    //M9
			Memory.Data['W46N9'].AutoReaction = '';			    //M10

			//Show
			// for (let i=0;i<Memory.MainRooms.length;i++) {
			// 	objRoom = Memory.Data[Memory.MainRooms[i]];
			// 	var fShow = 'LH';
			// 	fNeed = objRoom.MineralNeed[fShow]; 
			// 	if (fNeed == null) fNeed = 0;
			// 	if (fNeed != 0) console.log('NEED: ' + fShow + ':' + objRoom.Name + ' :' + fNeed);

			// 	fHave = 0;

			// 	var fStorage = Game.getObjectById(objRoom.Storage);
            // 	var fTerminal = Game.getObjectById(objRoom.Terminal);
            // 	if (fStorage != null) fHave = fHave + sf.GetStoredResources(fStorage.store,fShow);
			// 	if (fTerminal != null) fHave = fHave + sf.GetStoredResources(fTerminal.store,fShow);
			// 	if (fHave != 0) console.log('HAVE: ' + fShow + ':' + objRoom.Name + ' :' + fHave);
			// }

			
			dataNull.FillSlaveRooms();
			dataNull.RefillNullSlaiv();
			dataNull.ErazeOldNotUseRoom();

			dataNull.RefillConstantsMain();			   	//If argument = 1 - Refil Describe

			for (let i=0;i<Memory.SlaivRooms.length;i++) Memory.Data[Memory.SlaivRooms[i]].TypeOfRoom = 10;
			for (let i=0;i<Memory.SlaivRooms.length;i++) Memory.Data[Memory.SlaivRooms[i]].BuildRoadByCarrier = 1;
			

			Memory.Data['W44N4'].TypeOfRoom = 12;
			Memory.Data['W44N4'].GatherMineral = 1;

			Memory.Data['W44N6'].TypeOfRoom = 12;
			Memory.Data['W44N6'].GatherMineral = 1;

			Memory.Data['W45N6'].TypeOfRoom = 12;
			Memory.Data['W45N6'].GatherMineral = 1;

			Memory.Data['W44N5'].TypeOfRoom = 12;
			Memory.Data['W44N5'].GatherMineral = 1;

// 			Memory.Data['W46N6'].TypeOfRoom = 12;
// 			Memory.Data['W46N6'].GatherMineral = 1;

			//Memory.Data['W46N4'].TypeOfRoom = 12;
			//Memory.Data['W46N4'].GatherMineral = 1;

			//Memory.Data['W42N9'].TypeOfRoom = 11;

			



			dataNull.RefillConstantsSlaiv();			//If argument = 1 - Refil Describe



			
			//SELL
			try{
				if (Math.round(Game.time/100)*100==Game.time) logistics.SellMineral('L',150000, 0.03);
				// if (Math.round(Game.time/100)*100==Game.time) logistics.SellMineral('K',80000, 0.059);
				//if (Math.round(Game.time/100)*100==Game.time) logistics.SellMineral('H',80000, 0.04);
				if (Math.round(Game.time/100)*100==Game.time) logistics.SellMineral('O',150000, 0.015);
				// if (Math.round(Game.time/100)*100==Game.time) logistics.SellMineral('Z',80000, 0.075);
			}catch(err){
				afunc.ErrorMessage('ERROR!!! Mineral sell', err);	
			}


			
			
			//logistics.GetArrayOfMarket('H','W43N4',ORDER_SELL); //ORDER_SELL  ORDER_BUY
			
			//BUY
			try{
				if (Game.market.credits>10000){
					if (Math.round(Game.time/100)*100==Game.time) logistics.BuyMineral('Z','W44N2',20000, 0.25);
					if (Math.round(Game.time/100)*100==Game.time) logistics.BuyMineral('L','W41N3',20000, 0.25);
					if (Math.round(Game.time/100)*100==Game.time) logistics.BuyMineral('K','W43N4',20000, 0.25);
					if (Math.round(Game.time/100)*100==Game.time) logistics.BuyMineral('U','W43N6',20000, 0.25);
					if (Math.round(Game.time/100)*100==Game.time) logistics.BuyMineral('X','W43N6',20000, 0.25);
					if (Math.round(Game.time/100)*100==Game.time) logistics.BuyMineral('O','W43N6',20000, 0.25);
					if (Math.round(Game.time/100)*100==Game.time) logistics.BuyMineral('H','W43N5',20000, 0.25);
				}
			}catch(err){
				afunc.ErrorMessage('ERROR!!! Mineral buy', err);	
			}

			// try{
			// 	if (Game.market.credits>1000){
			// 		if (Math.round(Game.time/20)*20==Game.time) logistics.BuyMineral('X','W41N3',30000, 0.30);
			// 	}
			// }catch(err){
			// 	afunc.ErrorMessage('ERROR!!! Mineral buy', err);	
			// }

			//
			// if (Memory.tmpOrderCreated == 0){
			// 	//Game.market.createOrder(ORDER_SELL , 'GH2O', 5, 3000, 'W43N6');
			// 	Game.market.createOrder(ORDER_SELL , 'G', 2.98, 8000, 'W41N3');
			// 	//Game.market.createOrder(ORDER_BUY , 'energy', 0.031, 200000, 'W45N7');
			// 	Memory.tmpOrderCreated = 1;
			// }
			//Memory.tmpOrderCreated = 0;
			if (Memory.tmpOrderCreated == 0){
				// Memory.tmpOrderCreated = 1;
				// Game.market.changeOrderPrice('5b5f47d014cb970fb7351ccb', 3.68)
				// Game.market.extendOrder('5b5f47d014cb970fb7351ccb', 46000)
				//Game.market.cancelOrder('5b5990910c550833a87933ef');
				//Game.market.createOrder(ORDER_SELL , 'XKHO2', 2.397, 2000, 'W43N6');
				//Game.market.createOrder(ORDER_SELL , 'XGH2O', 6.299, 1340, 'W43N6');
				//Game.market.createOrder(ORDER_SELL , 'XGHO2', 5.071, 2000, 'W43N6');
				//Game.market.deal('5b5daaee14cb970fb7f68ff1', 15000, 'W43N6')
				// Game.market.createOrder(ORDER_BUY , 'Z', 0.077, 80000, 'W44N2');
				// Game.market.createOrder(ORDER_BUY , 'K', 0.030, 80000, 'W44N2');
				//Game.market.createOrder(ORDER_BUY , 'U', 0.063, 100000, 'W46N7');
				// Game.market.createOrder(ORDER_BUY , 'L', 0.059, 80000, 'W46N7');
				// Game.market.createOrder(ORDER_BUY , 'O', 0.025, 40000, 'W43N5');
				// Game.market.createOrder(ORDER_BUY , 'H', 0.102, 40000, 'W43N4');
				// Game.market.createOrder(ORDER_BUY , 'H', 0.102, 40000, 'W43N5');
				// Game.market.createOrder(ORDER_BUY , 'O', 0.025, 40000, 'W43N4');
				// 
				
				// Game.market.createOrder(ORDER_BUY , 'energy', 0.011, 200000, 'W43N6');
				// Game.market.createOrder(ORDER_BUY , 'energy', 0.011, 200000, 'W43N5');
				// Game.market.createOrder(ORDER_BUY , 'energy', 0.011, 200000, 'W43N4');
				// Game.market.createOrder(ORDER_BUY , 'energy', 0.011, 200000, 'W41N3');
				// Game.market.createOrder(ORDER_BUY , 'energy', 0.011, 200000, 'W45N7');
				// Game.market.createOrder(ORDER_BUY , 'energy', 0.011, 200000, 'W41N7');
				// Game.market.createOrder(ORDER_BUY , 'energy', 0.011, 200000, 'W44N2');
				// Game.market.createOrder(ORDER_BUY , 'energy', 0.011, 200000, 'W46N7');

				// G
				// 
				//Game.market.createOrder(ORDER_SELL , 'G', 2.5, 10000, 'W41N3');
				//Game.market.changeOrderPrice('5b5193355bb3ee087c919e13', 3)
				//H
				
				// Game.market.extendOrder('5b50d9e35bb3ee087c74228d', 80000)
				// Game.market.changeOrderPrice('5b50d9e35bb3ee087c74228e', 0.105)
				//
				//Game.market.createOrder(ORDER_SELL , 'LH2O', 3, 5000, 'W44N9');
				
			}
			//Game.market.cancelOrder('5b50e92e5bb3ee087c76e5c1');
			
			

		}
		
		try{
			logistics.OverflowControl();	
		}catch(err){
			afunc.ErrorMessage('ERROR!!! OverflowControl', err);
		}
		
		try{
			afunc.RecalculateAllWays();	
		}catch(err){
			afunc.ErrorMessage('ERROR!!! Recalculate ways', err);
		}

		try{
			afunc.TerminalTransferEnergy();
		}catch(err){
			afunc.ErrorMessage('ERROR!!! Terminal sent', err);
		}

		try{
			afunc.TerminalTransferMinerals();
		}catch(err){
			afunc.ErrorMessage('ERROR!!! Terminal sent minerals', err);
		}

		

    

		
	}, 
};

module.exports = dataMain;