
package com.mapper

import kotlin.js.Json
@JsModule("poisson-disk-sampling")
@JsNonModule
external class PoissonDiskSampling(json: Json) {
    fun fill(): Array<Array<Double>>
}
//@JsModule("poisson-disk-sampling")
//@JsNonModule
//external fun PoissonDiskSampling(json: Json): PoissonDiskSampling