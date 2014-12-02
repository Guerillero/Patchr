/*
 * Copyright (c) 2014 Thomas Fish <guerillero.net>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */
package net.guerillero.patchr;

import javax.swing.*;

import java.awt.*;
import java.io.*;
import java.util.*;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import java.awt.event.*;

public class Boxen extends JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2527471561792111001L;

	// Declare things to be used in boxen
	JButton openFileOneButton, openFileTwoButton, clearButton, diffButton,
			saveButton, patchButton;
	JTextPane differance;
	JFileChooser NYB;
	JScrollPane scrollx;
	String file1Path = "";
	String file2Path = "";
	String savePath = "";
	String stream = "";
	largeString z;

	// Boxen mostly works with the buttons and layout.
	public Boxen() {
		NYB = new JFileChooser();

		// Text area for the diff
		differance = new JTextPane();
		differance.setContentType("text/html");
		differance.setEditable(true);
		differance.setMargin(new Insets(10, 10, 10, 10));

		// Set a button to open both files
		diffButton = new JButton("Generate diff");
		patchButton = new JButton("Generate patch");
		clearButton = new JButton("Clear");
		saveButton = new JButton("Save");
		openFileOneButton = new JButton("Open file 1");
		openFileTwoButton = new JButton("Open file 2");

		//Set up Action Listeners
		clearButton.addActionListener(this);
		diffButton.addActionListener(this);
		patchButton.addActionListener(this);
		saveButton.addActionListener(this);
		openFileOneButton.addActionListener(this);
		openFileTwoButton.addActionListener(this);

		// make the panels
		JPanel OpenPanel = new JPanel();
		JPanel AdvancePanel = new JPanel();

		// add buttons to the panels
		OpenPanel.add(openFileOneButton);
		OpenPanel.add(openFileTwoButton);
		AdvancePanel.add(clearButton);
		AdvancePanel.add(diffButton);
		AdvancePanel.add(patchButton);
		AdvancePanel.add(saveButton);

		// make box
		Box lifterPuller = Box.createVerticalBox();
		lifterPuller.setSize(getMaximumSize());

		// LEGAL NOTICE
		JLabel LEGAL1 = new JLabel();
		JLabel LEGAL2 = new JLabel();
		LEGAL1.setText("<html><br><br>Created using `Diff Match and Patch` by Google. (Released under the Apache License 2.0)</html>");
		LEGAL1.setAlignmentX(CENTER_ALIGNMENT);
		LEGAL2.setText("<html><br>The modifications to DMP and all other code is by Thomas Fish.</html>");
		LEGAL2.setAlignmentX(CENTER_ALIGNMENT);

		JLabel WARNING = new JLabel();
		WARNING.setText("WARNING: Do not resize this window.");
		WARNING.setAlignmentX(CENTER_ALIGNMENT);

		// Make the dam text area scroll
		scrollx = new JScrollPane(differance,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		// put the boxes in further boxes
		lifterPuller.add(scrollx);
		lifterPuller.add(OpenPanel);
		lifterPuller.add(AdvancePanel);
		lifterPuller.add(LEGAL1);
		lifterPuller.add(LEGAL2);
		lifterPuller.add(WARNING);

		//Add liferpuller to life (in main)
		add(lifterPuller);
		
		//I know this is a hack but I can't think of any other way to make a bigger space for the text
		String f = "Your diff will go here here";
		for (int i = 0; i < 25; i++) {
			f = f + "<br>";
		}

		differance.setText(f);
	}

	// the guts of this operation
	public void guts(ActionEvent omega) {
		
		//File One
		System.out.println("Action Listend to");
		if (omega.getSource() == openFileOneButton) {
			int reply = NYB.showOpenDialog(Boxen.this);

			if (reply == JFileChooser.APPROVE_OPTION) {
				file2Path = NYB.getSelectedFile().getPath();
			} else {   //NULL
			}
		}

		//File Two
		else if (omega.getSource() == openFileTwoButton) {
			int reply = NYB.showOpenDialog(Boxen.this);
			if (reply == JFileChooser.APPROVE_OPTION) {
				file1Path = NYB.getSelectedFile().getPath();
			} else {   //NULL
			}
			
		} else if (omega.getSource() == clearButton) {
			// Clear Everything
			differance.setText(null);
		
		//Generate the diff
		} else if (omega.getSource() == diffButton) {
			String holdSteady = diffCreator(file1Path, file2Path);
			differance.setText(holdSteady);
		
		//Save the file
		} else if (omega.getSource() == saveButton) {
			int reply = NYB.showOpenDialog(Boxen.this);

			if (reply == JFileChooser.APPROVE_OPTION) {
				savePath = NYB.getSelectedFile().getPath();
				saver(stream, savePath);
			}else {   //NULL
			}
		
		//Generate the patch
		} else {
			String daria = patchCreator(file1Path, file2Path);
			differance.setText(daria);
		}
	}

	//Function that saves the file.
	public void saver(String v, String path) {
		try {
			BufferedWriter fout = new BufferedWriter(new FileWriter(path));
			fout.write(v);
			fout.close();
		} catch (IOException e) {
		}
	}

	// Opens the files, creates the diff, then returns it.
	public String diffCreator(String PathOne, String PathTwo) {
		largeString fin1String = new largeString();
		largeString fin2String = new largeString();
		largeString diffString = new largeString();

		// All of the things needed to run this function
		File FOne = new File(PathOne);
		File FTwo = new File(PathTwo);
		Scanner fin1;
		Scanner fin2;

		// This needs to be in a try to prevent errors
		try {
			// Make two scanners using the files that I already created
			fin1 = new Scanner(FOne);
			fin2 = new Scanner(FTwo);

			fin1String.setLS(fileMagic(fin1));
			fin2String.setLS(fileMagic(fin2));

			// Close the files
			fin2.close();
			fin1.close();
		} catch (FileNotFoundException e) {
		}

		// make DMP object
		diff_match_patch p = new diff_match_patch();
		//Don't timeout
		p.Diff_Timeout = 0;

		// make LL and populated it with diffs
		LinkedList<Diff> d = p
				.diff_main(fin1String.getLS(), fin2String.getLS());

		// enter the html into the diffstring
		diffString.setLS(p.diff_prettyHtml(d));

		// for saving purposes
		stream = diffString.getLS();

		return diffString.getLS();
	}

	//Read in the files. Generate the patch. Output it.
	public String patchCreator(String PathOne, String PathTwo) {
		largeString fin1String = new largeString();
		largeString fin2String = new largeString();
		largeString diffString = new largeString();

		// All of the things needed to run this function
		File FOne = new File(PathOne);
		File FTwo = new File(PathTwo);
		Scanner fin1;
		Scanner fin2;

		// This needs to be in a try to prevent errors
		try {
			// Make two scanners using the files that I already created
			fin1 = new Scanner(FOne);
			fin2 = new Scanner(FTwo);

			// Set the strings to the text of the files
			fin1String.setLS(fileMagic(fin1));
			fin2String.setLS(fileMagic(fin2));

			// Close the files
			fin2.close();
			fin1.close();
		} catch (FileNotFoundException e) {
		}

		// make DMP object
		diff_match_patch p = new diff_match_patch();
		p.Diff_Timeout = 0;

		// Make the patch and then turn it into text
		diffString.setLS( p.patch_toText( p.patch_make( fin1String.getLS(), fin2String.getLS() ) ) );
		stream = diffString.getLS();
		return stream;
	}

	// Does all of the file work of the read in
	public String fileMagic(Scanner s) {
		largeString z = new largeString();
		while (s.hasNext()) {
			z.setLS(z.getLS() + s.nextLine()
					+ System.getProperty("line.separator"));
		}
		return z.getLS();
	}

	public static void main(String[] args) {
		// Setup GUI
		JFrame life = new JFrame("Patchr");
		life.add(new Boxen());
		life.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Make the program the size of the screen
		Toolkit foo = Toolkit.getDefaultToolkit();
		life.setSize(foo.getScreenSize());

		// Turn on GUI
		life.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {

		guts(ae);

	}
}