var sf                      	= require('modul.smallfunc');

var buttlegroup = {
    GetWarriorBody(fType){

    },

	init: function() {
        if (Memory.ButtleGroup == null) Memory.ButtleGroup = new Object();
    },

    RefillGroup: function(fNameGroup) {
        buttlegroup.init();
        if (Memory.ButtleGroup[fNameGroup] == null) Memory.ButtleGroup.fNameGroup = new Object();
        objButtleGroup = Memory.ButtleGroup[fNameGroup];
        if (objButtleGroup.TypeOfGroup == null) objButtleGroup.TypeOfGroup = 1;
        if (objButtleGroup.SpawnRooms == null)  objButtleGroup.SpawnRooms = [];
        if (objButtleGroup.Queue == null)       objButtleGroup.Queue = [];
        if (objButtleGroup.Need == null)        objButtleGroup.Need = new Array(100); sf.ArrayFillZero(objButtleGroup.Need);
		objButtleGroup.CalcRoleCreeps 		= new Array(100); sf.ArrayFillZero(objButtleGroup.CalcRoleCreeps);
        objButtleGroup.CalcForQueue 		= new Array(100); sf.ArrayFillZero(objButtleGroup.CalcForQueue)
        
        //в очереди объект role,roomname,priority в комнате сортанули так чтоб своя комната если есть была первая а дальше по приоритету
        //взяли для спавна первого и удалили из очереди
        //Для ролей указывать конкретную комнату, если не вказана то не важно, например варриоры только в комнате где их сможем проапгрейдить
        //Формируем очередь, и с єтой очереди берут комнаты задачи, если не его крип то пропускать, 
    }
  
};

module.exports = buttlegroup;