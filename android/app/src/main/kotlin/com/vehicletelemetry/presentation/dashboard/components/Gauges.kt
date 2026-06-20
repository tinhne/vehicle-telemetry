package com.vehicletelemetry.presentation.dashboard.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vehicletelemetry.presentation.theme.AutomotiveColors
import kotlin.math.cos
import kotlin.math.sin

private const val START_ANGLE = 150f
private const val SWEEP = 240f

@Composable
fun SpeedGauge(speedKmh: Double, modifier: Modifier = Modifier) {
    val anim by animateFloatAsState(
        targetValue = speedKmh.toFloat().coerceIn(0f, 200f),
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "speed"
    )
    GaugeShell(
        label = "SPEED", value = "${anim.toInt()}", unit = "km/h",
        valueColor = when { anim > 130 -> AutomotiveColors.Critical; anim > 80 -> AutomotiveColors.Warning; else -> AutomotiveColors.Accent },
        modifier = modifier
    ) {
        Canvas(Modifier.fillMaxWidth().aspectRatio(1f)) {
            val sw = size.width * 0.038f
            val pad = sw / 2 + size.width * 0.04f
            val arc = Size(size.width - pad * 2, size.height - pad * 2)
            val tl = Offset(pad, pad)
            drawArc(AutomotiveColors.GaugeBg, START_ANGLE, SWEEP, false, tl, arc, style = Stroke(sw, cap = StrokeCap.Round))
            drawArc(AutomotiveColors.Normal.copy(.65f),   START_ANGLE,                   SWEEP*(80f/200f), false, tl, arc, style = Stroke(sw, cap = StrokeCap.Butt))
            drawArc(AutomotiveColors.Warning.copy(.65f),  START_ANGLE+SWEEP*(80f/200f),  SWEEP*(50f/200f), false, tl, arc, style = Stroke(sw, cap = StrokeCap.Butt))
            drawArc(AutomotiveColors.Critical.copy(.65f), START_ANGLE+SWEEP*(130f/200f), SWEEP*(70f/200f), false, tl, arc, style = Stroke(sw, cap = StrokeCap.Butt))
            drawNeedle(anim, 200f, when { anim > 130 -> AutomotiveColors.Critical; anim > 80 -> AutomotiveColors.Warning; else -> AutomotiveColors.Accent })
        }
    }
}

@Composable
fun RpmGauge(rpm: Int, modifier: Modifier = Modifier) {
    val anim by animateFloatAsState(
        targetValue = rpm.toFloat().coerceIn(0f, 7000f),
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "rpm"
    )
    val isRedline = anim >= 6000f
    GaugeShell(
        label = "RPM", value = "${(anim / 1000).toInt()}k", unit = "×1000 rpm",
        valueColor = if (isRedline) AutomotiveColors.Critical else AutomotiveColors.AccentBlue,
        modifier = modifier
    ) {
        Canvas(Modifier.fillMaxWidth().aspectRatio(1f)) {
            val sw = size.width * 0.038f
            val pad = sw / 2 + size.width * 0.04f
            val arc = Size(size.width - pad * 2, size.height - pad * 2)
            val tl = Offset(pad, pad)
            drawArc(AutomotiveColors.GaugeBg, START_ANGLE, SWEEP, false, tl, arc, style = Stroke(sw, cap = StrokeCap.Round))
            drawArc(AutomotiveColors.AccentBlue.copy(.6f), START_ANGLE, SWEEP*(6000f/7000f), false, tl, arc, style = Stroke(sw, cap = StrokeCap.Butt))
            drawArc(AutomotiveColors.Critical.copy(.8f), START_ANGLE+SWEEP*(6000f/7000f), SWEEP*(1000f/7000f), false, tl, arc, style = Stroke(sw, cap = StrokeCap.Butt))
            drawNeedle(anim, 7000f, if (isRedline) AutomotiveColors.Critical else AutomotiveColors.AccentBlue)
        }
    }
}

private fun DrawScope.drawNeedle(value: Float, maxValue: Float, color: Color) {
    val center = Offset(size.width / 2, size.height / 2)
    val outerR = size.width / 2 * 0.85f
    val innerR = outerR * 0.79f
    for (i in 0..8) {
        val a = Math.toRadians((START_ANGLE + (i / 8.0) * SWEEP).toDouble())
        drawLine(Color.White.copy(.4f),
            Offset(center.x + (outerR * cos(a)).toFloat(), center.y + (outerR * sin(a)).toFloat()),
            Offset(center.x + (innerR * cos(a)).toFloat(), center.y + (innerR * sin(a)).toFloat()),
            strokeWidth = 2f)
    }
    val angle = Math.toRadians((START_ANGLE + (value / maxValue) * SWEEP).toDouble())
    drawLine(color,
        start = Offset(center.x - (size.width/2*0.14f * cos(angle)).toFloat(), center.y - (size.width/2*0.14f * sin(angle)).toFloat()),
        end   = Offset(center.x + (size.width/2*0.68f * cos(angle)).toFloat(), center.y + (size.width/2*0.68f * sin(angle)).toFloat()),
        strokeWidth = size.width * 0.016f, cap = StrokeCap.Round)
    drawCircle(AutomotiveColors.GaugeBg, size.width * 0.052f, center)
    drawCircle(color, size.width * 0.026f, center)
}

@Composable
private fun GaugeShell(
    label: String, value: String, unit: String,
    valueColor: Color, modifier: Modifier = Modifier,
    canvas: @Composable ColumnScope.() -> Unit
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = AutomotiveColors.TextSecondary, fontSize = 10.sp, letterSpacing = 2.sp)
        Spacer(Modifier.height(2.dp))
        canvas()
        Text(value, color = valueColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Text(unit, color = AutomotiveColors.TextSecondary, fontSize = 10.sp)
    }
}
