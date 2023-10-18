
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import javax.swing.undo.*;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author lylem
 */
public class Main extends javax.swing.JFrame {

    /**
     * Creates new form Main
     */
    String Filename = "";
    String fname = "";
    String text = "";
//    String ogText;
    boolean isSaved = true;
        boolean addedSomething = true;
            ArrayList<String> fileNames = new ArrayList<>();
        ArrayList<Boolean> saved = new ArrayList<>();
        
        UndoManager undoManager = new UndoManager();
        
    public Main() {
        
        initComponents();
        fileNames.add(null);
        saved.add(true);
        addNewFile();

    }
    
    private void addNewFile(){
        addedSomething = true;
        JPanel newTab = new JPanel(new BorderLayout());
        JTextArea textArea = new JTextArea();
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.getDocument().addDocumentListener(new DocumentListener(){
        @Override
        public void insertUpdate(DocumentEvent e){
            updateLabel();
        }

        @Override
        public void removeUpdate(DocumentEvent e){
            updateLabel();
        }

        @Override
        public void changedUpdate(DocumentEvent e){
            updateLabel();
        }

        private void updateLabel(){
            saved.set(jTabbedPane1.getSelectedIndex(), false);
            saveAs.setEnabled(true);
            if(fileNames.get(jTabbedPane1.getSelectedIndex()) != null){
                save.setEnabled(true);
            }
        }
        });
        
        textArea.getDocument().addUndoableEditListener(undoManager);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        newTab.add(scrollPane, BorderLayout.CENTER);

        int tabCount = jTabbedPane1.getTabCount() + 1;
        jTabbedPane1.addTab("Tab " + tabCount, newTab);
        jTabbedPane1.setSelectedIndex(tabCount - 1);
        jTabbedPane1.setTabComponentAt(jTabbedPane1.indexOfComponent(newTab),getTitlePanel(jTabbedPane1, newTab, "new Tab"));
    }
    
    private void saveAsFunction(){
        int selectedTabIndex = jTabbedPane1.getSelectedIndex();
        saved.set(selectedTabIndex, true);
            String text = "";
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showSaveDialog(this);
                   String extension = ".rrr";
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    Filename = selectedFile.getAbsolutePath() + extension;
                    fname = selectedFile.getName();
                    try{
                        BufferedWriter bw = new BufferedWriter(new FileWriter(Filename));
                        if (selectedTabIndex != -1) {
                        Component selectedTab = jTabbedPane1.getComponentAt(selectedTabIndex);
                        JTextComponent selectedTextArea = findTextAreaInScrollPane(selectedTab);
                        if (selectedTextArea != null) {
                        text = selectedTextArea.getText();
                        jTabbedPane1.setTabComponentAt(selectedTabIndex,getTitlePanel(jTabbedPane1,(JPanel)jTabbedPane1.getComponentAt(selectedTabIndex), fname+".rrr"));
                        jTabbedPane1.getSelectedIndex();
                        }
                        }
                        bw.write(text);
                        bw.close();
                        fileNames.set(selectedTabIndex, fname);
                        }
                    catch(Exception ex){
                        ex.printStackTrace();
                        }
                    JOptionPane.showMessageDialog(this, "File saved: " + Filename);
                    isSaved = true;
                }
        
