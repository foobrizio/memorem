package graphic;

import java.awt.*;
import java.awt.event.*;

import com.jgoodies.forms.layout.*;
import com.jgoodies.forms.factories.FormFactory;

import javax.swing.*;
import javax.swing.plaf.basic.*;
import javax.swing.table.*;

import main.*;
import util.*;


@SuppressWarnings("serial")
class ModelloTable extends AbstractTableModel{
	
	String[] days = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
	int[] numDays = {31,28,31,30,31,30,31,31,30,31,30,31};
	String[][] calendar = new String[7][7];
	
	public ModelloTable(){
	
		for(int i=0;i< days.length;++i)
			calendar[0][i]=days[i];
		for(int i=1;i<7;++i)
			for(int j=0;j<7;++j)
				calendar[i][j]=" ";
	}
	/*
	private String getMese(int mese){
		
		switch(mese){
		case 1: return "January";
		case 2: return "February";
		case 3: return "March";
		case 4: return "April";
		case 5: return "May";
		case 6: return "June";
		case 7: return "July";
		case 8: return "August";
		case 9: return "September";
		case 10: return "October";
		case 11: return "November";
		case 12: return "December";
		default: return "eeeh??";
		}
	}*/
	
	/**
	 * Ritorna il numero di colonne della tabella
	 */
	@Override
	public int getColumnCount() {
		return 7;
	}
	
	/**
	 * Ritorna il numero di righe della tabella
	 */
	@Override
	public int getRowCount() {
		return 7;
	}
	

	/**
	 * Crea lo schema della tabella
	 */
	@Override
	public Object getValueAt(int riga, int colonna) {
		
		if(riga>=0 && colonna>=0)
			return calendar[riga][colonna];
		else return "";
	}
	
	@Override
	public void setValueAt(Object value,int riga,int colonna){
			
		if(riga>=0 && colonna>=0)
			calendar[riga][colonna]=(String)value;
	}
	
	public void setMonth(int year,int month){
		
		for(int i=1;i<7;i++)
			for(int j=0;j<7;j++)
				calendar[i][j]=" ";
	    int dayOfWeek = Data.dayOfWeek(year, month, 1)-1;
	    if(dayOfWeek<0)
	    	dayOfWeek+=7;
	   // System.out.println("day Of Week: "+dayOfWeek);
	    dayOfWeek+=7;
	    int daysInMonth=Data.daysOfMonth(year, month);
	    //System.out.println(getMese(month)+" ha "+daysInMonth+" giorni");
	    for(int i=1;i<=daysInMonth;i++){
	    	//System.out.println("dayOfWeek/7="+dayOfWeek/7);
	    	calendar[dayOfWeek/7][dayOfWeek%7]=Integer.toString(i);
	    	dayOfWeek++;
	    }
	}
}

@SuppressWarnings("serial")
class PopItem extends JMenuItem{
	private Memo m;
	
	public PopItem(String text){
		
		super(text);
	}
	
	public void setMemo(Memo m){
		
		this.m=m;
	}
	public Memo getMemo(){
		
		return m;
	}
	
}

