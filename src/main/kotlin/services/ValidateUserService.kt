package com.poicraft.bot.v4.plugin.services

import com.poicraft.bot.v4.plugin.constants.UserStatus
import com.poicraft.bot.v4.plugin.database.DatabaseManager
import com.poicraft.bot.v4.plugin.database.Users
import com.poicraft.bot.v4.plugin.remote.bdxws.BDXWSControl
import com.poicraft.bot.v4.plugin.remote.bdxws.data.OnChatRes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.ktorm.dsl.*

object ValidateUserService : Service() {
    @ExperimentalCoroutinesApi
    override fun init() {
        BDXWSControl.addEventListener(OnChatRes::class) {
            if (params.text.startsWith("#bind ")) {
                val qqId = try {
                    params.text.split(" ")[1].toLong()
                } catch (e: NumberFormatException) {
                    0L
                }
                val xboxID = params.sender
                if (DatabaseManager.instance().from(Users)
                        .select(Users.qqNumber)
                        .where {
                            (Users.qqNumber eq qqId) and (Users.xboxId eq xboxID)
                        }.totalRecords == 1
                ) {
                    DatabaseManager.instance().update(Users) {
                        set(it.status, UserStatus.VERIFIED.ordinal)
                        where {
                            (it.qqNumber eq qqId) and (it.xboxId eq xboxID)
                        }
                    }
                    BDXWSControl.runCmdNoRes("say @$xboxID 您已绑定成功")
                } else {
                    BDXWSControl.runCmdNoRes("say @$xboxID 请先在QQ群中绑定哦")
                }
            }

        }
    }
}