import kotlinx.browser.document
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement

fun createCanvasAndGetContext(width: Int, height: Int): CanvasRenderingContext2D {
    val canvas = (document.createElement("canvas") as HTMLCanvasElement).apply {
        this.width = width
        this.height = height
    }
    return canvas.getContext("2d") as CanvasRenderingContext2D
}

fun CanvasRenderingContext2D.renderInMask(image: HTMLCanvasElement, mask: HTMLCanvasElement) {
    drawImage(mask, 0.0, 0.0)
    globalCompositeOperation = "source-in"
    drawImage(image, 0.0, 0.0)
    globalCompositeOperation = "source-over"
}