package com.yummy.shkp.entity

import jakarta.persistence.*

@Entity
@Table(name = "sender")
data class Sender(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,
    val sender: String
)
