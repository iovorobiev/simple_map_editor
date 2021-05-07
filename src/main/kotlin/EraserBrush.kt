import org.w3c.dom.CanvasRenderingContext2D

class EraserBrush : Brush {
    override fun paint(ctx: CanvasRenderingContext2D, x: Double, y: Double, size: Double) {
        ctx.clearRect(x - size, y - size, size * 2, size * 2)
    }

}