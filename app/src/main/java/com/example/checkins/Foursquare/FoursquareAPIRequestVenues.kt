package com.example.checkins.Foursquare

import android.media.Image
import kotlin.collections.ArrayList

class FoursquareAPIRequestVenues {
    var response: FoursquareResponseVenue? = null
    var meta: Meta? = null
}

class FoursquareAPInuevoCheckin{
    var meta: Meta? = null
}

class Meta{
    var code:Int = 0
    var errorDetail:String = ""
}

class FoursquareResponseVenue{
    var venues:ArrayList<Venue>? = null

}

class Venue{
    var id:String =""
    var name:String = ""
    var location: Location? = null
    var categories:ArrayList<Category>? =null
    var stats:Stats?=null
    var imagePreview:String? = null
    var iconCategory: String? = null
}

class Location{
    var lat:Double =0.0
    var lng:Double =0.0
    var state:String =""
    var country:String =""


}

class Category{
    var id:String = ""
    var name:String =""
    var icon: Icon? = null

    var pluralName:String = ""
    var shortName:String = ""

}

open class Icon{
    var prefix:String =""
    var suffix:String =""

    var urlIcono:String=""

    fun construirURLImagen(tokenAccess:String, version:String,size:String):String{
        val prefix = prefix
        val suffix = suffix
        val token = "oauth_token=" + tokenAccess

        val url = prefix + size + suffix + "?" + token + "&" + version
        urlIcono = url
        return url
    }


}

class Stats{
    var checkinsCount:Int =0
    var usersCount:Int = 0
    var tipCount:Int = 0
}

class FoursquareAPISelfUser{
    var response: FoursquareResponseSelfUser? = null
    var meta: Meta? = null
}

class FoursquareResponseSelfUser{
    var user: User? = null
}

class User{
    var id:String = ""
    var firstName:String = ""
    var lastName:String = ""
    var photo: Photo? = null
    var friends: Friends? = null
    var tips: Tips? = null
    var photos: Photos? = null
    var checkins: Checkins? = null
}

class Tips{
    var count:Int = 0
}

class Photo: Icon(){
    var id:String = ""
    var width:Int = 0
    var height:Int = 0



}

class Friends{
    var count:Int = 0
}

class Photos{
    var count:Int = 0
    var items:ArrayList<Photo>? = null
}

class Checkins{

    var count:Int = 0
    var items:ArrayList<Checkin>? = null
}

class Checkin{
    var shout:String = ""
    var venue: Venue? = null
}

class FoursquareAPICategorias{
    var meta:Meta? = null
    var response: CategoriasResponse?=null
}

class CategoriasResponse{
    var categories : ArrayList<Category>? = null

}

class LikeResponse{
    var meta:Meta? = null

}

class VenuesDeLike{
    var response: VenuesDeLikesResponse? = null
    var meta: Meta? = null
}

class VenuesDeLikesResponse{
    var venues: VenuesDeLikesObject? = null
}

class VenuesDeLikesObject{
    var items:ArrayList<Venue>? = null
}

class ImagePreviewVenueResponse{
    var meta:Meta? = null
    var response:PhotosResponse?=null
}

class PhotosResponse{
    var photos:Photos? = null
}