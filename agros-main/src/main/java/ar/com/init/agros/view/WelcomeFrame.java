        package ar.com.init.agros.view;


import java.awt.Dimension;
import java.awt.Toolkit;

 /*
  * Clase GUI WelcomeFrame
  *
  * @author fbobbio
  * @version 02-ago-2009
  */
public class WelcomeFrame extends javax.swing.JFrame
{
    /** Crea una nueva GUI tipo WelcomeFrame */
    public WelcomeFrame()
    {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension dim = kit.getScreenSize();
        int height = dim.height;
        int width = dim.width;
        this.setLocation(width / 4, height / 4);
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelImage = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setAlwaysOnTop(true);
        setResizable(false);
        setUndecorated(true);

        jLabelImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ar/com/init/agros/view/resources/appview1.png"))); // NOI18N
        jLabelImage.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 3, 3, new java.awt.Color(0, 0, 0)));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jLabelImage)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jLabelImage)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelImage;
    // End of variables declaration//GEN-END:variables

}