import org.w3c.dom.CanvasRenderingContext2D

class SquareBrush : Brush {
    override fun paint(ctx: CanvasRenderingContext2D, x: Double, y: Double, size: Double) {
        ctx.rect(x - size, y - size, 2 * size, 2 * size)
    }

}