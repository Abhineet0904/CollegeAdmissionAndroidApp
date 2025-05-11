package com.example.project_sem5.Models_student

class User_student {
    var mail : String ?= null
    var pswrd : String ?= null
    var image : String ?= null
    var first_name : String ?= null
    var last_name : String ?= null
    var dob : String ?= null
    var gender : String ?= null
    var education : String ?= null
    var ssc : Float ?= null
    var hsc : Float ?= null
    var nationality : String ?= null
    var contact_number : String ?= null
    var address : String ?= null
    var quota : String ?= null

    constructor()

    constructor(mail: String?, pswrd: String?)
    {
        this.mail = mail
        this.pswrd = pswrd
    }

    constructor(mail: String?, pswrd: String?, image: String?, first_name: String?,
                last_name: String?, dob: String?, gender: String?, education: String?,
                ssc: Float?, hsc: Float?, nationality: String?, contact_number: String?,
                address: String?, quota: String?)
    {
        this.mail = mail
        this.pswrd = pswrd
        this.image = image
        this.first_name = first_name
        this.last_name = last_name
        this.dob = dob
        this.gender = gender
        this.education = education
        this.ssc = ssc
        this.hsc = hsc
        this.nationality = nationality
        this.contact_number = contact_number
        this.address = address
        this.quota = quota
    }
}