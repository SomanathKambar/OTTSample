package com.somanath.example.ottsample.ui.main.data_model

import com.google.gson.annotations.SerializedName

data class Content (

	@SerializedName("name") val name : String,
	@SerializedName("poster-image") val posterImage : String
)