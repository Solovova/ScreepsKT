//var messenger                      	= require('modul.messenger');

var messenger = {
    log: function(fType,fText,fColor,fRoom) {
        if (fType == 'LABFIL') return;
        if (fType == 'LOGIST') return;
        if (fType == 'ATTACK') return;
        if (fType == 'TRANS') return;
        if (fType == 'BUILDER') return;
        if (fType == 'QUEUE') return;
        if (fType == 'CPU') return;

        if (Memory.accaunt == 'testop' && fType == 'LAB') return;

        if (fRoom == null) fRoom = '';
        fHTMLColor = '';
        if (fColor==COLOR_YELLOW) fHTMLColor = 'yellow';
        if (fColor==COLOR_RED) fHTMLColor = 'red';
        if (fColor==COLOR_GREEN) fHTMLColor = 'green';
        if (fColor==COLOR_BLUE) fHTMLColor = 'blue';
        if (fColor==COLOR_ORANGE) fHTMLColor = 'orange';
        if (fColor!=null) fText = '<font color="' + fHTMLColor + '">' + fText + '</font>';
        
        console.log(fType + ' : ' + fRoom +' ' + fText);
    },	
};

//'<font color="yellow"> ' + objRoom.Name + ' SE </font>

module.exports = messenger;