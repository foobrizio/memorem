package graphic;

import javax.swing.*;

import main.Keeper;
import graphic.MemoremGUI.Lang;
import java.awt.*;
import java.awt.event.*;


@SuppressWarnings("serial")
public class StatPanel extends JDialog {
	
	private final Color myYellow=new Color(255,255,150);
	private final Color myBlue=new Color(150,200,255);
	private final Color myRed=new Color(255,120,100);
	
	private JTextField textH;
	private JTextField textM;
	private JTextField textL;
	private JTextField textComp;
	private JTextField textArch;
	private JTextField textActive;
	private JTextField textTotal;
	private JTextField inAttesa;
	private JLabel title, nome, priorLabel;
	private JPanel panel;
	private JLabel attesaLabel;
	
	private Lang language;
	private JLabel compLabel;
	private JLabel highLabel;
	private JLabel medLabel;
	private JLabel archLabel;
	private JLabel lowLabel;
	private JLabel activeLabel;
	private JLabel totalLabel;
	/**
	 * Create the panel.
	 */
	public StatPanel(Keeper k){
		
		panel=new ColoredPanel("./src/graphic/wallpapers/stats.jpg");
		setBounds(100, 100, 400, 300);
		getContentPane().add(panel,BorderLayout.CENTER);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		panel.setLayout(null);
		
		language=Lang.EN;							//lingua di default;
		
		title = new JLabel("Stats by");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setBounds(75, 24, 274, 15);
		panel.add(title);
		
		priorLabel = new JLabel("Priority");
		priorLabel.setBounds(75, 140, 53, 15);
		panel.add(priorLabel);
		
		highLabel = new JLabel("High:");
		highLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		highLabel.setBounds(24, 169, 33, 15);
		panel.add(highLabel);
		
		textH = new JTextField();
		textH.setForeground(myRed);
		textH.setBounds(75, 167, 53, 19);
		textH.setEditable(false);
		panel.add(textH);
		textH.setColumns(10);
		
		compLabel = new JLabel("Completed memos:");
		compLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		compLabel.setBounds(165, 136, 126, 15);
		panel.add(compLabel);
		
		textComp = new JTextField();
		textComp.setBounds(309, 134, 52, 19);
		textComp.setForeground(Color.BLACK);
		textComp.setEditable(false);
		panel.add(textComp);
		textComp.setColumns(10);
		
		medLabel = new JLabel("Normal:");
		medLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		medLabel.setBounds(9, 200, 48, 15);
		panel.add(medLabel);
		
		textM = new JTextField();
		textM.setForeground(myYellow);
		textM.setBounds(75, 198, 53, 19);
		textM.setEditable(false);
		panel.add(textM);
		textM.setColumns(10);
		
		archLabel = new JLabel("Stored memos:");
		archLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		archLabel.setBounds(175, 169, 116, 15);
		panel.add(archLabel);
		
		lowLabel = new JLabel("Low:");
		lowLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lowLabel.setBounds(9, 231, 48, 15);
		panel.add(lowLabel);
		
		textL = new JTextField();
		textL.setForeground(myBlue);
		textL.setBounds(75, 229, 53, 19);
		textL.setEditable(false);
		panel.add(textL);
		textL.setColumns(10);
		
		activeLabel = new JLabel("Active memos:");
		activeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		activeLabel.setBounds(204, 200, 87, 15);
		panel.add(activeLabel);
		
		textArch = new JTextField();
		textArch.setColumns(10);
		textArch.setForeground(Color.WHITE);
		textArch.setBounds(309, 165, 52, 19);
		textArch.setEditable(false);
		panel.add(textArch);
		
		textActive = new JTextField();
		textActive.setColumns(10);
		textActive.setForeground(Color.WHITE);
		textActive.setBounds(309, 196, 52, 19);
		textActive.setEditable(false);
		panel.add(textActive);
		
		totalLabel = new JLabel("All:");
		totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		totalLabel.setBounds(181, 231, 110, 15);
		panel.add(totalLabel);
		
		textTotal = new JTextField();
		textTotal.setColumns(10);
		textTotal.setForeground(Color.WHITE);
		textTotal.setBounds(309, 227, 52, 19);
		textTotal.setEditable(false);
		panel.add(textTotal);
		
		final JButton btnOk = new JButton("ok");
		btnOk.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent evt){
				setVisible(false);
				dispose();
				return;
			}
		});
		btnOk.addMouseListener(new MouseAdapter(){
			
			public void mouseEntered(MouseEvent evt){
				
				btnOk.setBorderPainted(true);
			}
			
			public void mouseExited(MouseEvent evt){
				
				btnOk.setBorderPainted(false);
				StatPanel.this.repaint();
			}
		});
		btnOk.setBounds(165, 263, 53, 25);
		btnOk.setBorderPainted(false);
		btnOk.setBackground(new Color(0,0,0,0));
		btnOk.setForeground(Color.RED);
		panel.add(btnOk);
		
		nome = new JLabel("New label");
		nome.setHorizontalAlignment(SwingConstants.CENTER);
		nome.setBounds(85, 51, 224, 15);
		panel.add(nome);
		
		attesaLabel = new JLabel("Pending memos:");
		attesaLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		attesaLabel.setBounds(175, 105, 116, 15);
		panel.add(attesaLabel);
		
		inAttesa = new JTextField();
		inAttesa.setText((String) null);
		inAttesa.setEditable(false);
		inAttesa.setColumns(10);
		inAttesa.setBounds(309, 103, 52, 19);
		panel.add(inAttesa);
		setVisible(true);
		
		setColors();
		calcolaStatistiche(k);
	}
	
	private void addToolTips(){
		
		if(language==Lang.IT){
			inAttesa.setToolTipText("I memo che aspettano di essere gestiti da te");
			textTotal.setToolTipText("Il numero completo di memo da te creati");
			textActive.setToolTipText("Gli impegni ancora da completare");
			textArch.setToolTipText("Gli impegni che non sei riuscito a portare a termine");
			textH.setToolTipText("Impegni ad alta priorità");
			textComp.setToolTipText("Impegni che sei riuscito a completare");
			textM.setToolTipText("Impegni a priorità standard");
			textL.setToolTipText("Impegni a bassa priorità");
		}
		else if(language==Lang.EN){
			inAttesa.setToolTipText("Memos that are waiting for you to decide what to do with them");
			textTotal.setToolTipText("All memos created by you");
			textActive.setToolTipText("Tasks that you didn't completed yet");
			textArch.setToolTipText("Tasks that you didn't reach to complete");
			textH.setToolTipText("High priority memos");
			textComp.setToolTipText("Tasks that you completed");
			textM.setToolTipText("Average priority memos");
			textL.setToolTipText("Low priority memos");
		}
	}
	private void calcolaStatistiche(Keeper k){
		
		if(k.getUser().getNickname().equals("admin")){
			title.setText("General statistics");
			nome.setVisible(false);
		}
		else{
			title.setText("Statistiche di "+k.getUser().getNickname());
			String name="";
			String surname="";
			if(!k.getUser().getNome().equals("---"))
				name=k.getUser().getNome();
			if(!k.getUser().getCognome().equals("---"))
				surname=k.getUser().getCognome();
			nome.setText(name+" "+surname);
		}
		Object[] result=k.statistiche();
		if(result==null)
			return;
		textH.setText(result[0].toString());
		textM.setText(result[1].toString());
		textL.setText(result[2].toString());
		inAttesa.setText(result[3].toString());
		textComp.setText(result[4].toString());
		textArch.setText(result[5].toString());
		textActive.setText(result[6].toString());
		textTotal.setText(result[7].toString());
	}
	private void setColors(){
		
		textH.setBackground(Color.BLACK);
		textM.setBackground(Color.BLACK);
		textL.setBackground(Color.BLACK);
		textActive.setBackground(Color.BLACK);
		textComp.setBackground(Color.BLACK);
		textTotal.setBackground(Color.BLACK);
		textArch.setBackground(Color.BLACK);
		inAttesa.setBackground(Color.BLACK);
		textH.setForeground(myRed);
		textM.setForeground(myYellow);
		textL.setForeground(myBlue);
		textActive.setForeground(Color.WHITE);
		textComp.setForeground(Color.WHITE);
		textTotal.setForeground(Color.WHITE);
		textArch.setForeground(Color.WHITE);
		inAttesa.setForeground(Color.WHITE);
	}
	
	public void setLanguage(Lang language){
		
		if(this.language.equals(language))
			return;
		else if(language==Lang.IT){
			title.setText("Statistiche di");
			compLabel.setText("Memo completati:");
			priorLabel.setText("Priorità");
			highLabel.setText("Alta:");
			lowLabel.setText("Bassa:");
			medLabel.setText("Media:");
			archLabel.setText("Memo archiviati:");
			activeLabel.setText("Memo attivi:");
			totalLabel.setText("Memo totali:");
			attesaLabel.setText("Memo in attesa:");
		}
		else if(language==Lang.ES){
			title.setText("Estadísticas");
			compLabel.setText("Memos completados:");
			priorLabel.setText("Prioridad");
			highLabel.setText("Alta:");
			lowLabel.setText("Baja:");
			medLabel.setText("Media:");
			archLabel.setText("Memos archivados:");
			activeLabel.setText("Memos activos:");
			totalLabel.setText("Memos totales:");
			attesaLabel.setText("Memos en espera:");
		}
		else if(language==Lang.DE){
			title.setText("Statistik von");
			compLabel.setText("Abgeschlossen memos:");
			priorLabel.setText("Priorität");
			highLabel.setText("Hoch:");
			lowLabel.setText("Niedrig:");
			medLabel.setText("Normal:");
			archLabel.setText("Memos gespeichert:");
			activeLabel.setText("Aktive memos:");
			totalLabel.setText("Alle:");
			attesaLabel.setText("Anhängig memos:");
		}
		else{
			title.setText("Stats by");
			compLabel.setText("Completed memos:");
			priorLabel.setText("Priority");
			highLabel.setText("High:");
			lowLabel.setText("Low:");
			medLabel.setText("Normal:");
			archLabel.setText("Stored memos:");
			activeLabel.setText("Active memos:");
			totalLabel.setText("All:");
			attesaLabel.setText("Pending memos:");
		}
		this.language=language;
		addToolTips();
	}
	
	public static void main(String[] args){
		
		StatPanel statistiche=new StatPanel(null);
		statistiche.setVisible(true);
		
	}
}
