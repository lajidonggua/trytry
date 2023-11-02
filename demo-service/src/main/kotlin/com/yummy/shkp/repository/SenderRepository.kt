package com.yummy.shkp.repository

import com.yummy.shkp.entity.Sender
import org.springframework.data.repository.CrudRepository

interface SenderRepository: CrudRepository<Sender, Long> {
}