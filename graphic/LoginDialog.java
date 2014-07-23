package graphic;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPasswordField;
import javax.swing.JRadioButton;

import org.apache.commons.codec.digest.DigestUtils;

import util.User;

import java.awt.Font;
import java.util.HashSet;
import java.util.Iterator;

@SuppressWarnings("serial")
public class LoginDialog extends JDialog implements ActionListener {

	private final JPanel contentPanel = new JPanel();
	private JTextField nickField,nameField,surnameField;
	private JButton okButton,cancelButton;
	private boolean oki=false,registrazione,autologin;
	private String pass,nick,name,surname;
	private char genre;
	private JPasswordField passwordField;
	private JLabel variableLabel,nameLabel,surnameLabel,genreLabel;
	private JRadioButton rdbtnMan;
	private JRadioButton rdbtnWoman;
	private GenHandler generale;
	private JLabel lblIlNicknameDeve;
	private JLabel lblAlmenoCaratteri;
	private HashSet<User> userList;

	
	
	class GenHandler implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			
			if(e.getSource()==rdbtnMan){
				rdbtnMan.setSelected(true);
				rdbtnWoman.setSelected(false);
			}
			else if(e.getSource()==rdbtnWoman){
				rdbtnWoman.setSelected(true);
				rdbtnMan.setSelected(false);
			}
		}
		public char getSelected(){
			
			if(rdbtnMan.isSelected())
				return 'm';
			else if(rdbtnWoman.isSelected())
				return 'f';
			else throw new RuntimeException("WTF");
		}
		
	}
	/**
	 * Create the dialog.
	 */
	public LoginDialog(JFrame owner,boolean registrazione,boolean autologin){
		
		super(owner,true);
		this.registrazione=registrazione;
		this.autologin=autologin;
		this.userList=new HashSet<User>();
		this.generale=new GenHandler();
		if(registrazione){
			setContentPane(new ColoredPanel("./src/graphic/wallpapers/man.png"));
		}
		else{
			setContentPane(new ColoredPanel("./src/graphic/wallpapers/man.jpg"));
		}
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{131, 68, 0, 0, 114, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{19, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		
		variableLabel = new JLabel();
		GridBagConstraints gbc_variableLabel = new GridBagConstraints();
		gbc_variableLabel.gridwidth = 6;
		gbc_variableLabel.insets = new Insets(0, 0, 5, 0);
		gbc_variableLabel.gridx = 0;
		gbc_variableLabel.gridy = 3;
		contentPanel.add(variableLabel, gbc_variableLabel);
		{
		JLabel NickLabel = new JLabel("Nickname");
		NickLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
		GridBagConstraints gbc_NickLabel = new GridBagConstraints();
		gbc_NickLabel.anchor = GridBagConstraints.EAST;
		gbc_NickLabel.insets = new Insets(0, 0, 5, 5);
		gbc_NickLabel.gridx = 0;
		gbc_NickLabel.gridy = 4;
		contentPanel.add(NickLabel, gbc_NickLabel);
		}
		
		nickField = new JTextField();
		nickField.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
		GridBagConstraints gbc_nickField = new GridBagConstraints();
		gbc_nickField.gridwidth = 4;
		gbc_nickField.fill = GridBagConstraints.HORIZONTAL;
		gbc_nickField.insets = new Insets(0, 0, 5, 5);
		gbc_nickField.gridx = 1;
		gbc_nickField.gridy = 4;
		contentPanel.add(nickField, gbc_nickField);
		nickField.setColumns(10);
		
		lblIlNicknameDeve = new JLabel("Il nickname deve avere almeno 3 caratteri");
		lblIlNicknameDeve.setForeground(Color.RED);
		lblIlNicknameDeve.setVisible(false);
		GridBagConstraints gbc_lblIlNicknameDeve = new GridBagConstraints();
		gbc_lblIlNicknameDeve.gridwidth = 5;
		gbc_lblIlNicknameDeve.insets = new Insets(0, 0, 5, 5);
		gbc_lblIlNicknameDeve.gridx = 0;
		gbc_lblIlNicknameDeve.gridy = 5;
		contentPanel.add(lblIlNicknameDeve, gbc_lblIlNicknameDeve);
		{
		JLabel PassLabel = new JLabel("Password");
		PassLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
		GridBagConstraints gbc_PassLabel = new GridBagConstraints();
		gbc_PassLabel.anchor = GridBagConstraints.EAST;
		gbc_PassLabel.insets = new Insets(0, 0, 5, 5);
		gbc_PassLabel.gridx = 0;
		gbc_PassLabel.gridy = 6;
		contentPanel.add(PassLabel, gbc_PassLabel);
		}
		
		passwordField = new JPasswordField();
		GridBagConstraints gbc_passwordField = new GridBagConstraints();
		gbc_passwordField.gridwidth = 4;
		gbc_passwordField.insets = new Insets(0, 0, 5, 5);
		gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField.gridx = 1;
		gbc_passwordField.gridy = 6;
		contentPanel.add(passwordField, gbc_passwordField);
		contentPanel.setBackground(new Color(0,0,0,0));
		
		lblAlmenoCaratteri = new JLabel("la password deve avere almeno 4 caratteri");
		lblAlmenoCaratteri.setForeground(Color.RED);
		lblAlmenoCaratteri.setVisible(false);
		GridBagConstraints gbc_lblAlmenoCaratteri = new GridBagConstraints();
		gbc_lblAlmenoCaratteri.gridwidth = 5;
		gbc_lblAlmenoCaratteri.insets = new Insets(0, 0, 5, 5);
		gbc_lblAlmenoCaratteri.gridx = 0;
		gbc_lblAlmenoCaratteri.gridy = 7;
		contentPanel.add(lblAlmenoCaratteri, gbc_lblAlmenoCaratteri);
		
		nameLabel = new JLabel("Nome");
		nameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
		GridBagConstraints gbc_nameLabel = new GridBagConstraints();
		gbc_nameLabel.anchor = GridBagConstraints.EAST;
		gbc_nameLabel.insets = new Insets(0, 0, 5, 5);
		gbc_nameLabel.gridx = 0;
		gbc_nameLabel.gridy = 8;
		contentPanel.add(nameLabel, gbc_nameLabel);
		
		nameField = new JTextField();
		nameField.setColumns(10);
		GridBagConstraints gbc_nameField = new GridBagConstraints();
		gbc_nameField.gridwidth = 4;
		gbc_nameField.insets = new Insets(0, 0, 5, 5);
		gbc_nameField.fill = GridBagConstraints.HORIZONTAL;
		gbc_nameField.gridx = 1;
		gbc_nameField.gridy = 8;
		contentPanel.add(nameField, gbc_nameField);
		
		surnameLabel = new JLabel("Cognome");
		surnameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
		GridBagConstraints gbc_surnameLabel = new GridBagConstraints();
		gbc_surnameLabel.anchor = GridBagConstraints.EAST;
		gbc_surnameLabel.insets = new Insets(0, 0, 5, 5);
		gbc_surnameLabel.gridx = 0;
		gbc_surnameLabel.gridy = 9;
		contentPanel.add(surnameLabel, gbc_surnameLabel);
		
		surnameField = new JTextField();
		surnameField.setColumns(10);
		GridBagConstraints gbc_surnameField = new GridBagConstraints();
		gbc_surnameField.gridwidth = 4;
		gbc_surnameField.insets = new Insets(0, 0, 5, 5);
		gbc_surnameField.fill = GridBagConstraints.HORIZONTAL;
		gbc_surnameField.gridx = 1;
		gbc_surnameField.gridy = 9;
		contentPanel.add(surnameField, gbc_surnameField);
		
		genreLabel = new JLabel("Genere");
		genreLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
		GridBagConstraints gbc_genreLabel = new GridBagConstraints();
		gbc_genreLabel.anchor = GridBagConstraints.EAST;
		gbc_genreLabel.insets = new Insets(0, 0, 0, 5);
		gbc_genreLabel.gridx = 0;
		gbc_genreLabel.gridy = 10;
		contentPanel.add(genreLabel, gbc_genreLabel);
		
		rdbtnMan = new JRadioButton("Maschio");
		rdbtnMan.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
		rdbtnMan.setSelected(true);
		rdbtnMan.setOpaque(false);
		rdbtnMan.setBorder(null);
		rdbtnMan.addActionListener(generale);
		GridBagConstraints gbc_rdbtnMan = new GridBagConstraints();
		gbc_rdbtnMan.insets = new Insets(0, 0, 0, 5);
		gbc_rdbtnMan.gridx = 1;
		gbc_rdbtnMan.gridy = 10;
		contentPanel.add(rdbtnMan, gbc_rdbtnMan);
		
		rdbtnWoman = new JRadioButton("Femmina");
		rdbtnWoman.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
		rdbtnWoman.addActionListener(generale);
		rdbtnWoman.setBorder(null);
		rdbtnWoman.setOpaque(false);
		GridBagConstraints gbc_rdbtnWoman = new GridBagConstraints();
		gbc_rdbtnWoman.insets = new Insets(0, 0, 0, 5);
		gbc_rdbtnWoman.gridx = 3;
		gbc_rdbtnWoman.gridy = 10;
		contentPanel.add(rdbtnWoman, gbc_rdbtnWoman);
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonPane.setBackground(new Color(0,0,0,0));
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
		if(!registrazione){
			variableLabel.setText("Accedi");
			setBounds(100, 100, 350, 160);
			nameLabel.setVisible(false);
			surnameLabel.setVisible(false);
			genreLabel.setVisible(false);
			rdbtnMan.setVisible(false);
			rdbtnWoman.setVisible(false);
			nameField.setVisible(false);
			surnameField.setVisible(false);
		}
		else{
			variableLabel.setText("Registrati");
			setBounds(100,100,450,230);
			nameLabel.setVisible(true);
			surnameLabel.setVisible(true);
			genreLabel.setVisible(true);
			rdbtnMan.setVisible(true);
			rdbtnWoman.setVisible(true);
			nameField.setVisible(true);
			surnameField.setVisible(true);
		}	
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource()==okButton){
			
			lblIlNicknameDeve.setVisible(false);
			lblAlmenoCaratteri.setVisible(false);
			char[] c=passwordField.getPassword();
			User user=null;
			pass=String.copyValueOf(c);
			nick=nickField.getText().trim();
			if(nick.length()<=2){
				this.repaint();
				lblIlNicknameDeve.setText("Il nickname deve avere almeno 3 caratteri");
				lblIlNicknameDeve.setVisible(true);
				return;
			}
			else if(nick.length()>16){
				this.repaint();
				lblIlNicknameDeve.setText("Il nickname non può avere più di 16 caratteri");
				lblIlNicknameDeve.setVisible(true);
				return;
			}
			if(registrazione){
				if(userList.contains(nick)){
					this.repaint();
					lblIlNicknameDeve.setText(nick+" è già in uso");
					lblIlNicknameDeve.setVisible(true);
					return;
				}//user CHeck
				else if(nick.equals("guest")){
					this.repaint();
					if(nick.equals("guest"))
						lblIlNicknameDeve.setText("Non puoi usare il nickname 'guest'");
					else if(nick.equals("admin"))
						lblIlNicknameDeve.setText("Non puoi usare il nickname 'admin'");
					lblIlNicknameDeve.setVisible(true);
					return;	
				}//guest Check
			}//solo se registrazione
			else{
				Iterator<User> it=userList.iterator();
				boolean found=false;
				
				while(it.hasNext() && !found){
					User y=it.next();
					if(y.getNickname().equals(nick)){
						found=true;
						user=y;
					}
				}
				if(user==null){
					this.repaint();
					lblIlNicknameDeve.setText("L'utente "+nick+" non esiste");
					lblIlNicknameDeve.setVisible(true);
					return;
				}//check del nickname
			}
			if(pass.length()<4){
				this.repaint();
				lblAlmenoCaratteri.setText("La password deve avere almeno 4 caratteri");
				lblAlmenoCaratteri.setVisible(true);
				return;
			}
			else if(pass.length()>16){
				this.repaint();
				lblAlmenoCaratteri.setText("La password non può avere più di 16 caratteri");
				lblAlmenoCaratteri.setVisible(true);
				return;
			}
			else{
				String sha=DigestUtils.shaHex(pass);
				if(!sha.equals(user.getPassword())){
					this.repaint();
					lblAlmenoCaratteri.setText("La password non è corretta");
					lblAlmenoCaratteri.setVisible(true);
					return;
				}
			}//check della password
			if(registrazione){
				name=nameField.getText().trim();
				surname=surnameField.getText().trim();
				genre=generale.getSelected();
			}
			oki=true;
			setVisible(false);
		}
		else if(e.getSource()==cancelButton){
			
			oki=false;
			pass=null;
			nick=null;
			setVisible(false);
		}
	}
	
	public void setUserList(HashSet<User> users){
		
		this.userList=users;
	}
	public boolean isOk(){
		
		return oki;
	}
	public boolean isForRegistration(){
		
		return registrazione;
	}
	public boolean autologin(){
		
		return autologin;
	}
	
	public String[] result(){
		
		String[] result=new String[2];
		if(registrazione)
			result=new String[5];
		result[0]=nick;
		result[1]=pass;
		if(registrazione){
			result[2]=name;
			result[3]=surname;
			result[4]=genre+"";
		}
		return result;
	}
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			LoginDialog dialog = new LoginDialog(null,false,false);
			HashSet<User> users=new HashSet<User>();
			users.add(new User("admin"));
			users.add(new User("fabrizio"));
			users.add(new User("roberto"));
			dialog.setUserList(users);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}