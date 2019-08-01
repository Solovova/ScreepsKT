var dataNull                   = require('modul.datanull');

var modul_auto = {
	
	//dataNull.RefillDefaultConstants();
	//dataNull.FillAllRoomConstants();
//modul_auto.runColonisation('W4N3','W5N3');
    runColonisation: function(fRoomDoinСolonization,fRoomСolonization) {
            //Код который если колонизирована комната переведет ее в мастер комнату в автомате, нужно потом переместить вручную
			//вверху нужно указызавть
			//1) колонизируемую в Memory.SlaivRooms
			//2) колонизируемую в Memory.Data[fRoomDoinСolonization].SlaivRooms
			//3) Memory.Data[fRoomСolonization].TypeOfRoom = 11;
			if (fRoomDoinСolonization!='') {
				if (Memory.Data[fRoomDoinСolonization].MaxSpawnEnergy<800){
					fRoomСolonization = '';
					var fIndSlave = Memory.SlaivRooms.indexOf(fRoomСolonization);
					Memory.SlaivRooms.splice(fIndSlave, 1);
					var fIndSlave = Memory.Data[fRoomDoinСolonization].SlaivRooms.indexOf(fRoomСolonization);
					Memory.Data[fRoomDoinСolonization].SlaivRooms.splice(fIndSlave, 1);
					Memory.Data[fRoomСolonization] = null;
					dataNull.RefillNull();	
					dataNull.RefillDefaultConstants();
					dataNull.FillAllRoomConstants();
				}
			}
						
			if (fRoomСolonization != '') {
				if ((Memory.Data[fRoomСolonization].Containers[3]!=null)){
					Memory.MainRooms.push(fRoomСolonization);
					var fIndSlave = Memory.SlaivRooms.indexOf(fRoomСolonization);
					Memory.SlaivRooms.splice(fIndSlave, 1);
					var fIndSlave = Memory.Data[fRoomDoinСolonization].SlaivRooms.indexOf(fRoomСolonization);
					Memory.Data[fRoomDoinСolonization].SlaivRooms.splice(fIndSlave, 1);
					Memory.Data[fRoomСolonization] = null;
					dataNull.RefillNull();	
					dataNull.RefillDefaultConstants();
					dataNull.FillAllRoomConstants();
					
					console.log('ALARM!!!!! Room:' + fRoomСolonization + ' already colonized, please rewrite type of rooms.')
				}
			}
			//----------------------------------------------------   
	}
};

module.exports = modul_auto;