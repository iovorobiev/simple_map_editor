import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement

class MapRenderer(
    canvas: HTMLCanvasElement,
    private val layers:List<Layer>
) {

    private val ctx = canvas.getContext("2d") as CanvasRenderingContext2D

    fun appendPath(newX: Double, newY: Double) {
        ctx.clearRect(0.0, 0.0, ctx.canvas.width.toDouble(), ctx.canvas.height.toDouble())
        layers.map { layer ->
            layer.prepareCanvasFor(newX, newY)
        }.forEach { layerCanvas ->
            ctx.drawImage(layerCanvas, 0.0, 0.0)
        }
    }
}