@SuppressWarnings({ "rawtypes", "unchecked" })
public class CalendarFrame extends JInternalFrame implements MouseListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7509519346884708664L;
	private MemoList ml;
	private Popup p;
	private Popopo po;
	private PopItem elimina,completa,modifica,one,three,seven,custom;
	private JTable table;
	private ModelloTable riccardino;
	private JComboBox comboBox,comboMonth;
	private Data oggi;
	private final String[] months={"January","February","March","April","May","June","July", "August","September",
			"October","November","December"};
	private final static Color myYellow=new Color(255,255,150);
	private final static Color myBlue=new Color(150,200,255);
	private final static Color myRed=new Color(255,120,100);
	private CustomRenderer renderer;
	private int[] cellaSelezionata;
	
	/**
	 * Classe interna che serve per gestire graficamente la tabella
	 * @author fabrizio
	 *
	 */
	@SuppressWarnings("serial") class CustomRenderer extends DefaultTableCellRenderer{
	    
		private JTable table;
		private Color[][] griglia;
		public CustomRenderer(JTable table){
			
			super();
			this.table=table;
			griglia=new Color[table.getRowCount()][table.getColumnCount()];
		}
		@Override
		public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column)
	    {
			JLabel c=(JLabel) super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
			if(row==0){
	        	//System.out.println("riga=="+row+",colonna=="+column);
	        	c.setBackground(Color.DARK_GRAY);
	        	if(column==6)
	        		c.setForeground(Color.RED.brighter());
	        	else
	        		c.setForeground(Color.YELLOW);
	        }
			else{
				if(griglia[row][column]==null)
					c.setBackground(Color.LIGHT_GRAY.brighter().brighter());
				else
					c.setBackground(griglia[row][column]);
				c.setForeground(Color.BLACK);
			}
	        return c;
	    }
		
		public void refresh(){
			
			System.out.println("refreshed");
			griglia=new Color[table.getRowCount()][table.getColumnCount()];
			MemoList forTheMonth=new MemoList();
			System.out.println("Quanti memo contiene ml? "+ml.size());
			for( Memo m : ml){	//arrivati qui abbiamo la memolist aggiornata per il mese :D
				//System.out.println();
				Data end=m.getEnd();
				int mese=end.mese();
				//System.out.println("mese:"+mese);
				int anno=end.anno();
				if(mese==(comboMonth.getSelectedIndex()+1) && anno==(comboBox.getSelectedIndex()+oggi.anno()))
					forTheMonth.add(m);
			}
			System.out.println("Quanti memo ci sono in questo mese? "+forTheMonth.size());
			for( Memo m : forTheMonth){		//ora inseriamo nella tabella i memo del mese
				
				int riga=1;		//nella riga 0 ci sono i nomi dei giorni, la riga 1 invece non è quasi mai piena
				int colonna=0;
				int maximum=0;
				while(maximum<50){	//finchè non viene trovata la posizione giusta
					//System.out.print("riga:"+riga+",colonna:"+colonna+" ..");
					int giorno=0;
					String value=(String)table.getValueAt(riga, colonna);
					if(value.trim().length()!=0){	//qui abbiamo una cella contenente un giorno
						giorno=Integer.parseInt(value);
						int memDay=m.getEnd().giorno();
						//System.out.println("giorno:"+giorno+" ,memDay:"+memDay);
						if(memDay==giorno){	//abbiamo trovato la posizione giusta
							cellaSelezionata[0]=riga;
							cellaSelezionata[1]=colonna;
							CalendarFrame.this.add(m);
							break;
						}
						else{
							if(colonna==6){
								colonna=0;
								riga++;
							}
							else colonna++;
						}
						//CalendarFrame.this.setVisible(false);
						//CalendarFrame.this.setVisible(true);
					}//se abbiamo selezionato un giorno
					else{
						if(colonna==6){
							colonna=0;
							riga++;
						}
						else colonna++;
					}//se non abbiamo selezionato un giorno			
				}//while
			}//for
		}
		
		public void colorizeCell(Color c,int row,int column){
			
			griglia[row][column]=c;
		}
	}
	
	
	/**
	 * Create the frame.
	 */
	public CalendarFrame(MemoList ml){
		super();
		this.ml=ml;
		//this.p=p;
		cellaSelezionata=new int[2];
		riccardino=new ModelloTable();
		table=new JTable(riccardino);
		renderer=new CustomRenderer(table);
		setBackground(Color.BLACK);
		this.oggi=new Data();
		String[] years=new String[10];
		for(int i=0;i<10;i++)
			years[i]=String.valueOf(oggi.anno()+i);
		comboBox = new JComboBox(years);
		comboMonth = new JComboBox(months);
		
		table.setBackground(Color.BLACK);
		table.setForeground(Color.WHITE);
		table.addMouseListener(this);
		//comboBox.addItemListener(new ComboHandler());
		//comboMonth.addItemListener(new ComboHandler());
		comboBox.addActionListener(new ComboHandler());
		comboMonth.addActionListener(new ComboHandler());
		comboMonth.setSelectedItem(comboMonth.getItemAt(oggi.mese()-1));
		setBounds(100, 100, 450, 300);
		setResizable(false);
		setBorder(null);
		setOpaque(false);
		setClosable(false);
		BasicInternalFrameUI ifui=(BasicInternalFrameUI)this.getUI();
		BasicInternalFrameTitlePane tp=(BasicInternalFrameTitlePane)ifui.getNorthPane();
		this.remove(tp);
		getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
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
				ColumnSpec.decode("default:grow"),
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
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
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
				RowSpec.decode("default:grow"),}));
		getContentPane().add(comboBox,"2,2,9,1,fill,default");
		getContentPane().add(comboMonth, "16, 2, 9, 1, fill, default");
		getContentPane().add(table,"2, 4, 45, 49, fill, fill");
		table.setOpaque(false);
		colorizeTable();
		//createRiccardino();
		setVisible(true);
	}
	
	public void setListener(Popup p){
		this.p=p;
	}
	
	
	private void colorizeTable(){
		
		for(int i=0;i<7;i++)
			table.getColumnModel().getColumn(i).setCellRenderer(renderer);
	}
	
	public CustomRenderer getCustomRenderer(){
		
		return renderer;
	}
	
	
	private class ComboHandler implements ActionListener {
		
	    public void actionPerformed(ActionEvent e) {
	    	
	    	riccardino.setMonth(comboBox.getSelectedIndex() + oggi.anno(), comboMonth.getSelectedIndex()+1);
	    	renderer.refresh();
	    	table.repaint();
	    }
	}
	/*private class ListHandler implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			System.out.println("hyeeeah");
			riccardino.setMonth(comboBox.getSelectedIndex() + 1998, comboMonth.getSelectedIndex());
				table.repaint();
	    }
	}
	*/
	@Override
	public void mouseClicked(MouseEvent click) {
		
		cellaSelezionata[0]=table.rowAtPoint(click.getPoint());
		cellaSelezionata[1]=table.columnAtPoint(click.getPoint());
		String valore=(String)table.getValueAt(cellaSelezionata[0], cellaSelezionata[1]);
		if(valore.trim().length()!=0){	//in questo modo siamo sicuri di prendere soltanto le celle non vuote
			
			int giorno=0;
			if(valore.length()<=2)
				giorno=Integer.parseInt(valore);
			int mese=comboMonth.getSelectedIndex()+1;
			int anno=comboBox.getSelectedIndex()+oggi.anno();
			if(click.getButton()==MouseEvent.BUTTON3){
				giorno=Integer.parseInt((String)riccardino.getValueAt(cellaSelezionata[0], cellaSelezionata[1]));
				Data curicazzi=new Data(anno,mese,giorno,0,0);
				MemoList delGiorno=new MemoList();
				for(Memo m: ml){
					Data end=m.getEnd();
					if(end.hasSameDay(curicazzi))
						delGiorno.add(m);
				}	//abbiamo finito di scandire i memo per il giorno cliccato
				if(delGiorno.size()!=0){
					//il menu a tendina si apre con il JPopupMenu
					po=new Popopo(delGiorno.get(0));
					System.out.println(delGiorno.get(0));
					po.show(click.getComponent(), click.getX(), click.getY());
				}
			}//fine delle funzionalità col tasto destro
			//PERSONALIZZIAMO IL NOSTRO POPUP PER LA DATA RICHIESTA
			else if(click.getButton()==MouseEvent.BUTTON1 && click.getClickCount()==2){		//doppio click
				Data cliccata=new Data(anno,mese,giorno,0,0);
				boolean esiste=false;
				giorno=Integer.parseInt((String)riccardino.getValueAt(cellaSelezionata[0], cellaSelezionata[1]));
				for(Memo m: ml)
					if(m.getEnd().hasSameDay(cliccata)){
						p.setDesc(m.description());
						p.setOld(m);
						esiste=true;
					}
				p.setModified(esiste);	//setModified deve stare in pole, perchè può modificare i valori dei parametri
				p.setMonth(comboMonth.getSelectedIndex());
				p.setYear(comboBox.getSelectedIndex()+oggi.anno());
				p.setDay(giorno-1);		//setDay va richiamato sempre dopo setMonth, perchè setMonth modifica il giorno
				System.out.println("Il giorno del memorem è "+giorno);
				p.setPrior(1);
				p.setHour(oggi.ora());
				p.setMinute(oggi.minuto());
				p.setVisible(true);
			}
			System.out.println(giorno+"-"+Data.monthToString(mese)+"-"+anno);	
		}	
	}

	@Override
	public void mouseEntered(MouseEvent arg0) 	{		}
	@Override
	public void mouseExited(MouseEvent arg0) 	{		}
	@Override
	public void mousePressed(MouseEvent arg0) 	{		}
	@Override
	public void mouseReleased(MouseEvent arg0)	{		}
	
	/**
	 * Aggiunge un memo al panel, colorando il calendario alla data di scadenza del memo
	 * @param m
	 */
	public void add(Memo m){
		
		/*if(m.getEnd().compareTo(oggi)<0){
			System.out.println("il memo è scaduto..nadaa");
			return;
		}*/
		System.out.println("Aggiunta al calendario in corso");
		ml.add(m);
		Color c=null;
		switch(m.priority()){
		case 0: c=myBlue;break;
		case 1: c=myYellow;break;
		default: c=myRed;
		}
		//System.out.println("celleSelezionate:"+cellaSelezionata[0]+","+cellaSelezionata[1]);
		this.renderer.colorizeCell(c, cellaSelezionata[0], cellaSelezionata[1]);
	}
	
	/**
	 * Rimuove un memo dal panel
	 * @param m
	 */
	public synchronized void remove(Memo m){
		
		ml.remove(m);
		int mese=m.getEnd().mese();
		int anno=m.getEnd().anno();
		if((comboMonth.getSelectedIndex()+1)==mese && (comboBox.getSelectedIndex()+oggi.anno())==anno){
			//stiamo effettivamente visualizzando il mese in cui dobbiamo rimuovere il memo...come nella stragrande maggioranza dei casi
			int riga=1,colonna=0,memDay=m.getEnd().giorno();
			boolean trovato=false;
			while(true){
				//System.out.println("while");
				String value=((String)table.getValueAt(riga, colonna)).trim();
				if(value.length()!=0){		//è un numero
					int day=Integer.parseInt(value);
					if(memDay==day){
						trovato=true;
						cellaSelezionata[0]=riga;
						cellaSelezionata[1]=colonna;
						break;
					}
					else if(memDay-day>=7){
						riga++;
					}
					else if(memDay-day<7){
						if(colonna==6){
							colonna=0;
							riga++;
						}
						else
							colonna++;
					}
				}//abbiamo selezionato un giorno
				else{
					if(colonna==6){
						colonna=0;
						riga++;
					}
					else colonna++;
				}//se non abbiamo selezionato un giorno		
			}//while
			
			if(trovato){
				table.setVisible(false);
				this.renderer.colorizeCell(null, cellaSelezionata[0], cellaSelezionata[1]);
				table.setVisible(true);
			}
		}
	}
	
	public void setPopItems(PopItem[] items){
		
		completa=items[0];
		modifica=items[1];
		elimina=items[2];
		one=items[3];
		three=items[4];
		seven=items[5];
		custom=items[6];
	}
	
	public MemoList getMemoList(){
		
		return ml;
	}
	
	public void setMemolist(MemoList ml){
		this.ml=ml;
	}
	
	@SuppressWarnings("serial")
	class Popopo extends JPopupMenu{
		
		private Color coloreSfondo;
		private Memo m;
		
		public Popopo(Memo m){
			
			this.m=m;
			elimina.setMemo(m);
			completa.setMemo(m);
			modifica.setMemo(m);
			one.setMemo(m);
			three.setMemo(m);
			seven.setMemo(m);
			custom.setMemo(m);
			setOpaque(true);
			switch(m.priority()){
			case 0: coloreSfondo=myBlue; break;
			case 1: coloreSfondo=myYellow; break;
			case 2: coloreSfondo=myRed; break;
			}
			JMenu rinvia=new JMenu("Rinvia");
			if(!m.isScaduto())
				modifica.setMemo(m);
			else{
				//modifica.setVisible(false);
				rinvia=new JMenu("Rinvia");
				rinvia.add(one);
				rinvia.add(three);
				rinvia.add(seven);
				rinvia.add(custom);
			}
			colora();
			this.add(completa);
			if(!m.isScaduto()){
				this.add(modifica);
			}
			else{
				rinvia.setBackground(coloreSfondo);
				this.add(rinvia);
			}
			this.add(elimina);
		}
		
		private void colora(){
			
			this.setBackground(coloreSfondo);
			completa.setBackground(coloreSfondo);
			modifica.setBackground(coloreSfondo);
			elimina.setBackground(coloreSfondo);
			one.setBackground(coloreSfondo);
			three.setBackground(coloreSfondo);
			seven.setBackground(coloreSfondo);
			custom.setBackground(coloreSfondo);
		}
		
		public Memo getMemo(){
			
			return m;
		}
	}
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrame f=new JFrame();
					f.setBackground(Color.black);
					Popup p=new Popup(false);
					MemoList ml=new MemoList();
					CalendarFrame frame = new CalendarFrame(ml);
					frame.setListener(p);
					f.setContentPane(frame);
					f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					f.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}