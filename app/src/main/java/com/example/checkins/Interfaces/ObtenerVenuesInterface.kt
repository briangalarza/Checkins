package com.example.checkins.Interfaces

import com.example.checkins.Foursquare.Category
import com.example.checkins.Foursquare.Venue

interface ObtenerVenuesInterface {
    fun venuesGenerados(venues:ArrayList<Venue>)
}