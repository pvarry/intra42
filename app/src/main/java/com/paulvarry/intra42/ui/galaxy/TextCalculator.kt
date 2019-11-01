package com.paulvarry.intra42.ui.galaxy

import android.graphics.Paint
import com.paulvarry.intra42.ui.galaxy.model.ProjectDataIntra
import java.util.*
import kotlin.math.roundToInt

object TextCalculator {

    internal fun split(projectData: ProjectDataIntra, paintText: Paint, scale: Float): List<String> {
        val oldTextSize = paintText.textSize
        paintText.textSize = oldTextSize * 1.05f // make text just a little bit bigger to avoid text glued to the border

        val projectWidth = projectData.kind!!.data.textWidth * scale // size of the preferable draw space
        val textWidth = paintText.measureText(projectData.name) // size of the entire text

        val textToDraw = ArrayList<String>()

        if (projectWidth != -1f && projectWidth < textWidth) {
            val numberCut = (textWidth / projectWidth).roundToInt() + 1
            var tmpText = projectData.name ?: ""
            var posToCut = tmpText.length / numberCut

            var i = 0
            while (true) {
                posToCut = splitAt(tmpText, posToCut)
                if (posToCut == -1) {
                    textToDraw.add(tmpText)
                    break
                }

                tmpText = if (tmpText[posToCut] == ' ') {
                    textToDraw.add(tmpText.substring(0, posToCut))
                    tmpText.substring(posToCut + 1)
                } else {
                    textToDraw.add(tmpText.substring(0, posToCut + 1))
                    tmpText.substring(posToCut + 1)
                }
                tmpText = tmpText.trim { it <= ' ' }
                i++
                posToCut = tmpText.length / (numberCut - i)
            }

        } else
            textToDraw.add(projectData.name ?: "")
        paintText.textSize = oldTextSize
        return textToDraw
    }

    private fun splitAt(stringToSplit: String?, posSplit: Int): Int {

        if (posSplit < 0 || stringToSplit == null || stringToSplit.length <= posSplit)
            return -1

        if (isSplittablePos(stringToSplit, posSplit))
            return posSplit

        val stringLength = stringToSplit.length
        var searchShift = 0

        var pursueBefore = true
        var pursueAfter = true
        while (pursueBefore || pursueAfter) {

            if (pursueBefore && posSplit - searchShift >= 0) {
                if (isSplittablePos(stringToSplit, posSplit - searchShift))
                    return posSplit - searchShift
            } else
                pursueBefore = false
            if (pursueAfter && posSplit + searchShift < stringLength) {
                if (isSplittablePos(stringToSplit, posSplit + searchShift))
                    return posSplit + searchShift
            } else
                pursueAfter = false

            searchShift++
        }

        return -1
    }

    private fun isSplittablePos(str: String, index: Int): Boolean {
        if (index <= 1 || index >= str.length - 2)
        // a single char can't be split apart.
            return false
        val c = str[index]
        return if (c == ' ' || c == '-' || c == '_') index > 2 && index < str.length - 3 || str.length >= 8 else false
    }

}