package com.pharbers.pattern.manager

import com.pharbers.pattern.steps.commonStep
import com.pharbers.pattern.entity.commonResult

case class SequenceSteps(steps : List[commonStep], cr : Option[commonResult] = None)
