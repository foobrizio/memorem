package graphic;

import graphic.MemoremGUI.Lang;

import java.awt.*;
import java.awt.event.*;

import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.layout.*;
import com.jgoodies.forms.factories.FormFactory;

import javax.swing.*;

import util.StringAnalyzer;
import util.User;

@SuppressWarnings("serial")
public class UserDialog extends JDialog{

	private final JPanel contentPanel = new JPanel();
	
	private final Color myYellow=new Color(255,255,150);
	private final Color myBlue=new Color(150,200,255);
	private final Color myRed=new Color(255,120,100);
	
	private JTextField nomeField;
	private JTextField cognomeField;
	private JTextField userField;
	private Mouse mouse;
	private JLabel titleLabel,nameLabel,modifica,surnameLabel,genLabel,langLabel,userLabel;
	private JRadioButton maleButton,femaleButton;
	private JButton okButton,cancelButton;
	private User modified;
	private boolean modification,isModified;
	private JComboBox<String> langBox;
	private Lang lang;
	private JLabel adviceLabel;
	
	private class Mouse extends MouseAdapter{
		
		public void mouseClicked(MouseEvent evt){
			
			if(evt.getSource()==modifica){
				
				abilitaModifiche();
			}
			
			/*
			 * SE CLICCHIAMO OK VOGLIAMO VERIFICARE CHE SIANO STATI MODIFICATI I DATI UTENTE
			 */
			else if(evt.getSource()==okButton){
				
				adviceLabel.setVisible(false);
				if(!StringAnalyzer.verificaStringa(nomeField.getText())){
					adviceLabel.setVisible(true);
					return;
				}
				if(!StringAnalyzer.verificaStringa(cognomeField.getText())){
					adviceLabel.setVisible(true);
					return;
				}
				if(!modified.getNome().equals(nomeField.getText().trim())){
					isModified=true;
					modified.setNome(nomeField.getText());
				}
				if(!modified.getCognome().equals(cognomeField.getText().trim())){
					isModified=true;
					modified.setCognome(cognomeField.getText());
				}
				if(modified.isMaschio()!=maleButton.isSelected()){
					isModified=true;
					if(maleButton.isSelected())
						modified.setMale();
					else
						modified.setFemale();
				}
				Lang sel=null;
				switch((String)langBox.getSelectedItem()){
				case "Italiano": sel=Lang.IT; break;
				case "Deutsch" : sel=Lang.DE; break;
				case "Español" : sel=Lang.ES; break;
				default: sel=Lang.EN;
				}
				if(sel!=modified.getLingua()){
					isModified=true;
					modified.setLingua(sel);
				}
				if(modification)
					abilitaModifiche();	
				UserDialog.this.dispose();
			}
			else if(evt.getSource()==cancelButton){
				
				adviceLabel.setVisible(false);
				if(modification)
					abilitaModifiche();
				UserDialog.this.dispose();
			}
			else if(evt.getSource()==maleButton){
				if(maleButton.isEnabled()){
					femaleButton.setSelected(false);
					maleButton.setSelected(true);
				}
			}
			else if(evt.getSource()==femaleButton){
				if(femaleButton.isEnabled()){
					maleButton.setSelected(false);
					femaleButton.setSelected(true);
				}
			}
		}
		public void mouseEntered(MouseEvent evt){
			if(evt.getSource()==modifica){
				
				modifica.setBounds(modifica.getX()-1, modifica.getY()-1, modifica.getWidth()+1, modifica.getHeight()+1);
				modifica.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
			}
			else if(evt.getSource()==okButton){
				
				okButton.setBorderPainted(true);
			}
			else if(evt.getSource()==cancelButton){
				
				cancelButton.setBorderPainted(true);
			}
		}
		public void mouseExited(MouseEvent evt){
			if(evt.getSource()==modifica){
				
				if(!modification){
					modifica.setBounds(modifica.getX()+1, modifica.getY()+1, modifica.getWidth()-1, modifica.getHeight()-1);
					modifica.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
				}
			}
			else if(evt.getSource()==okButton){
				
				okButton.setBorderPainted(false);
				repaint();
			}
			else if(evt.getSource()==cancelButton){
				
				cancelButton.setBorderPainted(false);
				repaint();
			}
		}
	}
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UserDialog dialog = new UserDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			User user=new User("michael","Michael","Sembello",'m',"en");
			dialog.visualizza(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public UserDialog( JFrame owner){
		super(owner,true);
		setBounds(100, 100, 400, 250);
		setContentPane(new ColoredPanel("files//wallpapers//blue-yellow.jpg"));
		getContentPane().setLayout(new BorderLayout());
		getContentPane().setBackground(new Color(0,0,0,0));
		setResizable(false);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setOpaque(false);
		getContentPane().add(contentPanel, BorderLayout.NORTH);
		modification=false;
		lang=Lang.EN;
		mouse=new Mouse();
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		{
			titleLabel = new JLabel("Profile");
			titleLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
			contentPanel.add(titleLabel, "6, 2, 3, 1, center, default");
		}
		modifica = new JLabel("modify");
		modifica.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
		modifica.addMouseListener(mouse);
		modifica.setBackground(new Color(0,0,0,0));
		modifica.setOpaque(false);
		contentPanel.add(modifica, "10, 2, right, default");
		
		adviceLabel = new JLabel("You can't insert characters like ',\\,?,(,),\"");
		adviceLabel.setBackground(new Color(0,0,0,0));
		adviceLabel.setVisible(false);
		contentPanel.add(adviceLabel, "4, 4, 7, 1");
		{
			userLabel = new JLabel("Username:");
			userLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
			contentPanel.add(userLabel, "4, 6, right, default");
		
			userField = new JTextField();
			userField.setFont(new Font("Comic Sans MS", Font.ITALIC, 12));
			contentPanel.add(userField, "6, 6, fill, default");
			userField.setEditable(false);
			userField.setColumns(10);
		
			nameLabel = new JLabel("Name:");
			nameLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
			contentPanel.add(nameLabel, "4, 8, right, default");
		
			nomeField = new JTextField();
			nomeField.setFont(new Font("Comic Sans MS", Font.ITALIC, 12));
			contentPanel.add(nomeField, "6, 8, fill, default");
			nomeField.setEditable(false);
			nomeField.setColumns(10);
		
			surnameLabel = new JLabel("Surname:");
			surnameLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
			contentPanel.add(surnameLabel, "8, 8, right, default");
		
			cognomeField = new JTextField();
			cognomeField.setFont(new Font("Comic Sans MS", Font.ITALIC, 12));
			contentPanel.add(cognomeField, "10, 8, fill, default");
			cognomeField.setEditable(false);
			cognomeField.setColumns(10);
		
			genLabel = new JLabel("Genre:");
			genLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
			contentPanel.add(genLabel, "4, 10, right, default");
		
			maleButton = new JRadioButton("Male");
			maleButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
			maleButton.setOpaque(false);
			maleButton.setEnabled(false);
			contentPanel.add(maleButton, "6, 10, left, default");
			maleButton.addMouseListener(mouse);
		
			femaleButton = new JRadioButton("Female");
			femaleButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
			femaleButton.setOpaque(false);
			femaleButton.setEnabled(false);
			contentPanel.add(femaleButton, "6, 12");
			femaleButton.addMouseListener(mouse);
		
			langLabel = new JLabel("Language:");
			langLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
			contentPanel.add(langLabel, "4, 14, right, default");
	
			String[] languages={"Italiano","English","Deutsch","Español"};
			langBox = new JComboBox<String>(languages);
			//langBox=new JComboBox<String>();
			langBox.setFont(new Font("Comic Sans MS", Font.ITALIC, 12));
			langBox.setEnabled(false);
			contentPanel.add(langBox, "6, 14, fill, default");
		
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				okButton.setBackground(new Color(0,0,0,0));
				okButton.setForeground(Color.GREEN);
				okButton.addMouseListener(mouse);
				okButton.setBorderPainted(false);
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton = new JButton("Cancel");
				cancelButton.addMouseListener(mouse);
				cancelButton.setBackground(new Color(0,0,0,0));
				cancelButton.setForeground(Color.RED);
				cancelButton.setBorderPainted(false);
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
				buttonPane.setOpaque(false);
			}
		}
		setColors();
	}
	
