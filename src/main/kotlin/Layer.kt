import org.w3c.dom.HTMLCanvasElement

interface Layer {
    val brushProvider: () -> Brush
    var coordsTransformerProvider: () -> ((Double) -> Double)
    fun prepareCanvasFor(newX: Double, newY: Double): HTMLCanvasElement
    
}

object CoordsTransformer {
    val identity: (Double) -> Double = { it }
    fun makeQuantized(gridSize: Double): (Double) -> Double = {
        gridSize * (it / gridSize).toInt() + gridSize / 2
    }
}