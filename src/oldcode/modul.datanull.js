var afunc                       = require('modul.afunc');
var sf                      	= require('modul.smallfunc');
var messenger                      	= require('modul.messenger');

var dataNull = {
	RefillNullForRoom: function(fRoomName) {
		if (Memory.Data[fRoomName] == null) Memory.Data[fRoomName] = new Object();
		objRoom = Memory.Data[fRoomName];
		if (objRoom.Name == null)				objRoom.Name   				= fRoomName;
		if (objRoom.TypeOfRoom == null)			objRoom.TypeOfRoom 		    = 0; //1-Master start,2-Master,10-Harvest,11-Colonise,12-Harvest dange, 13 - Spy room
		if (objRoom.MasterRoom == null)			objRoom.MasterRoom		   	= ''; 
		if (objRoom.SlaivRooms == null)			objRoom.SlaivRooms 			= [];
		if (objRoom.Describe == null)			objRoom.Describe   			= '';
		if (objRoom.NotColonise == null)		objRoom.NotColonise 		    = 0;
		//Constants
		
		objRoom.WallHints 						= Memory.constants.WallHints;
		objRoom.HitsRamparts					= Memory.constants.HitsRamparts;

		objRoom.StorageEnergyMin      			= Memory.constants.StorageEnergyMin;     //Количество ниже которого не брать никому кроме филлеров
		objRoom.StorageEnergyMax      			= Memory.constants.StorageEnergyMax;     
		objRoom.StorageEnergyForce    			= Memory.constants.StorageEnergyForce;    //Количество после которого форсировать использование енергии
		objRoom.StorageMineralAllMax  			= Memory.constants.StorageMineralAllMax;    //Максимальное количество минералов больше которого не ставить, чтоб не забить склад
		objRoom.StorageMineralExtractionMax  	= Memory.constants.StorageMineralExtractionMax;    //Максимальное количество минералов которые добываем

		objRoom.TerminalEnergyMin      			= Memory.constants.TerminalEnergyMin;
		objRoom.TerminalEnergyMax      			= Memory.constants.TerminalEnergyMax;
		objRoom.TerminalMineralAllMax  			= Memory.constants.TerminalMineralAllMax;
		objRoom.TerminalMineralEachMin 			= Memory.constants.TerminalMineralEachMin;

		objRoom.DefHitsCentral  				= Memory.constants.DefHitsCentral ;
		objRoom.DefHitsSecondFort 				= Memory.constants.DefHitsSecondFort;
	
		if (objRoom.MinDamageForRepair == null)	objRoom.MinDamageForRepair 	= 1000;
		if (objRoom.SentEnergyToRoom == null)	objRoom.SentEnergyToRoom	= '';
		if (objRoom.MaxSpawnEnergy == null)		objRoom.MaxSpawnEnergy 		= 0;
		if (objRoom.ReadyForType2 == null)		objRoom.ReadyForType2 		= 0;
		if (objRoom.ReadyForExpansion == null)	objRoom.ReadyForExpansion 	= 0;
		if (objRoom.ReadyForMainRoom == null)	objRoom.ReadyForMainRoom 	= 0;
		if (objRoom.IsBuild == null)	        objRoom.IsBuild 			= 0;
		if (objRoom.BuildSomething == null)	    objRoom.BuildSomething 		= 0;
		if (objRoom.BuildRoadByCarrier == null)	objRoom.BuildRoadByCarrier 	= 0;

		if (objRoom.LabReaction == null)		objRoom.LabReaction 		= '';
		if (objRoom.LabReaction_Reagent0 == null)		objRoom.LabReaction_Reagent0 		= '';
		if (objRoom.LabReaction_Reagent1 == null)		objRoom.LabReaction_Reagent1 		= '';
		if (objRoom.LabReactionStoping == null)	objRoom.LabReactionStoping 	= 0;

		if (objRoom.LabFillNuke == null)		objRoom.LabFillNuke 		= 0;
		if (objRoom.LabClearTrashInMinContainer == null) objRoom.LabClearTrashInMinContainer = 0;
			
		//Objects structure
		if (objRoom.Spawns == null)				objRoom.Spawns 				= [];
		if (objRoom.Storage == null)			objRoom.Storage 			= '';
		if (objRoom.Containers == null)			objRoom.Containers 			= [null,null,null,null,null];
		if (objRoom.Extractor == null)			objRoom.Extractor 			= '';
		if (objRoom.Terminal == null)			objRoom.Terminal 			= '';
		if (objRoom.Links == null)				objRoom.Links 				= [null,null,null,null,null,null];
		if (objRoom.LabsReaction == null)		objRoom.LabsReaction 		= [];
		if (objRoom.LabsUpgrade == null)		objRoom.LabsUpgrade 		= [];
		if (objRoom.Nuker == null)				objRoom.Nuker 				= '';
		if (objRoom.Observer == null)			objRoom.Observer 			= '';
		//Objects
		if (objRoom.Controller == null)			objRoom.Controller 			= '';
		if (objRoom.Sources == null)			objRoom.Sources 			= [];
		if (objRoom.SourcesCanHarvest == null)	objRoom.SourcesCanHarvest 	= [];
		if (objRoom.Mineral == null)			objRoom.Mineral 			= '';
		if (objRoom.KeperLair == null)			objRoom.KeperLair 			= [];
		//Calculated ways
		if (objRoom.ContainersWaysWaight == null)	    	objRoom.ContainersWaysWaight 				= [0,0,0,0,0];
		if (objRoom.ContainersWaysToStorage == null) 		objRoom.ContainersWaysToStorage 			= [null,null,null,null,null];
		if (objRoom.ContainersWaysToId == null) 			objRoom.ContainersWaysToId 					= [null,null,null,null,null];
		if (objRoom.ContainersWaysCarrierNeed == null) 		objRoom.ContainersWaysCarrierNeed 			= [null,null,null,null,null];
		if (objRoom.ContainersWaysNeedRecalculate == null) 	objRoom.ContainersWaysNeedRecalculate 		= 1;
		if (objRoom.SafeWay == null) 						objRoom.SafeWay 							= null;
		//Defense
		if (objRoom.IsInvaders == null)	        		objRoom.IsInvaders 				= 0;
		if (objRoom.AttackGroup == null)	        	objRoom.AttackGroup 			= new Object();
		if (objRoom.AttackGroup.Need == null)			objRoom.AttackGroup.Need 		= 0;
		if (objRoom.AttackGroup.Flag == null)			objRoom.AttackGroup.Flag 		= 0;
		if (objRoom.AttackGroup.NeedRanger == null)		objRoom.AttackGroup.NeedRanger 	= 0;
		if (objRoom.AttackGroup.NeedHealer == null)		objRoom.AttackGroup.NeedHealer 	= 0;
		if (objRoom.AttackGroup.NeedMile == null)		objRoom.AttackGroup.NeedMile 	= 0;
		if (objRoom.AttackGroup.AllNeed == null)		objRoom.AttackGroup.AllNeed 	= 0;
		if (objRoom.AttackGroup.Have == null)			objRoom.AttackGroup.Have 		= []; //id creep of group
		if (objRoom.AttackGroup.AttackId == null)		objRoom.AttackGroup.AttackId 	= '';
		if (objRoom.AttackGroup.HealId == null)			objRoom.AttackGroup.HealId 		= '';
		if (objRoom.AttackGroup.LeaderId == null)		objRoom.AttackGroup.LeaderId 	= '';
		if (objRoom.AttackGroup.LeaderStop == null)		objRoom.AttackGroup.LeaderStop 	= 0;
			
		//Temp constants
		objRoom.Queue 				= [];
		objRoom.Need0 				= new Array(100); sf.ArrayFillZero(objRoom.Need0);
		objRoom.Need1 				= new Array(100); sf.ArrayFillZero(objRoom.Need1);
		objRoom.Need2 				= new Array(100); sf.ArrayFillZero(objRoom.Need2);
		objRoom.CalcRoleCreeps 		= new Array(100); sf.ArrayFillZero(objRoom.CalcRoleCreeps);
		objRoom.CalcForQueue 		= new Array(100); sf.ArrayFillZero(objRoom.CalcForQueue);
		objRoom.NeedForQueue 		= new Array(100); sf.ArrayFillZero(objRoom.NeedForQueue);

		if (objRoom.MineralNeed == null)  objRoom.MineralNeed = new Object();
		objRoom.RoleId 				= null;
		objRoom.SentEnergyToRoom 		= '';
		objRoom.SentEnergyToRoomManual 	= '';
		objRoom.LabFillNuke 		= 0;
		//objRoom.MineralNeed['GH2O'] = 0;
		objRoom.UpgraderUp  		= 0;
		//objRoom.MineralNeed['LH2O'] = 0;
		objRoom.BuilderUp  			= 0;
		objRoom.BoostBuilder  		= 0;

		objRoom.OverflowNeedEnergy    = Memory.constants.OverflowNeedEnergy;
		objRoom.OverflowOnEnergy      = Memory.constants.OverflowOnEnergy;

		if (objRoom.NeedBoorst == null) 			objRoom.NeedBoorst			  = '';
		if (objRoom.NeedBoorstQuantity == null) 	objRoom.NeedBoorstQuantity	  = 0;
		if (objRoom.NeedBoorstId == null) 			objRoom.NeedBoorstId	  	  = '';

		if (objRoom.NeedBoorst !='' && Game.getObjectById(objRoom.NeedBoorstId) == null ) objRoom.NeedBoorst = '';

		// objRoom.NeedBoorst			  = '';
		// objRoom.NeedBoorstQuantity	  = 0;
		// objRoom.NeedBoorstId	  	  = '';
	},

	FillRoomConstants: function(objRoom) {
		if (Game.rooms[objRoom.Name] == null) return;
		if (Memory.ShowInfoAutoFillOnlyRoom == 'recalculated'){
			fShowInfo = -2;	
		}else{
			var fShowInfo = Memory.ShowInfoAutoFillOnlyRoom.indexOf(objRoom.Name);
		}
		
		//Controller
		var recalculate = '               : ';
		if ((objRoom.Controller=='')&&(objRoom.TypeOfRoom!=12)) {
			if (Game.rooms[objRoom.Name].controller!=null) {
				recalculate = ' (recalculated): ';
				objRoom.Controller = Game.rooms[objRoom.Name].controller.id;
			}
		};
		if (objRoom.Controller != '') objRoom.oController = Game.getObjectById(objRoom.Controller);
		if (fShowInfo == -2 && recalculate == ' (recalculated): ') console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Controller:' + objRoom.Controller);
		if ((fShowInfo >= 0)&&(objRoom.TypeOfRoom!=12)) console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Controller:' + objRoom.Controller);
		
		//Storage
		var recalculate = '               : ';
		if ((objRoom.TypeOfRoom<10)&&(objRoom.Storage=='')) {
			if (objRoom.oController.level>=3){
				var fStorage = Game.rooms[objRoom.Name].find(FIND_STRUCTURES, {
       			filter: (structure) => {
           			return (structure.structureType == STRUCTURE_STORAGE)
       				}
				});
				recalculate = ' (recalculated): ';
				if (fStorage.length!=0) {
					objRoom.Storage = fStorage[0].id;
					objRoom.ContainersWaysNeedRecalculate = 1;
				}
			}
		}
		if (objRoom.Storage != '') objRoom.oStorage = Game.getObjectById(objRoom.Storage);
		if (fShowInfo == -2 && recalculate == ' (recalculated): ') console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Storage   :' + objRoom.Storage);
		if ((fShowInfo >= 0)&&(objRoom.Storage!='')) console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Storage   :' + objRoom.Storage);
		
		
		//TypeOfRoom
		if (Memory.MainRooms.indexOf(objRoom.Name) != -1 && objRoom.TypeOfRoom == 0) {
			if (objRoom.Storage=='') {
				objRoom.TypeOfRoom = 1;
			}else{
				objRoom.TypeOfRoom = 2;
			}
		};

		//Spawns (пересчет всегда)
		var recalculate = '               : ';
		if (objRoom.TypeOfRoom<10) { 
			var targets = Game.rooms[objRoom.Name].find(FIND_STRUCTURES, {
            	filter: (structure) => {
                	return (structure.structureType == STRUCTURE_SPAWN);
            	}
			});
			if (targets.length!=objRoom.Spawns.length) {
				recalculate = ' (recalculated): ';
				objRoom.Spawns = [];
				for (let i=0;i<targets.length;i++) objRoom.Spawns.push(targets[i].id);
			}
			
		}
		if (fShowInfo == -2 && recalculate == ' (recalculated): ') console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Spawns    :' + objRoom.Spawns);
		if ((fShowInfo >= 0)&&(objRoom.Spawns.length!=0)) console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Spawns    :' + objRoom.Spawns);
		
		//Max spawn energy (пересчет всегда)
		var recalculate = '               : ';
		if (objRoom.TypeOfRoom<10) {			
			var fMaxSpawnEnergy = Game.rooms[objRoom.Name].energyCapacityAvailable;
			if (fMaxSpawnEnergy!=objRoom.MaxSpawnEnergy ) {
				recalculate = ' (recalculated): ';
				objRoom.MaxSpawnEnergy = fMaxSpawnEnergy;
				objRoom.ContainersWaysNeedRecalculate = 1; 
			}
			if (fShowInfo == -2 && recalculate == ' (recalculated): ') console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Max energy:' + objRoom.MaxSpawnEnergy);
			if (fShowInfo >= 0) console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Max energy:' + objRoom.MaxSpawnEnergy);
		}

		//Sources
		var recalculate = '               : ';
		if (objRoom.Sources.length==0) {
			recalculate = ' (recalculated): ';
        	var targets = Game.rooms[objRoom.Name].find(FIND_SOURCES);
			for (let i=0;i<targets.length;i++) objRoom.Sources.push(targets[i].id);	
		}
		if (fShowInfo == -2 && recalculate == ' (recalculated): ') console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Sources   :' + objRoom.Sources);
		if (fShowInfo >= 0) console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Sources   :' + objRoom.Sources);

		//Mineral
		var recalculate = '               : ';
		if ((objRoom.Mineral=='')&&((objRoom.TypeOfRoom==2)||(objRoom.TypeOfRoom==12)||(objRoom.TypeOfRoom==14))) {
			recalculate = ' (recalculated): ';
			var targets = Game.rooms[objRoom.Name].find(FIND_MINERALS);
			if (targets.length>0) objRoom.Mineral = targets[0].id	
		}
		if (objRoom.Mineral != '') objRoom.oMineral = Game.getObjectById(objRoom.Mineral);
		if (fShowInfo == -2 && recalculate == ' (recalculated): ') console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Mineral   :' + objRoom.Mineral);
		if (fShowInfo >= 0) console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Mineral   :' + objRoom.Mineral);

		//Extractor
		var recalculate = '               : ';
		var fNeedFindExtractor = 0;
		if ( objRoom.Mineral!='' && objRoom.Extractor=='' && objRoom.TypeOfRoom==2) {
			if (objRoom.oController.level>=6) fNeedFindExtractor = 1;
		};

		if (objRoom.Mineral!='' && objRoom.Extractor=='' &&( objRoom.TypeOfRoom==12 || objRoom.TypeOfRoom==14 )) {
			fNeedFindExtractor = 1;
		}

		if (fNeedFindExtractor == 1) {
			recalculate = ' (recalculated): ';
			var targets = Game.rooms[objRoom.Name].find(FIND_STRUCTURES, {
				filter: (structure) => {
					return (structure.structureType == STRUCTURE_EXTRACTOR);
				}
			});
			if (targets.length>0) objRoom.Extractor = targets[0].id	
		}

		if (objRoom.Extractor != '') objRoom.oExtractor = Game.getObjectById(objRoom.Extractor);
		if (fShowInfo == -2 && recalculate == ' (recalculated): ') console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Extractor :' + objRoom.Extractor);
		if (fShowInfo >= 0) console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Extractor :' + objRoom.Extractor);
		
		//SourcerCanHarvest
		var recalculate = '               : ';
		if (objRoom.TypeOfRoom==1) {       
			if (objRoom.SourcesCanHarvest.length!=objRoom.Sources.length) {
				recalculate = ' (recalculated): ';
				for (let i=0;i<objRoom.Sources.length;i++) objRoom.SourcesCanHarvest.push(0);
				for (let i=0;i<objRoom.Sources.length;i++){
					fSource = Game.getObjectById(objRoom.Sources[i]);
					var fFreeLand = 0;
					for (var dx=-1;dx<2;dx++)
						for (var dy=-1;dy<2;dy++) {
							var fTerrain = Game.rooms[objRoom.Name].lookForAt(LOOK_TERRAIN,fSource.pos.x+dx, fSource.pos.y+dy);
							if ((fTerrain == 'plain')||(fTerrain == 'swamp')) fFreeLand++;
						};
					objRoom.SourcesCanHarvest[i] = fFreeLand;	
				}
			}
			if (fShowInfo == -2 && recalculate == ' (recalculated): ') console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Can harv  :' + objRoom.SourcesCanHarvest);
			if (fShowInfo >= 0) console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Can harv  :' + objRoom.SourcesCanHarvest);
		}

		//Link On level 6 Controller starts to find 3 Link
		if (objRoom.TypeOfRoom==2){
			if (objRoom.oController.level>=6) {
				var recalculate = '               : ';
				var fNeedRecalculateLink = 0;
				
				if ((objRoom.Links[0] == null)&&(objRoom.Sources.length >= 1)) fNeedRecalculateLink = 1;
				if ((objRoom.Links[1] == null)&&(objRoom.Sources.length >= 2)) fNeedRecalculateLink = 1;
				if  (objRoom.Links[2] == null) fNeedRecalculateLink = 1;
				if (objRoom.BuildSomething==1) fNeedRecalculateLink = 1;
				if ( objRoom.Links[3] == null && objRoom.oController.level == 8) fNeedRecalculateLink = 1;
				
				if (fNeedRecalculateLink == 1){
					recalculate = ' (recalculated): ';
					objRoom.Links 				= [null,null,null,null,null,null];
					var links = Game.rooms[objRoom.Name].find(FIND_STRUCTURES, {
						filter: (structure) => {
							return (structure.structureType == STRUCTURE_LINK);
						}
					});
					for (let i=0;i<objRoom.Sources.length;i++){
						var fFindLink = null;
						var fSource = Game.getObjectById(objRoom.Sources[i]);
						for (let j=0;j<links.length;j++){
							if (afunc.CalcDistancePos(links[j].pos,fSource.pos)<3){
								fFindLink = links[j].id;
								break;	
							} 
						};
						objRoom.Links[i] = fFindLink;
					}
					if (objRoom.Storage!=''){
						var fStorage = Game.getObjectById(objRoom.Storage);
						var fFindLink = null;
						for (let j=0;j<links.length;j++){
							if (afunc.CalcDistancePos(links[j].pos,fStorage.pos)<3){
								fFindLink = links[j].id;
								break;	
							} 
						}
						objRoom.Links[2] = fFindLink;
					}

					if (objRoom.oController.level == 8){
						var fFindLink = null;
						for (let j=0;j<links.length;j++){
							if (objRoom.Links.indexOf(links[j].id) != -1) continue;
							if (afunc.CalcDistancePos(links[j].pos,objRoom.oController.pos)<4){
								fFindLink = links[j].id;
								break;	
							} 
						}
						objRoom.Links[3] = fFindLink;
					}

					//Остальные Links 4,5
					for (let i=4;i<objRoom.Links.length;i++){
						if (objRoom.Links[i]!=null) continue;
						for (let j=0;j<links.length;j++){
							if (objRoom.Links.indexOf(links[j].id)==-1){
								objRoom.Links[i] = links[j].id;
								break;	
							} 
						}
					}

				}
				if (fShowInfo == -2 && recalculate == ' (recalculated): ') console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Links     :' + objRoom.Links);
				if (fShowInfo >= 0) console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Links     :' + objRoom.Links);
			}

		}

		//Containers 0,1,2 - около Source; 3 - около Controllera; 4 - около Minerals
		var recalculate = '               : ';
		var fNeedRecalculateContainer = 0;
		if ((objRoom.Containers[0] == null)&&(objRoom.Sources.length >= 1)&&(objRoom.Links[0] == null)) fNeedRecalculateContainer = 1;
		if ((objRoom.Containers[1] == null)&&(objRoom.Sources.length >= 2)&&(objRoom.Links[1] == null)) fNeedRecalculateContainer = 1;
		if ((objRoom.Containers[2] == null)&&(objRoom.Sources.length >= 3)) fNeedRecalculateContainer = 1;
		
		if ((objRoom.Containers[0] != null)&&(objRoom.Links[0] != null)) fNeedRecalculateContainer = 1;
		if ((objRoom.Containers[1] != null)&&(objRoom.Links[1] != null)) fNeedRecalculateContainer = 1;
		if ((objRoom.Containers[3] != null)&&(objRoom.Links[3] != null)) fNeedRecalculateContainer = 1;

		if ((objRoom.Containers[3] == null)&&(objRoom.Links[3] == null)&&(objRoom.TypeOfRoom <10)) fNeedRecalculateContainer = 1;
		if ((objRoom.Containers[4] == null)&&(objRoom.Extractor!='')&&(objRoom.TypeOfRoom==2)) fNeedRecalculateContainer = 1;
		
		if (fNeedRecalculateContainer==1){
			recalculate = ' (recalculated): ';
			objRoom.ContainersWaysNeedRecalculate = 1; 
			var containers = Game.rooms[objRoom.Name].find(FIND_STRUCTURES, {
            	filter: (structure) => {
                	return (structure.structureType == STRUCTURE_CONTAINER);
            	}
			});
			for (let i=0;i<objRoom.Sources.length;i++){
				var fFindContainer = null;
				var fSource = Game.getObjectById(objRoom.Sources[i]);
				for (let j=0;j<containers.length;j++){
					if (afunc.CalcDistancePos(containers[j].pos,fSource.pos)<3){
						fFindContainer = containers[j].id;
						break;	
					} 
				};
				objRoom.Containers[i] = fFindContainer;
			}
			if (objRoom.oController!=null){
				var fFindContainer = null;
				for (let j=0;j<containers.length;j++){
					if (afunc.CalcDistancePos(containers[j].pos,objRoom.oController.pos)<4){
						if ((containers[j].id!=objRoom.Containers[0])&&(containers[j].id!=objRoom.Containers[1])&&(containers[j].id!=objRoom.Containers[2])){
							fFindContainer = containers[j].id;
							break;
						}
					} 
				}
				objRoom.Containers[3] = fFindContainer;
			}

			if (objRoom.Extractor!=''){
				var fExtractor = Game.getObjectById(objRoom.Extractor);
				var fFindExtractor = null;
				for (let j=0;j<containers.length;j++){
					if (afunc.CalcDistancePos(containers[j].pos,fExtractor.pos)<4){
						fFindExtractor = containers[j].id;
						break;	
					} 
				}
				objRoom.Containers[4] = fFindExtractor;
			}
		}
		if (fShowInfo == -2 && recalculate == ' (recalculated): ') console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Containers:' + objRoom.Containers);
		if (fShowInfo >= 0) console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Containers:' + objRoom.Containers);

		//Terminal
		var recalculate = '               : ';
		if ((objRoom.TypeOfRoom<10 || objRoom.TypeOfRoom==11 )&&(objRoom.Terminal=='')) { 
			if (objRoom.oController.level>=6) {
				var targets = Game.rooms[objRoom.Name].find(FIND_STRUCTURES, {
            		filter: (structure) => {
                		return (structure.structureType == STRUCTURE_TERMINAL);
					}
				});
				recalculate = ' (recalculated): ';
				if (targets.length!=0) objRoom.Terminal = targets[0].id;
			}
		}
		if (objRoom.Terminal != '') objRoom.oTerminal = Game.getObjectById(objRoom.Terminal);
		if (fShowInfo == -2 && recalculate == ' (recalculated): ') console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Terminal  :' + objRoom.Terminal);
		if ((fShowInfo >= 0)&&(objRoom.Terminal!='')) console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Terminal  :' + objRoom.Terminal);


		//Nuke
		if (objRoom.TypeOfRoom==2) {
			var recalculate = '               : ';
		 	if (objRoom.Nuker=='') {
				if (objRoom.oController.level==8) { 
					var recalculate = '               : ';
					var targets = Game.rooms[objRoom.Name].find(FIND_STRUCTURES, {
            			filter: (structure) => {
                			return (structure.structureType == STRUCTURE_NUKER);
						}
					});
					recalculate = ' (recalculated): ';
					if (targets.length!=0) objRoom.Nuker = targets[0].id;
				}
			}
			if (objRoom.Nuker != '') objRoom.oNuker = Game.getObjectById(objRoom.Nuker);
			if (fShowInfo == -2 && recalculate == ' (recalculated): ') console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Nuker     :' + objRoom.Nuker);
			if ((fShowInfo >= 0)&&(objRoom.Nuker!='')) console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Nuker     :' + objRoom.Nuker);
		}

		//Observer
		if (objRoom.TypeOfRoom==2) {
			var recalculate = '               : ';
		 	if (objRoom.Observer=='') {
				if (objRoom.oController.level==8) { 
					var recalculate = '               : ';
					var targets = Game.rooms[objRoom.Name].find(FIND_STRUCTURES, {
            			filter: (structure) => {
                			return (structure.structureType == STRUCTURE_OBSERVER);
						}
					});
					recalculate = ' (recalculated): ';
					if (targets.length!=0) objRoom.Observer = targets[0].id;
				}
			}
			if (objRoom.Observer != '') objRoom.oObserver = Game.getObjectById(objRoom.Observer);
			if (fShowInfo == -2 && recalculate == ' (recalculated): ') console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Observer  :' + objRoom.Observer);
			if ((fShowInfo >= 0)&&(objRoom.Observer!='')) console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Observer  :' + objRoom.Observer);
		}

		//KeperLair 0,1,2 - около Sourcer; 3 - около Минералов  
		var recalculate = '               : ';
		if ((objRoom.TypeOfRoom==12)&&(objRoom.KeperLair.length==0)) {
			var lair = Game.rooms[objRoom.Name].find(FIND_STRUCTURES, {
				filter: (structure) => {
					return (structure.structureType == STRUCTURE_KEEPER_LAIR);
				}
			});

			var mineral = Game.rooms[objRoom.Name].find(FIND_STRUCTURES, {
				filter: (structure) => {
					return (structure.structureType == STRUCTURE_EXTRACTOR);
				}
			});  
			var fIndexLairNearMineral = -1;
			for (let i=0;i<lair.length;i++) {
				var fDistance = afunc.CalcDistancePos(mineral[0].pos,lair[i].pos);
				if (fDistance>10) {
					objRoom.KeperLair.push(lair[i].id);
				}else{
					fIndexLairNearMineral = i;	
				}
			}
			objRoom.KeperLair.push(lair[fIndexLairNearMineral].id);
			recalculate = ' (recalculated): ';
		}
		if (fShowInfo == -2 && recalculate == ' (recalculated): ') console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Lair      :' + objRoom.KeperLair);
		if ((fShowInfo >= 0)&&(objRoom.TypeOfRoom==12)) console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Lair      :' + objRoom.KeperLair);

		//Labs
		if (objRoom.TypeOfRoom==2){
			if (objRoom.oController.level>=6 && objRoom.oTerminal!=null) {
					var recalculate = '               : ';
					var fNeedRecalculateLabs = 0;
					//if (objRoom.Name == 'W7N3') fNeedRecalculateLabs = 1;
					if (objRoom.LabsReaction.length != 10 && objRoom.oController.level==8) fNeedRecalculateLabs = 1;
					if (objRoom.LabsReaction.length != 6 && objRoom.oController.level==7) fNeedRecalculateLabs = 1;
					if (objRoom.LabsReaction.length != 3 && objRoom.oController.level==6) fNeedRecalculateLabs = 1;
				
					if (fNeedRecalculateLabs == 1){
						recalculate = ' (recalculated): ';
						objRoom.LabsReaction = [];


						var labs = Game.rooms[objRoom.Name].find(FIND_STRUCTURES, {
							filter: (structure) => {
								return (structure.structureType == STRUCTURE_LAB);
							}
						});

						//Сначала ищем на отдалении 1 от терминала
						var fMaxDistanceToUpgr = 3;
						for (let i=0;i<labs.length;i++) {
							var fDistance = afunc.CalcDistancePos(labs[i].pos,objRoom.oTerminal.pos);
							if (fDistance<=fMaxDistanceToUpgr) {
								objRoom.LabsReaction.push(labs[i].id);
								break;
							}
						}
						
						if (objRoom.LabsReaction.length == 1){
							//Дальше ищем 2 лаборатории отдаленные на не больше чем на 2 тайла от остальных
							for (let i=0;i<labs.length;i++) {
								if (objRoom.LabsReaction.indexOf(labs[i].id) != -1) continue;

								var fMaxDistance = 0;
								for (let j=0;j<labs.length;j++) {
									if (i==j) continue;
									var fDistance = afunc.CalcDistancePos(labs[i].pos,labs[j].pos);
									if (fDistance>fMaxDistance) fMaxDistance = fDistance;
								}
								
								if (fMaxDistance<=2) {
									objRoom.LabsReaction.push(labs[i].id);
								}

								if (objRoom.LabsReaction.length == 3) break;
							}

							if (objRoom.LabsReaction.length == 3){
								for (let i=0;i<labs.length;i++) {
									if (objRoom.LabsReaction.indexOf(labs[i].id) != -1) continue;
									objRoom.LabsReaction.push(labs[i].id);
								}
							}else{
								messenger.log('ERROR','In room:' + objRoom.Name + ' cnat find lab for start reaction',COLOR_RED);	
							}
						}else{
							messenger.log('ERROR','In room:' + objRoom.Name + ' cnat find lab for upgrage',COLOR_RED);
						}
					}
					if (fShowInfo == -2 && recalculate == ' (recalculated): ') console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Labs react:' + objRoom.LabsReaction);
					if (fShowInfo >= 0) console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Labs react:' + objRoom.LabsReaction);
			}
		}
		

		//

		//IsInvaders (пересчет всегда)
		recalculate = ' (recalculated): ';
		objRoom.IsInvaders = afunc.GetInvaders(objRoom);
		if (fShowInfo >= 0) console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') Invaders  :' + objRoom.IsInvaders);

		//IsBuild (пересчет всегда)
		recalculate = ' (recalculated): ';
		objRoom.BuildSomething = 0;
		var targets = Game.rooms[objRoom.Name].find(FIND_CONSTRUCTION_SITES);
        if (targets.length > 0){
			objRoom.IsBuild = 1;
		}else{
			if (objRoom.IsBuild == 1) {
				objRoom.ContainersWaysNeedRecalculate = 1;
				objRoom.BuildSomething = 1; //чтото построили и нужно пересчитать
			}
			objRoom.IsBuild = 0; 
		}
		if (fShowInfo >= 0) console.log('AUTO FILL:' + recalculate + objRoom.Describe + ' (' + objRoom.Name + ') IsBuild   :' + objRoom.IsBuild);
	},

	ResetData: function(){
		if (Memory.Data == null) return;
		var fKeysRoom = Object.keys(Memory.Data);
		for (let j = 0;j<fKeysRoom.length;j++){
			objRoom = Memory.Data[fKeysRoom[j]];
			var fKeys = Object.keys(objRoom);
    		for (let i = 0;i<fKeys.length;i++){
    			if (Memory.constants.DataSafeKeys.indexOf(fKeys[i]) == -1) delete objRoom[fKeys[i]];
			}
		} 
	},

	ErazeOldNotUseRoom: function(){
		objData = Memory.Data;
		var fKeys = Object.keys(objData);
    	for (let i = 0;i<fKeys.length;i++){
    		if (Memory.MainRooms.indexOf(fKeys[i]) == -1 && Memory.SlaivRooms.indexOf(fKeys[i]) == -1) delete objData[fKeys[i]];
    	}
	},

	RefillNullMain: function() {
		if (Memory.Data==null) Memory.Data = new Object();
		for (let i=0;i<Memory.MainRooms.length;i++){
			dataNull.RefillNullForRoom(Memory.MainRooms[i]);
		}
	},

	RefillNullSlaiv: function() {
		for (let i=0;i<Memory.SlaivRooms.length;i++){
			dataNull.RefillNullForRoom(Memory.SlaivRooms[i]);
		}
	},

	FillSlaveRooms: function() {
		Memory.SlaivRooms = [];
		for (let i=0;i<Memory.MainRooms.length;i++){
			objMasterRoom = Memory.Data[Memory.MainRooms[i]];
			for (let j=0;j<objMasterRoom.SlaivRooms.length;j++) Memory.SlaivRooms.push(objMasterRoom.SlaivRooms[j])
		}
	},



	RefillConstantsMain: function(fRefilDescribe){
		for (let i=0;i<Memory.MainRooms.length;i++){
			try{
				var objRoom =  Memory.Data[Memory.MainRooms[i]];
				if (Game.rooms[objRoom.Name]!=null){
					sf.CheckObjectExistsArray(objRoom.Containers);
					sf.CheckObjectExistsArray(objRoom.Links);
					
					if (sf.CheckObjectExistsArray(objRoom.LabsUpgrade) == 0) objRoom.LabsUpgrade = [];
					if (sf.CheckObjectExistsArray(objRoom.LabsReaction) == 0) objRoom.LabsReaction = [];
					if (sf.CheckObjectExistsArray(objRoom.Spawns) == 0) objRoom.Spawns = [];
					if (sf.CheckObjectExists(objRoom.Storage) == 0) 	objRoom.Storage = '';
					if (sf.CheckObjectExists(objRoom.Extractor) == 0) 	objRoom.Extractor = '';
					if (sf.CheckObjectExists(objRoom.Terminal) == 0) 	objRoom.Terminal = '';
					if (sf.CheckObjectExists(objRoom.Nuker) == 0) 		objRoom.Nuker = '';
					if (sf.CheckObjectExists(objRoom.Observer) == 0) 	objRoom.Observer = '';
				}

				if (objRoom.TypeOfRoom == 1 && objRoom.Storage!='') objRoom.TypeOfRoom = 2;
				if (objRoom.TypeOfRoom == 2 && objRoom.Storage=='')	objRoom.TypeOfRoom = 1;
			
				//Describe
				if (objRoom.Describe == '' || fRefilDescribe==1) objRoom.Describe = 'M' + i;
				dataNull.FillRoomConstants(objRoom);

				//Memory.R[6] = 0;
			}catch(err){
				afunc.ErrorMessage('ERROR!!! Main constants: ' + Memory.MainRooms[i], err);
			}
		}	
	},

	RefillConstantsSlaiv: function(fRefilDescribe){
		for (let i=0;i<Memory.SlaivRooms.length;i++){
			try{
				var objSlaivRoom =  Memory.Data[Memory.SlaivRooms[i]];
				if (Game.rooms[objSlaivRoom.Name]!=null){
					sf.CheckObjectExistsArray(objSlaivRoom.Containers);
				}

				//MasterRoom
				if (objSlaivRoom.MasterRoom == ''){
					for (let j=0;j<Memory.MainRooms.length;j++){
						var objRoomMain = Memory.Data[Memory.MainRooms[j]];
						if (objRoomMain.SlaivRooms.indexOf(objSlaivRoom.Name) != -1){
							objSlaivRoom.MasterRoom = Memory.MainRooms[j];
							break;	
						}
					}
				}

				//TypeOfRoom
				if (objSlaivRoom.TypeOfRoom == 0 && Game.rooms[objSlaivRoom.Name]!=null) {
					var lair = Game.rooms[objSlaivRoom.Name].find(FIND_STRUCTURES, {
						filter: (structure) => {
							return (structure.structureType == STRUCTURE_KEEPER_LAIR);
						}
					});
					if (lair.length==0) {
						objSlaivRoom.TypeOfRoom = 10;
					}else{
						objSlaivRoom.TypeOfRoom = 12;
					}
				};

				//Describe
				if (objSlaivRoom.Describe == '' || fRefilDescribe==1) {
					var indOfMasterRoom = Memory.MainRooms.indexOf(objSlaivRoom.MasterRoom);
					var indOfSlaveRoomInMasterRoom = Memory.Data[objSlaivRoom.MasterRoom].SlaivRooms.indexOf(objSlaivRoom.Name);
					objSlaivRoom.Describe = 'M' + indOfMasterRoom +'S' + indOfSlaveRoomInMasterRoom;	
				}
				dataNull.FillRoomConstants(objSlaivRoom);
			}catch(err){
				afunc.ErrorMessage('ERROR!!! Slaiv constants: ' + Memory.SlaivRooms[i], err);
			}
		}	
	},
};

module.exports = dataNull;