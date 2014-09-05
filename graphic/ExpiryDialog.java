package graphic;

import graphic.MemoremGUI.Lang;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TreeSet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import util.Data;

import javax.swing.JLabel;

import main.Memo;

/**
 * IN QUESTO DIALOG VERRANNO VISUALIZZATI I MEMO IN SCADENZA, DA FAR NOTARE ALL'UTENTE
 * @author fabrizio
 *
 */
@SuppressWarnings("serial")
public class ExpiryDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JLabel minuteLabel;
	private JLabel oraLabel;
	private JLabel oggiLabel;
	private JLabel title;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ExpiryDialog dialog = new ExpiryDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ExpiryDialog(JFrame owner) {
		super(owner,true);
		setBounds(100, 100, 320,170);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			minuteLabel = new JLabel("C'è un memo in scadenza tra 5 minuti");
			minuteLabel.setBounds(14, 54, 274, 15);
			contentPanel.add(minuteLabel);
		}
		{
			oraLabel = new JLabel("C'è un memo in scadenza tra 1 ora");
			oraLabel.setBounds(14, 81, 274, 15);
			contentPanel.add(oraLabel);
		}
		{
			oggiLabel = new JLabel("5 memo in totale scadranno oggi");
			oggiLabel.setBounds(14, 108, 274, 15);
			contentPanel.add(oggiLabel);
		}
		
		title = new JLabel("Attenzione");
		title.setBounds(113, 12, 78, 15);
		contentPanel.add(title);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				okButton.addActionListener(new ActionListener(){
					
					public void actionPerformed(ActionEvent evt){
						
						ExpiryDialog.this.dispose();
					}
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}
	
	public void visualizza(TreeSet<Memo> ml,Lang lang){
		
		int ora=0,minuti=0,giorno=0;
		for(Memo m: ml){
			Data d=m.getEnd();
			if(Data.minuteCount(new Data(), d)<=5)
				minuti++;
			else if(Data.minuteCount(new Data(), d)<=60)
				ora++;
			if(d.giorno()==new Data().giorno())
				giorno++;
		}
		if(lang==Lang.IT){
			title.setText("Attenzione");
			if(minuti==0)
				minuteLabel.setVisible(false);
			else{
				minuteLabel.setVisible(true);
				if(minuti==1)
					minuteLabel.setText("C'è un memo in scadenza tra 5 minuti");
				else
					minuteLabel.setText("Ci sono "+minuti+" memo in scadenza tra 5 minuti");
			}
			if(ora==0)
				oraLabel.setVisible(false);
			else{
				oraLabel.setVisible(true);
				if(ora==1)
					oraLabel.setText("C'è un memo in scadenza tra 1 ora");
				else
					oraLabel.setText("Ci sono "+ora+" memo in scadenza tra 1 ora");
			}
			if(giorno==0)	//impossibile se questo metodo viene richiamato
				oggiLabel.setVisible(false);
			else{
				oggiLabel.setVisible(true);
				if(giorno==1)
					oggiLabel.setText("Un memo scade in giornata");
				else
					oggiLabel.setText(giorno+" memo in totale scadranno oggi");
			}
		}//italiano
		else if(lang==Lang.EN){
			title.setText("Warning");
			if(minuti==0)
				minuteLabel.setVisible(false);
			else{
				minuteLabel.setVisible(true);
				if(minuti==1)
					minuteLabel.setText("One memo will expire within 5 minutes");
				else
					minuteLabel.setText("There are "+minuti+" memos expiring within 5 minutes");
			}
			if(ora==0)
				oraLabel.setVisible(false);
			else{
				oraLabel.setVisible(true);
				if(ora==1)
					oraLabel.setText("One memo will expire within an hour");
				else
					oraLabel.setText("There are "+ora+" memos expiring within an hour");
			}
			if(giorno==0)	//impossibile se questo metodo viene richiamato
				oggiLabel.setVisible(false);
			else{
				oggiLabel.setVisible(true);
				if(giorno==1)
					oggiLabel.setText("One memo expires today");
				else
					oggiLabel.setText(giorno+" memos overall will expire today");
			}
		}//inglese
		else if(lang==Lang.ES){
			title.setText("Advertencia");
			if(minuti==0)
				minuteLabel.setVisible(false);
			else{
				minuteLabel.setVisible(true);
				if(minuti==1)
					minuteLabel.setText("Hay un memo a expirar en 5 minutos");
				else
					minuteLabel.setText("Hay "+minuti+" memos con vencimiento en 5 minutos");
			}
			if(ora==0)
				oraLabel.setVisible(false);
			else{
				oraLabel.setVisible(true);
				if(ora==1)
					oraLabel.setText("Hay un memo a expirar entre 1 hora");
				else
					oraLabel.setText("Hay "+ora+" memos con vencimiento entre 1 ora");
			}
			if(giorno==0)	//impossibile se questo metodo viene richiamato
				oggiLabel.setVisible(false);
			else{
				oggiLabel.setVisible(true);
				if(giorno==1)
					oggiLabel.setText("Un memo expira hoy");
				else
					oggiLabel.setText(giorno+" memos en total vencen hoy");
			}
		}//spagnolo
		else{
			title.setText("Warnung");
			if(minuti==0)
				minuteLabel.setVisible(false);
			else{
				minuteLabel.setVisible(true);
				if(minuti==1)
					minuteLabel.setText("Eine memo wird innerhalb von 5 Minuten verfallen");
				else
					minuteLabel.setText(minuti+" memos wird innerhalb von 5 Minuten verfallen");
			}
			if(ora==0)
				oraLabel.setVisible(false);
			else{
				oraLabel.setVisible(true);
				if(ora==1)
					oraLabel.setText("Eine memo wird innerhalb von 1 stunde auslaufen");
				else
					oraLabel.setText(ora+" memos wird innerhalb von 1 stunde auslaufen");
			}
			if(giorno==0)	//impossibile se questo metodo viene richiamato
				oggiLabel.setVisible(false);
			else{
				oggiLabel.setVisible(true);
				if(giorno==1)
					oggiLabel.setText("Eine memo läuft heute");
				else
					oggiLabel.setText(giorno+"memos heute verfallen");
			}
		}//tedesco
		setVisible(true);
	}
}
