package view;

import datasets.Dataset;
import datasets.GraphDataset;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.text.NumberFormat;
import java.text.ParseException;

public class TopPanel extends JPanel {
    public static final int BUTTON_WIDTH = 100;
    public static final int BUTTON_HEIGHT = 30;

    private View view;

    private JToolBar toolBar;

    private JButton newButton;
    private JPopupMenu newPopup;
    private JButton exportButton;
    private JButton algorithmButton;

    public TopPanel(View view) {
        this.view = view;

        setPreferredSize(new Dimension(view.getWindowWidth(), view.getWindowHeight()));
        setLayout(null);

        toolBar = new JToolBar();
        toolBar.setLayout(null);

        addNewButton();
        addExportButton();
        addAlgorithmButton();

        add(toolBar);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    private void addNewButton() {
        newPopup = new JPopupMenu();

        //Create new dataset
        newPopup.add(new JMenuItem(new AbstractAction("Create new dataset") {
            public void actionPerformed(ActionEvent e) {
                ExtendedNumberFormatter formatter = createNumberFormatter();
                JFormattedTextField pointsTextField = new JFormattedTextField(formatter);
                JFormattedTextField dimensionTextField = new JFormattedTextField(formatter);
                JFormattedTextField clusterTextField = new JFormattedTextField(formatter);

                JComboBox<String> comboBox = new JComboBox<>();
                for (String type : Dataset.supportedDatasetTypes) {
                    comboBox.addItem(type);
                }
                comboBox.setAlignmentX(LEFT_ALIGNMENT);

                JPanel createPopupPanel = new JPanel();
                createPopupPanel.setLayout(new BoxLayout(createPopupPanel, BoxLayout.PAGE_AXIS));
                createPopupPanel.add(new JLabel("Number of points"));
                createPopupPanel.add(pointsTextField);
                createPopupPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                createPopupPanel.add(new JLabel("Number of dimensions"));
                createPopupPanel.add(dimensionTextField);
                createPopupPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                createPopupPanel.add(new JLabel("Number of clusters"));
                createPopupPanel.add(clusterTextField);
                createPopupPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                createPopupPanel.add(comboBox);

                int createResult = JOptionPane.showConfirmDialog(view, createPopupPanel,
                        "Choose parameters", JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);

                if (createResult == JOptionPane.OK_OPTION) {
                    int points = Integer.parseInt(getTextFieldValue(pointsTextField, 1000));
                    int dimensions = Integer.parseInt(getTextFieldValue(dimensionTextField, 2));
                    int clusters = Integer.parseInt(getTextFieldValue(clusterTextField, 4));
                    String datasetType = (String) comboBox.getSelectedItem();
                    view.createDataset(datasetType, points, dimensions, clusters);
                }
            }
        }));

        //Load dataset
        newPopup.add(new JMenuItem(new AbstractAction("Load new dataset") {
            public void actionPerformed(ActionEvent e) {
                final File[] file = new File[1];
                JPanel savePopupPanel = new JPanel();
                savePopupPanel.setLayout(new BoxLayout(savePopupPanel, BoxLayout.LINE_AXIS));
                JLabel label = new JLabel("No file selected");
                JButton fileButton = new JButton("Select File");
                fileButton.addActionListener((l) -> {
                    final JFileChooser fc = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter(".csv, .dot", "csv", "dot");
                    fc.setFileFilter(filter);
                    int returnVal = fc.showDialog(view, "Choose");
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        file[0] = fc.getSelectedFile();
                        label.setText(file[0].getName());
                    }
                });

                savePopupPanel.add(label);
                savePopupPanel.add(Box.createRigidArea(new Dimension(10, 0)));
                savePopupPanel.add(fileButton);

                JComboBox<String> comboBox = new JComboBox<>();
                for (String type : Dataset.supportedDatasetTypes) {
                    comboBox.addItem(type);
                }
                JPanel loadPopupPanel = new JPanel();
                loadPopupPanel.setLayout(new BoxLayout(loadPopupPanel, BoxLayout.PAGE_AXIS));
                loadPopupPanel.add(savePopupPanel);
                loadPopupPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                loadPopupPanel.add(comboBox);

                int loadResult = JOptionPane.showConfirmDialog(view, loadPopupPanel,
                        "Load dataset", JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);

                Object selectedFile = file[0];
                String datasetType = (String) comboBox.getSelectedItem();

                if (loadResult == JOptionPane.OK_OPTION && file[0] != null) {
                    String fileName = file[0].getAbsolutePath();
                    if (datasetType.equals(GraphDataset.name)) {
                        view.loadDatasetFromFile(datasetType, fileName, 0, 0, 0, 0);
                        return;
                    }
                    ExtendedNumberFormatter formatter = createNumberFormatter();
                    JFormattedTextField startRowTextField = new JFormattedTextField(formatter);
                    JFormattedTextField endRowTextField = new JFormattedTextField(formatter);
                    JFormattedTextField startColTextField = new JFormattedTextField(formatter);
                    JFormattedTextField endColTextField = new JFormattedTextField(formatter);

                    JPanel box1 = new JPanel();
                    box1.setLayout(new BoxLayout(box1, BoxLayout.LINE_AXIS));
                    JPanel box2 = new JPanel();
                    box2.setLayout(new BoxLayout(box2, BoxLayout.LINE_AXIS));

                    JPanel startRowPane = new JPanel();
                    startRowPane.setLayout(new BoxLayout(startRowPane, BoxLayout.PAGE_AXIS));
                    startRowPane.add(new JLabel("Start row:"));
                    startRowPane.add(startRowTextField);

                    JPanel endRowPane = new JPanel();
                    endRowPane.setLayout(new BoxLayout(endRowPane, BoxLayout.PAGE_AXIS));
                    endRowPane.add(new JLabel("End row:"));
                    endRowPane.add(endRowTextField);

                    JPanel startColumnPane = new JPanel();
                    startColumnPane.setLayout(new BoxLayout(startColumnPane, BoxLayout.PAGE_AXIS));
                    startColumnPane.add(new JLabel("Start column:"));
                    startColumnPane.add(startColTextField);

                    JPanel endColumnPane = new JPanel();
                    endColumnPane.setLayout(new BoxLayout(endColumnPane, BoxLayout.PAGE_AXIS));
                    endColumnPane.add(new JLabel("End column:"));
                    endColumnPane.add(endColTextField);

                    box1.add(startRowPane);
                    box1.add(Box.createRigidArea(new Dimension(5, 0)));
                    box1.add(endRowPane);
                    box2.add(startColumnPane);
                    box2.add(Box.createRigidArea(new Dimension(5, 0)));
                    box2.add(endColumnPane);

                    JPanel parameterPopupPanel = new JPanel();
                    parameterPopupPanel.setLayout(new BoxLayout(parameterPopupPanel, BoxLayout.PAGE_AXIS));
                    parameterPopupPanel.add(box1);
                    parameterPopupPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                    parameterPopupPanel.add(box2);

                    int parameterResult = JOptionPane.showConfirmDialog(view, parameterPopupPanel,
                            ("Choose loading parameters for " + selectedFile), JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE);

                    if (parameterResult == JOptionPane.OK_OPTION) {
                        int startRow = Integer.parseInt(getTextFieldValue(startRowTextField, 0));
                        int endRow = Integer.parseInt(getTextFieldValue(endRowTextField, -1));
                        int startCol = Integer.parseInt(getTextFieldValue(startColTextField, 0));
                        int endCol = Integer.parseInt(getTextFieldValue(endColTextField ,-1));
                        view.loadDatasetFromFile(datasetType, fileName, startRow, endRow, startCol, endCol);
                    }
                }
            }
        }));

        //Create button on toolbar
        newButton = new JButton("New");
        newButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                newPopup.show(e.getComponent(), 0, newButton.getY() + BUTTON_HEIGHT);
            }
        });
        toolBar.add(newButton);
    }

    private void addExportButton() {
        final JPopupMenu exportPopup = new JPopupMenu();
        exportPopup.add(new JMenuItem(new AbstractAction("Save dataset to file") {
            public void actionPerformed(ActionEvent e) {
                final File[] file = new File[1];
                JPanel savePopupPanel = new JPanel();
                savePopupPanel.setLayout(new BoxLayout(savePopupPanel, BoxLayout.LINE_AXIS));
                JLabel label = new JLabel("No file selected");
                JButton button = new JButton("Select File");
                button.addActionListener((l) -> {
                    final JFileChooser fc = new JFileChooser();
                    String extension = view.getDataset() instanceof GraphDataset ? "dot" : "csv";
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("."+extension, extension);
                    fc.setFileFilter(filter);
                    int returnVal = fc.showDialog(view, "Choose");
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        file[0] = fc.getSelectedFile();
                        if (!file[0].getName().endsWith("."+extension)) {
                            file[0] = new File(file[0].getPath()+"."+extension);
                        }
                        label.setText(file[0].getName());
                    }
                });
                savePopupPanel.add(label);
                savePopupPanel.add(Box.createRigidArea(new Dimension(10, 0)));
                savePopupPanel.add(button);
                int saveResult = JOptionPane.showConfirmDialog(view, savePopupPanel,
                        "Save dataset to file", JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);
                if (saveResult == JOptionPane.OK_OPTION && file[0] != null) {
                    view.getDataset().saveToFile(file[0]);
                }
            }
        }));
        exportButton = new JButton("Export");
        exportButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                exportPopup.show(e.getComponent(), 0, exportButton.getY() + BUTTON_HEIGHT);
            }
        });
        toolBar.add(exportButton);
    }

    private void addAlgorithmButton() {
        algorithmButton = new JButton("Algorithm");
        algorithmButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {

            }
        });
        toolBar.add(algorithmButton);
    }

    private JButton createOkayButton(JPanel panel) {
        JButton okayButton = new JButton("Okay");
        okayButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        return okayButton;
    }

    private JButton createCancelButton(JPanel panel) {
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        return cancelButton;
    }

    protected void setBounds() {
        setBounds(0, 0, view.windowWidth, view.topPanelHeight);
        toolBar.setBounds(0, 0, view.windowWidth, view.topPanelHeight);
        newButton.setBounds(BUTTON_HEIGHT, view.topPanelHeight / 2 - BUTTON_HEIGHT / 2, BUTTON_WIDTH, BUTTON_HEIGHT);
        exportButton.setBounds(BUTTON_HEIGHT + BUTTON_WIDTH, view.topPanelHeight / 2 - BUTTON_HEIGHT / 2, BUTTON_WIDTH, BUTTON_HEIGHT);
        algorithmButton.setBounds(BUTTON_HEIGHT + BUTTON_WIDTH * 2, view.topPanelHeight / 2 - BUTTON_HEIGHT / 2, BUTTON_WIDTH, BUTTON_HEIGHT);
    }

    private class ExtendedNumberFormatter extends NumberFormatter {
        public ExtendedNumberFormatter(NumberFormat f) {
            super(f);
        }

        @Override
        public Object stringToValue(String text) throws ParseException {
            if (text == "") {
                return null;
            }
            return super.stringToValue(text);
        }
    }

    private String getTextFieldValue(JFormattedTextField textfield, int defaultValue) {
        if (textfield.getValue() == null) {
            return ""+defaultValue;
        }
        return textfield.getValue().toString();
    }

    private ExtendedNumberFormatter createNumberFormatter() {
        NumberFormat format = NumberFormat.getIntegerInstance();
        format.setGroupingUsed(false);
        ExtendedNumberFormatter formatter = new ExtendedNumberFormatter(format);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        return formatter;
    }

}
