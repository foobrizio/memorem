package graphic;


import java.io.*;
import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout.Alignment;

@SuppressWarnings("serial")
public class IconChooser extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private String path;
	private GroupLayout gl=new GroupLayout(contentPanel);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			IconChooser dialog = new IconChooser("~/workspace/MemoRem/src/graphic/icons/");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public IconChooser(String cartella){
		path=cartella;
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		gl.setHorizontalGroup(
			gl.createParallelGroup(Alignment.LEADING)
				.addGap(0, 440, Short.MAX_VALUE)
		);
		gl.setVerticalGroup(
			gl.createParallelGroup(Alignment.LEADING)
				.addGap(0, 255, Short.MAX_VALUE)
		);
		contentPanel.setLayout(gl);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	} //costruttore
	
	public void add(File f){
		
		JPanel p0=new JPanel();
		p0.setBackground(Color.BLACK);
		p0.setVisible(true);
		
	}
}
