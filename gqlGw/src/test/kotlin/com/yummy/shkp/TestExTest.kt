package com.yummy.shkp

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.util.Deque
import kotlin.time.Duration.Companion.seconds

@SpringBootTest
class TestExTest {

    @Test
    fun t() {
        val input =
            "{\"noOtpPasswordLoginInput {\\n phoneNumber: \\\"+85263755566\\\"\\n password: \\\"P@ssw0rd\\\"\\n platform: MOBILE\\n}\\n\"}"
        println(input.toString())
        val regex = Regex("(?<=(plate: \\\")).*?(?=(\\\"))")
        val replacement = "***"
        val result: String = input.replace(regex, replacement)
        println(result)
        assert(true)
    }

    @Test
    fun countPointsTest() {
        assert(countPoints("B0B6G0R6R0R6G9") == 1)
        assert(countPoints("B0R0G0R9R0B0G0") == 1)
        assert(countPoints("G4") == 0)
    }


    fun countPoints(rings: String): Int {
        val a = HashMap<Int, Int>()
        for (index in rings.indices step 2) {
            val num = rings[index + 1] - '0' // Convert char to int
            val color = rings[index]
            val value = when (color) {
                'R' -> 0b0001
                'G' -> 0b0010
                else -> 0b0100
            }
            a[num] = (a[num] ?: 0) or value
        }
        return a.count { it.value == 0b0111 }
    }


    fun maximumInvitations(favorite: IntArray): Int {
        val n = favorite.size
        // in 统计每个节点的入度情况， max 统计每节点的最长链
        val inCount = IntArray(n)
        val max = IntArray(n)
        val d = ArrayDeque<Int>()
        favorite.forEach { fav ->
            inCount[fav]++
        }
        favorite.forEachIndexed { index, _ ->
            if (inCount[index] == 0) d.addFirst(index)
        }
        // 拓扑排序
        while (d.isNotEmpty()) {
            val cur = d.removeFirst()
            val ne = favorite[cur]
            max[ne] = max[ne].coerceAtLeast(max[cur] + 1)
            if (--inCount[ne] == 0) {
                d.addLast(ne)
            }
        }
        // max 即为每个节点的最长链
        // 圆桌最多可放置一个大于2 的环 （ans1 统计 最大值）
        // 或 最多可放置 多余等于 2 的环（ ans2累加改长度）
        var ans1 = 0
        var ans2 = 0
        favorite.forEachIndexed { index, fav ->
            if (inCount[index] == 0) return@forEachIndexed
            // 剩下的 inCount[index] > 0 的就是环里面的
            var cur = 1
            var tmpFav = fav
            while (tmpFav != index) {
                inCount[tmpFav] = 0
                tmpFav = favorite[tmpFav]
                cur++;
            }
            if (cur == 2) {
                ans2 += (2 + max[index] + max[favorite[index]])
            } else {
                ans1 = ans1.coerceAtLeast(cur)
            }
        }
        return ans1.coerceAtLeast(ans2)
    }

    @Test
    fun maximumInvitationsTest() {
        assert(maximumInvitations(intArrayOf(2, 2, 1, 2)) == 3)
        assert(maximumInvitations(intArrayOf(1, 2, 0)) == 3)
        assert(maximumInvitations(intArrayOf(3, 0, 1, 4, 1)) == 4)
    }

    @Test
    fun test() {
        val nums = intArrayOf(-2,1,-3,4,-1,2,1,-5,4);
        println(maxSubArray(nums))

    }
    fun maxSubArray(nums: IntArray): Int {
        var pre = 0;
        var max = nums.first()
        for (num in nums){
            pre = num.coerceAtLeast(pre + num)
            max = max.coerceAtLeast(pre)
            println("pre: $pre , max: $max  ")
        }
        return max
    }


}


val parentJob = Job()
suspend fun test1(s: String): Job {
    println("test")
    delay(1000)
    parentJob.cancelChildren()
    return CoroutineScope( SupervisorJob(parentJob)).launch {
        var i = 0
        while (i < 10) {
            i++
            println("test: $s")
            delay(500)
        }
    }
}

//fun main() = runBlocking {
//
//        val job1 = test1("a")
//        val job2 = test1("b")
//    }
//
//fun maximumMinutes(grid: Array<IntArray>): Int {
//    val fireList = List<Pair<Int, Int>>
//    grid.forEachIndexed { x, xArray ->
//        xArray.forEachIndexed { y, value ->
//            while (value)
//        }
//
//    }
//}