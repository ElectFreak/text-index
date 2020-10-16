class Trie<Key> {

    private val root = TrieNode<Key>(key = null, parent = null)

    fun insert(list: List<Key>, index: Long) {
        var current = root

        list.forEach { element ->
            if (current.children[element] == null) {
                current.children[element] = TrieNode(key = element, parent = current)
            }

            current = current.children[element]!!
        }

        current.index = index
        current.isTerminating = true
    }

    fun getIndexOrNull(list: List<Key>): Long? {
        var current = root

        list.forEach { element ->
            val child = current.children[element] ?: return null
            current = child
        }

        return if (current.isTerminating) current.index else null
    }

}

fun Trie<Char>.insert(string: String, index: Long) {
    insert(string.toList(), index)
}

fun Trie<Char>.getIndexOrNull(string: String): Long? {
    return getIndexOrNull(string.toList())
}
