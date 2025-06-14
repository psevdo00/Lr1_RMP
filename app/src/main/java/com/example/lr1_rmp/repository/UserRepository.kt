package com.example.lr1_rmp.repository

import com.example.lr1_rmp.database.interfaceDao.UserNameDAO

class UserRepository(private val userDAO: UserNameDAO) {

    suspend fun getUserName(login: String): Int{

        return userDAO.getIdByLogin(login);

    }

}