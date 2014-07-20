package graphic;

import javax.swing.*;

import main.Keeper;

import java.awt.*;
import java.awt.event.*;


@SuppressWarnings("serial")
public class StatPanel extends JDialog {
	
	private JTextField textH;
	private JTextField textM;
	private JTextField textL;
	private JTextField textComp;
	private JTextField textArch;
	private JTextField textActive;
	private JTextField textTotal;
	private JLabel title;
	private JPanel panel;
	/**
	 * Create the panel.
	 */
	public StatPanel(Keeper k){
		
		panel=new ColoredPanel("./src/graphic/wallpapers/stats.jpg");
		setBounds(100, 100, 400, 250);
		getContentPane().add(panel,BorderLayout.CENTER);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		panel.setLayout(null);
		
		title = new JLabel("Statistiche di");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setBounds(75, 24, 274, 15);
		panel.add(title);
		
		JLabel lblNewLabel = new JLabel("Priorità");
		lblNewLabel.setBounds(75, 51, 53, 15);
		panel.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Alta:");
		lblNewLabel_1.setBounds(31, 77, 33, 15);
		panel.add(lblNewLabel_1);
		
		textH = new JTextField();
		textH.setBounds(75, 75, 53, 19);
		textH.setEditable(false);
		panel.add(textH);
		textH.setColumns(10);
		
		JLabel lblNewLabel_4 = new JLabel("Memo completati:");
		lblNewLabel_4.setBounds(165, 51, 126, 15);
		panel.add(lblNewLabel_4);
		
		textComp = new JTextField();
		textComp.setBounds(297, 49, 52, 19);
		textComp.setEditable(false);
		panel.add(textComp);
		textComp.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Media:");
		lblNewLabel_2.setBounds(18, 103, 48, 15);
		panel.add(lblNewLabel_2);
		
		textM = new JTextField();
		textM.setBounds(75, 101, 53, 19);
		textM.setEditable(false);
		panel.add(textM);
		textM.setColumns(10);
		
		JLabel lblNewLabel_5 = new JLabel("Memo archiviati:");
		lblNewLabel_5.setBounds(175, 77, 116, 15);
		panel.add(lblNewLabel_5);
		
		JLabel lblNewLabel_3 = new JLabel("Bassa:");
		lblNewLabel_3.setBounds(18, 130, 48, 15);
		panel.add(lblNewLabel_3);
		
		textL = new JTextField();
		textL.setBounds(75, 128, 53, 19);
		textL.setEditable(false);
		panel.add(textL);
		textL.setColumns(10);
		
		JLabel lblMemoAttivi = new JLabel("Memo attivi:");
		lblMemoAttivi.setBounds(204, 103, 87, 15);
		panel.add(lblMemoAttivi);
		
		textArch = new JTextField();
		textArch.setColumns(10);
		textArch.setBounds(297, 75, 52, 19);
		textArch.setEditable(false);
		panel.add(textArch);
		
		textActive = new JTextField();
		textActive.setColumns(10);
		textActive.setBounds(297, 101, 52, 19);
		textActive.setEditable(false);
		panel.add(textActive);
		
		JLabel label = new JLabel("Memo totali:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setBounds(181, 130, 110, 15);
		panel.add(label);
		
		textTotal = new JTextField();
		textTotal.setColumns(10);
		textTotal.setBounds(297, 128, 52, 19);
		textTotal.setEditable(false);
		panel.add(textTotal);
		
		JButton btnOk = new JButton("ok");
		btnOk.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent evt){
				setVisible(false);
				dispose();
				return;
			}
		});
		btnOk.setBounds(165, 213, 53, 25);
		panel.add(btnOk);
		setVisible(true);
		
		calcolaStatistiche(k);
	}
	
	private void calcolaStatistiche(Keeper k){
		
		if(k.getUser().getNickname().equals("admin"))
			title.setText("Statistiche generali");
		else
			title.setText("Statistiche di "+k.getUser().getNome()+" "+k.getUser().getCognome());
		Object[] result=k.statistiche();
		if(result==null)
			return;
		textH.setText(result[0].toString());
		textM.setText(result[1].toString());
		textL.setText(result[2].toString());
		textComp.setText(result[3].toString());
		textArch.setText(result[4].toString());
		textActive.setText(result[5].toString());
		textTotal.setText(result[6].toString());
	}
	
	public static void main(String[] args){
		
		StatPanel statistiche=new StatPanel(null);
		statistiche.setVisible(true);
		
	}
}
