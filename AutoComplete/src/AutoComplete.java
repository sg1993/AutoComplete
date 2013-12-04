import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.GroupLayout;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

public class AutoComplete extends JFrame implements DocumentListener {

	private JLabel jLabel1;
	private JScrollPane jScrollPane1;
	private JTextArea textArea;

	private static enum Mode {
		INSERT, COMPLETION
	};

	private Mode mode = Mode.INSERT;

	private static final String COMMIT_ACTION = "commit";

	String currentWord = "";
	int lengthCur = 0;
	static Trie root;

	public static void main(String args[]) throws IOException {
		root = new Trie();
		BufferedReader br = new BufferedReader(
				new FileReader(
						"C:\\Eclipsews\\EclipseWorkspace\\AutoComplete\\src\\word_list.txt"));
		String s;
		while ((s = br.readLine()) != null) {
			Trie.insertString(root, s);
		}

		// System.out.println(root.searchForWord(root, "Zebedia", 0));
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				new AutoComplete();
			}
		});
	}

	public AutoComplete() {

		super("TextAreaDemo");
		setSize(600, 600);
		setVisible(true);
		initComponents();

		textArea.getDocument().addDocumentListener(this);

		InputMap im = textArea.getInputMap();
		ActionMap am = textArea.getActionMap();
		im.put(KeyStroke.getKeyStroke("ENTER"), COMMIT_ACTION);
		am.put(COMMIT_ACTION, new CommitAction());
		//
		// words = new ArrayList<String>(5);
		// words.add("spark");
		// words.add("special");
		// words.add("spectacles");
		// words.add("spectacular");
		// words.add("swing");
	}

	private void initComponents() {
		jLabel1 = new JLabel("Try typing your message with common words");

		textArea = new JTextArea();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		textArea.setColumns(20);
		textArea.setLineWrap(true);
		textArea.setRows(5);
		textArea.setWrapStyleWord(true);
		// textArea.setSize(500, 500);
		// textArea.setEditable(true);
		// textArea.insert("Shibin", 0);

		jScrollPane1 = new JScrollPane(textArea);

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);

		// Create a parallel group for the horizontal axis
		ParallelGroup hGroup = layout
				.createParallelGroup(GroupLayout.Alignment.LEADING);
		// Create a sequential and a parallel groups
		SequentialGroup h1 = layout.createSequentialGroup();
		ParallelGroup h2 = layout
				.createParallelGroup(GroupLayout.Alignment.TRAILING);
		// Add a scroll panel and a label to the parallel group h2
		h2.addComponent(jScrollPane1, GroupLayout.Alignment.LEADING,
				GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE);
		h2.addComponent(jLabel1, GroupLayout.Alignment.LEADING,
				GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE);

		// Add a container gap to the sequential group h1
		h1.addContainerGap();
		// Add the group h2 to the group h1
		h1.addGroup(h2);
		h1.addContainerGap();
		// Add the group h1 to hGroup
		hGroup.addGroup(Alignment.TRAILING, h1);
		// Create the horizontal group
		layout.setHorizontalGroup(hGroup);

		// Create a parallel group for the vertical axis
		ParallelGroup vGroup = layout
				.createParallelGroup(GroupLayout.Alignment.LEADING);
		// Create a sequential group
		SequentialGroup v1 = layout.createSequentialGroup();
		// Add a container gap to the sequential group v1
		v1.addContainerGap();
		// Add a label to the sequential group v1
		v1.addComponent(jLabel1);
		v1.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		// Add scroll panel to the sequential group v1
		v1.addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 100,
				Short.MAX_VALUE);
		v1.addContainerGap();
		// Add the group v1 to vGroup
		vGroup.addGroup(v1);
		// Create the vertical group
		layout.setVerticalGroup(vGroup);
		//pack();

	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		if (e.getLength() != 1) {
			return;
		}
		int to = e.getOffset();
		String s = null;
		try {
			s = textArea.getText(0, to + 1);
		} catch (BadLocationException ev) {
			ev.printStackTrace();
		}
		int from = to;
		for (; from >= 0; from--) {
			if (!Character.isLetterOrDigit(s.charAt(from))) {
				break;
			}
		}
		// System.out.println(s);
		// from += 1;
		if (s.charAt(to) == ' ') {// insert the previous word entered
			int t, f;
			for (t = to; t >= 0; t--) {
				if (Character.isLetterOrDigit(s.charAt(t))) {
					break;
				}
			}
			f = t;
			for (; f >= 0; f--) {
				if (!Character.isLetterOrDigit(s.charAt(f))) {
					break;
				}
			}

			if (t - f > 3) {// the previous word has more than one alphabet,
							// insert it
				String i = "";
				for(int j = f+1;j<=t;j++){
					i += Character.toString(s.charAt(j));
				}
				System.out.println(":" + i);
				Trie.insertString(root, i);
			}

		}
		if (to != from) {// string not empty
			currentWord = s.substring(from + 1);
			String st = Trie.searchForWord(root, currentWord, 0);
			if (st.length() >= 1) {// found somethin'
				// System.out.println(currentWord + " -> " + st);
				// st = st.substring(currentWord.length());

				SwingUtilities.invokeLater(new CompletionTask(st, currentWord,
						to + 1));
			} else {
				// Nothing found
				mode = Mode.INSERT;
			}
		}
	}

	// System.out.println(s);

	@Override
	public void removeUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub

	}

	public void update(DocumentEvent e, String str) {
		textArea.insert(str, e.getOffset() + 1);
	}

	class CompletionTask implements Runnable {

		String completion;
		int position;

		CompletionTask(String completion, String currentWord, int position) {
			this.completion = completion.substring(currentWord.length());
			this.position = position;
		}

		public void run() {
			textArea.insert(completion, position);
			textArea.setCaretPosition(position + completion.length());
			textArea.moveCaretPosition(position);
			mode = Mode.COMPLETION;
		}

	}

	private class CommitAction extends AbstractAction {
		public void actionPerformed(ActionEvent ev) {
			if (mode == Mode.COMPLETION) {
				int pos = textArea.getSelectionEnd();
				textArea.insert(" ", pos);
				textArea.setCaretPosition(pos + 1);
				mode = Mode.INSERT;
			} else {
				textArea.replaceSelection("\n");
			}
		}
	}
}
