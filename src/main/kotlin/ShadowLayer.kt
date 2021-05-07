import com.mapper.PoissonDiskSampling
import kotlinx.html.currentTimeMillis
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.js.json
import kotlin.math.PI
import kotlin.random.Random

class ShadowLayer(
    private val shadeSettings: ShadeSettings,
    override val brushProvider: () -> Brush,
    override var coordsTransformerProvider: () -> (Double) -> Double
) : Layer {

    private val outputCtx = createCanvasAndGetContext(shadeSettings.width, shadeSettings.height)
    private val shadowCachedCtx = createCanvasAndGetContext(shadeSettings.width, shadeSettings.height)
    private val shadowMaskCtx = createCanvasAndGetContext(shadeSettings.width, shadeSettings.height)

    init {
        shadowCachedCtx.drawShade(initShadowPoints())
    }

    private fun initShadowPoints(): List<ShadeLines> {
        val random = Random(currentTimeMillis())
        return PoissonDiskSampling(
            json(
                "shape" to arrayOf(shadeSettings.width, shadeSettings.height),
                "minDistance" to shadeSettings.minDistance,
                "maxDistance" to shadeSettings.maxDistance,
                "tries" to 12
            )
        )
            .fill()
            .map { coords ->
                ShadeLines(
                    coords[0],
                    coords[1],
                    random.nextInt(181) * PI / 180
                )
            }
    }

    override fun prepareCanvasFor(newX: Double, newY: Double): HTMLCanvasElement {
        val coordsTransformer = coordsTransformerProvider()
        shadowMaskCtx.renderShadowMask(coordsTransformer(newX), coordsTransformer(newY))
        outputCtx.apply {
            clearRect(0.0, 0.0, shadeSettings.width.toDouble(), shadeSettings.height.toDouble())
            renderInMask(shadowCachedCtx.canvas, shadowMaskCtx.canvas)
        }
        return outputCtx.canvas
    }

    private fun CanvasRenderingContext2D.renderShadowMask(x: Double, y: Double) {
        beginPath()
        moveTo(x, y)
        beginPath()
        brushProvider().paint(this, x, y, shadeSettings.shadeRadius)
        fillStyle = "rgba(0,0,0,1)"
        fill()
    }

    private fun CanvasRenderingContext2D.drawShade(points: List<ShadeLines>) {
        rect(0.0, 0.0, shadeSettings.width.toDouble(), shadeSettings.height.toDouble())
        fillStyle = "rgba(0,0,0,0.05)"
        fill()
        points.forEach { point ->
            beginPath()
            renderPoint(point.x, point.y, shadeSettings.lineLength, point.angle)
            stroke()
        }
        closePath()
    }

    private fun CanvasRenderingContext2D.renderPoint(x: Double, y: Double, lineLength: Int, angle: Double) {
        save()
        translate(x, y)
        rotate(angle)
        fillStyle = "black"
        strokeStyle = "black"
        moveTo(0.0, -lineLength / 2.0)
        lineTo(0.0, lineLength / 2.0)
        moveTo(-4.0, -lineLength / 2.0)
        lineTo(-4.0, lineLength / 2.0)
        moveTo(4.0, -lineLength / 2.0)
        lineTo(4.0, lineLength / 2.0)
        restore()
    }
}

data class ShadeSettings(
    val width: Int,
    val height: Int,
    val minDistance: Int,
    val maxDistance: Int,
    val lineLength: Int,
    val shadeRadius: Double,
)

data class ShadeLines(val x: Double, val y: Double, val angle: Double)
