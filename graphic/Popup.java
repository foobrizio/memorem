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
	private JLabel titolo,nome,prioritLabel,annoLabel,meseLabel,dayLabel,oraLabel,minutoLabel;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Popup dialog = new Popup(null,true);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog. if modified is true, the popup is opened for a modification of an already existing memo
	 */
	public Popup(JFrame owner,boolean modified){
		super(owner,true);
		this.modified=modified;
		if(owner!=null)
			setBounds((owner.getX()+owner.getWidth())/3, (owner.getY()+owner.getHeight())/3, 425, 200);
		else
			setBounds(100, 100, 425, 200);
		setResizable(false);
		titolo=new JLabel();
		nome = new JLabel("Nome");
		prioritLabel = new JLabel("Priorità");
		annoLabel = new JLabel("Anno");
		meseLabel = new JLabel("Mese");
		dayLabel = new JLabel("Giorno");
		oraLabel = new JLabel("Ora");
		minutoLabel = new JLabel("Minuto");
		if(modified){
			setContentPane(new ColoredPanel("./src/graphic/wallpapers/shadow.jpg"));
			titolo.setText("Modifica memo");
			Color color=Color.WHITE;
			titolo.setForeground(color);
			nome.setForeground(color);
			prioritLabel.setForeground(color);
			annoLabel.setForeground(color);
			meseLabel.setForeground(color);
			dayLabel.setForeground(color);
			oraLabel.setForeground(color);
			minutoLabel.setForeground(color);
		}
		else{
			setContentPane(new ColoredPanel("./src/graphic/wallpapers/new.jpg"));
			titolo.setText("Aggiungi memo");
			titolo.setForeground(new Color(255,255,150));
		}
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setOpaque(false);
		getContentPane().add(contentPanel, BorderLayout.NORTH);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] {30, 0, 0, 0, 30, 0, 30, 30, 30};
		gbl_contentPanel.rowHeights = new int[] {30, 30, 30, 30, 30};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 1.0, 0.0};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		contentPanel.setLayout(gbl_contentPanel);
		oggi=new Data();
		{
			GridBagConstraints gbc_titolo = new GridBagConstraints();
			gbc_titolo.gridwidth = 3;
			gbc_titolo.insets = new Insets(0, 0, 5, 5);
			gbc_titolo.gridx = 3;
			gbc_titolo.gridy = 0;
			contentPanel.add(titolo, gbc_titolo);
		}
		{
			GridBagConstraints gbc_nome = new GridBagConstraints();
			gbc_nome.anchor = GridBagConstraints.EAST;
			gbc_nome.insets = new Insets(0, 0, 5, 5);
			gbc_nome.gridx = 1;
			gbc_nome.gridy = 1;
			contentPanel.add(nome, gbc_nome);
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
			GridBagConstraints gbc_prioritLabel = new GridBagConstraints();
			gbc_prioritLabel.anchor = GridBagConstraints.EAST;
			gbc_prioritLabel.insets = new Insets(0, 0, 5, 5);
			gbc_prioritLabel.gridx = 1;
			gbc_prioritLabel.gridy = 2;
			contentPanel.add(prioritLabel, gbc_prioritLabel);
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
			
			GridBagConstraints gbc_annoLabel = new GridBagConstraints();
			gbc_annoLabel.anchor = GridBagConstraints.EAST;
			gbc_annoLabel.insets = new Insets(0, 0, 5, 5);
			gbc_annoLabel.gridx = 5;
			gbc_annoLabel.gridy = 2;
			contentPanel.add(annoLabel, gbc_annoLabel);
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
			
			GridBagConstraints gbc_meseLabel = new GridBagConstraints();
			gbc_meseLabel.anchor = GridBagConstraints.EAST;
			gbc_meseLabel.insets = new Insets(0, 0, 5, 5);
			gbc_meseLabel.gridx = 1;
			gbc_meseLabel.gridy = 3;
			contentPanel.add(meseLabel, gbc_meseLabel);
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
			
			GridBagConstraints gbc_dayLabel = new GridBagConstraints();
			gbc_dayLabel.anchor = GridBagConstraints.EAST;
			gbc_dayLabel.insets = new Insets(0, 0, 5, 5);
			gbc_dayLabel.gridx = 5;
			gbc_dayLabel.gridy = 3;
			contentPanel.add(dayLabel, gbc_dayLabel);
		}
		{	
			manageDay();
		}
		{
			
			GridBagConstraints gbc_oraLabel = new GridBagConstraints();
			gbc_oraLabel.anchor = GridBagConstraints.EAST;
			gbc_oraLabel.insets = new Insets(0, 0, 0, 5);
			gbc_oraLabel.gridx = 1;
			gbc_oraLabel.gridy = 4;
			contentPanel.add(oraLabel, gbc_oraLabel);
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
			
			GridBagConstraints gbc_minutoLabel = new GridBagConstraints();
			gbc_minutoLabel.anchor = GridBagConstraints.EAST;
			gbc_minutoLabel.insets = new Insets(0, 0, 0, 5);
			gbc_minutoLabel.gridx = 5;
			gbc_minutoLabel.gridy = 4;
			contentPanel.add(minutoLabel, gbc_minutoLabel);
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
			buttonPane.setOpaque(false);
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
			created=new Memo(desc,pri2,y,m,d,o,mi);
			if(modified)
				created.setIcon(old.getIcon());
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