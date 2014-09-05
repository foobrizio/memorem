package graphic;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

public class ConfDialog extends JDialog implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8450210120230331786L;
	private final JPanel contentPanel = new JPanel();
	private JPanel dataPanel,adminPanel;
	private JButton okButton,cancelButton;
	private JLabel lblJdbcdrivers;
	private JLabel lblJdbcurl;
	private JLabel lblJdbcusername;
	private JLabel lblJdbcpassword;
	private JTextField driverField;
	private JTextField urlField;
	private JTextField userField;
	private JTextField passField;
	private JLabel lblUsername;
	private JLabel lblPassword;
	private JTextField adminField;
	private JTextField adPassField;
	private JLabel dataAdvice;
	private JLabel adminAdvice;
	private Border normal;
	private boolean isForDatabase;
	private String adminPass;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ConfDialog dialog = new ConfDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.configuraDatabase(new File("files//.database.properties"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ConfDialog(JFrame owner) {
		super(owner,true);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 350, 220);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		dataPanel=new JPanel();
		dataPanel.setBounds(0, 0, 445, 238);
		//contentPanel.add(dataPanel);
		dataPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
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
				FormFactory.DEFAULT_ROWSPEC,}));
		{
			lblJdbcdrivers = new JLabel("jdbc.drivers:");
			dataPanel.add(lblJdbcdrivers, "4, 4, right, default");
		}
		{
			driverField = new JTextField();
			dataPanel.add(driverField, "6, 4, 4, 1, fill, default");
			driverField.setColumns(10);
			normal=driverField.getBorder();
		}
		{
			lblJdbcurl = new JLabel("jdbc.url:");
			dataPanel.add(lblJdbcurl, "4, 6, right, default");
		}
		{
			urlField = new JTextField();
			dataPanel.add(urlField, "6, 6, 4, 1, fill, default");
			urlField.setColumns(10);
		}
		{
			lblJdbcusername = new JLabel("jdbc.username:");
			dataPanel.add(lblJdbcusername, "4, 8, right, default");
		}
		{
			userField = new JTextField();
			dataPanel.add(userField, "6, 8, 4, 1, fill, default");
			userField.setColumns(10);
		}
		{
			lblJdbcpassword = new JLabel("jdbc.password:");
			dataPanel.add(lblJdbcpassword, "4, 10, right, default");
		}
		{
			passField = new JTextField();
			dataPanel.add(passField, "6, 10, 4, 1, fill, default");
			passField.setColumns(10);
		}
		{
			dataAdvice = new JLabel("Error: empty field");
			dataAdvice.setForeground(Color.RED);
			dataPanel.add(dataAdvice, "4, 12, 6, 1, center, default");
			adminPanel=new JPanel();
			adminPanel.setBounds(0, 0, 445, 238);
			//contentPanel.add(adminPanel);
			adminPanel.setLayout(new FormLayout(new ColumnSpec[] {
					FormFactory.RELATED_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.RELATED_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("default:grow"),
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
					FormFactory.DEFAULT_ROWSPEC,}));
			{
				lblUsername = new JLabel("username:");
				adminPanel.add(lblUsername, "4, 4, right, default");
			}
			{
				adminField = new JTextField();
				adminField.setText("admin");
				adminField.setEditable(false);
				adminPanel.add(adminField, "6, 4, fill, default");
				adminField.setColumns(10);
			}
			{
				lblPassword = new JLabel("password:");
				adminPanel.add(lblPassword, "4, 6, right, default");
			}
			{
				adPassField = new JTextField();
				adminPanel.add(adPassField, "6, 6, fill, default");
				adPassField.setColumns(10);
			}
			{
				adminAdvice = new JLabel("Error: empty field");
				adminAdvice.setForeground(Color.RED);
				adminAdvice.setVisible(false);
				adminPanel.add(adminAdvice, "4, 8, 3, 1, center, default");
			}
			dataAdvice.setVisible(false);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
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
	
	public void actionPerformed(ActionEvent evt){
		
		Border border=BorderFactory.createLineBorder(Color.RED);
		if(evt.getSource()==okButton){
			
			if(isForDatabase){
				driverField.setBorder(normal);
				urlField.setBorder(normal);
				userField.setBorder(normal);
				passField.setBorder(normal);
				adPassField.setBorder(normal);
				boolean error=false;
				if(driverField.getText().trim().length()==0){
					error=true;
					driverField.setBorder(border);
				}
				if(urlField.getText().trim().length()==0){
					error=true;
					urlField.setBorder(border);
				}
				if(userField.getText().trim().length()==0){
					error=true;
					userField.setBorder(border);
				}	
				if(passField.getText().trim().length()==0){
					error=true;
					passField.setBorder(border);
				}
				if(error)//ci sono spazi vuoti tra i TextField di configurazione del database
					dataAdvice.setVisible(true);
				else{//tutto è stato configurato, si passa alla scrittura del file
					String uno="jdbc.drivers: "+driverField.getText();
					String due="jdbc.url: "+urlField.getText();
					String tre="jdbc.username: "+userField.getText();
					String quattro="jdbc.password: "+passField.getText();
					try {
						BufferedWriter bw=new BufferedWriter(new FileWriter("files//.database.properties"));
						bw.write(uno+"\n");
						bw.write(due+"\n");
						bw.write(tre+"\n");
						bw.write(quattro+"\n");
						bw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				//dispose();
			}//ifForDatabase
			else{
				boolean error=false;
				if(adPassField.getText().trim().length()==0){
					error=true;
					adPassField.setBorder(border);
				}
				if(error){//non abbiamo inserito la password dell'admin
					adminAdvice.setVisible(true);
					return;
				}
				else{
					adminPass=adPassField.getText();
				}
			}
			this.dispose();
		}
		else if(evt.getSource()==cancelButton){
			
			this.dispose();
			this.getOwner().dispose();
		}
		
	}
	public void configuraAdmin(){
		
		this.setTitle("Admin creation");
		contentPanel.remove(dataPanel);
		contentPanel.add(adminPanel);
		isForDatabase=false;
		setVisible(true);
	}
	public void configuraDatabase(File f){
		
		this.setTitle("Database configuration");
		contentPanel.remove(adminPanel);
		contentPanel.add(dataPanel);
		isForDatabase=true;
		StringBuilder sb=new StringBuilder(500);
		try {
			BufferedReader br=new BufferedReader(new FileReader(f));
			String x=br.readLine();
			while(x!=null){
				sb.append(x+"\n");
				if(x.substring(0, 13).equals("jdbc.drivers:"))
					driverField.setText(x.substring(14).trim());
				else if(x.substring(0,9).equals("jdbc.url:"))
					urlField.setText(x.substring(10).trim());
				else if(x.substring(0,14).equals("jdbc.password:"))
					passField.setText(x.substring(14).trim());
				else if(x.substring(0,14).equals("jdbc.username:"))
					userField.setText(x.substring(14).trim());
				x=br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(this.getOwner(), "Il file di configurazione non è stato trovato");
			driverField.setText("");
			urlField.setText("");
			passField.setText("");
			userField.setText("");
		} catch(IOException ioe){
			JOptionPane.showMessageDialog(this.getOwner(), "Errore durante la lettura del file");
			return;
		}
		setVisible(true);
	}
	
	public String getAdminPass(){
		
		System.out.println("\""+adminPass+"\"");
		return adminPass;
	}
}
