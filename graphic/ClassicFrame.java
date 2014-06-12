package graphic;

import javax.swing.*;	//per JPanel e soci

import main.Memo;
import util.Data;
import util.MemoList;

import java.awt.*;		//per Color


@SuppressWarnings("serial")
public class ClassicFrame extends JInternalFrame{
	
	private Color sfondo=new Color(255,255,150);
	private JPanel dynamic;
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
		
		setTitle("Finestra Contenitrice");
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
		this.setName("Internal Frame Container");
		dynamic.setName("dynamic");
		setClosable(false);
		setOpaque(true);
		setVisible(true);
		this.setBorder(null);
		createButtons(vF,pF,dF);
		setJMenuBar(menuBar);
		this.menuBar.setVisible(true);
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
	
	/*public MemPanel[] getPanels(){
	 * 
		dynamic.setVisible(false);
		dynamic.setVisible(true);
		Component[] comp=dynamic.getComponents();
		MemPanel[] returning=new MemPanel[comp.length];
		for(int i=0;i<comp.length;i++)
			returning[i]=(MemPanel)comp[i];
		for(int i=0;i<comp.length;i++)
			if(returning[i].getMemo()==null)
				returning[i]=null;
		return returning;
	}
	*/	
	private void createContentPane(){
		
		dynamic=new JPanel();
		dynamic.setName("dynamic");
		dynamic.setLayout(new BoxLayout(dynamic, BoxLayout.Y_AXIS));
		dynamic.setBackground(sfondo);
		dynamic.setOpaque(true);
		getContentPane().setBackground(Color.BLACK);
		this.getContentPane().add(dynamic,BorderLayout.NORTH);
	}
	/**
	 * In questo modo ci assicuriamo che il PanelContainer contenga solamente MemPanel
	 */
	@Override
	public Component add(Component c){
		
		if(c instanceof MemPanel){
			//System.out.println("aggiunto");
			if(!personal.contains(((MemPanel)c).getMemo())){
				personal.add(((MemPanel)c).getMemo());
				System.out.println("Aggiunto Memo all'InternalFrameContainer");
				return dynamic.add(c);
			}
			else return null;
		}
		else return null;
	}
	
	public void remove(Memo m){
		
		System.out.print("rimozione dal classic in corso...");
		Component[] panels=dynamic.getComponents();
		for(int i=0;i<panels.length;i++){
			MemPanel mp=(MemPanel) panels[i];
			if(mp.getMemo().equals(m)){
				dynamic.remove(mp);
				personal.remove(m);
			}
		}
		dynamic.setVisible(false);
		dynamic.setVisible(true);
		System.out.println("completato");
		//this.repaint();
	}
	
	/**
	 * Cancella i memo visualizzati nella finestra dynamic
	 */
	void clearMemos(){
		
		dynamic.removeAll();
	}
	
	/**
	 * Provvede all'aggiornamento dei memo, intuendo se alcuni sono scaduti
	 * @param ora
	 */
	public boolean updateMemos(){
		
		MemPanel mp;
		Data end;
		Component[] panels=dynamic.getComponents();
		for(int i=0;i<panels.length;i++){
			mp=(MemPanel)panels[i];
			end=mp.getMemo().getEnd();
			if(end.compareTo(new Data())<0){
				boolean ret=mp.checkMemo();
				if(ret){
					dynamic.remove(mp);
					dynamic.add(mp);
				}
				System.out.println("Un memo è scaduto. ret:"+ret);
				return ret;
			}
		}
		return false;
	}
}