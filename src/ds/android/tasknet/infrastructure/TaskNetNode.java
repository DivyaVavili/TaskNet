package ds.android.tasknet.infrastructure;

import ds.android.tasknet.config.Preferences;
import ds.android.tasknet.distributor.TaskDistributor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class TaskNetNode implements ActionListener {

    JScrollPane scrollPane;
    JPanel mainPanel, scrollPanel, btnPanel, taskPanel, paraPanel, classPanel;
    JLabel host_label;
    JTextArea taMessages;
    JTextField tfMethodName, tfTaskLoad, tfClassFile, tfParams[];
    JFrame mainFrame;
    JButton btnDistributeTask, btnExecuteLocalTask, btnEnter, btnClassFile;
    String host, configuration_file, classFile;
    TaskDistributor distributor;
    Serializable[] paramsToSend;
    Class[] paramTypes;
    final int FRAME_WIDTH = 600;
    final int FRAME_HEIGHT = 150;

    public TaskNetNode(String host_name, String conf_file, String ipAddr) {
        Preferences.setHostDetails(conf_file, host_name);
        host = host_name;
        configuration_file = conf_file;
        distributor = new TaskDistributor(host, configuration_file, ipAddr);
        buildUI(conf_file);
    }

    void buildUI(String conf_file) {
        mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout());

        host_label = new JLabel(host.toUpperCase());
        mainPanel.add(host_label);

        classPanel = new JPanel(new FlowLayout());
        classPanel.add(new JLabel("Choose the class file:"));
        tfClassFile = new JTextField(20);
        classPanel.add(tfClassFile);
        btnClassFile = new JButton("Browse");
        btnClassFile.addActionListener(this);
        classPanel.add(btnClassFile);
        mainPanel.add(classPanel);

        taskPanel = new JPanel(new FlowLayout());

        taskPanel.add(new JLabel("Method Name: "));
        tfMethodName = new JTextField(10);
        taskPanel.add(tfMethodName);
        taskPanel.add(new JLabel("Task Load: "));
        tfTaskLoad = new JTextField(10);
        taskPanel.add(tfTaskLoad);
        mainPanel.add(taskPanel);

        btnDistributeTask = new JButton("Distribute task");
        btnDistributeTask.addActionListener(this);
//        btnDistributeTask.setEnabled(false);
        btnExecuteLocalTask = new JButton("Execute Task Locally");
        btnExecuteLocalTask.addActionListener(this);
//        btnExecuteLocalTask.setEnabled(false);

        btnEnter = new JButton("Enter parameters");
        btnEnter.addActionListener(this);

        btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(btnEnter);
        btnPanel.add(btnDistributeTask);
        btnPanel.add(btnExecuteLocalTask);
        mainPanel.add(btnPanel);

        paraPanel = new JPanel(new FlowLayout());
        mainPanel.add(paraPanel);

        mainFrame = new JFrame("TaskNet: " + host);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
        mainFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        mainFrame.add(mainPanel);
        mainFrame.setContentPane(mainPanel);
        mainFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == btnClassFile) {
            class MyFilter extends javax.swing.filechooser.FileFilter {

                public boolean accept(File file) {
                    if (file.isDirectory()) {
                        return true;
                    } else {
                        String filename = file.getName();
                        return filename.endsWith(".class");
                    }
                }

                public String getDescription() {
                    return "*.class";
                }
            }
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.addChoosableFileFilter(new MyFilter());
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                tfClassFile.setText(file.getAbsolutePath());
            }
        } else {
            if (tfMethodName.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Enter Method name", "Method Name", JOptionPane.WARNING_MESSAGE);
            } else if (tfTaskLoad.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Enter task load", "Task load", JOptionPane.WARNING_MESSAGE);
            }
            if (ae.getSource() == btnDistributeTask) {
                try {
//                    String className = tfClassFile.getText().substring(0, tfClassFile.getText().indexOf(".class"));
//                    String pTypes = "";
//                    for (int i = 0; i < tfParams.length; i++) {
//                        if (tfParams[i].getText().isEmpty()) {
//                            JOptionPane.showMessageDialog(null, "Enter all the parameters", "Parameter error", JOptionPane.WARNING_MESSAGE);
//                            return;
//                        } else {
//                            System.out.println("\n" + paramTypes[i].getSimpleName() + " " + tfParams[i].getText().getClass().toString());
//                            paramsToSend[i] = (Serializable)paramTypes[i].cast((Object)tfParams[i].getText());
//                            pTypes += " " + paramsToSend[i].getClass().toString();
//                        }
//                    }
//                    System.out.println("Parameter types after conversion: " + pTypes);
                    Serializable[] parameters = new Serializable[2];
                    parameters[0] = 10;
                    parameters[1] = 20;
                    distributor.distribute(tfClassFile.getText().substring(0, tfClassFile.getText().indexOf(".class")), tfMethodName.getText(), parameters, new Integer(tfTaskLoad.getText()));
                } catch (SecurityException ex) {
                    Logger.getLogger(TaskNetNode.class.getName()).log(Level.SEVERE, null, ex);
                } 
            } else if (ae.getSource() == btnExecuteLocalTask) {
                //System.out.println((new ArrayList<Double>((new SampleApplicationLocal()).method1(10, 20))).toString());
                distributor.executeTaskLocally(tfMethodName.getText(), new Integer(tfTaskLoad.getText()));
            } else if (ae.getSource() == btnEnter) {
                if (tfClassFile.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Choose class file", "Class File", JOptionPane.WARNING_MESSAGE);
                } else if (tfMethodName.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Enter Method name", "Method Name", JOptionPane.WARNING_MESSAGE);
                } else {
                    this.checkMethod(tfClassFile.getText(), tfMethodName.getText());
                }
            }
        }
    }

    private void checkMethod(String strClassName, String methodName) {
        try {
            boolean found = false;
            Method method = null;
            String className = strClassName.substring(0, strClassName.indexOf(".class"));
            Class aClass = Class.forName(className);
//            Class aClass = Class.forName("ds.android.tasknet.application.SampleApplicationLocal");
            Method[] methods = aClass.getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equals(methodName)) {
                    method = methods[i];
                    found = true;
                    break;
                }
            }
            if (found == true) {
                paramTypes = method.getParameterTypes();
                for (int i = 0; i < paramTypes.length; i++) {
                    System.out.print(paramTypes[i].getSimpleName() + " ");
                }
                this.repaintPanel(paramTypes.length, paramTypes);
            }
            if (found == false) {
                JOptionPane.showMessageDialog(null, "Method doesn't exist", "Method Name", JOptionPane.WARNING_MESSAGE);
            }
        } catch (ClassNotFoundException ex) {
            System.out.println(strClassName.substring(0, strClassName.indexOf(".class")));
            JOptionPane.showMessageDialog(null, "Class doesn't exist", "Class Name", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void repaintPanel(int paraNum, Class[] para) {
        tfParams = new JTextField[paraNum];
        GridLayout gridLayout = new GridLayout(paraNum, 2);
        gridLayout.setHgap(5);
        gridLayout.setVgap(5);
        mainFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT + (paraNum * 30));

        paraPanel.removeAll();
        paraPanel.setLayout(gridLayout);
        paraPanel.setSize(400, paraNum * 30);
        
        for (int i = 0; i < paraNum; i++) {
            paraPanel.add(new JLabel(para[i].getSimpleName() + ": "));
            tfParams[i] = new JTextField(10);
            paraPanel.add(tfParams[i]);
        }
        paraPanel.revalidate();
    }
}
