package mainRoom

import mainContext.messenger
import screeps.api.COLOR_RED
import screeps.api.get
import screeps.api.structures.StructureContainer

fun MainRoom.needCorrection2() {
    //1 harvester ,carrier ,filler , small harvester-filler, small filler
    //1.1 harvester ,carrier
    if (this.structureLinkNearSource.containsKey(0))
        if (this.need[1][1] == 0) this.need[1][1] = 1

    if (this.structureLinkNearSource.containsKey(1))
        if (this.need[1][3] == 0) this.need[1][3] = 1

    //1.2 filler
    if (this.need[0][5] ==0) this.need[0][5] = 1 //filler
    if (this.need[1][5] ==0) this.need[1][5] = 1 //filler

    //1.3 small filler
    if ((this.have[5]==0)&&(this.getResourceInStorage()>2000))  this.need[0][9]=1
    if ((this.have[5]==0)&&(this.getResourceInStorage()<=2000))  this.need[0][0]=2

    //2 Upgrader
    if (this.constant.sentEnergyToRoom == "") {
        if (this.getResourceInStorage() > this.constant.energyForceUpgrade ) {
            this.need[1][7]=2
            this.need[2][7]=2
        }else{
            this.need[1][7]=2
            this.need[2][7]=1
        }
    }else{
        this.need[1][7]=0
        this.need[2][7]=0
    }

    if (this.getResourceInStorage()<this.constant.energyUpgradable) {
        this.need[1][7]=0
        this.need[2][7]=0
    }

    //carrier

    if (this.have[7]>3) this.need[1][6]=this.have[7] -1
    else this.need[1][6]=this.have[7]

    //2.1 Small upgrader
    if (this.need[0][6] == 0 && this.need[1][6] == 0 && this.need[2][6] == 0 &&
            this.need[0][7] == 0 && this.need[1][7] == 0 && this.need[2][7] == 0 &&
            this.have[6] == 0 && this.have[7] == 0 && this.getTicksToDowngrade() < 10000)
        this.need[0][13]=1

    //8 Builder
    if ((this.constructionSite.isNotEmpty()) && (this.getResourceInStorage() > this.constant.energyBuilder)) {
        this.need[1][8]=1
    }

    //9 Logist
    this.need[0][14]=1

    //10 Mineral harvesting
    if (this.mineral.mineralAmount > 0 &&
            this.structureContainerNearMineral.size == 1
            && this.structureExtractor.size == 1) {
        if (getResource(this.mineral.mineralType) < this.constant.mineralMaxInRoom)
            this.need[1][15] = 1
        else parent.parent.messenger("INFO", this.name, "Mineral full", COLOR_RED)
    }

    val container: StructureContainer? = this.structureContainerNearMineral[0]
    if (container != null && (container.store[this.mineral.mineralType] ?: 0) > 0) this.need[1][16] = 1

    //11 cleaner
    if (this.constant.needCleaner) this.need[2][17] = 1
}