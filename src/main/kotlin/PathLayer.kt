import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.math.PI

class PathLayer(private val pathSettings: PathSettings, override val brushProvider: () -> Brush,
                override var coordsTransformerProvider: () -> (Double) -> Double
) : Layer {

    private val pathCtx = createCanvasAndGetContext(pathSettings.width, pathSettings.height)
    private val borderCtx = createCanvasAndGetContext(pathSettings.width, pathSettings.height)
    private val outputCtx = createCanvasAndGetContext(pathSettings.width, pathSettings.height)

    override fun prepareCanvasFor(newX: Double, newY: Double): HTMLCanvasElement {
        val coordsTransformer = coordsTransformerProvider()
        borderCtx.renderBorder(coordsTransformer(newX), coordsTransformer(newY))
        pathCtx.renderPath(coordsTransformer(newX), coordsTransformer(newY))
        outputCtx.apply {
            clearRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())
            drawImage(borderCtx.canvas, 0.0, 0.0)
            drawImage(pathCtx.canvas, 0.0, 0.0)
        }
        return outputCtx.canvas
    }

    private fun CanvasRenderingContext2D.renderBorder(x: Double, y: Double) {
        beginPath()
        moveTo(x, y)
        beginPath()
        brushProvider().paint(this, x, y, pathSettings.borderRadius)
        fillStyle = pathSettings.borderColor
        fill()
    }

    private fun CanvasRenderingContext2D.renderPath(x: Double, y: Double) {
        beginPath()
        moveTo(x, y)
        beginPath()
        brushProvider().paint(this, x, y, pathSettings.radius)
        fillStyle = pathSettings.pathColor
        fill()
    }
}

data class PathSettings(
    val width: Int,
    val height: Int,
    val radius: Double,
    val borderRadius: Double,
    val pathColor: String,
    val borderColor: String,
)