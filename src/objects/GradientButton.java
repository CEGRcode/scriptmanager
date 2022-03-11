package objects;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class GradientButton extends JButton{
	
	public GradientButton(String txtLabel) {
		super(txtLabel);
		setContentAreaFilled(false);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Paint savePaint = g2.getPaint();
		//Color transparentColor = Color.WHITE;
		//Color transparentColor = new Color(255,255,255,0);
		Color transparentColor = new Color(getBackground().getRed(), getBackground().getGreen(), getBackground().getBlue(),0);
		GradientPaint newPaint = new GradientPaint(0, 0, transparentColor, 0, getHeight(), getBackground());
		
		g2.setPaint(newPaint);
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setPaint(savePaint);
		
		super.paintComponent(g);
	}
}
