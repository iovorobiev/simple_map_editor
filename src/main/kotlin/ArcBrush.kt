import org.w3c.dom.CanvasRenderingContext2D
import kotlin.math.PI

class ArcBrush : Brush {
    override fun paint(ctx: CanvasRenderingContext2D, x: Double, y: Double, size: Double) {
        ctx.arc(x, y, size, 0.0, 2 * PI)
    }
}