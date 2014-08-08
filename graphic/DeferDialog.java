package graphic;

import graphic.MemoremGUI.Lang;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import main.Memo;
import util.Data;
import util.MemoList;

@SuppressWarnings("serial")
public class DeferDialog extends JDialog implements ActionListener{

	private final JPanel contentPanel = new JPanel();
	private final Color myYellow=new Color(255,255,150);
	private final Color myBlue=new Color(150,200,255);
	private final Color myRed=new Color(255,120,100);
	private final Font comic=new Font("Comic Sans MS", Font.BOLD, 12);
	
	private Mouse mouse;
	private JButton vediDopo,chiudi;
	private MemoList ml,handled;
	private int pages;
	private int curPage;
	private JLabel frecciaS,frecciaD,pageLabel;
	private JLabel descLabel;
	private JLabel lblScadutoIl,lblAlle;
	private JLabel dateLabel,timeLabel;
	private JLabel completa,archivia,rinvia;
	private JMenuItem one,three,seven,month;
	private Popopo popopo;
	private Lang lang;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			MemoList ml=new MemoList();
			Memo one=new Memo("Canapisa!!!","high",2014,5,31,16,0);
			Memo two=new Memo("vigilia Canapisa",2014,5,30,16,0);
			Memo three=new Memo("antivigilia Canapisa","low",2014,5,29,16,0);
			Memo past=new Memo("questo è vecchio",2014,2,21,13,0);
			Memo prova2=new Memo("prova2",2014,7,17,0,0);
			ml.add(one);ml.add(two);
			ml.add(three);
			ml.add(past);
			ml.add(prova2);
			DeferDialog dialog = new DeferDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.gestisci(ml);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DeferDialog(JFrame owner) {
		
		super(owner,true);
		setBounds(100, 100, 550, 350);
		setResizable(false);
		this.lang=Lang.EN;
		setContentPane(new ColoredPanel("./src/graphic/wallpapers/desk.jpg"));
		UIManager.getLookAndFeelDefaults().put("Label.font", comic );
		handled=new MemoList();
		mouse=new Mouse();
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setOpaque(false);
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		SpringLayout sl_contentPanel = new SpringLayout();
		contentPanel.setLayout(sl_contentPanel);
		
		frecciaS = new JLabel("«««");
		frecciaS.setFont(new Font("Comic Sans MS", Font.BOLD | Font.ITALIC, 16));
		frecciaS.addMouseListener(mouse);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, frecciaS, 10, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, frecciaS, 10, SpringLayout.WEST, contentPanel);
		contentPanel.add(frecciaS);
		
