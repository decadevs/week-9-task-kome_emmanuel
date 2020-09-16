package com.decagon.maptrack.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties

data class MyLocationLog(
    var Latitude: Double? = 0.0,
    var Longitude: Double? = 0.0
)