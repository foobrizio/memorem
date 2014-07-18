package graphic;

import javax.swing.*;	//per JPanel e soci

import main.Memo;
import util.Data;
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
	private JMenu mnPriorit;
	/*
	 * mnFiltraPerData è così composto
	 * 			today		//	"oggi"
	 * 			week		//	"in settimana"
	 * 			month		// 	"in questo mese"
	 * 			year		//	"in quest'anno"
	 * 			always		//	"sempre"
	 */
	private JMenu mnFiltraPerData;

	
	public ClassicFrame(JRadioButtonMenuItem[] vF, JCheckBoxMenuItem[] pF,JRadioButtonMenuItem[] dF){
		
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
		this.menuBar.setVisible(true);
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
		visual=new JMenu("Visualizza");
		visual.add(vF[0]);
		visual.add(vF[1]);
		menuBar.add(visual);
		//priorFilter
		mnPriorit = new JMenu("Filtra per priorità");
		mnPriorit.add(pF[0]);
		mnPriorit.add(pF[1]);
		mnPriorit.add(pF[2]);
		menuBar.add(mnPriorit);
		//dateFilter
		mnFiltraPerData = new JMenu("Filtra per data");
		mnFiltraPerData.add(dF[0]);
		mnFiltraPerData.add(dF[1]);
		mnFiltraPerData.add(dF[2]);
		mnFiltraPerData.add(dF[3]);
		mnFiltraPerData.add(dF[4]);
		menuBar.add(mnFiltraPerData);
	}
	
	private void createContentPane(){
		jp=new JPanel();
		JScrollPane scroll=new JScrollPane(jp,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setOpaque(false);
		scroll.setBorder(null);
		getContentPane().add(scroll);
		
	}
	
	@Override
	public void setVisible(boolean aFlag){
		
		if(aFlag)
			System.out.println("Classic visibile");
		else
			System.out.println("Classic invisibile");
		super.setVisible(aFlag);
	}
	/**
	 * In questo modo ci assicuriamo che il PanelContainer contenga solamente MemPanel
	 */
	@Override
	public Component add(Component c){		//qui dentro si può aggiungere soltanto MemPanels
		
		if(c instanceof MemPanel){
			if(!personal.contains(((MemPanel)c).getMemo())){
				personal.add(((MemPanel)c).getMemo());
				return dynamic.add(c,personal.indexOf(((MemPanel)c).getMemo()));
			}
		}
		return null;
	}
	
	public MemPanel get(Memo m){
		
		Component[] panels=dynamic.getComponents();
		for(int i=0;i<panels.length;i++){
			MemPanel mp=(MemPanel) panels[i];
			if(mp.getMemo().equals(m))
				return mp;
		}
		return null;
	}
	
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
	 * Cancella i memo visualizzati nella finestra dynamic
	 */
	void clearMemos(){
		
		dynamic.removeAll();
		personal.clear();
	}
	
	/**
	 * Provvede all'aggiornamento dei memo, intuendo se alcuni sono scaduti.
	 * @return il numero dei memo scaduti (solitamente 1, ma a volte alcuni memo scadono contemporaneamente)
	 */
	public int updateMemos(){
		
		MemPanel mp;
		Data end;
		Component[] panels=dynamic.getComponents();
		int cont=0;
		for(int i=0;i<panels.length;i++)
			if(panels[i] instanceof MemPanel){
				mp=(MemPanel)panels[i];
				end=mp.getMemo().getEnd();
				if(end.compareTo(new Data())<0){	//vengono controllati solo i memo scaduti
					boolean ret=mp.checkMemo();
					//System.out.println(mp.getMemo().description());
					if(ret)			//un memo è appena scaduto ed è stato notificato
						cont++;
				}
			}
		return cont;
	}
}