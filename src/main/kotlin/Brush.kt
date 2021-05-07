import org.w3c.dom.CanvasRenderingContext2D

interface Brush {
    fun paint(ctx: CanvasRenderingContext2D, x: Double, y: Double, size: Double)
}