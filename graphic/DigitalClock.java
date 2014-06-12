package graphic;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;

/**
* This class displays a digital clock on the screen.
* @author Kevin Walker
*/
public class DigitalClock extends JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4792919259898910841L;
	// All other steps should have their code added here.
	JLabel timeLabel = new JLabel();
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	Timer timer;
	
	public DigitalClock(){
		
		super();
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		timeLabel.setText(sdf.format(new Date(System.currentTimeMillis())));
		timeLabel.setFont(new Font("Dialog", Font.PLAIN, 24));
		timer = new Timer(1000, this);
		timer.setRepeats(true);
		timer.start();
		add(timeLabel);
		//pack();
		this.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent evt){
		
		if (evt.getSource().equals(timer))
			timeLabel.setText(sdf.format(new Date(System.currentTimeMillis())));
	}
	
	public static void main(String[] args){
		
		JFrame j=new JFrame();
		j.add(new DigitalClock());
		j.setVisible(true);
	}
	
}