	public void abilitaModifiche(){
		if(!modification){
			modification=true;
			modifica.setForeground(Color.RED);
		}
		else if(modification){
			modification=false;
			modifica.setForeground(myRed.darker());
		}
		langBox.setEnabled(modification);
		maleButton.setEnabled(modification);
		femaleButton.setEnabled(modification);
		nomeField.setEditable(modification);
		cognomeField.setEditable(modification);
	}
	public User getUser(){
		
		return modified;
	}
	
	public boolean isModified(){
		
		return isModified;
	}
	private void setColors(){
		
		titleLabel.setForeground(myRed);
		userLabel.setForeground(myYellow);
		nameLabel.setForeground(myYellow);
		surnameLabel.setForeground(myYellow);
		langLabel.setForeground(myYellow);
		genLabel.setForeground(myYellow);
		maleButton.setForeground(myBlue);
		adviceLabel.setForeground(Color.RED);
		femaleButton.setForeground(Color.PINK);
		modifica.setForeground(myRed.darker());
		
	}
	public void setLanguage(Lang lang){
		
		if(lang.equals(this.lang))
			return;
		this.lang=lang;
		if(lang==Lang.EN){
			adviceLabel = new JLabel("You can't insert characters like ',\\,?,(,),\"");
			titleLabel.setText("Profile");
			modifica.setText("modify");
			maleButton.setText("Male");
			femaleButton.setText("Female");
			langLabel.setText("Language");
			nameLabel.setText("Name");
			surnameLabel.setText("Surname");
			genLabel.setText("Genre");
		}
		else if(lang==Lang.IT){
			adviceLabel = new JLabel("Non puoi inserire caratteri come ',\\,?,(,),\"");
			titleLabel.setText("Profilo");
			maleButton.setText("Maschio");
			modifica.setText("modifica");
			femaleButton.setText("Femmina");
			langLabel.setText("Lingua");
			nameLabel.setText("Nome");
			surnameLabel.setText("Cognome");
			genLabel.setText("Genere");
		}
		else if(lang==Lang.DE){
			adviceLabel = new JLabel("Sie können keine Zeichen wie einfügen ',\\,?,(,),\"");
			titleLabel.setText("Profilbild");
			maleButton.setText("Mann");
			modifica.setText("Änderung");
			femaleButton.setText("Weib");
			langLabel.setText("Sprache");
			nameLabel.setText("Name");
			surnameLabel.setText("Nachname");
			genLabel.setText("Genre");
		}
		else{
			adviceLabel = new JLabel("No se puede insertar caracteres como ',\\,?,(,),\"");
			titleLabel.setText("Perfil");
			maleButton.setText("Macho");
			modifica.setText("modifica");
			femaleButton.setText("Hembra");
			langLabel.setText("Idioma");
			nameLabel.setText("Nombre");
			surnameLabel.setText("Apellido");
			genLabel.setText("Género");
		}
	}//setLanguage
	
	public void visualizza(User user){
		
		//modification=false;
		isModified=false;
		userField.setText(user.getNickname());
		nomeField.setText(user.getNome());
		cognomeField.setText(user.getCognome());
		if(user.isMaschio()){
			maleButton.setSelected(true);
			femaleButton.setSelected(false);
		}
		else{
			maleButton.setSelected(false);
			femaleButton.setSelected(true);
		}
		switch(user.getLingua()){
		case IT: langBox.setSelectedItem("Italiano"); break;
		case DE: langBox.setSelectedItem("Deutsch"); break;
		case ES: langBox.setSelectedItem("Español"); break;
		default: langBox.setSelectedItem("English");
		}
		char sesso=user.isMaschio()?'m':'f';
		modified=new User(user.getNickname(),user.getNome(),user.getCognome(),sesso,(String)langBox.getSelectedItem());
		setVisible(true);
	}
}
