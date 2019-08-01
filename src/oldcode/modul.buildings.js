var buildings = {
    Build: function(objRoom,fPrimeColor,fSecondaryColor,fWhatBuild,fCount) {
        //return 1 если чтото нашли и строим 0 если ничего не нашли
        var fBuild = 0;
        var flags = Game.rooms[objRoom.Name].find(FIND_FLAGS, {
            filter: (structure) => {
                return ((structure.color == fPrimeColor)&&(structure.secondaryColor == fSecondaryColor));
            }
        });
        if (flags.length<fCount) fCount = flags.length;
        for (var i=0;i<fCount;i++){
            if (Game.rooms[objRoom.Name].createConstructionSite(flags[i].pos, fWhatBuild) == OK) {
            	flags[i].remove();
				fBuild = 1;
			};
        }   
        return fBuild;
    },


    Building: function(objRoom) {
        //10 color COLOR_WHITE

        //secondaryColor
        //1 COLOR_RED       STRUCTURE_EXTENSION 
        //2 COLOR_PURPLE    STRUCTURE_CONTAINER near controller 
        //3 COLOR_BLUE      STRUCTURE_TOWER 
        //4 COLOR_CYAN      STRUCTURE_ROAD after tower
        //5 COLOR_GREEN     STRUCTURE_STORAGE 
		//6 COLOR_YELLOW    STRUCTURE_CONTAINER near source 
		//7 COLOR_ORANGE    STRUCTURE_ROAD before storage
		//8 COLOR_BROWN     STRUCTURE_SPAWN

        if (Math.round(Game.time/100)*100!=Game.time) return; //проверяем каждые 100 тиков
		if (!Game.rooms[objRoom.Name]) return;
        var targets = Game.rooms[objRoom.Name].find(FIND_CONSTRUCTION_SITES);
        if(targets.length > 0) return; //чтото строится не проверяем
        
		var controller = Game.getObjectById(objRoom.Controller);
		if (!controller) return;
        
        if (controller.level == 1) {
			if (buildings.Build(objRoom,COLOR_WHITE,COLOR_YELLOW,STRUCTURE_CONTAINER,2)==1) return;
			if (buildings.Build(objRoom,COLOR_WHITE,COLOR_BROWN,STRUCTURE_SPAWN,1)==1) return;
        }

        if (controller.level == 2) {
            if (objRoom.MaxSpawnEnergy!=550) {//строим extension 5
                if (buildings.Build(objRoom,COLOR_WHITE,COLOR_RED,STRUCTURE_EXTENSION,5)==1) return;
            }
            if (buildings.Build(objRoom,COLOR_WHITE,COLOR_PURPLE,STRUCTURE_CONTAINER,1)==1) return;
        }

        if (controller.level == 3) {
            if (objRoom.MaxSpawnEnergy!=800) {//строим extension 5
                if (buildings.Build(objRoom,COLOR_WHITE,COLOR_RED,STRUCTURE_EXTENSION,5)==1) return;
            };

            if (buildings.Build(objRoom,COLOR_WHITE,COLOR_BLUE,STRUCTURE_TOWER,1)==1) return;
            if (buildings.Build(objRoom,COLOR_WHITE,COLOR_CYAN,STRUCTURE_ROAD,80)==1) return;
        };

        if (controller.level == 4) {
			if (buildings.Build(objRoom,COLOR_WHITE,COLOR_ORANGE,STRUCTURE_ROAD,80)==1) return;
            if (objRoom.MaxSpawnEnergy!=1300) {//строим extension 10
                if (buildings.Build(objRoom,COLOR_WHITE,COLOR_RED,STRUCTURE_EXTENSION,10)==1) return;
            }
            if (buildings.Build(objRoom,COLOR_WHITE,COLOR_GREEN,STRUCTURE_STORAGE,1)==1) return;
        };
    },


SaveFlags: function(fRoomName) {
	if (!Game.rooms[fRoomName]) return;
	if (Memory.SaveFlags!=1) return;

    var flags = Game.rooms[fRoomName].find(FIND_FLAGS);
    var fCreateFlagStrAll = '';
    for (i=0;i<flags.length;i++){
        //var fPosition = new RoomPosition(8,30,'W4N3'); fPosition.createFlag(null,COLOR_WHITE,COLOR_WHITE);
        var fCreateFlagStr = 'var fPosition = new RoomPosition(' + flags[i].pos.x + ',' + flags[i].pos.y + ',fRoomName);fPosition.createFlag(null,';
        fCreateFlagStr =  fCreateFlagStr + 10 + ',' + flags[i].secondaryColor + ');' + '\n' + '\t\t\t';
        fCreateFlagStrAll = fCreateFlagStrAll + fCreateFlagStr;
        
        
    };
    Memory.SaveFlags = 0;
    console.log(fCreateFlagStrAll);  
},

LoadFlags: function(fRoomName) {
	if (!Game.rooms[fRoomName]) return;
    var flags = Game.rooms[fRoomName].find(FIND_FLAGS);
    if (flags.length!=0) return;
    // if (fRoomName=='W4N3'){
    //     	var fPosition = new RoomPosition(6,30,fRoomName);fPosition.createFlag(null,10,10);
	// 		var fPosition = new RoomPosition(43,2,fRoomName);fPosition.createFlag(null,10,10);
	// 		var fPosition = new RoomPosition(27,2,fRoomName);fPosition.createFlag(null,10,10);
	// 		var fPosition = new RoomPosition(31,23,fRoomName);fPosition.createFlag(null,10,1);
	// 		var fPosition = new RoomPosition(29,23,fRoomName);fPosition.createFlag(null,10,1);
	// 		var fPosition = new RoomPosition(27,23,fRoomName);fPosition.createFlag(null,10,1);
	// 		var fPosition = new RoomPosition(28,22,fRoomName);fPosition.createFlag(null,10,1);
	// 		var fPosition = new RoomPosition(30,22,fRoomName);fPosition.createFlag(null,10,1);
	// 		var fPosition = new RoomPosition(27,21,fRoomName);fPosition.createFlag(null,10,1);
	// 		var fPosition = new RoomPosition(29,21,fRoomName);fPosition.createFlag(null,10,1);
	// 		var fPosition = new RoomPosition(31,21,fRoomName);fPosition.createFlag(null,10,1);
	// 		var fPosition = new RoomPosition(28,20,fRoomName);fPosition.createFlag(null,10,1);
	// 		var fPosition = new RoomPosition(30,20,fRoomName);fPosition.createFlag(null,10,1);
	// 		var fPosition = new RoomPosition(9,32,fRoomName);fPosition.createFlag(null,10,2);
	// 		var fPosition = new RoomPosition(34,25,fRoomName);fPosition.createFlag(null,10,3);
	// 		var fPosition = new RoomPosition(34,21,fRoomName);fPosition.createFlag(null,10,5);
	// 		var fPosition = new RoomPosition(37,23,fRoomName);fPosition.createFlag(null,10,1);
	// 		var fPosition = new RoomPosition(39,23,fRoomName);fPosition.createFlag(null,10,1);
	// 		var fPosition = new RoomPosition(41,23,fRoomName);fPosition.createFlag(null,10,1);
	// 		var fPosition = new RoomPosition(38,22,fRoomName);fPosition.createFlag(null,10,1);
	// 		var fPosition = new RoomPosition(40,22,fRoomName);fPosition.createFlag(null,10,1);
	// 		var fPosition = new RoomPosition(37,21,fRoomName);fPosition.createFlag(null,10,1);
	// 		var fPosition = new RoomPosition(39,21,fRoomName);fPosition.createFlag(null,10,1);
	// 		var fPosition = new RoomPosition(41,21,fRoomName);fPosition.createFlag(null,10,1);
	// 		var fPosition = new RoomPosition(38,20,fRoomName);fPosition.createFlag(null,10,1);
	// 		var fPosition = new RoomPosition(40,20,fRoomName);fPosition.createFlag(null,10,1);
	// 		var fPosition = new RoomPosition(10,32,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(21,25,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(20,26,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(19,27,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(18,28,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(17,29,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(16,30,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(15,31,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(14,32,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(13,32,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(12,32,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(11,32,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(9,32,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(9,31,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(9,33,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(9,34,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(8,31,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(22,24,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(33,23,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(32,23,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(33,24,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(32,24,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(33,25,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(34,24,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(35,24,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(35,25,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(34,26,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(35,23,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(36,23,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(36,22,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(35,22,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(34,22,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(33,22,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(32,21,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(32,22,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(33,21,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(35,21,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(36,21,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(34,20,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(33,20,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(35,20,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(36,20,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(32,20,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(31,20,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(29,20,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(27,20,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(28,21,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(30,21,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(31,22,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(29,22,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(27,22,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(28,23,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(30,23,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(31,24,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(30,24,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(29,24,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(28,24,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(27,24,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(26,24,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(25,24,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(24,24,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(23,24,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(27,5,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(27,6,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(28,7,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(29,8,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(30,9,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(30,10,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(30,11,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(31,12,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(32,13,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(33,14,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(33,15,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(33,16,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(33,17,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(33,18,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(33,19,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(37,20,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(38,19,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(39,18,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(40,17,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(41,16,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(42,15,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(43,14,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(43,13,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(43,12,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(43,11,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(43,10,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(43,9,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(43,7,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(43,8,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(43,9,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(43,6,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(44,5,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(39,20,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(41,20,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(41,22,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(39,22,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(40,21,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(38,21,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(37,22,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(38,23,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(40,23,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(40,19,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(42,21,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(42,23,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(39,24,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(37,24,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(41,24,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(30,19,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(28,19,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(26,23,fRoomName);fPosition.createFlag(null,10,7);
	// 		var fPosition = new RoomPosition(26,21,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(28,4,fRoomName);fPosition.createFlag(null,10,6);
	// 		var fPosition = new RoomPosition(45,4,fRoomName);fPosition.createFlag(null,10,6);
	// 		var fPosition = new RoomPosition(25,22,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(24,23,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(29,18,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(30,17,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(31,16,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(32,15,fRoomName);fPosition.createFlag(null,10,4);
	// 		var fPosition = new RoomPosition(31,18,fRoomName);fPosition.createFlag(null,10,10);
	// };
	
	// if (fRoomName=='W5N3'){
	// 		var fPosition = new RoomPosition(28,7,fRoomName);fPosition.createFlag(null,10,8);
	// 		var fPosition = new RoomPosition(16,3,fRoomName);fPosition.createFlag(null,10,6);
	// 		var fPosition = new RoomPosition(6,22,fRoomName);fPosition.createFlag(null,10,6);
	// 		var fPosition = new RoomPosition(26,7,fRoomName);fPosition.createFlag(null,10,1);
	// 		var fPosition = new RoomPosition(24,7,fRoomName);fPosition.createFlag(null,10,1);
	// 		var fPosition = new RoomPosition(22,7,fRoomName);fPosition.createFlag(null,10,1);
	// 		var fPosition = new RoomPosition(25,8,fRoomName);fPosition.createFlag(null,10,1);
	// 		var fPosition = new RoomPosition(23,8,fRoomName);fPosition.createFlag(null,10,1);
	// 		var fPosition = new RoomPosition(36,7,fRoomName);fPosition.createFlag(null,10,2);
	// }
},

RemoveFlags: function(fRoomName) {
    var flags = Game.rooms[fRoomName].find(FIND_FLAGS);
    for (i=0;i<flags.length;i++){
        flags[i].remove();
    }  
},

}

module.exports = buildings;