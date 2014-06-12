package graphic;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPasswordField;

@SuppressWarnings("serial")
public class LoginDialog extends JDialog implements ActionListener {

	private final JPanel contentPanel = new JPanel();
	private JTextField NickText;
	private JButton okButton,cancelButton;
	private boolean oki=false,registrazione;
	private String pass,nick;
	private JLabel variableLabel;
	private JPasswordField passwordField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			LoginDialog dialog = new LoginDialog(true);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public LoginDialog(boolean registrazione){
		this.registrazione=registrazione;
		setBounds(100, 100, 250, 130);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{131, 68, 114, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{19, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 1.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		
		variableLabel = new JLabel("Accedi");
		GridBagConstraints gbc_variableLabel = new GridBagConstraints();
		gbc_variableLabel.gridwidth = 4;
		gbc_variableLabel.insets = new Insets(0, 0, 5, 0);
		gbc_variableLabel.gridx = 0;
		gbc_variableLabel.gridy = 3;
		contentPanel.add(variableLabel, gbc_variableLabel);
		{
		JLabel NickLabel = new JLabel("Nickname");
		GridBagConstraints gbc_NickLabel = new GridBagConstraints();
		gbc_NickLabel.insets = new Insets(0, 0, 5, 5);
		gbc_NickLabel.gridx = 0;
		gbc_NickLabel.gridy = 4;
		contentPanel.add(NickLabel, gbc_NickLabel);
		}
		
		NickText = new JTextField();
		GridBagConstraints gbc_NickText = new GridBagConstraints();
		gbc_NickText.fill = GridBagConstraints.HORIZONTAL;
		gbc_NickText.gridwidth = 3;
		gbc_NickText.insets = new Insets(0, 0, 5, 0);
		gbc_NickText.gridx = 1;
		gbc_NickText.gridy = 4;
		contentPanel.add(NickText, gbc_NickText);
		NickText.setColumns(10);
		{
		JLabel PassLabel = new JLabel("Password");
		GridBagConstraints gbc_PassLabel = new GridBagConstraints();
		gbc_PassLabel.insets = new Insets(0, 0, 5, 5);
		gbc_PassLabel.gridx = 0;
		gbc_PassLabel.gridy = 5;
		contentPanel.add(PassLabel, gbc_PassLabel);
		}
		
		passwordField = new JPasswordField();
		GridBagConstraints gbc_passwordField = new GridBagConstraints();
		gbc_passwordField.gridwidth = 3;
		gbc_passwordField.insets = new Insets(0, 0, 5, 5);
		gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField.gridx = 1;
		gbc_passwordField.gridy = 5;
		contentPanel.add(passwordField, gbc_passwordField);
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		okButton = new JButton("OK");
		okButton.setActionCommand("OK");
		okButton.addActionListener(this);
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(this);
		buttonPane.add(cancelButton);
		if(registrazione)
			variableLabel.setText("Registrati");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource()==okButton){
			
			System.out.println("premuto ok");
			oki=true;
			char[] c=passwordField.getPassword();
			pass=String.copyValueOf(c);
			nick=NickText.getText().trim();
			setVisible(false);
		}
		else if(e.getSource()==cancelButton){
			
			System.out.println("premuto annulla");
			oki=false;
			pass=null;
			nick=null;
			setVisible(false);
		}
	}
	
	public boolean isOk(){
		
		return oki;
	}
	public boolean isForRegistration(){
		
		return registrazione;
	}
	
	public String[] result(){
		
		String[] result=new String[2];
		result[0]=nick;
		result[1]=pass;
		return result;
	}
}