		frecciaD = new JLabel("»»»");
		frecciaD.setFont(new Font("Comic Sans MS", Font.BOLD | Font.ITALIC, 16));
		frecciaD.addMouseListener(mouse);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, frecciaD, 0, SpringLayout.NORTH, frecciaS);
		sl_contentPanel.putConstraint(SpringLayout.EAST, frecciaD, -10, SpringLayout.EAST, contentPanel);
		contentPanel.add(frecciaD);
		{
			pageLabel = new JLabel("1/1");
			sl_contentPanel.putConstraint(SpringLayout.NORTH, pageLabel, 0, SpringLayout.NORTH, frecciaS);
			sl_contentPanel.putConstraint(SpringLayout.WEST, pageLabel, 170, SpringLayout.EAST, frecciaS);
			sl_contentPanel.putConstraint(SpringLayout.EAST, pageLabel, -176, SpringLayout.WEST, frecciaD);
			pageLabel.setFont(new Font("Comic Sans MS", Font.BOLD | Font.ITALIC, 16));
			pageLabel.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(pageLabel);
		}
		{
			descLabel = new JLabel("description");
			sl_contentPanel.putConstraint(SpringLayout.EAST, descLabel, 0, SpringLayout.EAST, frecciaD);
			descLabel.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 16));
			sl_contentPanel.putConstraint(SpringLayout.NORTH, descLabel, 33, SpringLayout.SOUTH, frecciaS);
			sl_contentPanel.putConstraint(SpringLayout.WEST, descLabel, 0, SpringLayout.WEST, frecciaS);
			descLabel.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(descLabel);
		}
		{
			lblScadutoIl = new JLabel("expired on:");
			sl_contentPanel.putConstraint(SpringLayout.NORTH, lblScadutoIl, 108, SpringLayout.SOUTH, descLabel);
			lblScadutoIl.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
			contentPanel.add(lblScadutoIl);
		}
		{
			dateLabel = new JLabel("data");
			sl_contentPanel.putConstraint(SpringLayout.NORTH, dateLabel, 0, SpringLayout.NORTH, lblScadutoIl);
			sl_contentPanel.putConstraint(SpringLayout.EAST, dateLabel, -50, SpringLayout.EAST, contentPanel);
			dateLabel.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 16));
			contentPanel.add(dateLabel);
		}
		{
			lblAlle = new JLabel("at:");
			sl_contentPanel.putConstraint(SpringLayout.NORTH, lblAlle, 16, SpringLayout.SOUTH, lblScadutoIl);
			sl_contentPanel.putConstraint(SpringLayout.EAST, lblScadutoIl, 0, SpringLayout.EAST, lblAlle);
			sl_contentPanel.putConstraint(SpringLayout.WEST, lblAlle, 66, SpringLayout.WEST, contentPanel);
			lblAlle.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
			lblAlle.setHorizontalAlignment(SwingConstants.RIGHT);
			contentPanel.add(lblAlle);
		}
		{
			timeLabel = new JLabel("hour");
			sl_contentPanel.putConstraint(SpringLayout.WEST, timeLabel, 45, SpringLayout.EAST, lblAlle);
			sl_contentPanel.putConstraint(SpringLayout.EAST, timeLabel, -50, SpringLayout.EAST, contentPanel);
			sl_contentPanel.putConstraint(SpringLayout.WEST, dateLabel, 0, SpringLayout.WEST, timeLabel);
			sl_contentPanel.putConstraint(SpringLayout.NORTH, timeLabel, 0, SpringLayout.NORTH, lblAlle);
			timeLabel.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 16));
			contentPanel.add(timeLabel);
		}
		
		Component horizontalGlue = Box.createHorizontalGlue();
		contentPanel.add(horizontalGlue);
		
		completa= new JLabel("complete");
		completa.setFont(comic);
		sl_contentPanel.putConstraint(SpringLayout.EAST, completa, 0, SpringLayout.EAST, lblScadutoIl);
		completa.addMouseListener(mouse);
		
		contentPanel.add(completa);
		
		archivia = new JLabel("store");
		archivia.setFont(comic);
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, archivia, -10, SpringLayout.SOUTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, completa, 0, SpringLayout.NORTH, archivia);
		sl_contentPanel.putConstraint(SpringLayout.WEST, archivia, 0, SpringLayout.WEST, dateLabel);
		archivia.addMouseListener(mouse);
		
		contentPanel.add(archivia);
		
		rinvia = new JLabel("delay");
		rinvia.setFont(comic);
		
		sl_contentPanel.putConstraint(SpringLayout.NORTH, rinvia, 0, SpringLayout.NORTH, completa);
		sl_contentPanel.putConstraint(SpringLayout.WEST, rinvia, 54, SpringLayout.EAST, archivia);
		rinvia.addMouseListener(mouse);
		contentPanel.add(rinvia);
		
	
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setOpaque(false);
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			
			vediDopo = new JButton("See later");
			vediDopo.setFont(comic);
			vediDopo.setOpaque(false);
			vediDopo.setBackground(new Color(0,0,0,0));
			vediDopo.setBorderPainted(false);
			vediDopo.setForeground(myBlue);
			
			getRootPane().setDefaultButton(vediDopo);
			vediDopo.addActionListener(this);
			vediDopo.setFocusPainted(false);
			vediDopo.addMouseListener(mouse);
			//vediDopo.setActionCommand("Cancel");
			buttonPane.add(vediDopo);

			
			chiudi = new JButton("Close");
			chiudi.setOpaque(false);
			chiudi.setBackground(new Color(0,0,0,0));
			chiudi.setBorderPainted(false);
			chiudi.setForeground(myRed);
			chiudi.setFont(comic);
			
			chiudi.addActionListener(this);
			chiudi.addMouseListener(mouse);
			buttonPane.add(chiudi);
		}
		one=new JMenuItem("Delay at tomorrow");
		three=new JMenuItem("Delay by three days");
		seven=new JMenuItem("Delay by one week");
		month=new JMenuItem("Delay by one month");
		one.addActionListener(this);
		three.addActionListener(this);
		seven.addActionListener(this);
		month.addActionListener(this);
		addToolTips();
	}
	
	private void addToolTips(){
		if(lang==Lang.IT){
			chiudi.setToolTipText("Chiude la finestra");
			vediDopo.setToolTipText("Rimanda la decisione ad un secondo momento");
			rinvia.setToolTipText("Clicca se hai bisogno più tempo per i tuoi impegni");
			completa.setToolTipText("Clicca e aggiungi un altro successo allo storico dei tuoi impegni");
			archivia.setToolTipText("Non sei riuscito a completare questo memo?");
			frecciaD.setToolTipText("Scorre a destra");
			frecciaS.setToolTipText("Scorre a sinistra");
		}
		else if(lang==Lang.ES){
			chiudi.setToolTipText("Cerrar la ventana");
			vediDopo.setToolTipText("Posponer la decisión de un momento posterior");
			rinvia.setToolTipText("Haga clic si necesita más tiempo");
			completa.setToolTipText("Haga clic y añadir otro éxito !!");
			archivia.setToolTipText("¿No se llega a completar esto?");
			frecciaD.setToolTipText("Avanza a la derecha");
			frecciaS.setToolTipText("Avanza a la izquierda");
		}
		else if(lang==Lang.DE){
			chiudi.setToolTipText("Schließen Sie das Fenster");
			vediDopo.setToolTipText("Verschieben Entscheidung auf einen späteren Zeitpunkt");
			rinvia.setToolTipText("Klicken Sie auf, wenn Sie mehr Zeit benötigen");
			completa.setToolTipText("Klicken Sie auf und fügen Sie einen weiteren Erfolg !!");
			archivia.setToolTipText("Hast du nicht zu erreichen, um diese zu vervollständigen?");
			frecciaD.setToolTipText("blättern Sie nach rechts");
			frecciaS.setToolTipText("blättern Sie nach links");
		}
		else{
			chiudi.setToolTipText("Close the window");
			vediDopo.setToolTipText("Postpone decision to a later time");
			rinvia.setToolTipText("Click if you need more time");
			completa.setToolTipText("Click and add another success!!");
			archivia.setToolTipText("Didn't you reach to complete this?");
			frecciaD.setToolTipText("Scroll right");
			frecciaS.setToolTipText("Scroll left");
		}
	}
	private void aggiornaLabels(){
		
		pages=ml.size();
		if(pages==0){
			setVisible(false);
			return;
		}
		if(curPage>pages)
			curPage=pages;
		pageLabel.setText(curPage+"/"+pages);
		if(curPage==pages)
			frecciaD.setVisible(false);
		else
			frecciaD.setVisible(true);
		if(curPage==1)
			frecciaS.setVisible(false);
		else
			frecciaS.setVisible(true);
		Memo cur=ml.get(curPage-1);
		descLabel.setText("\""+cur.description()+"\"");
		dateLabel.setText(cur.getEnd().getData());
		timeLabel.setText(cur.getEnd().getOra());
		setColors(cur.priority());
	}

	private void prepareInterface(){
		
		if(ml.size()==1)
			frecciaD.setVisible(false);
		frecciaS.setVisible(false);
		curPage=1;
		pages=ml.size();
		pageLabel.setText("1/"+pages);
		popopo=new Popopo();
		popopo.colora();
		setPopupMenu();
		aggiornaLabels();
	}

	private void setColors(int prior){
		
		frecciaS.setForeground(Color.WHITE.darker());
		frecciaD.setForeground(Color.WHITE.darker());
		lblScadutoIl.setForeground(Color.WHITE.darker());
		pageLabel.setForeground(Color.WHITE.darker());
		lblAlle.setForeground(Color.WHITE.darker());
		archivia.setForeground(Color.WHITE.darker());
		completa.setForeground(Color.WHITE.darker());
		frecciaS.setForeground(Color.WHITE.darker());
		rinvia.setForeground(Color.WHITE.darker());
		Color c=Color.BLACK;
		switch(prior){
		case 0: c=myBlue; 	break;
		case 1: c=myYellow; break;
		case 2: c=myRed;	break;
		}
		descLabel.setForeground(c);
		dateLabel.setForeground(c);
		timeLabel.setForeground(c);
		popopo.colora();
	}

	private void setPopupMenu(){
		
		popopo.add(one);
		popopo.add(three);
		popopo.add(seven);
		popopo.add(month);
	}

	public void gestisci(MemoList mmm){
		
		
		handled.clear();
		if(this.ml==null){
			if(mmm.size()==0)
				return;
			this.ml=new MemoList(mmm);
			pages=mmm.size();
			prepareInterface();
			setVisible(true);
		}
		else{
			for(Memo m:mmm)
				if(!ml.contains(m))
					ml.add(m);
			pages=ml.size();
			prepareInterface();
			setVisible(true);
		}
	}
	public MemoList getHandled(){
		
		return handled;
	}
	
	public void setLanguage(Lang lang){
		
		if(this.lang==lang)
			return;	
		if(lang==Lang.IT){
			lblScadutoIl.setText("scaduto il:");
			lblAlle.setText("alle:");
			completa.setText("completa");
			archivia.setText("archivia");
			rinvia.setText("rinvia");
			vediDopo.setText("Vedi dopo");
			chiudi.setText("Chiudi");
			one.setText("Rinvia a domani");
			three.setText("Rinvia di tre giorni");
			seven.setText("Rinvia di una settimana");
			month.setText("Rinvia di un mese");
		}
		else if(lang==Lang.ES){
			lblScadutoIl.setText("expirado el:");
			lblAlle.setText("A las:");
			completa.setText("completa");
			archivia.setText("tienda");
			rinvia.setText("pospone");
			vediDopo.setText("Ve despues");
			chiudi.setText("Cerra");
			one.setText("Refiere hasta mañana");
			three.setText("Refiere tres días");
			seven.setText("Refiere a una semana");
			month.setText("Refiere a un mes");
		}
		else if(lang==Lang.DE){
			lblScadutoIl.setText("lief am:");
			lblAlle.setText("um:");
			completa.setText("komplett");
			archivia.setText("dpeicher");
			rinvia.setText("verzögerung");
			vediDopo.setText("Siehe später");
			chiudi.setText("Schließen");
			one.setText("Verzögerung am morgen");
			three.setText("Verzögerung um drei Tage");
			seven.setText("Verzögerung um eine Woche");
			month.setText("Verzögerung um einen Monat");
		}
		else{
			lblScadutoIl.setText("expired on:");
			lblAlle.setText("at:");
			completa.setText("complete");
			archivia.setText("store");
			rinvia.setText("delay");
			vediDopo.setText("See later");
			chiudi.setText("Close");
			one.setText("Delay at tomorrow");
			three.setText("Delay by three days");
			seven.setText("Delay by one week");
			month.setText("Delay by one month");
		}
		addToolTips();
		this.lang=lang;
	}
	
	private class Mouse extends MouseAdapter{
		
		public void mouseClicked(MouseEvent evt){
			
			if(evt.getSource()==frecciaS){
				
				curPage--;
				if(curPage<pages)
					frecciaD.setVisible(true);
				pageLabel.setText(curPage+"/"+pages);
				if(curPage==1)
					frecciaS.setVisible(false);
			}
			else if(evt.getSource()==frecciaD){
				
				curPage++;
				if(curPage>1)
					frecciaS.setVisible(true);
				pageLabel.setText(curPage+"/"+pages);
				if(curPage==pages)
					frecciaD.setVisible(false);
			}
			else if(evt.getSource()==completa){
				
				Memo m=ml.get(curPage-1);//prendo il memo vis nella pag
				m.spunta();//rendo completato
				handled.add(m);//add in lista dei gestiti 
				ml.remove(m);// remove da memo da gestire
			}
			else if(evt.getSource()==archivia){
				
				Memo m=ml.get(curPage-1);
				m.isNotificato();
				handled.add(m);
				ml.remove(m);
			}
			else if(evt.getSource()==rinvia){
				
				popopo.show(rinvia, evt.getX(), evt.getY());
			}
			aggiornaLabels();
		}
		public void mousePressed(MouseEvent evt){
			
			if(evt.getSource()==frecciaS){
				frecciaS.setForeground(Color.RED);
			}
			else if(evt.getSource()==frecciaD){
				frecciaD.setForeground(Color.RED);
			}
		}
		public void mouseReleased(MouseEvent evt){
			if(evt.getSource()==frecciaS){
				frecciaS.setForeground(Color.GREEN);
			}
			else if(evt.getSource()==frecciaD){
				frecciaD.setForeground(Color.GREEN);
			}
			repaint();
		}
		public void mouseEntered(MouseEvent evt){
			
			if(evt.getSource()==frecciaS)
				frecciaS.setForeground(Color.GREEN);
			else if(evt.getSource()==frecciaD)
				frecciaD.setForeground(Color.GREEN);
			else if(evt.getSource()==completa)
				completa.setForeground(Color.GREEN);
			else if(evt.getSource()==archivia)
				archivia.setForeground(Color.RED);
			else if(evt.getSource()==rinvia)
				rinvia.setForeground(descLabel.getForeground());
			else if(evt.getSource()==vediDopo)
				vediDopo.setBorderPainted(true);
			else if(evt.getSource()==chiudi)
				chiudi.setBorderPainted(true);
		}
		public void mouseExited(MouseEvent evt){
			
			if(evt.getSource()==frecciaS)
				frecciaS.setForeground(Color.WHITE.darker());
			else if(evt.getSource()==frecciaD)
				frecciaD.setForeground(Color.WHITE.darker());
			else if(evt.getSource()==completa)
				completa.setForeground(Color.WHITE.darker());
			else if(evt.getSource()==archivia)
				archivia.setForeground(Color.WHITE.darker());
			else if(evt.getSource()==rinvia)
				rinvia.setForeground(Color.WHITE.darker());
			else if(evt.getSource()==vediDopo)
				vediDopo.setBorderPainted(false);
			else if(evt.getSource()==chiudi)
				chiudi.setBorderPainted(false);
		}
	}
	
	class Popopo extends JPopupMenu{
		
		private Color coloreSfondo;
		
		public Popopo(){
			
			setOpaque(true);
		}
		
		void colora(){
			
			switch(ml.get(curPage-1).priority()){
			case 0: coloreSfondo=myBlue; break;
			case 1: coloreSfondo=myYellow; break;
			case 2: coloreSfondo=myRed; break;
			}
			one.setOpaque(false);
			three.setOpaque(false);
			seven.setOpaque(false);
			month.setOpaque(false);
			this.setBackground(coloreSfondo);
		}
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		if(arg0.getSource()==vediDopo){
			
			ml.remove(ml.get(curPage-1));
			pages--;
			aggiornaLabels();
			return;
		}
		else if(arg0.getSource()==chiudi){
			
			setVisible(false);
			return;
		}
		
		Memo cur=ml.get(curPage-1);
		Data d=new Data();
		
		if(arg0.getSource()==one){
			d=new Data().domani();
			ml.remove(cur);
			cur.setEnd(d.anno(), d.mese(), d.giorno(), d.ora(), d.minuto());
			cur.setRevalued();
			handled.add(cur);
		}
		else if(arg0.getSource()==three){
			for(int i=0;i<3;i++)
				d=d.domani();
			ml.remove(cur);
			cur.setEnd(d.anno(), d.mese(), d.giorno(), d.ora(), d.minuto());
			cur.setRevalued();
			handled.add(cur);
		}
		else if(arg0.getSource()==seven){
			for(int i=0;i<7;i++)
				d=d.domani();
			ml.remove(cur);
			cur.setEnd(d.anno(), d.mese(), d.giorno(), d.ora(), d.minuto());
			cur.setRevalued();
			handled.add(cur);
		}
		else if(arg0.getSource()==month){
			for(int i=0;i<30;i++)
				d=d.domani();
			ml.remove(cur);
			cur.setEnd(d.anno(), d.mese(), d.giorno(), d.ora(), d.minuto());
			cur.setRevalued();
			handled.add(cur);
		}
		aggiornaLabels();
		
	}
}
