var afunc                               = require('modul.afunc');

var role_Tower = {
    RunTower: function(objRoom){

        
        var fTask = null;
        var fTarget = null;
        var fMaxTower = null;

        if (Game.rooms[objRoom.Name] == null) return;

        //0
        var Hostile = Game.rooms[objRoom.Name].find(FIND_HOSTILE_CREEPS);
        if (fTask == null){
            var DamagedStructures = Game.rooms[objRoom.Name].find(FIND_STRUCTURES, {
                filter: (structure) => (structure.structureType == STRUCTURE_RAMPART || structure.structureType == STRUCTURE_WALL)&&(structure.hits<100000)
            });
            if(DamagedStructures.length != 0 && Hostile.length>0) {
                var fMinHealth = 300000000;
                for (let i=0;i<DamagedStructures.length;i++){
                    if (DamagedStructures[i].hits<fMinHealth){
                        fTarget = DamagedStructures[i];
                        fMinHealth = DamagedStructures[i].hits;
                    }
                }
                fTask = 'repair';                 
            }
        }

        //1 
        if (fTask == null){
        if(Hostile.length != 0) {
            //
            
                fTask = 'attack';
            //&& objRoom.Name !='W41N3'
            //fTarget = Hostile[Math.floor(Math.random() * Hostile.length)]
            //fTarget = Hostile;
            //fArray = 1;
                if (objRoom.TowerLastTarget == Hostile[0].id && Hostile.length>=2){
                    fTarget = Hostile[1];
                }else{
                    fTarget = Hostile[0];
                }
                objRoom.TowerLastTarget = fTarget.id;
            }else{
                objRoom.TowerLastTarget = null;
            }
        }

        //2
        if (fTask == null){
            var damagedCreeps = Game.rooms[objRoom.Name].find(FIND_MY_CREEPS, {
                filter: function(object) {
                    return object.hits < object.hitsMax;
                }
            });

            if(damagedCreeps.length != 0) {
                fTask = 'heal';
                fTarget = damagedCreeps[0];    
            }
        }

        //3 Containers
        if (fTask == null){
            var DamagedContainers = Game.rooms[objRoom.Name].find(FIND_STRUCTURES, {
                filter: (structure) => (structure.structureType == STRUCTURE_CONTAINER)&&(structure.hits<=230000)
            });
            if(DamagedContainers.length != 0) {
                fTask = 'repair';
                fTarget = DamagedContainers[0]; 
                fMaxTower = 1;   
            }
        }

         //3.1 STRUCTURE_RAMPART<5000
         if (fTask == null){
             
             var DamagedContainers = Game.rooms[objRoom.Name].find(FIND_STRUCTURES, {
                 filter: (structure) => (structure.structureType == STRUCTURE_RAMPART)&&(structure.hits<=5000)
             });
             if(DamagedContainers.length != 0) {
                 fTask = 'repair';
                 fTarget = DamagedContainers[0]; 
                 fMaxTower = 1;   
             }
         }

        //4 Roads
        if (fTask == null){
            var _Start = Game.cpu.getUsed();
            var DamagedRoads =  Game.rooms[objRoom.Name].find(FIND_STRUCTURES, {
                filter: (structure) => (structure.structureType == STRUCTURE_ROAD && structure.hits < (structure.hitsMax - 1000))
            });
            Memory._Sum = Memory._Sum + Game.cpu.getUsed()-_Start;
            if(DamagedRoads.length != 0) {
                fTask = 'repair';
                fTarget = DamagedRoads[0];
                fMaxTower = 1;    
            }
        }

        //5 Ramparts и Wall держим на уровне objRoom.WallHints
        if (fTask == null){
            var fStorage = Game.getObjectById(objRoom.Storage);
            if (fStorage != null){
                var fWallHints = objRoom.WallHints;

                if (objRoom.BoostBuilder >= 1) fWallHints = 2000;
                if (fStorage.store.energy>=objRoom.StorageEnergyMin){
                    var DamagedStructures = Game.rooms[objRoom.Name].find(FIND_STRUCTURES, {
                        filter: (structure) => ((structure.structureType == STRUCTURE_RAMPART || structure.structureType == STRUCTURE_WALL) && structure.hits<fWallHints)
                    });
                    if(DamagedStructures.length != 0) {
                        var fMinHealth = 300000000;
                        for (let i=0;i<DamagedStructures.length;i++){
                            if (DamagedStructures[i].hits<fMinHealth){
                                fTarget = DamagedStructures[i];
                                fMinHealth = DamagedStructures[i].hits;
                            }
                        }
                        fTask = 'repair';
                        fMaxTower = 2; 
                    }
                }    
            }
        }

        //6 Ramparts ближе 6 поднимаем до objRoom.HitsRamparts 
        if (fTask == null){
            var fStorage = Game.getObjectById(objRoom.Storage);
            if (fStorage != null){
                if (fStorage.store.energy>=objRoom.StorageEnergyForce){
                    var fController = Game.getObjectById(objRoom.Controller);
                    if (fController.level == 8){
                        var fMaxRampartsHits = objRoom.HitsRamparts;
                        var fNuker = Game.getObjectById(objRoom.Nuker);
                        var fObserver = Game.getObjectById(objRoom.Observer);
                        var Rampart = fStorage.pos.findClosestByRange(FIND_STRUCTURES, {
                            filter: (structure) => (structure.structureType == STRUCTURE_RAMPART)&&(structure.hits<=fMaxRampartsHits)
                        });

                        if (Rampart!=null){
                            var fDistance = afunc.CalcDistancePos(fStorage.pos,Rampart.pos);
                            if (fDistance>=6 && fNuker!=null) fDistance = afunc.CalcDistancePos(fNuker.pos,Rampart.pos);
                            if (fDistance>=6 && fObserver!=null) fDistance = afunc.CalcDistancePos(fObserver.pos,Rampart.pos);
                            if (fDistance<6){
                                fTask = 'repair';
                                fTarget = Rampart;
                                fMaxTower = 2; 
                            }
                        }
                    }
                }
            }
        }


        if (fTask == null) return;

        var towers = Game.rooms[objRoom.Name].find(FIND_STRUCTURES, {
                filter: (structure) => {
                return (structure.structureType == STRUCTURE_TOWER);
                }
        });

        if (fMaxTower ==null) fMaxTower = towers.length;
        if (fMaxTower > towers.length) fMaxTower = towers.length;
       
        
        for (var i = 0; i < fMaxTower; i++) {
            var tower = towers[i];
            if(tower) {
                if (fTask == 'attack'){
                    tower.attack(fTarget);
                    continue;
                }

                if (fTask == 'heal'){
                    tower.heal(fTarget);
                    continue;
                }

                if (fTask == 'repair'){
                    tower.repair(fTarget);
                    continue;
                }
            }
        }

    }
};

module.exports = role_Tower;