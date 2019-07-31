fun constantInit(mainRoom: MainRoom) {
    if (mainRoom.name == "W3N4") {
                                          //0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9
        arrayCopy(mainRoom.need[0], arrayOf(0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0))
        arrayCopy(mainRoom.need[1], arrayOf(0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0))
        arrayCopy(mainRoom.need[2], arrayOf(0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0))
    }
}