        save.setEnabled(false);
        saveAs.setEnabled(false);
    }
    
    private void saveFunction(){
        int selectedTabIndex = jTabbedPane1.getSelectedIndex();
        saved.set(selectedTabIndex, true);
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileNames.get(selectedTabIndex)));
            if (selectedTabIndex != -1) {
            Component selectedTab = jTabbedPane1.getComponentAt(selectedTabIndex);
            JTextComponent selectedTextArea = findTextAreaInScrollPane(selectedTab);
            if (selectedTextArea != null) {
            text = selectedTextArea.getText();
            }
            }
            bw.write(text);
            bw.close();
            
            JOptionPane.showMessageDialog(this, "File saved: " + fileNames.get(selectedTabIndex));
            isSaved = true;
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        save.setEnabled(false);
        saveAs.setEnabled(false);
    }
    
    private JPanel getTitlePanel(final JTabbedPane tabbedPane, final JPanel panel, String title)
    {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);
        JLabel titleLbl = new JLabel(title);
        titleLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        titlePanel.add(titleLbl);
        JButton closeButton = new JButton("x");
        closeButton.addMouseListener(new MouseAdapter()
    {
        @Override
        public void mouseClicked(MouseEvent e)
        {
            if (saved.get(jTabbedPane1.getSelectedIndex()) == false){
                int response = JOptionPane.showConfirmDialog(null, "Do you want to save your changes first?", "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION);

                if (response == JOptionPane.YES_OPTION){
                    if(fileNames.get(jTabbedPane1.getSelectedIndex()) != null){
                        saveFunction();
                    }
                    else {
                        saveAsFunction();
                    }
                }
                else if (response == JOptionPane.NO_OPTION){
                    tabbedPane.remove(panel);
                    fileNames.remove(jTabbedPane1.getSelectedIndex()+1);
                    saved.remove(jTabbedPane1.getSelectedIndex()+1);
                }

            }
            else{
                tabbedPane.remove(panel);
                fileNames.remove(jTabbedPane1.getSelectedIndex()+1);
                saved.remove(jTabbedPane1.getSelectedIndex()+1);
            }
            
            
        }
        });
        titlePanel.add(closeButton);

        return titlePanel;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        save = new javax.swing.JButton();
        run = new javax.swing.JButton();
        openFile = new javax.swing.JButton();
        compile = new javax.swing.JButton();
        newFile = new javax.swing.JButton();
        saveAs = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        undoButton = new javax.swing.JButton();
        redoButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(Filename);

        save.setIcon(new javax.swing.ImageIcon("C:\\Users\\lylem\\Downloads\\floppy-disk (1).png")); // NOI18N
        save.setEnabled(false);
        save.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                saveMouseEntered(evt);
            }
        });
        save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveActionPerformed(evt);
            }
        });

        run.setIcon(new javax.swing.ImageIcon("C:\\Users\\lylem\\Downloads\\run (1).png")); // NOI18N
        run.setEnabled(false);
        run.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                runMouseEntered(evt);
            }
        });
        run.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runActionPerformed(evt);
            }
        });

        openFile.setIcon(new javax.swing.ImageIcon("C:\\Users\\lylem\\Downloads\\folder.png")); // NOI18N
        openFile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                openFileMouseEntered(evt);
            }
        });
        openFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openFileActionPerformed(evt);
            }
        });

        compile.setIcon(new javax.swing.ImageIcon("C:\\Users\\lylem\\Downloads\\compiler.png")); // NOI18N
        compile.setEnabled(false);
        compile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                compileMouseEntered(evt);
            }
        });

        newFile.setIcon(new javax.swing.ImageIcon("C:\\Users\\lylem\\Downloads\\save-as.png")); // NOI18N
        newFile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                newFileMouseEntered(evt);
            }
        });
        newFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newFileActionPerformed(evt);
            }
        });

        saveAs.setText("As");
        saveAs.setEnabled(false);
        //saveAs.setEnabled(false);
        saveAs.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                saveAsMouseEntered(evt);
            }
        });
        saveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsActionPerformed(evt);
            }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);
        jTextArea1.getDocument().addDocumentListener(new DocumentListener(){
            @Override
            public void insertUpdate(DocumentEvent e){
                updateLabel();
            }

            @Override
            public void removeUpdate(DocumentEvent e){
                updateLabel();
            }

            @Override
            public void changedUpdate(DocumentEvent e){
                updateLabel();
            }

            private void updateLabel(){
                isSaved = false;
                save.setEnabled(true);
                saveAs.setEnabled(true);
                newFile.setEnabled(true);
                //    System.out.println("HELLOOOO");
            }
        });

        undoButton.setText("jButton1");
        undoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoButtonActionPerformed(evt);
            }
        });

        redoButton.setText("redo");
        redoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 533, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(66, 66, 66))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(newFile, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(save, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(undoButton)
                        .addGap(18, 18, 18)
                        .addComponent(redoButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(saveAs, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(openFile, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42)
                        .addComponent(compile, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(run, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(108, 108, 108))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(compile, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(openFile, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(run, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(save, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(newFile, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(saveAs, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(undoButton)
                        .addComponent(redoButton)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(40, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(61, 61, 61))))
        );

        //save.setEnabled(false);
        jTabbedPane1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (jTabbedPane1.getSelectedIndex()>=0){
                    int selectedTabIndex = jTabbedPane1.getSelectedIndex();
                    Component selectedTab = jTabbedPane1.getComponentAt(selectedTabIndex);

                    if (saved.get(selectedTabIndex) == true){
                        save.setEnabled(false);
                        saveAs.setEnabled(false);
                    }
                    else{

                        saveAs.setEnabled(true);
                        if (fileNames.get(selectedTabIndex) != null){
                            save.setEnabled(true);
                        }
                    }
                }
            }
        });

        pack();
    }// </editor-fold>//GEN-END:initComponents

 
     private static JTextArea findTextAreaInScrollPane(Component component) {
        if (component instanceof Container) {
            Component[] components = ((Container) component).getComponents();
            for (Component comp : components) {
                if (comp instanceof JScrollPane) {
                    Component[] scrollPaneComponents = ((JScrollPane) comp).getViewport().getComponents();
                    for (Component scrollComp : scrollPaneComponents) {
                        if (scrollComp instanceof JTextArea) {
                            return (JTextArea) scrollComp;
                        }
                    }
                }
            }
        }
        return null;
    }
     
    private void runActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_runActionPerformed

    private void saveMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveMouseEntered
        // TODO add your handling code here:
        save.setToolTipText("Save");
    }//GEN-LAST:event_saveMouseEntered

    private void newFileMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_newFileMouseEntered
        // TODO add your handling code here:
        newFile.setToolTipText("New File");
    }//GEN-LAST:event_newFileMouseEntered

    private void saveAsMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveAsMouseEntered
        // TODO add your handling code here:
        saveAs.setToolTipText("Save As");
    }//GEN-LAST:event_saveAsMouseEntered

    private void openFileMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_openFileMouseEntered
        // TODO add your handling code here:
        openFile.setToolTipText("Open File");
    }//GEN-LAST:event_openFileMouseEntered

    private void compileMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_compileMouseEntered
        // TODO add your handling code here:
        compile.setToolTipText("Compile");
    }//GEN-LAST:event_compileMouseEntered

    private void runMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_runMouseEntered
        // TODO add your handling code here:
        run.setToolTipText("Run");
    }//GEN-LAST:event_runMouseEntered

    private void saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveActionPerformed
        // TODO add your handling code here:
        saveFunction();
    }//GEN-LAST:event_saveActionPerformed

    private void saveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsActionPerformed
        saveAsFunction();
    }//GEN-LAST:event_saveAsActionPerformed

    private void openFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openFileActionPerformed
        // TODO add your handling code here:
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    int result = fileChooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
          try {
            StringBuilder fileContents = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
            String line;
            while ((line = reader.readLine()) != null) {
                fileContents.append(line).append("\n");
            }
            reader.close();
            JPanel newTab = new JPanel(new BorderLayout());
            JTextArea textArea = new JTextArea();
            textArea.setWrapStyleWord(true);
            textArea.setLineWrap(true);
            JScrollPane scrollPane = new JScrollPane(textArea);
            newTab.add(scrollPane, BorderLayout.CENTER);
            textArea.setText(fileContents.toString());
            textArea.getDocument().addDocumentListener(new DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e){
                    updateLabel();
                }

                @Override
                public void removeUpdate(DocumentEvent e){
                    updateLabel();
                }

                @Override
                public void changedUpdate(DocumentEvent e){
                    updateLabel();
                }

                private void updateLabel(){
                    saved.set(jTabbedPane1.getSelectedIndex(), false);
                    saveAs.setEnabled(true);
                    save.setEnabled(true);
                }
            });
            Filename = selectedFile.getAbsolutePath();
            fname = selectedFile.getName();
            fileNames.add(Filename);
            saved.add(true);
            int tabCount = jTabbedPane1.getTabCount() + 1;
            jTabbedPane1.addTab("Tab " + tabCount, newTab);
            jTabbedPane1.setSelectedIndex(tabCount - 1);
            jTabbedPane1.setTabComponentAt(jTabbedPane1.indexOfComponent(newTab),getTitlePanel(jTabbedPane1, newTab, fname));
            setTitle(Filename);
            save.setEnabled(false);
            saveAs.setEnabled(false);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error reading the file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    }//GEN-LAST:event_openFileActionPerformed

    private void newFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newFileActionPerformed
        // TODO add your handling code here:
        fileNames.add(null);
        saved.add(true);
        addNewFile();
    }//GEN-LAST:event_newFileActionPerformed

    private void undoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoButtonActionPerformed
        if(undoManager.canUndo()){
            try{
                undoManager.undo();
            }
            catch(CannotUndoException ex){
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_undoButtonActionPerformed

    private void redoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoButtonActionPerformed
        // TODO add your handling code here:
        if(undoManager.canRedo()){
            try{
                undoManager.redo();
            }
            catch(CannotRedoException ex){
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_redoButtonActionPerformed

    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
//        System.out.println("HELLO WORLD!");
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
        

        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton compile;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JButton newFile;
    private javax.swing.JButton openFile;
    private javax.swing.JButton redoButton;
    private javax.swing.JButton run;
    private javax.swing.JButton save;
    private javax.swing.JButton saveAs;
    private javax.swing.JButton undoButton;
    // End of variables declaration//GEN-END:variables
}
