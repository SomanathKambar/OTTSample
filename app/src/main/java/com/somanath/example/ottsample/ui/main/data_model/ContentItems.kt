package com.somanath.example.ottsample.ui.main.data_model

import com.google.gson.annotations.SerializedName

data class ContentItems (

	@SerializedName("content") val content : MutableList<Content>
)