package graphic;

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
			ml.add(one,two,three,past,prova2);
			DeferDialog dialog = new DeferDialog(null,ml);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DeferDialog(JFrame owner,MemoList ml) {
		
		super(owner,true);
		setBounds(100, 100, 550, 350);
		setResizable(false);
		setContentPane(new ColoredPanel("./src/graphic/wallpapers/desk.jpg"));
		this.ml=ml;
		handled=new MemoList();
		mouse=new Mouse();
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setOpaque(false);
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		SpringLayout sl_contentPanel = new SpringLayout();
		contentPanel.setLayout(sl_contentPanel);
		
		frecciaS = new JLabel("«««");
		frecciaS.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
		frecciaS.addMouseListener(mouse);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, frecciaS, 10, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, frecciaS, 10, SpringLayout.WEST, contentPanel);
		contentPanel.add(frecciaS);
		
		frecciaD = new JLabel("»»»");
		frecciaD.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
		frecciaD.addMouseListener(mouse);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, frecciaD, 0, SpringLayout.NORTH, frecciaS);
		sl_contentPanel.putConstraint(SpringLayout.EAST, frecciaD, -10, SpringLayout.EAST, contentPanel);
		contentPanel.add(frecciaD);
		{
			pageLabel = new JLabel("1/1");
			sl_contentPanel.putConstraint(SpringLayout.NORTH, pageLabel, 0, SpringLayout.NORTH, frecciaS);
			sl_contentPanel.putConstraint(SpringLayout.WEST, pageLabel, 170, SpringLayout.EAST, frecciaS);
			sl_contentPanel.putConstraint(SpringLayout.EAST, pageLabel, -176, SpringLayout.WEST, frecciaD);
			pageLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
			pageLabel.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(pageLabel);
		}
		{
			descLabel = new JLabel("descrizione");
			sl_contentPanel.putConstraint(SpringLayout.EAST, descLabel, 0, SpringLayout.EAST, frecciaD);
			descLabel.setFont(new Font("Dialog", Font.BOLD, 16));
			sl_contentPanel.putConstraint(SpringLayout.NORTH, descLabel, 33, SpringLayout.SOUTH, frecciaS);
			sl_contentPanel.putConstraint(SpringLayout.WEST, descLabel, 0, SpringLayout.WEST, frecciaS);
			descLabel.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(descLabel);
		}
		{
			lblScadutoIl = new JLabel("scaduto il:");
			sl_contentPanel.putConstraint(SpringLayout.NORTH, lblScadutoIl, 108, SpringLayout.SOUTH, descLabel);
			lblScadutoIl.setFont(new Font("Dialog", Font.BOLD, 16));
			contentPanel.add(lblScadutoIl);
		}
		{
			dateLabel = new JLabel("data");
			sl_contentPanel.putConstraint(SpringLayout.NORTH, dateLabel, 0, SpringLayout.NORTH, lblScadutoIl);
			sl_contentPanel.putConstraint(SpringLayout.EAST, dateLabel, -50, SpringLayout.EAST, contentPanel);
			dateLabel.setFont(new Font("Dialog", Font.BOLD, 16));
			contentPanel.add(dateLabel);
		}
		{
			lblAlle = new JLabel("alle:");
			sl_contentPanel.putConstraint(SpringLayout.NORTH, lblAlle, 16, SpringLayout.SOUTH, lblScadutoIl);
			sl_contentPanel.putConstraint(SpringLayout.EAST, lblScadutoIl, 0, SpringLayout.EAST, lblAlle);
			sl_contentPanel.putConstraint(SpringLayout.WEST, lblAlle, 66, SpringLayout.WEST, contentPanel);
			lblAlle.setFont(new Font("Dialog", Font.BOLD, 16));
			lblAlle.setHorizontalAlignment(SwingConstants.RIGHT);
			contentPanel.add(lblAlle);
		}
		{
			timeLabel = new JLabel("ora");
			sl_contentPanel.putConstraint(SpringLayout.WEST, timeLabel, 45, SpringLayout.EAST, lblAlle);
			sl_contentPanel.putConstraint(SpringLayout.EAST, timeLabel, -50, SpringLayout.EAST, contentPanel);
			sl_contentPanel.putConstraint(SpringLayout.WEST, dateLabel, 0, SpringLayout.WEST, timeLabel);
			sl_contentPanel.putConstraint(SpringLayout.NORTH, timeLabel, 0, SpringLayout.NORTH, lblAlle);
			timeLabel.setFont(new Font("Dialog", Font.BOLD, 16));
			contentPanel.add(timeLabel);
		}
		
		Component horizontalGlue = Box.createHorizontalGlue();
		contentPanel.add(horizontalGlue);
		
		completa= new JLabel("Completa");
		sl_contentPanel.putConstraint(SpringLayout.EAST, completa, 0, SpringLayout.EAST, lblScadutoIl);
		completa.addMouseListener(mouse);
		contentPanel.add(completa);
		
		archivia = new JLabel("Archivia");
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, archivia, -10, SpringLayout.SOUTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, completa, 0, SpringLayout.NORTH, archivia);
		sl_contentPanel.putConstraint(SpringLayout.WEST, archivia, 0, SpringLayout.WEST, dateLabel);
		archivia.addMouseListener(mouse);
		contentPanel.add(archivia);
		
		rinvia = new JLabel("Rinvia");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, rinvia, 0, SpringLayout.NORTH, completa);
		sl_contentPanel.putConstraint(SpringLayout.WEST, rinvia, 54, SpringLayout.EAST, archivia);
		rinvia.addMouseListener(mouse);
		contentPanel.add(rinvia);
		
	
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setOpaque(false);
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				vediDopo = new JButton("Vedi dopo");
				vediDopo.addActionListener(this);
				vediDopo.setActionCommand("Cancel");
				buttonPane.add(vediDopo);
			}
			
			chiudi = new JButton("Chiudi");
			chiudi.addActionListener(this);
			buttonPane.add(chiudi);
		}
		one=new JMenuItem("Rinvia a domani");
		three=new JMenuItem("Rinvia di tre giorni");
		seven=new JMenuItem("Rinvia di una settimana");
		month=new JMenuItem("Rinvia di un mese");
		one.addActionListener(this);
		three.addActionListener(this);
		seven.addActionListener(this);
		month.addActionListener(this);
		prepareInterface();
	}
	
	public MemoList getHandled(){
		
		return handled;
	}
	
	private void setPopupMenu(){
		
		popopo.add(one);
		popopo.add(three);
		popopo.add(seven);
		popopo.add(month);
	}
	
	private void prepareInterface(){
		
		if(ml.size()==1)
			frecciaD.setVisible(false);
		frecciaS.setVisible(false);
		curPage=1;
		pages=ml.size();
		pageLabel.setText("1/"+pages);
		popopo=new Popopo();
		setPopupMenu();
		aggiornaLabels();
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
				
				Memo m=ml.get(curPage-1);
				m.spunta();
				handled.add(m);
				ml.remove(m);
			}
			else if(evt.getSource()==archivia){
				
				Memo m=ml.get(curPage-1);
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
		}
		public void mouseEntered(MouseEvent evt){
			
			if(evt.getSource()==frecciaS){
				frecciaS.setForeground(Color.GREEN);
			}
			else if(evt.getSource()==frecciaD){
				frecciaD.setForeground(Color.GREEN);
			}
			else if(evt.getSource()==completa){
				completa.setForeground(Color.GREEN);
			}
			else if(evt.getSource()==archivia){
				archivia.setForeground(Color.RED);
			}
			else if(evt.getSource()==rinvia){
				rinvia.setForeground(descLabel.getForeground());
			}
		}
		public void mouseExited(MouseEvent evt){
			
			if(evt.getSource()==frecciaS){
				frecciaS.setForeground(Color.WHITE.darker());
			}
			else if(evt.getSource()==frecciaD){
				frecciaD.setForeground(Color.WHITE.darker());
			}
			else if(evt.getSource()==completa){
				completa.setForeground(Color.WHITE.darker());
			}
			else if(evt.getSource()==archivia){
				archivia.setForeground(Color.WHITE.darker());
			}
			else if(evt.getSource()==rinvia){
				rinvia.setForeground(Color.WHITE.darker());
			}
		}
	}
	
	class Popopo extends JPopupMenu{
		
		private Color coloreSfondo;
		
		public Popopo(){
			
			setOpaque(true);
			colora();
		}
		
		private void colora(){
			
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
			handled.add(cur);
		}
		else if(arg0.getSource()==three){
			for(int i=0;i<3;i++)
				d=d.domani();
			ml.remove(cur);
			cur.setEnd(d.anno(), d.mese(), d.giorno(), d.ora(), d.minuto());
			handled.add(cur);
		}
		else if(arg0.getSource()==seven){
			for(int i=0;i<7;i++)
				d=d.domani();
			ml.remove(cur);
			cur.setEnd(d.anno(), d.mese(), d.giorno(), d.ora(), d.minuto());
			handled.add(cur);
		}
		else if(arg0.getSource()==month){
			for(int i=0;i<30;i++)
				d=d.domani();
			ml.remove(cur);
			cur.setEnd(d.anno(), d.mese(), d.giorno(), d.ora(), d.minuto());
			handled.add(cur);
		}
		aggiornaLabels();
		
	}
}
