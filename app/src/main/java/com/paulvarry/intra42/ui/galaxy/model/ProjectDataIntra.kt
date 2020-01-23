package com.paulvarry.intra42.ui.galaxy.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProjectDataIntra {

    @SerializedName(API_STATE)
    var state: State? = null
    @SerializedName(API_FINAL_MARK)
    var finalMark: Int? = null
    @SerializedName(API_ID)
    val id: Int = 0
    @SerializedName(API_KIND)
    var kind: Kind? = null
    @SerializedName(API_NAME)
    val name: String? = null
    @SerializedName(API_X)
    var x: Int = 0
    @SerializedName(API_Y)
    var y: Int = 0
    @SerializedName(API_BY)
    var by: List<By>? = null
    @SerializedName(API_PROJECT_ID)
    val projectId: Int = 0
    @SerializedName(API_DIFFICULTY)
    @Expose
    val difficulty: String? = null
    @SerializedName(API_DURATION)
    @Expose
    val duration: String? = null
    @SerializedName(API_RULES)
    @Expose
    val rules: String? = null
    @SerializedName(API_DESCRIPTION)
    @Expose
    val description: String? = null
    @SerializedName(API_SLUG)
    @Expose
    val slug: String? = null

    enum class Kind(val data: DrawType) {
        @SerializedName("project")
        PROJECT(DrawType.Circle(50)),
        @SerializedName("big_project")
        BIG_PROJECT(DrawType.Circle(70)),
        @SerializedName("piscine")
        PISCINE(DrawType.Rectangle(60, 180)),
        @SerializedName("rush")
        RUSH(DrawType.RoundRect(60, 180)),
        @SerializedName("part_time")
        PART_TIME(DrawType.Circle(150)),
        @SerializedName("first_internship")
        FIRST_INTERNSHIP(DrawType.Circle(100)),
        @SerializedName("second_internship")
        SECOND_INTERNSHIP(DrawType.Circle(100)),
        @SerializedName("exam")
        EXAM(DrawType.Circle(80)),

        @SerializedName("inner_solo")
        INNER_SOLO(DrawType.Circle(50)),
        @SerializedName("inner_linked")
        INNER_LINKED(DrawType.Circle(50)),
        @SerializedName("inner_exam")
        INNER_EXAM(DrawType.RoundRect(60, 120)),
        @SerializedName("inner_planet")
        INNER_PLANET(DrawType.Circle(30)),
        @SerializedName("inner_satellite")
        INNER_SATELLITE(DrawType.Circle(20)),
        @SerializedName("inner_big")
        INNER_BIG(DrawType.Circle(75));

        sealed class DrawType(val textWidth: Float) {
            class Circle(val radius: Int) : DrawType(radius * 2.1f)
            class Rectangle(val height: Int, val width: Int) : DrawType(width.toFloat())
            class RoundRect(val height: Int, val width: Int) : DrawType(width * 1.01f)
        }
    }

    /**
     * Enum of `FAIL, DONE, AVAILABLE, IN_PROGRESS, UNAVAILABLE`
     */
    enum class State(
            /**
             * Get the layer of this ProjectData, this work like z index in CSS.
             *
             * @return layer index.
             */
            val layerIndex: Int) {
        @SerializedName("fail")
        FAIL(4),
        @SerializedName("done")
        DONE(3),
        @SerializedName("available")
        AVAILABLE(1),
        @SerializedName("in_progress")
        IN_PROGRESS(2),
        @SerializedName("unavailable")
        UNAVAILABLE(0);

        /**
         * Test if the state send in parameter is more important then the source state
         */
        fun overriddenBy(state: State?) = (state != null && state.layerIndex > this.layerIndex)

    }

    inner class By {

        @SerializedName("parent_id")
        val parentId: Int = 0
        @SerializedName("points")
        var points: List<List<Float>>? = null

    }

    companion object {

        private const val API_STATE = "state"
        private const val API_FINAL_MARK = "final_mark"
        private const val API_ID = "id"
        private const val API_KIND = "kind"
        private const val API_NAME = "name"
        private const val API_X = "x"
        private const val API_Y = "y"
        private const val API_BY = "by"
        private const val API_PROJECT_ID = "project_id"
        private const val API_DIFFICULTY = "difficulty"
        private const val API_DURATION = "duration"
        private const val API_RULES = "rules"
        private const val API_DESCRIPTION = "description"
        private const val API_SLUG = "slug"
    }

}
