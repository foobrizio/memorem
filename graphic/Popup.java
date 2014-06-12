package graphic;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import main.Memo;
import util.Data;

import java.awt.*;
import java.awt.event.*;


@SuppressWarnings("serial")
public class Popup extends JDialog implements ActionListener{


	private final JPanel contentPanel = new JPanel();
	private JTextField descrizione;
	private JComboBox<String> prior,mese;
	private JComboBox<Integer> anno,giorno,ora,minuto;
	private JButton okButton,cancelButton;
	private boolean okPressed,modified,eliminated;
	private Memo created,old;
	private Data oggi;
	private JLabel titolo;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Popup dialog = new Popup(true);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog. if modified is true, the popup is opened for a modification of an already existing memo
	 */
	public Popup(boolean modified) {
		this.modified=modified;
		setBounds(100, 100, 450, 200);
		setResizable(false);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.NORTH);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] {30, 0, 0, 0, 30, 0, 30, 30, 30};
		gbl_contentPanel.rowHeights = new int[] {30, 30, 30, 30, 30};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 1.0, 0.0};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		contentPanel.setLayout(gbl_contentPanel);
		oggi=new Data();
		{
			titolo = new JLabel("New label");
			if(modified)
				titolo.setText("Modifica memo");
			else
				titolo.setText("Aggiungi memo");
			GridBagConstraints gbc_titolo = new GridBagConstraints();
			gbc_titolo.gridwidth = 3;
			gbc_titolo.insets = new Insets(0, 0, 5, 5);
			gbc_titolo.gridx = 3;
			gbc_titolo.gridy = 0;
			contentPanel.add(titolo, gbc_titolo);
		}
		{
			JLabel lblNome = new JLabel("Nome");
			GridBagConstraints gbc_lblNome = new GridBagConstraints();
			gbc_lblNome.anchor = GridBagConstraints.EAST;
			gbc_lblNome.insets = new Insets(0, 0, 5, 5);
			gbc_lblNome.gridx = 1;
			gbc_lblNome.gridy = 1;
			contentPanel.add(lblNome, gbc_lblNome);
		}
		{
			descrizione = new JTextField();
			GridBagConstraints gbc_textField = new GridBagConstraints();
			gbc_textField.gridwidth = 6;
			gbc_textField.insets = new Insets(0, 0, 5, 5);
			gbc_textField.fill = GridBagConstraints.HORIZONTAL;
			gbc_textField.gridx = 2;
			gbc_textField.gridy = 1;
			contentPanel.add(descrizione, gbc_textField);
			descrizione.setColumns(10);
		}
		{
			JLabel lblPriorit = new JLabel("Priorità");
			GridBagConstraints gbc_lblPriorit = new GridBagConstraints();
			gbc_lblPriorit.anchor = GridBagConstraints.EAST;
			gbc_lblPriorit.insets = new Insets(0, 0, 5, 5);
			gbc_lblPriorit.gridx = 1;
			gbc_lblPriorit.gridy = 2;
			contentPanel.add(lblPriorit, gbc_lblPriorit);
		}
		{
			String[] priorita={ "Bassa", "Normale", "Alta" };
			prior = new JComboBox<String>(priorita);
			//prior=new JComboBox<String>();
			prior.setSelectedIndex(1);	//in questo modo la scelta di default è "normale"
			GridBagConstraints gbc_comboBox = new GridBagConstraints();
			gbc_comboBox.gridwidth = 3;
			gbc_comboBox.insets = new Insets(0, 0, 5, 5);
			gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBox.gridx = 2;
			gbc_comboBox.gridy = 2;
			contentPanel.add(prior, gbc_comboBox);
		}
		{
			JLabel lblAnno = new JLabel("Anno");
			GridBagConstraints gbc_lblAnno = new GridBagConstraints();
			gbc_lblAnno.anchor = GridBagConstraints.EAST;
			gbc_lblAnno.insets = new Insets(0, 0, 5, 5);
			gbc_lblAnno.gridx = 5;
			gbc_lblAnno.gridy = 2;
			contentPanel.add(lblAnno, gbc_lblAnno);
		}
		{
			manageYear(oggi.anno());
			anno.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent evt){
					manageDay();
				}
			});
		}
		{
			JLabel lblMese = new JLabel("Mese");
			GridBagConstraints gbc_lblMese = new GridBagConstraints();
			gbc_lblMese.anchor = GridBagConstraints.EAST;
			gbc_lblMese.insets = new Insets(0, 0, 5, 5);
			gbc_lblMese.gridx = 1;
			gbc_lblMese.gridy = 3;
			contentPanel.add(lblMese, gbc_lblMese);
		}
		{
			manageMonth();
			mese.setSelectedIndex(oggi.mese()-1);
			mese.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent evt){
					manageDay();
				}
			});
		}
		{
			JLabel lblGiorno = new JLabel("Giorno");
			GridBagConstraints gbc_lblGiorno = new GridBagConstraints();
			gbc_lblGiorno.anchor = GridBagConstraints.EAST;
			gbc_lblGiorno.insets = new Insets(0, 0, 5, 5);
			gbc_lblGiorno.gridx = 5;
			gbc_lblGiorno.gridy = 3;
			contentPanel.add(lblGiorno, gbc_lblGiorno);
		}
		{	
			manageDay();
		}
		{
			JLabel lblOra = new JLabel("Ora");
			GridBagConstraints gbc_lblOra = new GridBagConstraints();
			gbc_lblOra.anchor = GridBagConstraints.EAST;
			gbc_lblOra.insets = new Insets(0, 0, 0, 5);
			gbc_lblOra.gridx = 1;
			gbc_lblOra.gridy = 4;
			contentPanel.add(lblOra, gbc_lblOra);
		}
		{
			Integer[] ore=new Integer[24];
			for(int i=0;i<24;i++)
				ore[i]=new Integer(i);
			ora = new JComboBox<Integer>(ore);
			//ora=new JComboBox<Integer>();
			ora.setSelectedIndex(oggi.ora());
			GridBagConstraints gbc_comboBox = new GridBagConstraints();
			gbc_comboBox.insets = new Insets(0, 0, 0, 5);
			gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBox.gridx = 2;
			gbc_comboBox.gridy = 4;
			contentPanel.add(ora, gbc_comboBox);
		}
		{
			JLabel lblMinuto = new JLabel("Minuto");
			GridBagConstraints gbc_lblMinuto = new GridBagConstraints();
			gbc_lblMinuto.anchor = GridBagConstraints.EAST;
			gbc_lblMinuto.insets = new Insets(0, 0, 0, 5);
			gbc_lblMinuto.gridx = 5;
			gbc_lblMinuto.gridy = 4;
			contentPanel.add(lblMinuto, gbc_lblMinuto);
		}
		{
			Integer[] minuti=new Integer[60];
			for(int i=0;i<60;i++)
				minuti[i]=new Integer(i);
			minuto = new JComboBox<Integer>(minuti);
			//minuto=new JComboBox<Integer>();
			minuto.setSelectedItem(oggi.minuto()-1);
			GridBagConstraints gbc_comboBox = new GridBagConstraints();
			gbc_comboBox.gridwidth = 2;
			gbc_comboBox.insets = new Insets(0, 0, 0, 5);
			gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBox.gridx = 6;
			gbc_comboBox.gridy = 4;
			contentPanel.add(minuto, gbc_comboBox);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				okButton.addActionListener(this);
			}
			{
				cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
				cancelButton.addActionListener(this);
			}
		}
	}
	
	private void manageYear(int year){
		Integer[] annos=new Integer[10];
		for(int i=0;i<10;i++){
			annos[i]=new Integer(year+i);
		}
		anno = new JComboBox<Integer>(annos);
		//anno= new JComboBox<Integer>();
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.gridwidth = 2;
		gbc_comboBox.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 6;
		gbc_comboBox.gridy = 2;
		contentPanel.add(anno, gbc_comboBox);
	}
	
	private void manageMonth(){
		String mesi[]={ "Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"};
		mese = new JComboBox<String>(mesi);
		//mese=new JComboBox<String>();
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.gridwidth = 2;
		gbc_comboBox.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 2;
		gbc_comboBox.gridy = 3;
		contentPanel.add(mese, gbc_comboBox);
	}
	
	private void manageDay(){
		
		if(giorno!=null)
			giorno.setVisible(false);
		Integer[] giorni;
		String gdms=(String)mese.getSelectedItem();
		int gdm=Data.daysOfMonth((Integer)anno.getSelectedItem(),Data.monthToInt(gdms));
		//System.out.println(gdms+" ha "+gdm+" giorni");
		giorni=new Integer[gdm];
		for(int i=0;i<gdm;i++){
			giorni[i]=new Integer(i+1);
		}
		int x=0;
		if(giorno!=null)
			x=giorno.getSelectedIndex();
		giorno = new JComboBox<Integer>(giorni);
		//giorno=new JComboBox<Integer>();
		switch(x){
		case 0: giorno.setSelectedIndex(0); break;
		case 31: case 30: case 29: giorno.setSelectedIndex((x>=gdm)?giorni.length-1:x); break;
		default: giorno.setSelectedIndex(x);
		}
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.gridwidth = 2;
		gbc_comboBox.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 6;
		gbc_comboBox.gridy = 3;
		giorno.setVisible(true);
		contentPanel.add(giorno, gbc_comboBox);
	}
	
	public boolean getOk(){
		
		return okPressed;
	}
	
	public boolean getEliminated(){
		
		return eliminated;
	}
	
	public boolean getModified(){
		
		return modified;
	}
	
	public Memo getCreated(){
		
		return created;
	}
	
	public Memo getOld(){
		
		return old;
	}
	
	public void setOld(Memo old){
		
		this.old=old;
	}
	
	public void setModified(boolean modified){
		
		this.modified=modified;
		if(modified)
			titolo.setText("Modifica memo");
		else{
			titolo.setText("Aggiungi memo");
			descrizione.setText("");
			prior.setSelectedIndex(1);
			mese.setSelectedIndex(oggi.mese()-1);
			giorno.setSelectedIndex(oggi.giorno()-1);
			
		}
	}
	
	public void setOk(boolean ok){
		
		this.okPressed=ok;
	}
	
	public void setEliminated(boolean eliminated){
		
		this.eliminated=eliminated;
	}
	
	public void actionPerformed(ActionEvent evt){
		
		if(evt.getSource().equals(okButton)){
			System.out.println("ok è stato premuto");
			okPressed=true;
			String desc=descrizione.getText();
			String pri1=(String)prior.getSelectedItem(),pri2;
			switch(pri1){
			case "Normale":pri2="normal"; break;
			case "Alta":pri2="high";break;
			case "Bassa":pri2="low";break;
			default: pri2="nothing";
			}
			int y=(Integer)anno.getSelectedItem();
			int m=Data.monthToInt((String)mese.getSelectedItem());
			int d=(Integer)giorno.getSelectedItem();
			int o=(Integer)ora.getSelectedItem();
			int mi=(Integer)minuto.getSelectedItem();
			//System.out.println(desc+","+pri2+","+y+","+m+","+d+","+o+","+mi);
			created=new Memo(desc,pri2,y,m,d,o,mi);
			setVisible(false);
		}
		else if(evt.getSource().equals(cancelButton)){
			okPressed=false;
			dispose();
		}
	}
	
	public void setDesc(String desc){
		descrizione.setText(desc);
	}
	
	public void setPrior(int priorn){
		
		switch(priorn){
		case 0: prior.setSelectedIndex(0); break;
		case 1: prior.setSelectedIndex(1); break;
		case 2: prior.setSelectedIndex(2); break;
		default: return;
		}
		
	}
	public void setYear(int year){
			
		manageYear(year);
	}
	
	public void setMonth(int month){
		
		//manageMonth();
		mese.setSelectedIndex(month);
	}
	
	public void setDay(int day){
		
		giorno.setSelectedIndex(day);
	}
	
	public void setHour(int hour){
		
		ora.setSelectedIndex(hour);
	}
	
	public void setMinute(int minute){
		
		minuto.setSelectedIndex(minute);
	}
}