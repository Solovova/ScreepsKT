var sf                      	    = require('modul.smallfunc');
var messenger                      	= require('modul.messenger');
var afunc                           = require('modul.afunc');
var messenger                      	= require('modul.messenger');
//var logistics                      	= require('modul.logistics');

var logistics = {
    GetTaskForLogist: function(objRoom,carryCapacity) {
        const fStorage  = Game.getObjectById(objRoom.Storage);
        const fTerminal = Game.getObjectById(objRoom.Terminal);

        if (fStorage == null || fTerminal == null) return null;
        result = new Object();
        if (result.isTask == null) result.isTask =0;
        if (carryCapacity==null) carryCapacity = 500;

        //Работа с лабой 0
        //Fill Labs for Upgarade


        var fNeedBoorst = objRoom.NeedBoorst;
        var fNeedQuantityBoorst = objRoom.NeedBoorstQuantity;

        //0) наполняем енергией 0 Lab
        if (objRoom.LabsReaction.length>0){
            var fLab = Game.getObjectById(objRoom.LabsReaction[0]);
            if (result.isTask == 0) {             
                var fMineral = RESOURCE_ENERGY;
                var fNeedCarry = (fLab.energyCapacity - fLab.energy);
                if (fNeedCarry>carryCapacity) fNeedCarry=carryCapacity;

                if (fNeedCarry>0){
                    result.isTask = 1;
                    result.idFrom = fTerminal.id;
                    result.idTo = fLab.id;
                    result.Mineral = fMineral;
                    result.Quantity = fNeedCarry; 
                    result.Task = 'T6';                
                } 
            }
        }
 
        //1) Если objRoom.NeedBoorst != ''
        if (objRoom.LabsReaction.length>0 && fNeedBoorst != ''){
            var fLab = Game.getObjectById(objRoom.LabsReaction[0]);
            
            //M Если не тот бурс то убрать
            if (result.isTask == 0 && fLab.mineralType!=fNeedBoorst && fLab.mineralType != null) {
                var fMineral = fLab.mineralType;
                var fNeedCarry = fLab.mineralAmount;
                if (fNeedCarry>carryCapacity) fNeedCarry=carryCapacity;
                if (fNeedCarry>0){
                    result.isTask = 1;
                    result.idFrom = fLab.id;
                    result.idTo = fTerminal.id;
                    result.Mineral = fMineral;
                    result.Quantity = fNeedCarry; 
                    result.Task = 'M0 clean';                  
                }
            }

            //Если больше чем нужно то отнести
            if (result.isTask == 0 && fLab.mineralType==fNeedBoorst && fLab.mineralAmount > fNeedQuantityBoorst){
                //if (objRoom.Name == 'W5N3') console.log('ssssssssssssssssssss')
                var fMineral = fNeedBoorst;
                var fNeedCarry = fLab.mineralAmount - fNeedQuantityBoorst;
                if (fNeedCarry>carryCapacity) fNeedCarry = carryCapacity; 
                if (fNeedCarry>0){
                    result.isTask = 1;
                    result.idFrom = fLab.id;
                    result.idTo = fTerminal.id;
                    result.Mineral = fMineral;
                    result.Quantity = fNeedCarry;   
                    result.Task = 'M1 lab->term';                
                }
            }

            if (result.isTask == 0 && ((fLab.mineralType==fNeedBoorst && fLab.mineralAmount < fNeedQuantityBoorst) || (fLab.mineralType==null))){
                var fMineral = fNeedBoorst;
                var fTerminalMineralInStore = sf.GetStoredResources(fTerminal.store,fMineral);
                var fNeedCarry = fNeedQuantityBoorst - fLab.mineralAmount;
                if (fNeedCarry>fTerminalMineralInStore) fNeedCarry = fTerminalMineralInStore;
                if (fNeedCarry>carryCapacity) fNeedCarry = carryCapacity; 
                if (fNeedCarry>0){
                    result.isTask = 1;
                    result.idFrom = fTerminal.id;
                    result.idTo = fLab.id;
                    result.Mineral = fMineral;
                    result.Quantity = fNeedCarry;   
                    result.Task = 'M1 term->lab';                
                }
            }
        }

        if (objRoom.LabsReaction.length>0 && fNeedBoorst == ''){
            var fLab = Game.getObjectById(objRoom.LabsReaction[0]);

            //M Если не тот бурс то убрать
            if (result.isTask == 0 && fLab.mineralAmount != 0 && fLab.mineralType != objRoom.LabReaction) {
                var fMineral = fLab.mineralType;
                var fNeedCarry = fLab.mineralAmount;
                if (fNeedCarry>carryCapacity) fNeedCarry=carryCapacity;
                if (fNeedCarry>0){
                    result.isTask = 1;
                    result.idFrom = fLab.id;
                    result.idTo = fTerminal.id;
                    result.Mineral = fMineral;
                    result.Quantity = fNeedCarry; 
                    result.Task = 'M0 clean';                  
                }
            }
        }

        if (objRoom.LabsReaction.length>0 && fNeedBoorst == '' && objRoom.LabReaction !=''){
            var fLab = Game.getObjectById(objRoom.LabsReaction[0]);

            //M take production
            if (result.isTask == 0 && fLab.mineralAmount >= carryCapacity && fLab.mineralType == objRoom.LabReaction) {
                var fMineral = fLab.mineralType;
                var fNeedCarry = carryCapacity;
                result.isTask = 1;
                result.idFrom = fLab.id;
                result.idTo = fTerminal.id;
                result.Mineral = fMineral;
                result.Quantity = fNeedCarry; 
                result.Task = 'M0 take production';                  
            }
        }

        
       


        //T0
        if (result.isTask == 0) {
            if (sf.GetMineralsInStore(fStorage.store)<objRoom.StorageMineralAllMax){
                let stored_resources = _.filter(Object.keys(fTerminal.store), resource => fTerminal.store[resource] > 0);
                for (let i = 0;i<stored_resources.length;i++){
                    if (stored_resources[i]==RESOURCE_ENERGY) continue;
                    var fQuantityInTerminal = sf.GetStoredResources(fTerminal.store,stored_resources[i]);
                    if (fQuantityInTerminal>objRoom.TerminalMineralEachMin) {
                        var fNeedTransfer = (fQuantityInTerminal - objRoom.TerminalMineralEachMin);
                        if (fNeedTransfer>carryCapacity) fNeedTransfer=carryCapacity;
                        if (fNeedTransfer>(objRoom.StorageMineralAllMax - sf.GetMineralsInStore(fStorage.store) )) fNeedTransfer=(objRoom.StorageMineralAllMax - sf.GetMineralsInStore(fStorage.Store));
                        if (fNeedTransfer>0){
                            result.isTask = 1;
                            result.idFrom = fTerminal.id;
                            result.idTo = fStorage.id;
                            result.Mineral = stored_resources[i];
                            result.Quantity = fNeedTransfer;
                            result.Task = 'T0';
                            break;
                        }
                    }
                }                
            }
        }

        //2) Перебираем все минералы в Storge и если его количество в Terminal меньше чем objRoom.TerminalMineralEachMin то переносим и 
        //минералов в Terminal меньше objRoom.TerminalMineralAllMax
        //T1
        if (result.isTask == 0) {
            if (sf.GetMineralsInStore(fTerminal.store)<objRoom.TerminalMineralAllMax){

                let stored_resources = _.filter(Object.keys(fStorage.store), resource => fStorage.store[resource] > 0);
                for (let i = 0;i<stored_resources.length;i++){
                    if (stored_resources[i]==RESOURCE_ENERGY) continue;
                    var fQuantityInTerminal = sf.GetStoredResources(fTerminal.store,stored_resources[i]);
                    var fQuantityInStorage = sf.GetStoredResources(fStorage.store,stored_resources[i]);
                    if (fQuantityInTerminal<objRoom.TerminalMineralEachMin) {
                        var fNeedTransfer = (objRoom.TerminalMineralEachMin - fQuantityInTerminal);
                        if (fNeedTransfer>carryCapacity) fNeedTransfer=carryCapacity;
                        if (fNeedTransfer>(objRoom.TerminalMineralAllMax - sf.GetMineralsInStore(fTerminal.store) )) fNeedTransfer= (objRoom.TerminalMineralAllMax - sf.GetMineralsInStore(fTerminal.store));
                        if (fNeedTransfer>fQuantityInStorage) fNeedTransfer = fQuantityInStorage;
                        if (fNeedTransfer>0){
                            result.isTask = 1;
                            result.idFrom = fStorage.id;
                            result.idTo = fTerminal.id;
                            result.Mineral = stored_resources[i];
                            result.Quantity = fNeedTransfer;
                            result.Task = 'T1';
                            break;
                        }
                    }
                }                
            }
        }

        //если в терминале енергии меньше
        //T2
        if (result.isTask == 0) {
            var fQuantityInTerminal = sf.GetStoredResources(fTerminal.store,RESOURCE_ENERGY);
            var fQuantityInStorage = sf.GetStoredResources(fStorage.store,RESOURCE_ENERGY);

            if (fQuantityInTerminal<objRoom.TerminalEnergyMin){
                var fNeedTransfer = (objRoom.TerminalEnergyMin - fQuantityInTerminal);
                if (fNeedTransfer>carryCapacity) fNeedTransfer=carryCapacity;
                if (fNeedTransfer>(fQuantityInStorage - objRoom.StorageEnergyMin) ) fNeedTransfer = (fQuantityInStorage - objRoom.StorageEnergyMin);
                if (fNeedTransfer>0){
                    result.isTask = 1;
                    result.idFrom = fStorage.id;
                    result.idTo = fTerminal.id;
                    result.Mineral = RESOURCE_ENERGY;
                    result.Quantity = fNeedTransfer;
                    result.Task = 'T2';
                }
            }                
        }

        //T3
        if (result.isTask == 0) {
            var fQuantityInTerminal = sf.GetStoredResources(fTerminal.store,RESOURCE_ENERGY);
            var fQuantityInStorage = sf.GetStoredResources(fStorage.store,RESOURCE_ENERGY);

            if (fQuantityInTerminal>objRoom.TerminalEnergyMax){
                var fNeedTransfer = (fQuantityInTerminal - objRoom.TerminalEnergyMax);
                if (fNeedTransfer>carryCapacity) fNeedTransfer=carryCapacity;
                if (fNeedTransfer>(objRoom.StorageEnergyMax - fQuantityInStorage) ) fNeedTransfer = (objRoom.StorageEnergyMax -fQuantityInStorage);
                if (fNeedTransfer>0){
                    result.isTask = 1;
                    result.idFrom = fTerminal.id;
                    result.idTo = fStorage.id;
                    result.Mineral = RESOURCE_ENERGY;
                    result.Quantity = fNeedTransfer;
                    result.Task = 'T3';
                }
            }                
        } 

        //Держим в Storage min в Terminal max
        //T4
        if (result.isTask == 0) {
            var fQuantityInTerminal = sf.GetStoredResources(fTerminal.store,RESOURCE_ENERGY);
            var fQuantityInStorage = sf.GetStoredResources(fStorage.store,RESOURCE_ENERGY);
            if (objRoom.SentEnergyToRoom!=''){
                var fTerminalTo = Game.getObjectById(Memory.Data[objRoom.SentEnergyToRoom].Terminal);
            }
            if ((objRoom.SentEnergyToRoom != '' && fTerminalTo !=null) && fQuantityInStorage > (objRoom.StorageEnergyMin + carryCapacity)){
                
                var fNeedTransfer = (objRoom.TerminalEnergyMax - fQuantityInTerminal);
                if (fNeedTransfer>carryCapacity) fNeedTransfer=carryCapacity;
                if (fNeedTransfer>(fQuantityInStorage - objRoom.StorageEnergyMin) ) fNeedTransfer = (fQuantityInStorage - objRoom.StorageEnergyMin);
                if (fNeedTransfer>0){
                    result.isTask = 1;
                    result.idFrom = fStorage.id;
                    result.idTo = fTerminal.id;
                    result.Mineral = RESOURCE_ENERGY;
                    result.Quantity = fNeedTransfer;
                    result.Task = 'T4';
                }
            }
        }

        //Держим в Storage max в Terminal min
        //T5
        if (result.isTask == 0) {
            var fQuantityInTerminal = sf.GetStoredResources(fTerminal.store,RESOURCE_ENERGY);
            var fQuantityInStorage = sf.GetStoredResources(fStorage.store,RESOURCE_ENERGY);
            if (objRoom.SentEnergyToRoom == '' && fQuantityInTerminal >= (objRoom.TerminalEnergyMin + carryCapacity)){
                var fNeedTransfer = (fQuantityInTerminal - objRoom.TerminalEnergyMin);
                if (fNeedTransfer>carryCapacity) fNeedTransfer=carryCapacity;
                if (fNeedTransfer>(objRoom.StorageEnergyMax - fQuantityInStorage) ) fNeedTransfer = (objRoom.StorageEnergyMax -fQuantityInStorage);
                if (fNeedTransfer>0){
                    result.isTask = 1;
                    result.idFrom = fTerminal.id;
                    result.idTo = fStorage.id;
                    result.Mineral = RESOURCE_ENERGY;
                    result.Quantity = fNeedTransfer;
                    result.Task = 'T5';
                }
            }
        }

        //мусор из контейнера для минералов заберем
        //T6
        if (result.isTask == 0) {
            var fContainer = Game.getObjectById(objRoom.Containers[4]);
            var fMineral = RESOURCE_ENERGY;
            if (fContainer!=null && fContainer.store[fMineral]>0){
                fNeedCarry = fContainer.store[fMineral];
                if (fNeedCarry>carryCapacity) fNeedCarry=carryCapacity;
                result.isTask = 1;
                result.idFrom = fContainer.id;
                result.idTo = objRoom.Storage;
                result.Mineral = fMineral;
                result.Quantity = fNeedCarry;  
                result.Task = 'T6';                 
            }
        }

        //Дроп мусора
        // if (result.isTask == 0) {
        //     var fTerminal = Game.getObjectById(objRoom.Terminal);
        //     var fMineral = 'UH';
        //     if (fTerminal.store[fMineral]>0){
        //         fNeedCarry = fTerminal.store[fMineral];
        //         if (fNeedCarry>carryCapacity) fNeedCarry=carryCapacity;
        //         result.isTask = 1;
        //         result.idFrom = fTerminal.id;
        //         result.idTo = 'drop';
        //         result.Mineral = fMineral;
        //         result.Quantity = fNeedCarry;                   
        //     }
        // }

        //Дроп мусора
        // if (result.isTask == 0) {
        //     //var fTerminal = Game.getObjectById(objRoom.Terminal);
        //     let stored_resources = _.filter(Object.keys(fTerminal.store), resource => fTerminal.store[resource] > 0);
        //     for (let i = 0;i<stored_resources.length;i++){
        //         if (stored_resources[i]==RESOURCE_ENERGY) continue;
        //         var fQuantityInTerminal = sf.GetStoredResources(fTerminal.store,stored_resources[i]);
        //         if (fQuantityInTerminal < 100) {
        //             var fNeedTransfer = fQuantityInTerminal;
        //             if (fNeedTransfer>0){
        //                 result.isTask = 1;
        //                 result.idFrom = fTerminal.id;
        //                 result.idTo = 'drop';
        //                 result.Mineral = stored_resources[i];
        //                 result.Quantity = fNeedTransfer;
        //                 result.Task = 'T1 drop';
        //                 break;
        //             }
        //         }                
        //     }
        // }


        if (result.isTask == 0) return null;
        return result; 
    },

    GetTaskForLabfiller: function(objRoom,carryCapacity) {
        //if (objRoom.LabReaction == '') return;

        const fStorage  = Game.getObjectById(objRoom.Storage);
        const fTerminal = Game.getObjectById(objRoom.Terminal);

        if (fStorage == null || fTerminal == null) return null;
        result = new Object();
        if (result.isTask == null) result.isTask =0;
        if (carryCapacity==null) carryCapacity = 500;

        //Some specific task
        //T0  Убрать минерал из Lab
        // if (result.isTask == 0 && objRoom.Name == 'W44N9') {
            
        //     var idLab = '5b51b02bd15be7657ad7fb3e';
        //     var fLab = Game.getObjectById(idLab);
        //     var fMineral = 'GH2O';
        //     var fNeedCarry = fLab.mineralAmount;
        //     if (fNeedCarry>carryCapacity) fNeedCarry=carryCapacity;
        //     if (fNeedCarry>0){
        //         result.isTask = 1;
        //         result.idFrom = idLab;
        //         result.idTo = fTerminal.id;
        //         result.Mineral = fMineral;
        //         result.Quantity = fNeedCarry; 
        //         result.Task = 'T0';                  
        //     }
        // }

        //T0  Загрузить минерал в Lab
        // if (result.isTask == 0 && objRoom.Name == 'W44N9') {
        //     var idLab = '5b51b02bd15be7657ad7fb3e';
        //     var fLab = Game.getObjectById(idLab);
        //     var fMineral = 'LH2O';
        //     var fNeedCarry = 1000-fLab.mineralAmount;
        //     if (fNeedCarry>carryCapacity) fNeedCarry=carryCapacity;
        //     if (fNeedCarry>0){
        //         result.isTask = 1;
        //         result.idFrom = fTerminal.id;
        //         result.idTo = idLab;
        //         result.Mineral = fMineral;
        //         result.Quantity = fNeedCarry; 
        //         result.Task = 'T0';                  
        //     }
        // }

        //Empty energy 
         //T1
         
        //  if (result.isTask == 0) {
        //     for (let i=0;i<objRoom.LabsReaction.length;i++){
                
        //         var fLab = Game.getObjectById(objRoom.LabsReaction[i]);
        //         if (fLab == null) continue;
        //         if (fLab.energy!=0){
        //             var fNeedTransfer = fLab.energy;
        //             if (fNeedTransfer>carryCapacity) fNeedTransfer=carryCapacity;
        //             if (fNeedTransfer>0){
        //                 result.isTask = 1;
        //                 result.idFrom = fLab.id;
        //                 result.idTo = fTerminal.id;
        //                 result.Mineral = RESOURCE_ENERGY;
        //                 result.Quantity = fNeedTransfer;
        //                 result.Task = 'T1';
        //                 break;
        //             }
        //         }
        //     }    
        // }

        // if (result.isTask == 0) {
        //     for (let i=0;i<objRoom.LabsReaction.length;i++){
        //         var fLab = Game.getObjectById(objRoom.LabsReaction[i]);
        //         if (fLab == null) continue;
        //         if (fLab.mineralAmount!=0){
        //             var fNeedTransfer = fLab.mineralAmount;
        //             if (fNeedTransfer>carryCapacity) fNeedTransfer=carryCapacity;
        //             if (fNeedTransfer>0){
        //                 result.isTask = 1;
        //                 result.idFrom = fLab.id;
        //                 result.idTo = fTerminal.id;
        //                 result.Mineral = fLab.mineralType;
        //                 result.Quantity = fNeedTransfer;
        //                 result.Task = 'T2';
        //                 break;
        //             }
        //         }
        //     }    
        // }

        //-----------------------------------------------------------------------------------
        
         //Empty energy 
         //T1
         if (result.isTask == 0 && objRoom.LabReactionStoping == 1) {
            for (let i=1;i<objRoom.LabsReaction.length;i++){
                var fLab = Game.getObjectById(objRoom.LabsReaction[i]);
                if (fLab == null) continue;
                if (fLab.energy!=0){
                    var fNeedTransfer = fLab.energy;
                    if (fNeedTransfer>carryCapacity) fNeedTransfer=carryCapacity;
                    if (fNeedTransfer>0){
                        result.isTask = 1;
                        result.idFrom = fLab.id;
                        result.idTo = fTerminal.id;
                        result.Mineral = RESOURCE_ENERGY;
                        result.Quantity = fNeedTransfer;
                        result.Task = 'T1';
                        break;
                    }
                }
            }    
        }


        //Empty minerals 
        //T2
        if (result.isTask == 0 && objRoom.LabReactionStoping == 1) {
            for (let i=1;i<objRoom.LabsReaction.length;i++){
                var fLab = Game.getObjectById(objRoom.LabsReaction[i]);
                if (fLab == null) continue;
                if (fLab.mineralAmount!=0){
                    var fNeedTransfer = fLab.mineralAmount;
                    if (fNeedTransfer>carryCapacity) fNeedTransfer=carryCapacity;
                    if (fNeedTransfer>0){
                        result.isTask = 1;
                        result.idFrom = fLab.id;
                        result.idTo = fTerminal.id;
                        result.Mineral = fLab.mineralType;
                        result.Quantity = fNeedTransfer;
                        result.Task = 'T2';
                        break;
                    }
                }
            }    
        }

        //Get Products from Lab 
        //T4
        if (result.isTask == 0 && objRoom.LabReaction !='' && objRoom.LabReactionStoping == 0) {
            for (let j=0;j<8;j++){
                for (let i=0;i<3;i++){
                    if (objRoom.Labs[j][i]==null) continue;
                    if (objRoom.Labs[j][i].production!=1) continue;
                    var idLab = objRoom.Labs[j][i].id;
                    var fLab = Game.getObjectById(idLab);
                    var fMineral = fLab.mineralType;
                    if (fLab.mineralAmount>=carryCapacity){
                        result.isTask = 1;
                        result.idFrom = idLab;
                        result.idTo = fTerminal.id;
                        result.Mineral = fMineral;
                        result.Quantity = carryCapacity; 
                        result.Task = 'T4';                  
                    }
                }
                if (result.isTask == 1) break;
            }   
        }

        //Fill Labs for reaction
        //T3
        if (result.isTask == 0 && objRoom.LabReaction !='' && objRoom.LabReactionStoping == 0) {
            const fTerminal = Game.getObjectById(objRoom.Terminal);
            for (let j=0;j<8;j++){
                for (let i=0;i<3;i++){
                    if (objRoom.Labs[j][i]==null) continue;
                    var fMineral = objRoom.Labs[j][i].mineral;
                    if (fMineral == null || fMineral == '') continue;
                    var idLab = objRoom.Labs[j][i].id;
                    var fLab = Game.getObjectById(idLab);
                    //if (fLab.mineralType==null) continue;
                    if ((fLab.mineralType != fMineral)&&(fLab.mineralType!=null)) continue;
                    if ((fTerminal.store[fMineral]>=carryCapacity)&&(fLab.mineralAmount<=(fLab.mineralCapacity-carryCapacity))){
                        result.isTask = 1;
                        result.idFrom = fTerminal.id;
                        result.idTo = idLab;
                        result.Mineral = fMineral;
                        result.Quantity = carryCapacity;
                        result.Task = 'T3';
                        break;                  
                    }
                }
                if (result.isTask == 1) break;
            }   
        }

        

        

        

        //Fill nuker G
        //T7
        if (result.isTask == 0 && objRoom.LabFillNuke == 1) {
            var idLab = objRoom.LabsUpgrade[0];
            var fLab = Game.getObjectById(idLab);
            var fMineral = 'G';
            var fNuker = Game.getObjectById(objRoom.Nuker);
            if (fNuker!=null){
                var fNeed = fNuker.ghodiumCapacity - fNuker.ghodium;
                if (fNeed>carryCapacity) fNeed=carryCapacity;
                if (fNeed>0){
                    if (fTerminal.store[fMineral]>=fNeed){
                        result.isTask = 1;
                        result.idFrom = fTerminal.id;
                        result.idTo = fNuker.id;
                        result.Mineral = fMineral;
                        result.Quantity = fNeed;    
                        result.Task = 'T7';               
                    }
                }
            }
        }

        //Fill nuker Energy
        //T8
        if (result.isTask == 0 && objRoom.LabFillNuke == 1) {
            var idLab = objRoom.LabsUpgrade[0];
            var fLab = Game.getObjectById(idLab);
            var fMineral = RESOURCE_ENERGY;
            var fNuker = Game.getObjectById(objRoom.Nuker);
            if (fNuker!=null){
                var fNeed = fNuker.energyCapacity - fNuker.energy;
                if (fNeed>carryCapacity) fNeed=carryCapacity;
                if (fNeed>0){
                    if (fTerminal.store[fMineral]>=fNeed && fStorage.store.energy > objRoom.StorageEnergyForce){
                        result.isTask = 1;
                        result.idFrom = fStorage.id;
                        result.idTo = fNuker.id;
                        result.Mineral = fMineral;
                        result.Quantity = fNeed;                   
                        result.Task = 'T8';
                    }
                }
            }
        }

        

        

        if (result.isTask == 0) return null;
        return result; 
    },

    GetTaskForBoostBuilder: function(objRoom,creep) {
        const fStorage  = Game.getObjectById(objRoom.Storage);
        const fTerminal = Game.getObjectById(objRoom.Terminal);

        if (fStorage == null || fTerminal == null) return null;
        result = new Object();
        if (result.isTask == null) result.isTask =0;


        
        //T1
        if (result.isTask == 0) {
            var fTarget = creep.pos.findClosestByRange(FIND_CONSTRUCTION_SITES);
            if(fTarget != null) {
                result.isTask       = 1;
                result.idRepair     = fTarget.id;
                result.RapairToHits = 0;
                result.Task         = 'build';
            }
        }

        //T2
        if (result.isTask == 0) {
            var fMaxHits = Math.max(objRoom.DefHitsSecondFort,objRoom.DefHitsCentral);
            var DamagedStructures = Game.rooms[objRoom.Name].find(FIND_STRUCTURES, {
                filter: (structure) => (structure.structureType == STRUCTURE_RAMPART)&&(structure.hits<=fMaxHits)
            });
            if(DamagedStructures.length != 0) {
                var fMinHealth = 300000000;
                var fTarget = null;
                for (let i=0;i<DamagedStructures.length;i++){
                    fDistance = afunc.CalcDistancePos(DamagedStructures[i].pos,objRoom.oStorage.pos);
                    var fHits = DamagedStructures[i].hits;
                    if (fDistance >= 5 && fHits >= objRoom.DefHitsSecondFort) continue;
                    if (fDistance < 5 && fHits  >= objRoom.DefHitsCentral) continue;

                    fDistance = afunc.CalcDistancePos(DamagedStructures[i].pos,creep.pos);
                    //if (fDistance>3 && fHits>120000) fHits = fHits +50000*fDistance;
                    if (fDistance>3) fHits = fHits +50000*fDistance;
                    if (fHits<fMinHealth){
                        
                        fTarget = DamagedStructures[i];
                        fMinHealth = fHits;
                    }
                }
                if (fTarget != null){
                    result.isTask       = 1;
                    result.idRepair     = fTarget.id;
                    result.RapairToHits = fTarget.hits + 50000;
                    result.Task         = 'repair';
                }
            }
        }

        if (result.isTask == 0) return null;
        return result; 
    },

    LabReactionStart: function(objRoom,fReaction) {
        if (objRoom.LabReaction != ''){
            messenger.log('LAB','in room:' + objRoom.Name + ' already run reaction:' + objRoom.LabReaction + ' cant start new:'+ fReaction,COLOR_YELLOW);
            return;
        }

        if (objRoom.LabsReaction.length < 3){
            //if (fLab.mineralAmount!=0){
                messenger.log('LAB','ERROR in room:' + objRoom.Name + ' cant start reaction not anouth labs have only:' + objRoom.LabsReaction.length);
                return;    
            //}
        }

        for (let i=1;i<objRoom.LabsReaction.length;i++){
            var fLab = Game.getObjectById(objRoom.LabsReaction[i]);
            if (fLab.mineralAmount!=0){
                messenger.log('LAB','ERROR in room:' + objRoom.Name + ' cant start reaction labs not empty id:' + objRoom.LabsReaction[i]);
                objRoom.LabReaction == 'empty before start';
                objRoom.LabReactionStoping  = 1;
                return;    
            }
        }

        objRoom.Labs = new Array(8);
        for (let i = 0;i<8;i++){
            objRoom.Labs[i] = new Array(3);
            for (let j = 0;j<3;j++) objRoom.Labs[i][j] = new Object(); 
        }

        

    
        var reagents = sf.GetComponentOfReaction(fReaction);
        var f1Reagent = reagents.fReagent0;
        var f2Reagent = reagents.fReagent1;
        
        if (f1Reagent != null && f2Reagent != null){
            objRoom.MineralNeed[f1Reagent] = 3000;
            objRoom.MineralNeed[f2Reagent] = 3000;

            objRoom.Labs[0][0].id           = objRoom.LabsReaction[1];
            objRoom.Labs[0][0].mineral      = f1Reagent;
            objRoom.Labs[0][1].id           = objRoom.LabsReaction[2];
            objRoom.Labs[0][1].mineral      = f2Reagent;
            objRoom.Labs[0][2].id           = objRoom.LabsReaction[0];
                   
            
            for (let i=3;i<objRoom.LabsReaction.length;i++){
                objRoom.Labs[i-2][0].id           = objRoom.LabsReaction[1];
                objRoom.Labs[i-2][0].mineral      = f1Reagent;
                objRoom.Labs[i-2][1].id           = objRoom.LabsReaction[2];
                objRoom.Labs[i-2][1].mineral      = f2Reagent;
                objRoom.Labs[i-2][2].id           = objRoom.LabsReaction[i];
                objRoom.Labs[i-2][2].production   = 1;        
            }
            objRoom.LabReaction         = fReaction;
            objRoom.LabReactionStoping  = 0;
            objRoom.LabReaction_Reagent0 = f1Reagent;
            objRoom.LabReaction_Reagent1 = f2Reagent;
        }
    },

    LabReactionStop: function(objRoom) { //1 - stop ok; 0 - stoping
        if (objRoom.LabReaction == ''){
            messenger.log('LAB','Room:' + objRoom.Name + ' reaction stoped', COLOR_RED);
            return 1;
        }

        if (objRoom.LabReactionStoping == 1){
            for (let i=1;i<objRoom.LabsReaction.length;i++){
                var fLab = Game.getObjectById(objRoom.LabsReaction[i]);
                if (fLab.mineralAmount!=0) {
                    messenger.log('LAB','Room:' + objRoom.Name + ' reaction stoping', COLOR_YELLOW);
                    return 0;
                }
            }
            objRoom.LabReaction = '';
            objRoom.LabReactionStoping = 0;
            return 1;
        }

        if (objRoom.LabReaction!=''){
            objRoom.MineralNeed[objRoom.LabReaction_Reagent0] = 0;
            objRoom.MineralNeed[objRoom.LabReaction_Reagent1] = 0;

            objRoom.LabReactionStoping  = 1;
        }

    },

    LabReactionRun: function(objRoom) {
        if (objRoom.ErrorStoped == 1){
            messenger.log('LAB','ERROR stop reaction in room:' + objRoom.Name,COLOR_YELLOW);
            return;
        }

        if (objRoom.LabReaction == ''){
            messenger.log('LAB','in room:' + objRoom.Name + ' cant run reaction, dont started',COLOR_YELLOW);
            return;
        }

        if (objRoom.LabReactionStoping == 1){
            messenger.log('LAB','in room:' + objRoom.Describe + ' (' +objRoom.Name + ') cant run reaction, stoping reaction',COLOR_YELLOW);
            return;
        }
        
        var fCounter = 0;


        if (objRoom.LabReaction != ''){
            //Доработка!!! если не нужен burst и в лабе продукции продукция то запустить 0
            var fLab = Game.getObjectById(objRoom.LabsReaction[0]);
            if (Memory.constants.UseReactionOnBoorstLab == 1 && objRoom.NeedBoorst == '' && (fLab.mineralType == objRoom.LabReaction || fLab.mineralType == null)){
                var Lab = new Array(3);
			    Lab[0] = Game.getObjectById(objRoom.Labs[0][0].id);
			    Lab[1] = Game.getObjectById(objRoom.Labs[0][1].id);
                Lab[2] = Game.getObjectById(objRoom.Labs[0][2].id);
                if (!(Lab[0] == null || Lab[1] == null || Lab[2] == null)){
                    if (Lab[2].cooldown == 0) {
                        Lab[2].runReaction(Lab[0], Lab[1])
                        fCounter++;
                    }    
                }
            }



            for (let i=1;i<8;i++){
                var Lab = new Array(3);
			    Lab[0] = Game.getObjectById(objRoom.Labs[i][0].id);
			    Lab[1] = Game.getObjectById(objRoom.Labs[i][1].id);
                Lab[2] = Game.getObjectById(objRoom.Labs[i][2].id);
                if (Lab[0] == null || Lab[1] == null || Lab[2] == null) continue;

			    if (Lab[2].cooldown == 0) {
                    Lab[2].runReaction(Lab[0], Lab[1])
                    fCounter++;
                    if (fCounter == 2) return;
                }
            }
        }
    },

    LabReactionCalculateProcessing: function(objRoom) {
        if (objRoom.LabReaction == 'G'){
            var fNumLabs = objRoom.LabsReaction.length - 2;
            var fTickPerReaction = 5;//REACTION_TIME[objRoom.LabReaction];
            var fProduce = Math.round(1000/fTickPerReaction*5*fNumLabs);
            Memory.Mineral.Processing['ZK'] = Memory.Mineral.Processing['ZK'] - fProduce;
            Memory.Mineral.Processing['UL'] = Memory.Mineral.Processing['UL'] - fProduce;
            Memory.Mineral.Processing['G'] = Memory.Mineral.Processing['G'] + fProduce;
            return;
        }

        if (objRoom.LabReaction != ''){
            var fNumLabs = objRoom.LabsReaction.length - 2;
            var fTickPerReaction = REACTION_TIME[objRoom.LabReaction];
            var fProduce = Math.round(1000/fTickPerReaction*5*fNumLabs);
            Memory.Mineral.Processing[objRoom.LabReaction_Reagent0] = Memory.Mineral.Processing[objRoom.LabReaction_Reagent0] - fProduce;
            Memory.Mineral.Processing[objRoom.LabReaction_Reagent1] = Memory.Mineral.Processing[objRoom.LabReaction_Reagent1] - fProduce;
            Memory.Mineral.Processing[objRoom.LabReaction] = Memory.Mineral.Processing[objRoom.LabReaction] + fProduce;
        }
    },

    GetQuantityMineral: function(fMineral) {
        var fMineralQuantity = 0;
        for (let i=0;i<Memory.MainRooms.length;i++){
            var objRoom = Memory.Data[Memory.MainRooms[i]];
            if (objRoom == null) continue;
            var fStorage = Game.getObjectById(objRoom.Storage);
            var fTerminal = Game.getObjectById(objRoom.Terminal);
            if (fStorage != null) fMineralQuantity = fMineralQuantity + sf.GetStoredResources(fStorage.store,fMineral);
            if (fTerminal != null) fMineralQuantity = fMineralQuantity + sf.GetStoredResources(fTerminal.store,fMineral);
        }
        return fMineralQuantity;
    },



    GetArrayOfMarket(fMineral,fRoomName,fTypeOrder){
        fPriceMineral = 0.03;
        if (fTypeOrder == null) fTypeOrder = ORDER_SELL;
        
        var Orders = Game.market.getAllOrders(order => order.resourceType == fMineral &&
            order.type == fTypeOrder
        );

        
        for (let j=0;j<Orders.length;j++){
            var fCostTrasaction = Game.market.calcTransactionCost(1000, fRoomName, Orders[j].roomName)*fPriceMineral/1000;
            if (fTypeOrder == ORDER_SELL) {
                Orders[j].realprice = Orders[j].price + fCostTrasaction;
            }else{
                Orders[j].realprice = Orders[j].price - fCostTrasaction;
            }
        }

        if (fTypeOrder == ORDER_SELL) {
            Orders.sort(function(a,b) {
                return a.realprice - b.realprice;
            });
            
        }else{
            Orders.sort(function(a,b) {
                return b.realprice - a.realprice;
            });
        }

        for (let j=0;j<Orders.length;j++){
            if (fTypeOrder == ORDER_SELL) {
                var fText = 'Order SELL:';
            }else{
                var fText = 'Order BUY:';
            }
            console.log(fText + Orders[j].id + ' price:' + Orders[j].price + ' real price:' + Orders[j].realprice + ' quantity:' + Orders[j].amount)
        }
        return Orders;
    },

    BuyMineral(fMineral,fRoomName,fNeedMineral,fMaxPrice){
        var fHaveMineral = logistics.GetQuantityMineral(fMineral);
        var fMinBuyingQuantity = 10000;
        if (fHaveMineral>=fNeedMineral) return;
        fNeedBuy=fMinBuyingQuantity;
        var fOrders = logistics.GetArrayOfMarket(fMineral,fRoomName,ORDER_SELL);
        if (fOrders.length==0) return;
        if (fOrders[0].realprice>fMaxPrice) return;
        fCanBuy = fOrders[0].amount;
        if (fCanBuy>fNeedBuy) fCanBuy=fNeedBuy;
        if (Game.market.deal(fOrders[0].id, fCanBuy, fRoomName) == OK) {
            var fText = 'buy mineral:' + fMineral + ' ticks:' + Game.time + ' quantity:' + fCanBuy + ' price:' + fOrders[0].price;
            fText = fText + ' rprice:' + fOrders[0].realprice + ' id:' + fOrders[0].id;
            Memory.logs.buyorders.push(fText);
        }
    },

    SellMineral: function(fMineral,fMinQuantity,fMinPrice) {
        var fSellQuantity = logistics.GetQuantityMineral(fMineral) - fMinQuantity;
        if (fSellQuantity>=10000) {
            fSellQuantity = 10000; 
        }else{
            return;
        }

        for (let i=0;i<Memory.MainRooms.length;i++) {
            var objRoom = Memory.Data[Memory.MainRooms[i]];
            var fTerminal = Game.getObjectById(objRoom.Terminal);
            var fStorage = Game.getObjectById(objRoom.Storage);
            if ((fTerminal == null)||(fStorage == null)) continue;
            var fMineralHaveTerminal = sf.GetStoredResources(fTerminal.store,fMineral);

            if (fMineralHaveTerminal<fSellQuantity) continue;

            var fOrders = logistics.GetArrayOfMarket(fMineral,objRoom.Name,ORDER_BUY);
            if (fOrders.length==0) return;
            if (fOrders[0].realprice<fMinPrice) continue;
            if (fSellQuantity>fOrders[0].amount) fSellQuantity=fOrders[0].amount;
            if (fSellQuantity<=0) return;
            if (Game.market.deal(fOrders[0].id, fSellQuantity, objRoom.Name) == OK) {
                var fText = 'sell mineral:' + fMineral + ' ticks:' + Game.time + ' quantity:' + fSellQuantity + ' price:' + fOrders[0].price;
                fText = fText + ' rprice:' + fOrders[0].realprice + ' id:' + fOrders[0].id;
                Memory.logs.sellorders.push(fText);
                return;
            }
        }
    },

    allReactionControl: function() {
        try{
            Memory.Mineral              = new Object();
            Memory.Mineral.Have         = new Object();
            Memory.Mineral.Max          = new Object();
            Memory.Mineral.Min          = new Object();
            Memory.Mineral.Processing   = new Object();



            //var fMineralControl =         ['GH2O','LH2O','LH','UH2O','UH','G','GH','OH','LO','KO','GO','ZK','UL','H','O','Z','L','K','U','X','KHO2','XKHO2'];
            
            var fMineralControl =         ['H','O','Z','L','K','U','X'];

            var fKeys0 = Object.keys(REACTIONS)
            for (var i0 = 0;i0<fKeys0.length;i0++){
                var fKeys1 = Object.keys(REACTIONS[fKeys0[i0]]);
                for (let i1 = 0;i1<fKeys1.length;i1++){
                    fMineralControl.push(REACTIONS[fKeys0[i0]][fKeys1[i1]])
                }
            }
            
            for (let i = 0;i<fMineralControl.length;i++){
                Memory.Mineral.Have[fMineralControl[i]] = logistics.GetQuantityMineral(fMineralControl[i]);
                Memory.Mineral.Processing[fMineralControl[i]] = 0;	
            }
        }catch(err){
            afunc.ErrorMessage('ERROR!!! Mineral info', err);	
        }

        Memory.Mineral.Max['UL'] = 10000;
        Memory.Mineral.Max['ZK'] = 10000;
        Memory.Mineral.Max['GH'] = 10000;
        Memory.Mineral.Max['OH'] = 10000;
        Memory.Mineral.Max['UH'] = 10000;
        Memory.Mineral.Max['G'] = 200000;

        Memory.Mineral.Max['LH'] = 10000;
        Memory.Mineral.Max['LH2O'] = 50000;
        Memory.Mineral.Max['UH2O'] = 30000;
        Memory.Mineral.Max['KHO2'] = 10000;
        Memory.Mineral.Max['XKHO2'] = 40000;
        Memory.Mineral.Max['GHO2'] = 10000;

        Memory.Mineral.Max['LO'] = 5000;
        Memory.Mineral.Max['KO'] = 5000;
        Memory.Mineral.Max['GO'] = 5000;


        for (let i=0;i<Memory.MainRooms.length;i++){
            try{
                objRoom = Memory.Data[Memory.MainRooms[i]];
                if (objRoom.LabReaction == '' && objRoom.AutoReaction == '') {
                    if (objRoom.LabsReaction.length>=3){
                        messenger.log('LAB','Room dont produce:' + objRoom.Describe + ' (' +objRoom.Name + ') have labs: ' + objRoom.LabsReaction.length,COLOR_YELLOW);
                    }
                    continue;
                }

                //Ничего не делалось, запускаем
                if (objRoom.LabReaction == '' && objRoom.AutoReaction != '') {
                    logistics.LabReactionStart(objRoom,objRoom.AutoReaction);
                }

                //Запуск другой реакции
                if (objRoom.LabReaction != '' && objRoom.LabReaction != objRoom.AutoReaction) {
                    logistics.LabReactionStop(objRoom);    
                }


                //Запуск реакции
                if (objRoom.LabReaction != '' && objRoom.LabReaction == objRoom.AutoReaction) {
                    logistics.LabReactionCalculateProcessing(objRoom);
                    var fMaxProduct = Memory.Mineral.Max[objRoom.LabReaction];
                    if (fMaxProduct==null){
                        logistics.LabReactionRun(objRoom);
                    }else{
                        var fHaveProduct = Memory.Mineral.Have[objRoom.LabReaction];
                        if (fHaveProduct == null) fHaveProduct = 0;
                        if (fHaveProduct>fMaxProduct){
                            messenger.log('LAB','Room :' + objRoom.Describe + ' (' +objRoom.Name + ') labs stoped. We have: ' + objRoom.LabReaction + ' :' + fHaveProduct + ' max:' + fMaxProduct ,COLOR_YELLOW);
                        }else{
                            logistics.LabReactionRun(objRoom);
                        }
                    }
                }
            }catch(err){
                afunc.ErrorMessage('ERROR!!! Run reaction in room:' + Memory.MainRooms[i], err);	
            }
        }

        try{
            var fTextBig = '';
            var fTextBigCounter = 0;
            var fShowQueue = ['H','GH2O','GH','OH','O','LH2O','LH','GO','Z','UH2O','UH','','U','','','','K','KHO2','','KO','L','GHO2','','LO','X','G','ZK','UL','XKHO2','XGHO2','XGH2O'];
            for (let i = 0;i<fShowQueue.length;i++){
                if (fShowQueue[i]!=''){
                    var fHaveProduct = Memory.Mineral.Have[fShowQueue[i]]; if (fHaveProduct == null) fHaveProduct = 0;
                    var fMaxProduct  = Memory.Mineral.Max[fShowQueue[i]];
                    var fProcProduct = Memory.Mineral.Processing[fShowQueue[i]]; if (fProcProduct == null) fProcProduct = 0;
                
                    fText = sf.padLeft(''+fShowQueue[i],6) + ': ' + sf.padLeft(''+fHaveProduct,9);
                    if (fMaxProduct == null){
                        fText = fText + '            ';
                    }else{
                        fText = fText + ' Max: ' + sf.padLeft(''+fMaxProduct,8);    
                    }

                    if (fProcProduct != 0) fText = fText + ' Balance: ' + sf.padLeft(''+fProcProduct,6);    
                }else{
                    fText = '';
                }
                
                fTextBig = fTextBig + sf.padRight('|'+fText,55);
                fTextBigCounter++;    
               
                if (fTextBigCounter==4){
                    messenger.log('LAB',fTextBig,COLOR_ORANGE);
                    fTextBigCounter = 0;
                    var fTextBig = '';	
                }
            }
            if (fTextBig!='')  messenger.log('LAB',fTextBig,COLOR_ORANGE);
        }catch(err){
            afunc.ErrorMessage('ERROR!!! Mineral info', err);	
        }
    },

    OverflowControl: function() {
        var fOverFlowManual = '';
        var fOverFlowAutomat = '';

        for (let i=0;i<Memory.MainRooms.length;i++){
            try{
                objRoom = Memory.Data[Memory.MainRooms[i]];
                if (sf.isTerminalAndStorage(objRoom) == 0) continue;
                
                if (objRoom.SentEnergyToRoomManual != ''){
                    objRoom.SentEnergyToRoom = objRoom.SentEnergyToRoomManual;
                    objRoomTo = Memory.Data[objRoom.SentEnergyToRoom];
                    fEnergeTo = sf.GetEnergyInRoom(objRoomTo);
                    fEnergeFrom = sf.GetEnergyInRoom(objRoom);
                    fOverFlowManual = fOverFlowManual + objRoom.Describe + ' (' +objRoom.Name + ') [' + fEnergeFrom +'] -->';
                    fOverFlowManual = fOverFlowManual + ' ' + objRoomTo.Describe + ' (' +objRoomTo.Name + ') [' + fEnergeTo +']; ';
                    continue;    
                }
                
                var fEnergyInRoomFrom = sf.GetEnergyInRoom(objRoom);
                if (fEnergyInRoomFrom>objRoom.OverflowOnEnergy){
                    var fRoomTo = null;
                    var fNeedEnergy = 0;
                    for (let j=0;j<Memory.MainRooms.length;j++){
                        if (i==j) continue;
                        objRoomTo = Memory.Data[Memory.MainRooms[j]];
                        if (sf.isTerminalAndStorage(objRoomTo) == 0) continue;
                        if (objRoomTo.SentEnergyToRoomManual != '') continue;
                        var fEnergyInRoomTo = sf.GetEnergyInRoom(objRoomTo);
                        fTmpNeedEnergy = (objRoomTo.OverflowNeedEnergy - fEnergyInRoomTo);
                        if (fTmpNeedEnergy > fNeedEnergy){
                            fNeedEnergy = fTmpNeedEnergy;
                            fRoomTo = Memory.MainRooms[j];
                        }
                    }

                    if (fRoomTo!=null){
                        objRoom.SentEnergyToRoom = fRoomTo;
                        objRoomTo = Memory.Data[fRoomTo];
                        fEnergeTo = sf.GetEnergyInRoom(objRoomTo);
                        fEnergeFrom = sf.GetEnergyInRoom(objRoom);
                        fOverFlowAutomat = fOverFlowAutomat + objRoom.Describe + ' (' +objRoom.Name + ') [' + fEnergeFrom +'] -->';
                        fOverFlowAutomat = fOverFlowAutomat + ' ' + objRoomTo.Describe + ' (' +objRoomTo.Name + ') [' + fEnergeTo +']; ';
                    }
                }
            }catch(err){
                afunc.ErrorMessage('ERROR!!! OverflowControl', err);
            }
        }

        if (fOverFlowManual != ''){
            fOverFlowManual = 'Manual  :' + fOverFlowManual; 
            messenger.log('FLOW',fOverFlowManual,COLOR_ORANGE);	   
        }

        if (fOverFlowAutomat != ''){
            fOverFlowAutomat = 'Automat :' + fOverFlowAutomat; 
            messenger.log('FLOW',fOverFlowAutomat,COLOR_ORANGE);	   
        }
    },
}

module.exports = logistics;