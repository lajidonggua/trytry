package com.yummy.shkp

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.system.measureTimeMillis

@SpringBootApplication
class GqlGwApplication

//fun main(args: Array<String>) {
//    SpringApplication.run(GqlGwApplication::class.java, *args)
//}
fun main() {
    val str = "{\"result\":1,\"memId\":\"SHKP1374404\",\"FA\":1,\"FC\":1,\"FM\":1,\"PS\":1,\"outstandingAmt\":\"0.01\",\"outstandingRecords\":[{\"receiptNo\":\"202005180001\",\"lpn\":\"AX12345\",\"driveOutTime\":\"2021-05-18 14:18:30\",\"carParkId\":6,\"mallId\":59,\"mallName\":{\"en\":\"VWalk\",\"tc\":\"VWalk\",\"sc\":\"VWalk\"},\"paymentType\":4,\"validationStatus\":2}]}"
    val appStatusCheckApiResponse = Gson().fromJson(str, AppStatusCheckApiResponse::class.java)
    println(appStatusCheckApiResponse)

}
data class AppStatusCheckApiResponse(
    val result: Int,
    @SerializedName("memId")
    val memberId: String,
    @SerializedName("FA")
    val fa: Int?,
    @SerializedName("FC")
    val fc: Int?,
    @SerializedName("FM")
    val fm: Int?,
    @SerializedName("PS")
    val ps: Int?,
    @SerializedName("outstandingAmt")
    val outstandingAmt: String,
    @SerializedName("outstandingRecords")
    val outstandingRecords: List<OutStandingRecordDto>
)

data class OutStandingRecordDto(
    @SerializedName("receiptNo")
    val receiptNo: String,
    @SerializedName("lpn")
    val carPlate: String,
    @SerializedName("driveOutTime")
    val driveOutTime: String,
    @SerializedName("carParkId")
    val carParkId: Int,
    @SerializedName("mallId")
    val mallId: Int,
    @SerializedName("mallName")
    val mallName: Map<String, String>,
    @SerializedName("paymentType")
    val paymentType: Int,
    @SerializedName("validationStatus")
    val validationStatus: Int
)