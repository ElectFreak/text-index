import me.electfreak.getIndexOrNull
import me.electfreak.getTextIndexFromText
import me.electfreak.wordsTrie
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.io.File
import java.util.stream.IntStream
import java.util.stream.Stream

val textIndex = getTextIndexFromText(File("data/Childhood.txt").readLines())

class TextIndexTests {
    @TestFactory
    fun `words count`(): Stream<DynamicTest> {
        val words = listOf(
            "дом", "детство", "отец", "шампанское", "лев",
            "мамаша", "висок"
        )

        val expected: List<Int> = listOf(21, 6, 17, 1, 4, 7, 3)
        return IntStream.range(0, 7).mapToObj { n ->
            DynamicTest.dynamicTest("Test word count for $n word") {
                assertEquals(expected[n], textIndex.wordsInfo[wordsTrie.getIndexOrNull(words[n])!!]!!.size)
            }
        }
    }

}