//This is the implementation of trie-data structure in java.
//A Trie(prefix-tree can be used effectively in auto-completion..

class datanode {
	String str;
}

public class Trie {
	int tag[];// specifies whether datanode or trienode
	Trie t[];
	datanode d[];

	Trie() {
		d = new datanode[256];
		tag = new int[256];
		t = new Trie[256];
		for (int i = 0; i < 256; i++) {
			tag[i] = -1;
			// t[i] = null;
		}
	}

	static int ret_pos(char ch) {
		return (int) (ch);
	}

	static void pushDown(Trie node, String s, int pos) {
		if (pos > s.length())
			return;
		else if (pos == s.length()) {
			int ch_pos = ret_pos(' ');
			node.tag[ch_pos] = 1;
			node.d[ch_pos] = new datanode();
			node.d[ch_pos].str = s;
			return;
		}

		int ch_pos = ret_pos(s.charAt(pos));
		if (node.tag[ch_pos] == -1) {// if its NULL
			node.tag[ch_pos] = 1;// mark as datnode
			node.d[ch_pos] = new datanode();// create a new datanode
			node.d[ch_pos].str = s;
			return;
		} else if (node.tag[ch_pos] == 0) {// if its a trienode
			pushDown(node.t[ch_pos], s, pos + 1);// recursively push down the
													// string
		} else {// if its already a datanode
			String str = node.d[ch_pos].str;
			if (!str.equals(s)) {//if the word to be entered is not already present, only then insert it.
				// System.out.println("Found " + str + " at " + ch_pos);
				node.tag[ch_pos] = 0;// mark as trienode
				node.t[ch_pos] = new Trie();

				node.d[ch_pos] = null;
				// System.out.println("inserting " + s + " and " + str);
				pushDown(node.t[ch_pos], str, pos + 1);
				pushDown(node.t[ch_pos], s, pos + 1);
			}
		}
	}

	static void insertString(Trie root, String s) {
		if (root == null) {
			root = new Trie();
			pushDown(root, s, 0);
		} else {
			pushDown(root, s, 0);
		}
	}

	static void displayLexicographically(Trie node) {
		for (int i = 0; i < 256; i++) {
			if (node.tag[i] == 1) {
				System.out.println(node.d[i].str);
			} else if (node.tag[i] == 0) {
				displayLexicographically(node.t[i]);
			}
		}
	}

	static String searchForWord(Trie node, String s, int pos) {
		int i = 0;
		int ch_pos = ret_pos(s.charAt(0));
		String result = "";
		int len = s.length();
		while (i < len) {// && node.tag[ch_pos]!=-1 && node.tag[ch_pos]!=1){
			ch_pos = ret_pos(s.charAt(i));
			if (node.tag[ch_pos] == -1) {
				return "";
			} else if (node.tag[ch_pos] == 1) {// System.out.println("arrgggh");
				if (i < len - 1) {// premature match
					String st = node.d[ch_pos].str;
					if (st.length() <= len) {
						return "";
					} else {

						if (st.substring(0, len).equals(s)) {
							return st;
						} else {
							return "";
						}
					}
				} else {
					return node.d[ch_pos].str;
				}
			} else {
				node = node.t[ch_pos];
				i++;
			}
		}
		// we finished along the given string, now give a possible suggestion
		result = returnSuggestion(node);
		return result;
	}

	private static String returnSuggestion(Trie node) {
		// System.out.println("arrgggh12");
		// TODO Auto-generated method stub
		for (int i = 255; i >= 0; i--) {
			if (node.tag[i] == 1) {
				return node.d[i].str;
			} else if (node.tag[i] == 0) {
				return returnSuggestion(node.t[i]);
			}
		}
		return "";
	}

}
