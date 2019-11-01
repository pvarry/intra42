package com.paulvarry.intra42.ui.galaxy.model

import com.google.gson.annotations.SerializedName

data class CircleHolder(

        @SerializedName("radius")
        val radius: Int,

        @SerializedName("circles")
        val circles: List<GalaxyCircle>
)