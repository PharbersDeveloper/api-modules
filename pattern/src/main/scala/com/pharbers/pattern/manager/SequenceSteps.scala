package com.pharbers.pattern.manager

import com.pharbers.pattern.steps.{commonResult, commonStep}

case class SequenceSteps(steps : List[commonStep], cr : Option[commonResult] = None)
