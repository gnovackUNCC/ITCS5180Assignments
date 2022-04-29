package com.gnovack.group30_inclass04

import com.gnovack.group30_inclass04.DataServices
import com.gnovack.group30_inclass04.DataServices.AccountRequestTask
import java.io.Serializable
import java.util.*

object DataServices {
    private val accounts: HashMap<String?, Account?> = object : HashMap<String?, Account?>() {
        init {
            put("a@a.com", Account("Alice Smith", "a@a.com", "test123"))
            put("b@b.com", Account("Bob Smith", "b@b.com", "test123"))
            put("c@c.com", Account("Charles Smith", "c@c.com", "test123"))
        }
    }

    fun login(email: String?, password: String): AccountRequestTask {
        if (email == null || email.isEmpty()) {
            return AccountRequestTask("Enter valid email!")
        }
        if (!accounts.containsKey(email.trim { it <= ' ' }.lowercase(Locale.getDefault()))) {
            return AccountRequestTask("No user with this email!")
        }
        val account = accounts[email.trim { it <= ' ' }.lowercase(Locale.getDefault())]
        return if (account == null || account.password != password) {
            AccountRequestTask("The provided email/password do not match!")
        } else AccountRequestTask(account)
    }

    fun register(name: String?, email: String?, password: String?): AccountRequestTask {
        if (name == null || name.isEmpty()) {
            return AccountRequestTask("Enter valid name!")
        }
        if (email == null || email.isEmpty()) {
            return AccountRequestTask("Enter valid email!")
        }
        if (password == null || password.isEmpty()) {
            return AccountRequestTask("Enter valid password!")
        }
        if (accounts.containsKey(email.trim { it <= ' ' }.lowercase(Locale.getDefault()))) {
            return AccountRequestTask("Email provided already taken by another account. Choose another email!")
        }
        val account = Account(name,
            email.trim { it <= ' ' }.lowercase(Locale.getDefault()), password)
        accounts[email.trim { it <= ' ' }.lowercase(Locale.getDefault())] = account
        return AccountRequestTask(account)
    }

    fun update(oldAccount: Account?, name: String?, password: String?): AccountRequestTask {
        if (oldAccount == null) {
            return AccountRequestTask("Enter valid account !!")
        }
        if (name == null || name.isEmpty()) {
            return AccountRequestTask("Enter valid name!")
        }
        if (password == null || password.isEmpty()) {
            return AccountRequestTask("Enter valid password!")
        }
        if (oldAccount.getEmail() == null || oldAccount.getEmail()!!.isEmpty()) {
            return AccountRequestTask("Enter valid email!")
        }
        if (!accounts.containsKey(oldAccount.getEmail()!!.trim { it <= ' ' }
                .lowercase(Locale.getDefault()))) {
            return AccountRequestTask("Provided account is invalid!")
        }
        val email = oldAccount.getEmail()!!.trim { it <= ' ' }.lowercase(Locale.getDefault())
        val account = Account(name, email, password)
        accounts[email] = account
        return AccountRequestTask(account)
    }

    class Account(val name: String, private val email: String, val password: String) :
        Serializable {
        fun getEmail(): String {
            return email
        }
    }

    class AccountRequestTask {
        var isSuccessful: Boolean
            private set
        var errorMessage: String?
            private set
        var account: Account?
            private set

        constructor(error: String?) {
            isSuccessful = false
            errorMessage = error
            account = null
        }

        constructor(account: Account?) {
            isSuccessful = true
            errorMessage = null
            this.account = account
        }
    }
}