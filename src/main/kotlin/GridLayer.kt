import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.math.PI

class GridLayer(
    private val settings: GridSettings,
    override val brushProvider: () -> Brush,
    override var coordsTransformerProvider: () -> (Double) -> Double,
) : Layer {
    private val cachedGridCtx = createCanvasAndGetContext(settings.width, settings.height)
    private val pathMaskCtx = createCanvasAndGetContext(settings.width, settings.height)
    private val outputCtx = createCanvasAndGetContext(settings.width, settings.height)

    init {
        cachedGridCtx.renderGrid()
    }

    override fun prepareCanvasFor(newX: Double, newY: Double): HTMLCanvasElement {
        val coordsTransformer = coordsTransformerProvider()
        pathMaskCtx.renderShadowMask(coordsTransformer(newX), coordsTransformer(newY))
        outputCtx.apply {
            clearRect(0.0, 0.0, settings.width.toDouble(), settings.height.toDouble())
            renderInMask(cachedGridCtx.canvas, pathMaskCtx.canvas)
        }
        return outputCtx.canvas
    }

    private fun CanvasRenderingContext2D.renderGrid() {
        val verticalLines = (canvas.width / settings.gridSize).toInt()
        val horizontalLines = (canvas.height / settings.gridSize).toInt()

        beginPath()
        for (i in 0..verticalLines) {
            moveTo((i * settings.gridSize), 0.0)
            lineTo((i * settings.gridSize), canvas.height.toDouble())
        }
        setLineDash(settings.lineDashSettings)
        stroke()

        beginPath()
        for (i in 0..horizontalLines) {
            moveTo(0.0, (i * settings.gridSize))
            lineTo(canvas.width.toDouble(), (i * settings.gridSize))
        }
        setLineDash(settings.lineDashSettings)
        stroke()
    }

    private fun CanvasRenderingContext2D.renderShadowMask(x: Double, y: Double) {
        beginPath()
        moveTo(x, y)
        beginPath()
        brushProvider().paint(this, x, y, settings.maskRadius)
        fillStyle = "rgba(0,0,0,1)"
        fill()
        closePath()
    }
}

data class GridSettings(
    val width: Int,
    val height: Int,
    val lineDashSettings: Array<Double>,
    val gridSize: Double,
    val gridColor: String,
    val maskRadius: Double,
)