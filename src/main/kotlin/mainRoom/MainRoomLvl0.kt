package mainRoom

fun MainRoom.needCorrection0() {
    when {
        this.room.energyCapacityAvailable >= 800 ->
            if (this.need[0][0] == 0) {
                this.need[0][0] = 10
                if (this.source.size == 1) this.need[0][0] = this.need[0][0] / 2 + 1
            }
        this.room.energyCapacityAvailable >= 400 ->
            if (this.need[0][0] == 0) {
                this.need[0][0] = 14
                if (this.source.size == 1) this.need[0][0] = this.need[0][0] / 2 + 1
            }
        else -> if (this.need[0][0] == 0) {
            this.need[0][0] = 16
            if (this.source.size == 1) this.need[0][0] = this.need[0][0] / 2 + 1
        }
    }

    if (this.source.containsKey(0) && this.structureContainerNearSource.containsKey(0)) {
        if (this.need[1][1] == 0) this.need[1][1] = 1
        //this.need[0][0] = this.need[0][0] - 2
    }

    if (this.source.containsKey(1) && this.structureContainerNearSource.containsKey(1))
        if (this.need[1][3] == 0) this.need[1][3] = 1
}