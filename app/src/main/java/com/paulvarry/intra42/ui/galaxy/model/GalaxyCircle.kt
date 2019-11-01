package com.paulvarry.intra42.ui.galaxy.model

import com.google.gson.annotations.SerializedName

data class GalaxyCircle(

        @SerializedName("projects")
        val projects: List<Project>,

        @SerializedName("angle")
        val angle: Int
) {

    data class Project(

            @SerializedName("kind")
            val kind: ProjectDataIntra.Kind,

            @SerializedName("id")
            val id: Int,

            @SerializedName("ids")
            val ids: List<Int>,

            @SerializedName("satellites")
            val satellites: List<Int>
    )
}