package com.example.project_sem5.Models_college

class User_college {
    var mail : String ?= null
    var pswrd : String ?= null
    var college_name : String ?= null
    var ugcno : String ?= null
    var contact_number : String ?= null
    var address : String ?= null

    constructor()

    constructor(mail: String?, pswrd: String?)
    {
        this.mail = mail
        this.pswrd = pswrd
    }

    constructor(mail : String?, pswrd : String?, college_name: String?,
                ugcno : String?, contact_number: String?, address: String?)
    {
        this.mail = mail
        this.pswrd = pswrd
        this.college_name = college_name
        this.ugcno = ugcno
        this.contact_number = contact_number
        this.address = address
    }
}