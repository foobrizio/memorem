package graphic;

import javax.swing.*;

import main.Keeper;

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
	private JLabel title, nome;
	private JPanel panel;
	private JLabel lblMemoInAttesa;
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
		
		title = new JLabel("Statistiche di");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setBounds(75, 24, 274, 15);
		panel.add(title);
		
		JLabel lblNewLabel = new JLabel("Priorità");
		lblNewLabel.setBounds(75, 140, 53, 15);
		panel.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Alta:");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_1.setBounds(24, 169, 33, 15);
		panel.add(lblNewLabel_1);
		
		textH = new JTextField();
		textH.setForeground(myRed);
		textH.setBounds(75, 167, 53, 19);
		textH.setToolTipText("Impegni ad alta priorità");
		textH.setEditable(false);
		panel.add(textH);
		textH.setColumns(10);
		
		JLabel lblNewLabel_4 = new JLabel("Memo completati:");
		lblNewLabel_4.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_4.setBounds(165, 136, 126, 15);
		panel.add(lblNewLabel_4);
		
		textComp = new JTextField();
		textComp.setBounds(309, 134, 52, 19);
		textComp.setToolTipText("Impegni che sei riuscito a completare");
		textComp.setForeground(Color.BLACK);
		textComp.setEditable(false);
		panel.add(textComp);
		textComp.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Media:");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_2.setBounds(9, 200, 48, 15);
		panel.add(lblNewLabel_2);
		
		textM = new JTextField();
		textM.setForeground(myYellow);
		textM.setToolTipText("Impegni a priorità standard");
		textM.setBounds(75, 198, 53, 19);
		textM.setEditable(false);
		panel.add(textM);
		textM.setColumns(10);
		
		JLabel lblNewLabel_5 = new JLabel("Memo archiviati:");
		lblNewLabel_5.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_5.setBounds(175, 169, 116, 15);
		panel.add(lblNewLabel_5);
		
		JLabel lblNewLabel_3 = new JLabel("Bassa:");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_3.setBounds(9, 231, 48, 15);
		panel.add(lblNewLabel_3);
		
		textL = new JTextField();
		textL.setForeground(myBlue);
		textL.setToolTipText("Impegni a bassa priorità");
		textL.setBounds(75, 229, 53, 19);
		textL.setEditable(false);
		panel.add(textL);
		textL.setColumns(10);
		
		JLabel lblMemoAttivi = new JLabel("Memo attivi:");
		lblMemoAttivi.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMemoAttivi.setBounds(204, 200, 87, 15);
		panel.add(lblMemoAttivi);
		
		textArch = new JTextField();
		textArch.setColumns(10);
		textArch.setForeground(Color.WHITE);
		textArch.setToolTipText("Gli impegni che non sei riuscito a portare a termine");
		textArch.setBounds(309, 165, 52, 19);
		textArch.setEditable(false);
		panel.add(textArch);
		
		textActive = new JTextField();
		textActive.setColumns(10);
		textActive.setForeground(Color.WHITE);
		textActive.setToolTipText("Gli impegni ancora da completare");
		textActive.setBounds(309, 196, 52, 19);
		textActive.setEditable(false);
		panel.add(textActive);
		
		JLabel label = new JLabel("Memo totali:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setBounds(181, 231, 110, 15);
		panel.add(label);
		
		textTotal = new JTextField();
		textTotal.setToolTipText("Il numero completo di memo da te creati");
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
		
		lblMemoInAttesa = new JLabel("Memo in attesa:");
		lblMemoInAttesa.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMemoInAttesa.setBounds(175, 105, 116, 15);
		panel.add(lblMemoInAttesa);
		
		inAttesa = new JTextField();
		inAttesa.setText((String) null);
		inAttesa.setEditable(false);
		inAttesa.setColumns(10);
		inAttesa.setBounds(309, 103, 52, 19);
		inAttesa.setToolTipText("I memo che aspettano di essere gestiti da te");
		panel.add(inAttesa);
		setVisible(true);
		
		setColors();
		calcolaStatistiche(k);
	}
	
	private void calcolaStatistiche(Keeper k){
		
		if(k.getUser().getNickname().equals("admin")){
			title.setText("Statistiche generali");
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
	
	public static void main(String[] args){
		
		StatPanel statistiche=new StatPanel(null);
		statistiche.setVisible(true);
		
	}
}
