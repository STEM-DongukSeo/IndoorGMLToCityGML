package edu.pnu.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SampleUI extends JFrame {

	private JPanel contentPane;
	private JPanel panel;
	private JLabel lblIndoorgmlDocument;
	private JTextField textField_IndoorGML;
	private JButton btnLoad;
	private JTextArea textArea;
	private JButton btnTranslate;
	private JLabel lblCitygmlDocument;
	private JTextField textField_CityGML;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SampleUI frame = new SampleUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SampleUI() {
		setTitle("IndoorGML to CityGML");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 398, 314);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		contentPane.add(getPanel(), BorderLayout.CENTER);
	}

	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(null);
			panel.add(getLblIndoorgmlDocument());
			panel.add(getTextField_IndoorGML());
			panel.add(getBtnLoad());
			panel.add(getTextArea());
			panel.add(getBtnTranslate());
			panel.add(getLblCitygmlDocument());
			panel.add(getTextField_CityGML());
		}
		return panel;
	}
	private JLabel getLblIndoorgmlDocument() {
		if (lblIndoorgmlDocument == null) {
			lblIndoorgmlDocument = new JLabel("IndoorGML Document");
			lblIndoorgmlDocument.setFont(new Font("Arial", Font.BOLD, 16));
			lblIndoorgmlDocument.setBounds(12, 10, 176, 19);
		}
		return lblIndoorgmlDocument;
	}
	private JTextField getTextField_IndoorGML() {
		if (textField_IndoorGML == null) {
			textField_IndoorGML = new JTextField();
			textField_IndoorGML.setBounds(12, 39, 242, 21);
			textField_IndoorGML.setColumns(10);
		}
		return textField_IndoorGML;
	}
	private JButton getBtnLoad() {
		if (btnLoad == null) {
			btnLoad = new JButton("Load");
			btnLoad.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					JFileChooser fileChooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("IndoorGML Document (*.gml)",
                            "gml");
                    fileChooser.setFileFilter(filter);

                    int returnVal = fileChooser.showOpenDialog(SampleUI.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                    	File file = fileChooser.getSelectedFile();
                    	textField_IndoorGML.setText(file.getAbsolutePath());
                    	textArea.append("Load IndoorGML Document.\n");
                    }
				}
			});
			btnLoad.setBounds(266, 38, 97, 23);
		}
		return btnLoad;
	}
	private JTextArea getTextArea() {
		if (textArea == null) {
			textArea = new JTextArea();
			textArea.setBounds(12, 134, 351, 122);
		}
		return textArea;
	}
	private JButton getBtnTranslate() {
		if (btnTranslate == null) {
			btnTranslate = new JButton("Translate");
			btnTranslate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser fileChooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("CityGML Document (*.gml)",
                            "gml");
                    fileChooser.setFileFilter(filter);

                    int returnVal = fileChooser.showSaveDialog(SampleUI.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {                    	
                        File file = fileChooser.getSelectedFile();
                        textField_CityGML.setText(file.getAbsolutePath());
            	    	textArea.append("Translate IndoorGML to CityGML...\n");
            	    	sleep(1500);
            			textArea.append("Create CityGML Document.\n");                
            			textArea.append("Done.\n");
                    }
				}
			});
			btnTranslate.setBounds(266, 102, 97, 23);
		}
		return btnTranslate;
	}
	private JLabel getLblCitygmlDocument() {
		if (lblCitygmlDocument == null) {
			lblCitygmlDocument = new JLabel("CityGML Document");
			lblCitygmlDocument.setFont(new Font("Arial", Font.BOLD, 16));
			lblCitygmlDocument.setBounds(12, 78, 176, 15);
		}
		return lblCitygmlDocument;
	}
	private JTextField getTextField_CityGML() {
		if (textField_CityGML == null) {
			textField_CityGML = new JTextField();
			textField_CityGML.setBounds(12, 103, 242, 21);
			textField_CityGML.setColumns(10);
		}
		return textField_CityGML;
	}
	
	public void sleep(int time){
	    try {
			Thread.sleep(time);
	    } catch (InterruptedException e) { }

	}
}
