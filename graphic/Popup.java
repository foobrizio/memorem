package graphic;

import graphic.MemoremGUI.Lang;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.apache.commons.codec.digest.DigestUtils;

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
	private boolean okPressed,modified;
	private Memo created,old;
	private Data data;
	private JLabel titolo,nome,prioritLabel,annoLabel,meseLabel,dayLabel,oraLabel,minutoLabel;
	private JLabel avviso;
	private Lang language;
	
	private final Color myYellow=new Color(255,255,150);
	private final Font comic=new Font("Comic Sans MS", Font.BOLD, 12);
	
	private final String[] prioritaIT={ "Bassa", "Normale", "Alta"};
	private final String[] prioritaEN={ "Low", "Normal", "High"};
	private final String[] prioritaES={ "Baja", "Media", "Alta"};
	private final String[] prioritaDE={ "Hoch", "Mittel", "Niedrig"};
	private final String mesiIT[]={ "Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"};
	private final String mesiEN[]={ "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
	private final String mesiES[]={ "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
	private final String mesiDE[]={ "Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"};
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Popup dialog = new Popup(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setLanguage(Lang.EN);
			//dialog.modifica(new Memo("",0,new Data()));
			dialog.aggiungi();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog. if modified is true, the popup is opened for a modification of an already existing memo
	 */
	public Popup(JFrame owner){
		
		super(owner,true);
		
		this.language=Lang.EN;								//lingua di default;
		
		this.addWindowListener(new WindowAdapter(){
			
			public void windowDeactivated(WindowEvent wevt){
				
				avviso.setVisible(false);
			}
		});
		
		if(owner!=null)
			setBounds((owner.getX()+owner.getWidth())/3, (owner.getY()+owner.getHeight())/3, 425, 250);
		else
			setBounds(100, 100, 425, 250);
		
		setContentPane(new ColoredPanel("files//wallpapers//new.jpg"));
		setResizable(false);
		titolo=new JLabel();
		titolo.setFont(new Font("Comic Sans MS", Font.BOLD | Font.ITALIC, 14));
		titolo.setHorizontalAlignment(SwingConstants.CENTER);
		nome = new JLabel("Name");
		nome.setFont(comic);
		prioritLabel = new JLabel("Priority");
		prioritLabel.setFont(comic);
		annoLabel = new JLabel("Year");
		annoLabel.setFont(comic);
		meseLabel = new JLabel("Month");
		meseLabel.setFont(comic);
		dayLabel = new JLabel("Day");
		dayLabel.setFont(comic);
		oraLabel = new JLabel("Hour");
		oraLabel.setFont(comic);
		minutoLabel = new JLabel("Minute");
		minutoLabel.setFont(comic);
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setOpaque(false);
		getContentPane().add(contentPanel, BorderLayout.NORTH);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] {30, 0, 0, 0, 30, 0, 30, 30, 30};
		gbl_contentPanel.rowHeights = new int[] {35, 26, 26, 26, 26, 26};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 1.0, 0.0};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		contentPanel.setLayout(gbl_contentPanel);
		GridBagConstraints gbc_titolo = new GridBagConstraints();
		gbc_titolo.gridwidth = 3;
		gbc_titolo.insets = new Insets(0, 0, 5, 5);
		gbc_titolo.gridx = 3;
		gbc_titolo.gridy = 0;
		contentPanel.add(titolo, gbc_titolo);
		
		avviso = new JLabel("You can't add a memo with a date in the past");
		avviso.setForeground(Color.RED);
		avviso.setVisible(false);
		GridBagConstraints gbc_avviso = new GridBagConstraints();
		gbc_avviso.gridwidth = 7;
		gbc_avviso.insets = new Insets(0, 0, 5, 5);
		gbc_avviso.gridx = 1;
		gbc_avviso.gridy = 1;
		contentPanel.add(avviso, gbc_avviso);
		
		GridBagConstraints gbc_nome = new GridBagConstraints();
		gbc_nome.anchor = GridBagConstraints.EAST;
		gbc_nome.insets = new Insets(0, 0, 5, 5);
		gbc_nome.gridx = 1;
		gbc_nome.gridy = 2;
		contentPanel.add(nome, gbc_nome);
		
		descrizione = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.gridwidth = 6;
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 2;
		gbc_textField.gridy = 2;
		contentPanel.add(descrizione, gbc_textField);
		descrizione.setColumns(10);
		
		GridBagConstraints gbc_prioritLabel = new GridBagConstraints();
		gbc_prioritLabel.anchor = GridBagConstraints.EAST;
		gbc_prioritLabel.insets = new Insets(0, 0, 5, 5);
		gbc_prioritLabel.gridx = 1;
		gbc_prioritLabel.gridy = 3;
		contentPanel.add(prioritLabel, gbc_prioritLabel);
		
		managePrior();
				
		GridBagConstraints gbc_annoLabel = new GridBagConstraints();
		gbc_annoLabel.anchor = GridBagConstraints.EAST;
		gbc_annoLabel.insets = new Insets(0, 0, 5, 5);
		gbc_annoLabel.gridx = 5;
		gbc_annoLabel.gridy = 3;
		contentPanel.add(annoLabel, gbc_annoLabel);
		
		data=new Data();
		manageYear(data.anno());
		anno.addActionListener(this);
					
		GridBagConstraints gbc_meseLabel = new GridBagConstraints();
		gbc_meseLabel.anchor = GridBagConstraints.EAST;
		gbc_meseLabel.insets = new Insets(0, 0, 5, 5);
		gbc_meseLabel.gridx = 1;
		gbc_meseLabel.gridy = 4;
		contentPanel.add(meseLabel, gbc_meseLabel);
		
		manageMonth();
		
		//mese=new JComboBox<String>();
					
		GridBagConstraints gbc_dayLabel = new GridBagConstraints();
		gbc_dayLabel.anchor = GridBagConstraints.EAST;
		gbc_dayLabel.insets = new Insets(0, 0, 5, 5);
		gbc_dayLabel.gridx = 5;
		gbc_dayLabel.gridy = 4;
		contentPanel.add(dayLabel, gbc_dayLabel);
		
		manageDay();
		//giorno=new JComboBox<Integer>();
		
		GridBagConstraints gbc_oraLabel = new GridBagConstraints();
		gbc_oraLabel.anchor = GridBagConstraints.EAST;
		gbc_oraLabel.insets = new Insets(0, 0, 0, 5);
		gbc_oraLabel.gridx = 1;
		gbc_oraLabel.gridy = 5;
		contentPanel.add(oraLabel, gbc_oraLabel);
		
		Integer[] ore=new Integer[24];
		for(int i=0;i<24;i++)
			ore[i]=new Integer(i);
		ora = new JComboBox<Integer>(ore);
		//ora=new JComboBox<Integer>();
		//ora.setSelectedIndex(data.ora());
		GridBagConstraints gbc_comboBox_azz = new GridBagConstraints();
		gbc_comboBox_azz.insets = new Insets(0, 0, 0, 5);
		gbc_comboBox_azz.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_azz.gridx = 2;
		gbc_comboBox_azz.gridy = 5;
		contentPanel.add(ora, gbc_comboBox_azz);
		
		GridBagConstraints gbc_minutoLabel = new GridBagConstraints();
		gbc_minutoLabel.anchor = GridBagConstraints.EAST;
		gbc_minutoLabel.insets = new Insets(0, 0, 0, 5);
		gbc_minutoLabel.gridx = 5;
		gbc_minutoLabel.gridy = 5;
		contentPanel.add(minutoLabel, gbc_minutoLabel);

		Integer[] minuti=new Integer[60];
		for(int i=0;i<60;i++)
			minuti[i]=new Integer(i);
		minuto = new JComboBox<Integer>(minuti);
		//minuto=new JComboBox<Integer>();
		//minuto.setSelectedItem(data.minuto()-1);
		GridBagConstraints gbc_comboBox_min = new GridBagConstraints();
		gbc_comboBox_min.gridwidth = 2;
		gbc_comboBox_min.insets = new Insets(0, 0, 0, 5);
		gbc_comboBox_min.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_min.gridx = 6;
		gbc_comboBox_min.gridy = 5;
		contentPanel.add(minuto, gbc_comboBox_min);

		MouseAdapter mouseFocus=new MouseAdapter(){
			
			public void mouseEntered(MouseEvent evt){
				
				((JButton)evt.getSource()).setBorderPainted(true);
			}
			public void mouseExited(MouseEvent evt){
				
				((JButton)evt.getSource()).setBorderPainted(false);
				((JButton)evt.getSource()).setFocusPainted(false);
				Popup.this.repaint();
			}
		};
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonPane.setOpaque(false);
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		okButton = new JButton("OK");
		okButton.setActionCommand("OK");
		okButton.setBackground(new Color(0,0,0,0));
		buttonPane.add(okButton);
		okButton.setBorderPainted(false);
		getRootPane().setDefaultButton(okButton);
		okButton.addActionListener(this);
		okButton.addMouseListener(mouseFocus);
		okButton.setFont(comic);
	
		cancelButton = new JButton("Cancel");
		cancelButton.setBackground(new Color(0,0,0,0));
		cancelButton.setActionCommand("Cancel");
		cancelButton.setBorderPainted(false);
		buttonPane.add(cancelButton);
		cancelButton.addActionListener(this);
		cancelButton.addMouseListener(mouseFocus);
		cancelButton.setFont(comic);
	
	}
	private void addToolTips(boolean modifica){
		
		if(!modifica){
			if(language==Lang.IT){
				minuto.setToolTipText("Seleziona qui il minuto di scadenza del memo");
				descrizione.setToolTipText("Inserisci qui una breve descrizione del memo");
				prior.setToolTipText("Seleziona qui il livello di priorità");
				anno.setToolTipText("Seleziona qui l'anno di scadenza del memo");
				mese.setToolTipText("Seleziona qui il mese di scadenza del memo");
				giorno.setToolTipText("Seleziona qui il giorno di scadenza del memo");
				ora.setToolTipText("Seleziona qui l'ora di scadenza del memo");
			}
			else if(language==Lang.ES){
				minuto.setToolTipText("Seleccionar el minuto de fechas aquí");
				descrizione.setToolTipText("Introduzca una breve descripción de la nota aquí");
				prior.setToolTipText("Seleccionar el nivel de prioridad aquí");
				anno.setToolTipText("Seleccionar año de expiración aquí");
				mese.setToolTipText("Seleccionar mese de expiración aquí");
				giorno.setToolTipText("Seleccionar día de expiración aquí");
				ora.setToolTipText("Seleccionar hora de expiración aquí");
			}
			else if(language==Lang.DE){
				minuto.setToolTipText("Wählen Ablauf Minute hier");
				descrizione.setToolTipText("Legen Sie eine kurze Beschreibung der Memo hier");
				prior.setToolTipText("Wählen Sie die Prioritätsstufe hier");
				anno.setToolTipText("Wählen Ablauf Jahr hier");
				mese.setToolTipText("Wählen Ablauf Monat hier");
				giorno.setToolTipText("Wählen Ablauf Tag hier");
				ora.setToolTipText("Wählen Ablauf Stunde hier");
			}
			else{
				minuto.setToolTipText("Select expiry minute here");
				descrizione.setToolTipText("Insert a brief description of memo here");
				prior.setToolTipText("Select the level of priority here");
				anno.setToolTipText("Select expiry year here");
				mese.setToolTipText("Select expiry month here");
				giorno.setToolTipText("Select expiry day here");
				ora.setToolTipText("Select expiry hour here");
			}
		}
		else{
			if(language==Lang.IT){
				descrizione.setToolTipText("La descrizione del memo");
				prior.setToolTipText("Il livello di priorità del memo");
				anno.setToolTipText("L'anno di scadenza");
				mese.setToolTipText("Il mese di scadenza");
				giorno.setToolTipText("Il giorno di scadenza");
				ora.setToolTipText("L'ora di scadenza");
				minuto.setToolTipText("Il minuto di scadenza");
			}
			else if(language==Lang.DE){
				descrizione.setToolTipText("Memo Beschreibung");
				prior.setToolTipText("Prioritätsstufe Memos");
				anno.setToolTipText("Ablauf Jahr Memos");
				mese.setToolTipText("Verfallmonats Memos");
				giorno.setToolTipText("Memos Verfallstag");
				ora.setToolTipText("Memos Ablauf Stunde");
				minuto.setToolTipText("Memos Ablauf Minute");
			}
			else if(language==Lang.ES){
				descrizione.setToolTipText("Descripción de Memo");
				prior.setToolTipText("Memo's priority level");
				anno.setToolTipText("Memo's expiry year");
				mese.setToolTipText("Memo's expiry month");
				giorno.setToolTipText("Memo's expiry day");
				ora.setToolTipText("Memo's expiry hour");
				minuto.setToolTipText("Memo's expiry minute");
			}
			else{
				descrizione.setToolTipText("Memo's description");
				prior.setToolTipText("El nivel de prioridad del memo");
				anno.setToolTipText("El año de vencimiento");
				mese.setToolTipText("El mes de vencimiento");
				giorno.setToolTipText("El día de vencimiento");
				ora.setToolTipText("La hora de vencimiento");
				minuto.setToolTipText("La expiración minutos");
			}
		}
		
	}
	
	private void managePrior(){
		
		if(language==Lang.IT)
			prior=new JComboBox<String>(prioritaIT);
		else if(language==Lang.ES)
			prior=new JComboBox<String>(prioritaES);
		else if(language==Lang.DE)
			prior=new JComboBox<String>(prioritaDE);
		else
			prior=new JComboBox<String>(prioritaEN);
		
		prior.setSelectedIndex(1);	//in questo modo la scelta di default è "normale"
		GridBagConstraints gbc_comboBox_2 = new GridBagConstraints();
		gbc_comboBox_2.gridwidth = 3;
		gbc_comboBox_2.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_2.gridx = 2;
		gbc_comboBox_2.gridy = 3;
		contentPanel.add(prior, gbc_comboBox_2);
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
		gbc_comboBox.gridy = 3;
		contentPanel.add(anno, gbc_comboBox);
		manageMonth();
	}
	
	private void manageMonth(){
		
		
		if(mese==null){
			if(language==Lang.IT)
				mese=new JComboBox<String>(mesiIT);
			else if(language==Lang.ES)
				mese=new JComboBox<String>(mesiES);
			else if(language==Lang.DE)
				mese=new JComboBox<String>(mesiDE);
			else
				mese=new JComboBox<String>(mesiEN);
			
			this.mese.setSelectedIndex(this.data.mese()-1);
			mese.addActionListener(this);
			GridBagConstraints gbc_comboBox_mese = new GridBagConstraints();
			gbc_comboBox_mese.gridwidth = 2;
			gbc_comboBox_mese.insets = new Insets(0, 0, 5, 5);
			gbc_comboBox_mese.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBox_mese.gridx = 2;
			gbc_comboBox_mese.gridy = 4;
			mese.setVisible(true);
			contentPanel.add(mese, gbc_comboBox_mese);
		}
		else{
			int x=mese.getSelectedIndex();
			this.mese.setSelectedIndex(x);
			
		}
		//this.mese = new JComboBox<String>(mesi);
		
		
		//mese.setSelectedIndex(data.mese()-1);
		manageDay();
	}
	
	private void manageDay(){
		
		Integer[] giorni;
		String gdms=(String)mese.getSelectedItem();
		int gdm=Data.daysOfMonth((Integer)anno.getSelectedItem(),Data.monthToInt(gdms,language));
		giorni=new Integer[gdm];
		for(int i=0;i<gdm;i++){
			giorni[i]=new Integer(i+1);
		}
		if(giorno==null){
			giorno=new JComboBox<Integer>(giorni);
			this.giorno.setSelectedIndex(this.data.giorno()-1);
		}
		else{
			int x=giorno.getSelectedIndex()+1;
			contentPanel.remove(giorno);
			this.giorno = new JComboBox<Integer>(giorni);
			//giorno=new JComboBox<Ihis.data.giorno()nteger>();
			int durata=Data.daysOfMonth((Integer)anno.getSelectedItem(), mese.getSelectedIndex()+1);
			//System.out.println(anno.getSelectedItem());
			//System.out.println(durata);
			//System.out.println("Durata di "+mese.getSelectedItem()+": "+durata);
			//System.out.println("Giorno selezionato:"+x);
			if(durata<x)
				this.giorno.setSelectedIndex(durata-1);
			else
				this.giorno.setSelectedIndex(x-1);
			GridBagConstraints gbc_comboBox_1 = new GridBagConstraints();
			gbc_comboBox_1.gridwidth = 2;
			gbc_comboBox_1.insets = new Insets(0, 0, 5, 5);
			gbc_comboBox_1.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBox_1.gridx = 6;
			gbc_comboBox_1.gridy = 4;
			giorno.setVisible(true);
			contentPanel.add(giorno, gbc_comboBox_1);
			this.revalidate();
			repaint();
		}
	}
	
	public void actionPerformed(ActionEvent evt){
		
		if(evt.getSource()==anno || evt.getSource()==mese){
			
			Popup.this.manageDay();
		}
		
		else if(evt.getSource().equals(okButton)){
			
			String desc=descrizione.getText();
			if(desc.length()>255){
				avviso.setVisible(true);
				if(language==Lang.IT)
					avviso.setText("La descrizione non può superare i 255 caratteri");
				else if(language==Lang.DE)
					avviso.setText("Beschreibung kann nicht überholen 255 Zeichen");
				else if(language==Lang.ES)
					avviso.setText("La descripción no puede superar 255 caracteres");
				else
					avviso.setText("Description can't be more than 255 characters");
				descrizione.setText("");
				return;
			}
			int pri2=prior.getSelectedIndex();
			int d=(Integer)giorno.getSelectedItem();
			System.out.println(d);
			int m=Data.monthToInt((String)mese.getSelectedItem(),language);	
			int y=(Integer)anno.getSelectedItem();
			int o=(Integer)ora.getSelectedItem();
			int mi=(Integer)minuto.getSelectedItem();
			String id=null,icon=null;
			Data end=new Data(y,m,d,o,mi);
			if(modified){
				id=old.getId();
				icon=old.getIcon();
			}
			else{
				id=DigestUtils.shaHex(desc+end.toString());
				icon="note.png";
			}
			
			System.out.println(end);
			if(end.compareTo(new Data())>0){
				avviso.setVisible(false);
				okPressed=true;
				if(modified)
					created=new Memo(desc,pri2,end,icon,id);
				else
					created=new Memo(desc,pri2,end);
				created.setPure(false);
				if(modified)
					created.setIcon(old.getIcon());
				setVisible(false);
			}
			else{
				if(language==Lang.IT)
					avviso.setText("Non puoi aggiungere un memo con scadenza nel passato");
				else if(language==Lang.DE)
					avviso.setText("Sie können ein Memo in der Vergangenheit nicht hinzufügen");
				else if(language==Lang.ES)
					avviso.setText("No se puede añadir una nota en el pasado");
				else
					avviso.setText("You can't add a memo in the past");
				avviso.setVisible(true);
			}
		}
		else if(evt.getSource().equals(cancelButton)){
			
			okPressed=false;
			created=null;
			dispose();
		}
	}

	public void aggiungi(){
		
		data=new Data();
		modified=false;
		if(language==Lang.IT)
			titolo.setText("Aggiungi memo");
		else if(language==Lang.ES)
			titolo.setText("Añadir memo");
		else if(language==Lang.DE)
			titolo.setText("Memo hinzufügen");
		else
			titolo.setText("Add memo");
		descrizione.setText("");
		prior.setSelectedIndex(1);
		anno.setSelectedItem(data.anno());
		mese.setSelectedIndex(data.mese()-1);
		giorno.setSelectedItem(data.giorno());
		ora.setSelectedItem(data.ora());
		minuto.setSelectedItem(data.minuto());
		addToolTips(false);
		((ColoredPanel)getContentPane()).setSfondo("files//wallpapers//new.jpg");
		//titolo.setForeground(new Color(255,255,150));
		titolo.setForeground(myYellow);
		nome.setForeground(Color.BLACK);
		okButton.setForeground(Color.BLACK);
		cancelButton.setForeground(Color.BLACK);
		prioritLabel.setForeground(Color.BLACK);
		annoLabel.setForeground(Color.BLACK);
		meseLabel.setForeground(Color.BLACK);
		dayLabel.setForeground(Color.BLACK);
		oraLabel.setForeground(Color.BLACK);
		minutoLabel.setForeground(Color.BLACK);
		setVisible(true);
	}

	public Memo getCreated(){
		
		return created;
	}

	public boolean getModified(){
		
		return modified;
	}

	public Memo getOld(){
		
		return old;
	}

	public boolean isOk(){
		
		return okPressed;
	}

	public void modifica(Memo m){
		
		modified=true;
		this.old=m;
		((ColoredPanel)getContentPane()).setSfondo("files//wallpapers//shadow.jpg");
		if(language==Lang.IT || language==Lang.ES)
			titolo.setText("Modifica memo");
		else if(language==Lang.DE)
			titolo.setText("Memo ändern");
		else
			titolo.setText("Modify memo");
		Color color=Color.WHITE;
		this.data=m.getEnd();
		manageYear(data.anno());
		descrizione.setText(m.description());
		prior.setSelectedIndex(m.priority());
		anno.setSelectedItem(data.anno());
		mese.setSelectedIndex(data.mese()-1);
		giorno.setSelectedItem(data.giorno());
		ora.setSelectedItem(data.ora());
		minuto.setSelectedItem(data.minuto());
		okButton.setForeground(color);
		cancelButton.setForeground(color);
		titolo.setForeground(color);
		nome.setForeground(color);
		prioritLabel.setForeground(color);
		annoLabel.setForeground(color);
		meseLabel.setForeground(color);
		dayLabel.setForeground(color);
		oraLabel.setForeground(color);
		minutoLabel.setForeground(color);
		addToolTips(true);
		setVisible(true);
	}
	
	public void setLanguage(Lang language){
		
		if(this.language.equals(language))
			return;
		this.language=language;
		if(language==Lang.IT){
			
			prioritLabel.setText("Priorità");
			nome.setText("Nome");
			annoLabel.setText("Anno");
			meseLabel.setText("Mese");
			dayLabel.setText("Giorno");
			oraLabel.setText("Ora");
			minutoLabel.setText("Minuto");
			contentPanel.remove(mese);
			contentPanel.remove(prior);
		}
		else if(language==Lang.EN){
			
			prioritLabel.setText("Priorität");
			nome.setText("Vorname");
			annoLabel.setText("Jahr");
			meseLabel.setText("Monat");
			dayLabel.setText("Tag");
			oraLabel.setText("Stunde");
			minutoLabel.setText("Minute");
			contentPanel.remove(mese);
			contentPanel.remove(prior);	
		}
		else if(language==Lang.ES){
		
			prioritLabel.setText("Prioridad");
			nome.setText("Nombre");
			annoLabel.setText("Año");
			meseLabel.setText("Mes");
			dayLabel.setText("Día");
			oraLabel.setText("Hora");
			minutoLabel.setText("Minuto");
			contentPanel.remove(mese);
			contentPanel.remove(prior);
		}
		else{
			prioritLabel.setText("Priority");
			nome.setText("Name");
			annoLabel.setText("Year");
			meseLabel.setText("Month");
			dayLabel.setText("Day");
			oraLabel.setText("Hour");
			minutoLabel.setText("Minute");
			contentPanel.remove(mese);
			contentPanel.remove(prior);
		}
		mese=null;
		managePrior();
		manageMonth();
	}
}