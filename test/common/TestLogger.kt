package common

import community.flock.common.define.Logger

object TestLogger : Logger {
    override fun log(s: String) {
        println(s)
    }

    override fun error() {
        TODO("Not yet implemented")
    }

    override fun warn() {
        TODO("Not yet implemented")
    }
}
