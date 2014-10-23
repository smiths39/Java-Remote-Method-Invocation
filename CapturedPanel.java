import javax.swing.*;
import java.awt.*;
import java.io.*;

import javax.swing.border.Border;

public class CapturedPanel extends JPanel implements Obtainer {

	private JTextArea output;

	public CapturedPanel() {
	
		setLayout(new BorderLayout());
		output = createArea();
		add(output);
	}

	private JTextArea createArea() {
	
		JTextArea textArea = new JTextArea(4, 13);
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		
		textArea.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(20,20,20,20)));
		textArea.setBounds(60, 150, 120, 50);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		
		return textArea;
	}

	@Override
	public void appendText(final String text) {

		// Generates predefined text to output
		if (EventQueue.isDispatchThread()) {
		
			output.append(text);
			output.setCaretPosition(output.getText().length());
		} else {

			// Generates output defined during runtime execution
			EventQueue.invokeLater(new Runnable() {
			
				@Override
				public void run() {
					appendText(text);
				}
			});
		}
	}        
}