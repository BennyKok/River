package com.bennyv17.river.tutorial

import com.bennyv17.river.item.SimpleTutorialItem
import com.bennyv17.river.item.TutorialItemActionCallback

interface Tutorial{
    fun getTutorialItems(callback: TutorialItemActionCallback): List<SimpleTutorialItem>
}