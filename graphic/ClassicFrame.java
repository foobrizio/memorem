package graphic;

import graphic.MemoremGUI.Lang;

import javax.swing.*;	//per JPanel e soci

import main.Memo;
import util.MemoList;

import java.awt.*;		//per Color


@SuppressWarnings("serial")
public class ClassicFrame extends JInternalFrame{
	
	private JPanel dynamic;
	private JPanel jp;
	private MemoList personal;
	private JMenuBar menuBar;
	/*
	 * Il visual è così composto:
	 * 			memoAttivi
	 * 			memoScaduti
	 */
	private JMenu visual;
	/*
	 * mnPriorit è così composto:
	 * 			cbm1		// "alta"
	 * 			cbm2		// "media"
	 * 			cbm3		// "bassa"
	 */
	JMenu mnPriorit;
	/*
	 * mnFiltraPerData è così composto
	 * 			today		//	"oggi"
	 * 			week		//	"in settimana"
	 * 			month		// 	"in questo mese"
	 * 			year		//	"in quest'anno"
	 * 			always		//	"sempre"
	 */
	private JMenu mnData;
	private Lang language;

	
	public ClassicFrame(JRadioButtonMenuItem[] vF, JCheckBoxMenuItem[] pF,JRadioButtonMenuItem[] dF){
		
		language=Lang.EN;
		setResizable(false);		//il JInternalFrame non può cambiare forma
		createContentPane();
		//i prossimi tre passaggi servono per eliminare la barra in alto
		//BasicInternalFrameUI ifui=(BasicInternalFrameUI)this.getUI();
		//BasicInternalFrameTitlePane tp=(BasicInternalFrameTitlePane)ifui.getNorthPane();
        menuBar=new JMenuBar();
        personal=new MemoList();
		//tp.add(menuBar);
		//ifui.setNorthPane(tp);
		//this.remove(tp);
		this.setName("Classic Frame");
		jp.setLayout(new GridLayout(0, 1, 0, 0));
		
		dynamic=new JPanel();
		dynamic.setName("dynamic");
		dynamic.setLayout(new BoxLayout(dynamic, BoxLayout.PAGE_AXIS));
		dynamic.setOpaque(false);
		jp.add(dynamic);
		setClosable(false);
		
		setOpaque(false);
		setBackground(new Color(0,0,0,0));
		jp.setBackground(new Color(0,0,0,0));
		
		this.setBorder(null);
		createButtons(vF,pF,dF);
		setJMenuBar(menuBar);
		this.menuBar.setVisible(false);
		//mouseM=new MouseMotion();
		//dynamic.addMouseMotionListener(mouseM);

		//setVisible(true);
	}
	/**
	 * Crea la barra degli strumenti interna
	 * @param vF
	 * @param pF
	 * @param dF
	 */
	private void createButtons(JRadioButtonMenuItem[] vF,JCheckBoxMenuItem[] pF,JRadioButtonMenuItem[] dF){
		
		//visualFilter
		visual=new JMenu("Show");
		visual.add(vF[0]);
		visual.add(vF[1]);
		menuBar.add(visual);
		//priorFilter
		mnPriorit = new JMenu("Filter by priority");
		mnPriorit.add(pF[0]);
		mnPriorit.add(pF[1]);
		mnPriorit.add(pF[2]);
		menuBar.add(mnPriorit);
		//dateFilter
		mnData = new JMenu("Filter by date");
		mnData.add(dF[0]);
		mnData.add(dF[1]);
		mnData.add(dF[2]);
		mnData.add(dF[3]);
		mnData.add(dF[4]);
		mnData.add(dF[5]);
		mnData.add(dF[6]);
		menuBar.add(mnData);
	}
	
