package com.innov8.memeit.Loaders

import android.os.Parcel
import android.os.Parcelable
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import com.memeit.backend.dataclasses.User

class FollowerLoader(private val uid: String?) : UserListLoader {
    override var skip: Int = 0

    constructor(parcel: Parcel) : this(parcel.readString())

    override fun load(limit: Int, onSuccess: (List<User>) -> Unit, onError: (String) -> Unit) {
        if (uid == null)
            MemeItUsers.getMyFollowersList(skip, limit).call(onSuccess, onError)
        else
            MemeItUsers.getFollowersListForUser(uid, skip, limit).call(onSuccess, onError)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FollowerLoader> {
        override fun createFromParcel(parcel: Parcel): FollowerLoader {
            return FollowerLoader(parcel)
        }

        override fun newArray(size: Int): Array<FollowerLoader?> {
            return arrayOfNulls(size)
        }
    }
}