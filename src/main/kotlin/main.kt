import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.DIV
import kotlinx.html.P
import org.w3c.dom.*
import kotlin.math.abs
import kotlin.math.hypot

const val RIGHT_MOUSE_BUTTON: Short = 2

fun main() {
    window.onload = {
        runMain()
    }
}

data class Point(val x: Double, val y: Double)

fun runMain() {
    val resultCanvas = document.getElementById("map") as HTMLCanvasElement
    val brushes = document.getElementsByName("brush")

    var brush: Brush = SquareBrush()
    for (i in 0 until brushes.length) {
        (brushes[i] as HTMLInputElement).onclick = {

            brush = when ((it.target as? HTMLInputElement)?.value ) {
                "square" -> SquareBrush()
                "circle" -> ArcBrush()
                "eraser" -> EraserBrush()
                else -> ArcBrush()
            }
            null
        }
    }
    val gridSize = 32.0
    val snap = document.getElementById("snap") as HTMLInputElement
    var coordsTransformer = CoordsTransformer.identity
    snap.onclick = {
        coordsTransformer = if (snap.checked) {
            CoordsTransformer.makeQuantized(gridSize)
        } else {
            CoordsTransformer.identity
        }
        null
    }

    val pathRadius = 20.0
    val pathBorderRadius = 4.0
    val shadowLayer = ShadowLayer(
        ShadeSettings(
            resultCanvas.width,
            resultCanvas.height,
            10,
            12,
            12,
            pathRadius + pathBorderRadius + 8
        ),
        { brush }
    ) {
        coordsTransformer
    }
    val pathLayer = PathLayer(
        PathSettings(
            resultCanvas.width,
            resultCanvas.height,
            pathRadius,
            pathRadius + pathBorderRadius,
            "rgba(242, 238, 230, 1)",
            "black"
        ),
        { brush }
    ) {
        coordsTransformer
    }
    val gridLayer = GridLayer(
        GridSettings(
            resultCanvas.width,
            resultCanvas.height,
            arrayOf(1.0, 4.0),
            gridSize,
            "black",
            pathRadius + pathBorderRadius
        ),
        { brush },
    ) {
        coordsTransformer
    }
    val mapRenderer = MapRenderer(resultCanvas, listOf(shadowLayer, pathLayer, gridLayer))
    var draw = false
    var prevX = 0.0
    var prevY = 0.0
    resultCanvas.apply {
        onmousedown = mouseDown@{ event ->
            if (event.button == RIGHT_MOUSE_BUTTON) {
                return@mouseDown null
            }
            draw = true
            prevX = (event.clientX - offsetLeft).toDouble()
            prevY = (event.clientY - offsetTop).toDouble()
            mapRenderer.appendPath(
                prevX,
                prevY
            )
        }
        onmousemove = mouseMove@{ event ->
            if (!draw || event.button == RIGHT_MOUSE_BUTTON) {
                return@mouseMove null
            }
            val x = (event.clientX - offsetLeft).toDouble()
            val y = (event.clientY - offsetTop).toDouble()
            val distance = hypot(abs(x - prevX), abs(y - prevY))
            if (distance > 12) {
                mapRenderer.appendPath(x, y)
            }
            null
        }
        onmouseup = { event ->
            draw = false
            Unit
        }
    }
}