	private void createContentPane(){
		jp=new JPanel();
		JScrollPane scroll=new JScrollPane(jp,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setOpaque(false);
		scroll.setBorder(null);
		getContentPane().add(scroll);
		
	}
	
	/**
	 * In questo modo ci assicuriamo che il PanelContainer contenga solamente MemPanel
	 */
	@Override
	public Component add(Component c){		//qui dentro si può aggiungere soltanto MemPanels
		
		if(c instanceof MemPanel){
			((MemPanel)c).setLanguage(language);
			if(!personal.contains(((MemPanel)c).getMemo())){
				personal.add(((MemPanel)c).getMemo(),false);
				int indexOf=personal.indexOf(((MemPanel)c).getMemo());
				Component added=dynamic.add(c,indexOf);
				if(added!=null){
					repaint();
					return added;
				}
				
			}
		}
		return null;
	}
	
	/**
	 * Ritorna il memPanel relativo al memo corrispondente
	 * @param m
	 * @return
	 */
	public MemPanel get(Memo m){
		
		Component[] panels=dynamic.getComponents();
		for(int i=0;i<panels.length;i++){
			MemPanel mp=(MemPanel) panels[i];
			if(mp.getMemo().equals(m))
				return mp;
		}
		return null;
	}
	
	/**
	 * Modifica il MemPanel contenente il vecchio memo, aggiornando i valori nuovi
	 * @param vecchio
	 * @param nuovo
	 */
	public void modifica(Memo vecchio,Memo nuovo){
		
		Component[] panels=dynamic.getComponents();
		boolean found=false;
		MemPanel old=new MemPanel(vecchio);
		for(int i=0;i<panels.length;i++){
			if(!found && panels[i] instanceof MemPanel && ((MemPanel)panels[i]).getMemo().equals(vecchio)){
					((MemPanel)panels[i]).setMemo(nuovo);
					found=true;
					old=(MemPanel)panels[i];
					personal.remove(vecchio);
					personal.add(nuovo);
			}
			if(found){
				int pos=personal.indexOf(nuovo);
				MemPanel newOne=new MemPanel(nuovo);
				newOne.passaTestimone(old);
				newOne.setLanguage(language);
				//mp.addMouseMotionListener(mouseM);
				dynamic.remove(old);
				dynamic.add(newOne, pos);
				dynamic.revalidate();
				repaint();
				return;
			}
		}
	}
	
	/**
	 * Rimuove il MemPanel con dentro il memo m
	 * @param m
	 */
	public void remove(Memo m){		//funziona
		
		Component[] panels=dynamic.getComponents();
		for(int i=0;i<panels.length;i++){
			MemPanel mp=(MemPanel) panels[i];
			if(mp.getMemo().equals(m)){
				dynamic.remove(mp);
				personal.remove(m);
			}
		}
		dynamic.revalidate();
		repaint();
	}
	
	/**
	 * Fa apparire e scomparire la barra interna
	 * @param flag
	 */
	public void setBarVisible(boolean flag){
		
		if(flag)
			menuBar.setVisible(true);
		else menuBar.setVisible(false);
	}
	/**
	 * Attiva o disattiva l'interfaccia guest
	 * @param scelta
	 */
	public void setGuestInterface(boolean scelta){
		
		if(scelta){
			menuBar.setEnabled(false);
			menuBar.setVisible(false);
		}
		else{
			menuBar.setEnabled(true);
			menuBar.setVisible(true);
		}
	}
	
	/**
	 * Setta la lingua del frame
	 * @param language
	 */
	public void setLanguage(Lang language){
		
		if(this.language.equals(language))
			return;
		this.language=language;
		if(language==Lang.IT){
			visual.setText("Visualizza");
			mnPriorit.setText("Filtra per priorità");
			mnData.setText("Filtra per data");
		}
		else if(language==Lang.EN){
			
			visual.setText("Show");
			mnPriorit.setText("Filter by priority");
			mnData.setText("Filter by date");
		}
		else if(language==Lang.ES){
			
			visual.setText("Ve");
			mnPriorit.setText("Filtra por prioridad");
			mnData.setText("Filtra por fecha");
		}
		else if(language==Lang.DE){
			visual.setText("Gesieh");
			mnPriorit.setText("Durchdringen für priorität");
			mnData.setText("Durchdringen für zeitpunkt");
			
		}
		Component[] mempanels=dynamic.getComponents();
		for(int i=0;i<mempanels.length;i++){
			if(mempanels[i] instanceof MemPanel){
				MemPanel mp=(MemPanel)mempanels[i];
				mp.setLanguage(language);
			}
		}
	}
	/**
	 * Provvede all'aggiornamento dei memo, intuendo se alcuni sono scaduti.
	 * @return il numero dei memo scaduti (solitamente 1, ma a volte alcuni memo scadono contemporaneamente)
	 */
	public int updateMemos(){
		
		MemPanel mp;
		Component[] panels=dynamic.getComponents();
		int cont=0;
		for(int i=0;i<panels.length;i++)
			if(panels[i] instanceof MemPanel){
				mp=(MemPanel)panels[i];
				boolean ret=mp.checkMemo();
				if(ret)			//un memo è appena scaduto ed è stato notificato
					cont++;
			}
		return cont;
	}
	/**
	 * Cancella i memo visualizzati nella finestra dynamic
	 */
	void clearMemos(){
		
		dynamic.removeAll();
		personal.clear();
	}
}