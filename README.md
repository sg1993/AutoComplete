AutoComplete
============

A simple AutoComplete UI, which uses a Trie-based implementation for efficiency.

The words in a 'word_list.txt' file are first inserted into the trie.
In case a word not recognised is typed, it is automatically inserted into the trie( principle of locality: words used in a message might need to be used later in the message).
