package com.yummy.shkp.base.utils

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger


fun <T : Any> T.logger(): Logger = LogManager.getLogger(this::class.java)
