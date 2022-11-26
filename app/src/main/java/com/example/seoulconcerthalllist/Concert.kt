package com.example.seoulconcerthalllist

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
class Concert(
    var number: String?,
    var name: String?,
    var phone: String?,
    var state: String?,
    var address:String?,
    var star: Int?) : Parcelable {

    companion object:Parceler<Concert>{
        override fun create(parcel: Parcel): Concert {
            return Concert(parcel)
        }

        override fun Concert.write(parcel: Parcel, flags: Int) {
            parcel.writeString(number)
            parcel.writeString(name)
            parcel.writeString(phone)
            parcel.writeString(state)
            parcel.writeString(address)
            parcel.writeInt(star!!)
        }
    }

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt()
    )
}