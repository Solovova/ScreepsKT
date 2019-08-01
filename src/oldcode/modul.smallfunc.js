//var sf                      	= require('modul.smallfunc');

var smallfunc = {
    padLeft: function (input, totalWidth){
        var result = input;
        if (result.length < totalWidth){
            for(var i = result.length; i < totalWidth; i++){
                result = ' ' + result; 
            }       
        }
        return result;
	},
	
	padRight: function (input, totalWidth){
        var result = input;
        if (result.length < totalWidth){
            for(var i = result.length; i < totalWidth; i++){
                result = result + ' '; 
            }       
        }
        return result;
    },

    ArrayFillZero: function (arr){
        for (let i=0;i<arr.length;i++) arr[i]=0;
    },

    CheckObjectExistsArray(fArrId){
		var isAllPresent = 1;
		for (let i=0;i<fArrId.length;i++){
			if (fArrId[i]!=null) {
				fObj = Game.getObjectById(fArrId[i]);
				if (fObj==null) {
					fArrId[i]=null;
					isAllPresent=0;
				}
			} 
		}
		return isAllPresent;
	},

	CheckObjectExists(fId){
		var isPresent = 1;
		if (fId!=''){
			fObj = Game.getObjectById(fId);
			if (fObj==null)	isPresent=0;
		}
		return isPresent
	},

	GetStoredResources(store,resource){
		if (store[resource] == null) return 0;
		return store[resource];
	},

	GetMineralsInStore(store){
		var fEnergy = store[RESOURCE_ENERGY];
		if (fEnergy == 0) fEnergy = 0;
		return (_.sum(store)-fEnergy);
	},

	isTerminalAndStorage(objRoom){
		if (objRoom.oTerminal == null || objRoom.oStorage == null) return 0;
		return 1;
	},

	GetEnergyInRoom(objRoom){
		if (smallfunc.isTerminalAndStorage(objRoom)==0) return null;
		return (objRoom.oTerminal.store[RESOURCE_ENERGY] + objRoom.oStorage.store[RESOURCE_ENERGY]);
	},

	GetBodyCountParts(fBody){
		var result = new Object();
		for(var i = 0; i < fBody.length; ++i){
			if (result[fBody[i].type] == null){
				result[fBody[i].type] = 1;
			}else{
				result[fBody[i].type]++;
			}
		}
		return result;
	},

	GetMineralInLab(fLab,fMineral){
		if (fLab == null) return 0;
		if (fLab.mineralType != fMineral) return 0;
		return fLab.mineralAmount;
	},

	GetComponentOfReaction(fReaction){
		var fReagent0 = null;
		var fReagent1 = null;
		var fKeys0 = Object.keys(REACTIONS)
		for (var i0 = 0;i0<fKeys0.length;i0++){
			var fKeys1 = Object.keys(REACTIONS[fKeys0[i0]]);
			for (let i1 = 0;i1<fKeys1.length;i1++){
				if (REACTIONS[fKeys0[i0]][fKeys1[i1]] == fReaction){
					var fReagent0 = fKeys0[i0];
					var fReagent1 = fKeys1[i1];	
					break;
				}
			}
		}
		var result = new Object();
		result.fReagent0 = fReagent0;
		result.fReagent1 = fReagent1;
		return result;
	}


	
	
};

module.exports = smallfunc;