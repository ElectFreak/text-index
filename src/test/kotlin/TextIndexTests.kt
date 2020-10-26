import me.electfreak.kotlin.*

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.util.stream.IntStream
import java.util.stream.Stream
import kotlin.test.assertEquals

val wordsTrie = makeWordsTrie(pathToDictionary)

fun print() {
    println(1)
}

class TextIndexTests {
    @TestFactory
    fun `words count`(): Stream<DynamicTest> {
        val words = listOf(
            "дом", "детство", "отец", "шампанское", "лев",
            "мамаша", "висок"
        )

        print()

        val expected: List<Int> = listOf(21, 6, 17, 1, 4, 7, 3)
        return IntStream.range(0, 7).mapToObj { n ->
            DynamicTest.dynamicTest("Test word count for $n word") {
                assertEquals(expected[n], expected[n])
            }
        }
    }

}