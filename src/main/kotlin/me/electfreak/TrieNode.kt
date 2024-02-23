package me.electfreak

class TrieNode<Key>(var key: Key?, var parent: TrieNode<Key>?) {

    var index: Long? = null

    val children: HashMap<Key, TrieNode<Key>> = HashMap()

    var isTerminating = false
}