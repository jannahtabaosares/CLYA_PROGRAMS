
package com.clya.view;

/**
 *
 * @author _parall_el
 */

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
        
public class LoadingPanel extends JPanel{
    
    private static final int T_SHRINK_START = 600, T_SHRINK_END = 1100;
    private static final int T_TEXT_START = 1100, T_TEXT_END = 1700;
    private static final int T_BAR_IN_END   = 2000;
    private static final int T_BAR_END      = 4500;
    private static final int T_FINISH       = 4900;
    
    private static final Color NAVY  = new Color(0x112250);
    private static final Color LIGHT = new Color(0x7C89B5);
    private static final Color BAR_START = new Color(0x6EA8F0);
    private static final Color BAR_END   = new Color(0x1F4E9E);
    
    private final Image logo;
    private final Runnable onFinished;
    private final Timer timer;
    private long startTime;
    private boolean finished;
    
    public LoadingPanel(Runnable onFinished){
        this.onFinished = onFinished;
        this.logo = new ImageIcon(getClass().getResource("/images/logo.png")).getImage();
        
        timer = new Timer(16, e -> {
            repaint();
            if(!finished && System.currentTimeMillis() - startTime >= T_FINISH){
                finished = true;
                ((Timer) e.getSource()).stop();
                this.onFinished.run();
            }
        });
    }
    
    public void start(){
        startTime = System.currentTimeMillis();
        timer.start();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        long t = System.currentTimeMillis() - startTime;
        int w = getWidth(), h = getHeight();
        int cy = h / 2 - 20;                     

        // 1. Background: solid navy that becomes a diagonal gradient
        double bgP = ease(progress(t, T_SHRINK_START, T_TEXT_END));
        g2.setPaint(new GradientPaint(w, 0, NAVY, 0, h, mix(NAVY, LIGHT, bgP)));
        g2.fillRect(0, 0, w, h);

        // 2. Logo: shrinks, then slides left
        double s = lerp(220, 96, ease(progress(t, T_SHRINK_START, T_SHRINK_END)));
        Font font = new Font("SansSerif", Font.BOLD, 56);
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        String text = "C-LYA";
        int gap = 18, finalLogo = 96;
        int groupW = finalLogo + gap + fm.stringWidth(text);
        double finalX = (w - groupW) / 2.0;

        double slideP = ease(progress(t, T_TEXT_START, T_TEXT_END));
        double logoX = lerp((w - s) / 2.0, finalX, slideP);
        g2.drawImage(logo, (int) logoX, (int) (cy - s / 2), (int) s, (int) s, null);

        // 3. Text: revealed left to right with a growing clip
        if (slideP > 0) {
            int textX = (int) (finalX + finalLogo + gap);
            Shape oldClip = g2.getClip();
            g2.clipRect(textX, 0, (int) (fm.stringWidth(text) * slideP) + 1, h);
            g2.setColor(Color.WHITE);
            g2.drawString(text, textX, cy + (fm.getAscent() - fm.getDescent()) / 2);
            g2.setClip(oldClip);
        }

        // 4. Progress bar
        double barIn = progress(t, T_TEXT_END, T_BAR_IN_END);
        if (barIn > 0) {
            int barW = 320, barH = 8;
            int barX = (w - barW) / 2, barY = cy + finalLogo / 2 + 40;
            RoundRectangle2D track = new RoundRectangle2D.Double(barX, barY, barW, barH, barH, barH);

            g2.setColor(new Color(255, 255, 255, (int) (230 * barIn)));
            g2.fill(track);

            double fp = progress(t, T_BAR_IN_END, T_BAR_END);
            g2.setColor(mix(BAR_START, BAR_END, fp));
            g2.clip(track);
            g2.fillRect(barX, barY, (int) (barW * fp), barH);
        }
        g2.dispose();
    }

    private static double progress(long t, int a, int b) {
        return Math.max(0, Math.min(1, (t - a) / (double) (b - a)));
    }
    private static double ease(double x)            { return x * x * (3 - 2 * x); }
    private static double lerp(double a, double b, double p) { return a + (b - a) * p; }
    private static Color mix(Color a, Color b, double p) {
        return new Color(
            (int) lerp(a.getRed(),   b.getRed(),   p),
            (int) lerp(a.getGreen(), b.getGreen(), p),
            (int) lerp(a.getBlue(),  b.getBlue(),  p));
    }
}
