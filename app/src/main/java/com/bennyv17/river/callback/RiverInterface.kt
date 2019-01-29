package com.bennyv17.river.callback

interface RiverInterface {
    fun isPermissionGranted(): Boolean
    fun isUnlocked(): Boolean
    fun unlockExtras()
}