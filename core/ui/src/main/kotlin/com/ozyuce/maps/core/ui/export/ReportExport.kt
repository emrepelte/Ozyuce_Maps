package com.ozyuce.maps.core.ui.export

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import java.io.File
import java.io.FileOutputStream

// İstediğiniz Compose içeriğini offscreen ölçüp çizer; scroll'dan bağımsız tam sayfa verir.
fun renderComposableToView(
    context: Context,
    widthPx: Int,
    heightPx: Int,
    content: @Composable () -> Unit
): View {
    val composeView = ComposeView(context).apply {
        layoutParams = FrameLayout.LayoutParams(widthPx, heightPx)
        setContent(content)
        // Offscreen ölçüm
        measure(
            View.MeasureSpec.makeMeasureSpec(widthPx, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(heightPx, View.MeasureSpec.EXACTLY)
        )
        layout(0, 0, measuredWidth, measuredHeight)
    }
    return composeView
}

fun exportViewAsPng(view: View, fileName: String = "report_${'$'}{System.currentTimeMillis()}"): File {
    val bmp: Bitmap = if (view.width > 0 && view.height > 0) {
        view.drawToBitmap()
    } else {
        // Güvenlik: ölçü yoksa ölç -> çiz
        view.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.WRAP_CONTENT, View.MeasureSpec.UNSPECIFIED)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888).also { b ->
            Canvas(b).apply { view.draw(this) }
        }
    }

    val outFile = File(view.context.cacheDir, "$fileName.png")
    FileOutputStream(outFile).use { fos ->
        bmp.compress(Bitmap.CompressFormat.PNG, 100, fos)
    }
    return outFile
}

fun exportViewAsPdf(view: View, fileName: String = "report_${'$'}{System.currentTimeMillis()}"): File {
    val width = if (view.width > 0) view.width else 1080
    val height = if (view.height > 0) view.height else {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.WRAP_CONTENT, View.MeasureSpec.UNSPECIFIED)
        )
        view.measuredHeight
    }
    if (view.width == 0 || view.height == 0) {
        view.layout(0, 0, width, height)
    }

    val doc = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(width, height, 1).create()
    val page = doc.startPage(pageInfo)
    view.draw(page.canvas) // p2/p3 yok; doğrudan view çiziliyor
    doc.finishPage(page)

    val outFile = File(view.context.cacheDir, "$fileName.pdf")
    FileOutputStream(outFile).use { fos -> doc.writeTo(fos) }
    doc.close()
    return outFile
}

fun shareFile(
    context: Context,
    file: File,
    mime: String,
    subject: String? = null,
    body: String? = null,
    to: Array<String>? = null
) {
    val uri: Uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
    val send = Intent(Intent.ACTION_SEND).apply {
        type = mime
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        subject?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
        body?.let { putExtra(Intent.EXTRA_TEXT, it) }
        to?.let { putExtra(Intent.EXTRA_EMAIL, it) } // e-posta ile paylaşım için
    }
    context.startActivity(Intent.createChooser(send, "Paylaş"))
}
