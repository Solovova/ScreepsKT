package mainRoom

import constants.CacheCarrier
import mainContext.getCacheRecordRoom

fun MainRoom.needCorrection1() {
    //1 harvester ,carrier ,filler , small harvester-filler, small filler
    //1.1 harvester ,carrier
    val carrierAuto0: CacheCarrier? = parent.parent.getCacheRecordRoom("mainContainer0",this)
    if (carrierAuto0!=null) {
        if (this.need[1][1] == 0) this.need[1][1] = 1
        if (this.need[1][2] == 0) this.need[1][2] = carrierAuto0.needCarriers
    }

    val carrierAuto1: CacheCarrier? = parent.parent.getCacheRecordRoom("mainContainer1",this)
    if (carrierAuto1!=null) {
        if (this.need[1][3] == 0) this.need[1][3] = 1
        if (this.need[1][4] == 0) this.need[1][4] = carrierAuto1.needCarriers
    }

    //1.2 filler
    if (this.need[0][5] ==0) this.need[0][5] = 1 //filler
    if (this.need[1][5] ==0) this.need[1][5] = 1 //filler

    //1.3 small filler
    if ((this.have[5]==0)&&(this.getResourceInStorage()>2000))  this.need[0][9]=1
    if ((this.have[5]==0)&&(this.getResourceInStorage()<=2000))  this.need[0][0]=2
    if (this.getResourceInStorage()==0)  this.need[0][0]=2

    //2 Upgrader
    if (this.room.energyCapacityAvailable >= 1800) {
        this.need[1][7] = 1
        this.need[2][7] = 3
    } else {
        this.need[1][7] = 2
        this.need[2][7] = 2
    }

    if (this.getResourceInStorage()<this.constant.energyUpgradable) {
        this.need[1][7]=0
        this.need[2][7]=0
    }

    //carrier

    if (this.have[7] <= 3) this.need[1][6]=this.have[7]
    else this.need[1][6]=this.have[7]-1



    //2.1 Small upgrader
    if (this.need[0][6] == 0 && this.need[1][6] == 0 && this.need[2][6] == 0 &&
            this.need[0][7] == 0 && this.need[1][7] == 0 && this.need[2][7] == 0 &&
            this.have[6] == 0 && this.have[7] == 0 && this.getTicksToDowngrade() < 10000)
        this.need[0][13]=1

    //8 Builder
    if ((this.constructionSite.isNotEmpty()) && (this.getResourceInStorage() > this.constant.energyBuilder)) {
        this.need[1][8]=1
    }
}