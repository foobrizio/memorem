package graphic;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.apache.commons.codec.digest.DigestUtils;

import util.User;
import graphic.MemoremGUI.Lang;

import com.jgoodies.forms.layout.*;
import com.jgoodies.forms.factories.FormFactory;

@SuppressWarnings("serial")
public class ModPassDialog extends JDialog implements ActionListener{

	private final JPanel contentPanel = new JPanel();
	private JPasswordField passVecchia;
	private JPasswordField passNuova;
	private JPasswordField passConferma;
	private JButton okButton;
	private JButton cancelButton;
	private JButton fakeButton;
	private char[] oldP,newP;
	private User user;
	private JLabel oldAdvice;
	private JLabel passAdvice;
	private Lang language;
	private JLabel confPass;
	private JLabel newPass;
	private JLabel oldPass;
	
	/**
	 * Create the dialog.
	 */
	public ModPassDialog(JButton fake,User user) {
		setBounds(100, 100, 350, 230);
		this.fakeButton=fake;
		this.user=user;
		this.language=Lang.EN;
		setContentPane(new ColoredPanel("files//wallpapers//password.jpg"));
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
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
			oldPass = new JLabel("Old password:");
			oldPass.setHorizontalAlignment(SwingConstants.RIGHT);
			contentPanel.add(oldPass, "2, 4, right, default");
			contentPanel.setOpaque(false);
			contentPanel.setBackground(new Color(0,0,0,0));
		}
		{
			passVecchia = new JPasswordField();
			contentPanel.add(passVecchia, "4, 4, fill, default");
			passVecchia.setColumns(10);
		}
		{
			oldAdvice = new JLabel("The user's password is wrong");
			oldAdvice.setVisible(false);
			oldAdvice.setHorizontalAlignment(SwingConstants.CENTER);
			oldAdvice.setForeground(Color.RED);
			contentPanel.add(oldAdvice, "2, 6, 3, 1");
		}
		{
			newPass = new JLabel("New password:");
			newPass.setHorizontalAlignment(SwingConstants.RIGHT);
			contentPanel.add(newPass, "2, 8, right, default");
		}
		{
			passNuova = new JPasswordField();
			passNuova.setColumns(10);
			contentPanel.add(passNuova, "4, 8, fill, default");
		}
		{
			passAdvice = new JLabel("Passwords don't match");
			passAdvice.setVisible(false);
			passAdvice.setHorizontalAlignment(SwingConstants.CENTER);
			passAdvice.setForeground(Color.RED);
			contentPanel.add(passAdvice, "2, 10, 3, 1");
		}
		{
			confPass = new JLabel("Confirm password:");
			contentPanel.add(confPass, "2, 12, right, default");
		}
		{
			passConferma = new JPasswordField();
			passConferma.setColumns(10);
			contentPanel.add(passConferma, "4, 12, fill, default");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBackground(new Color(0,0,0,0));
			buttonPane.setOpaque(false);
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				this.okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				okButton.addActionListener(this);
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(this);
				buttonPane.add(cancelButton);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		if(arg0.getSource()==okButton){
			
			char[] newPass1=passNuova.getPassword();
			char[] newPass2=passConferma.getPassword();
			String nuova=String.copyValueOf(passVecchia.getPassword());
			if(!(DigestUtils.shaHex(nuova).equals(user.getPassword()))){
				System.out.println(DigestUtils.shaHex(nuova));
				System.out.println(user.getPassword());
				oldAdvice.setVisible(true);
				passAdvice.setVisible(false);
				passVecchia.setText("");
				passNuova.setText("");
				passConferma.setText("");
				return;
			}
			if(newPass1.length!=newPass2.length){
				oldAdvice.setVisible(false);
				passAdvice.setVisible(true);
				passVecchia.setText("");
				passNuova.setText("");
				passConferma.setText("");
				return;
			}
			else for(int i=0;i<newPass1.length;i++)	//ultimo check sulla correttezza della nuova password
				if(newPass1[i]!=newPass2[i]){
					passAdvice.setVisible(true);
					passVecchia.setText("");
					passNuova.setText("");
					passConferma.setText("");
					return;
				}
			oldP=passVecchia.getPassword();
			newP=passNuova.getPassword();
			passVecchia.setText("");
			passNuova.setText("");
			passConferma.setText("");
			fakeButton.doClick();
		}
		else if(arg0.getSource()==cancelButton){
			
			this.dispose();
		}
	}//actionPerformed
	
	public String[] getPasswords(){
		
		String[] result=new String[2];
		String una="",due="";
		int i=0,j=0;
		while(i<oldP.length || j<newP.length){
			if(i<oldP.length)
				una=una+oldP[i++];
			if(j<newP.length)
				due=due+newP[j++];	
		}
		result[0]=una;
		result[1]=due;
		return result;
	}
	
	public void setLanguage(Lang lang){
		
		if(this.language.equals(lang))
			return;
		else if(lang==Lang.IT){
			oldPass.setText("Vecchia password:");
			newPass.setText("Nuova password:");
			confPass.setText("Conferma password:");
			oldAdvice.setText("La password utente è sbagliata");
			passAdvice.setText("Le due password non combaciano");
		}
		else if(lang==Lang.DE){
			oldPass.setText("Altes password:");
			newPass.setText("Neues password:");
			confPass.setText("Password bestätigen:");
			oldAdvice.setText("Password des Benutzers ist falsch");
			passAdvice.setText("Passwörter stimmen nicht überein");
		}
		else if(lang==Lang.ES){
			oldPass.setText("Password anterior:");
			newPass.setText("Nueva password:");
			confPass.setText("Confirma password:");
			oldAdvice.setText("La password de usuario es incorrecta");
			passAdvice.setText("Las dos password no coinciden");
		}
		else{
			oldPass.setText("Old password:");
			newPass.setText("New password:");
			confPass.setText("Confirm password:");
			oldAdvice.setText("The user's password is wrong");
			passAdvice.setText("Passwords don't match");
		}
		this.language=lang;
	}
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ModPassDialog dialog = new ModPassDialog(new JButton("OK"),null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
