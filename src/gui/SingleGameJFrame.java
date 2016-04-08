package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import viewmodel.SingleGameCanvas;
import datamodel.Database;

public class SingleGameJFrame extends JFrame {
	SingleGameCanvas singleGameCanvas;
	Database database;
	int gameIndex;

	JPanel mainPanel;
	
	JMenuBar menuBar;
	JMenu attrMenu;
	JMenuItem playerAttr;
	JMenuItem teamAttr;
	
	JMenu viewMenu;
	JMenuItem statItem;
	JMenu connectItem;
	JMenuItem all;
	JMenuItem foul;
	JMenuItem steal;
	JMenuItem block;
	
	JMenu layoutMenu;
	JMenuItem position;
	JMenuItem start;
	JMenuItem plusMinus;
	JMenuItem points;
	JMenuItem minutes;

	//width, height for window and canvas
	int winWidth = 1920, winHeight = 1080;
	int singleGameCanvasWidth = 1920, singleGameCanvasHeight = 1040;
	
	public SingleGameJFrame (int gameIndex, Database database) {
		this.gameIndex = gameIndex;
		this.database = database;
		int leftTeam = database.gameMap.get(gameIndex).leftTeamIndex;
		int rightTeam = database.gameMap.get(gameIndex).rightTeamIndex;
		String team1 = database.teamsMap.get(leftTeam).name;
		String team2 = database.teamsMap.get(rightTeam).name;
		
		this.setTitle(team1 + " VS." + team2);
		setSize(new Dimension(winWidth, winHeight));
		
		initComponents();
		initVisualization();
		
		setVisible(true);
	}
	
	private void initVisualization () {
		singleGameCanvas.init();
	}

	private void initComponents() {
		assert SwingUtilities.isEventDispatchThread();

		menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		attrMenu = new JMenu("Attribute");
		viewMenu = new JMenu("View");
		layoutMenu = new JMenu("Layout");
		menuBar.add(attrMenu);
		menuBar.add(viewMenu);
		menuBar.add(layoutMenu);
		
		playerAttr = new JMenuItem("Player");
		teamAttr = new JMenuItem("Team");
		
		attrMenu.add(playerAttr);
		attrMenu.add(teamAttr);
		
		statItem = new JMenuItem("Statistic");
		connectItem = new JMenu("Connection");
		
		all = new JMenuItem("All");
		foul = new JMenuItem("Foul");
		steal = new JMenuItem("Steal");
		block = new JMenuItem("Block");
		
		connectItem.add(all);
		connectItem.add(foul);
		connectItem.add(steal);
		connectItem.add(block);
		viewMenu.add(statItem);
		viewMenu.add(connectItem);
		
		position = new JMenuItem("Position");
		start = new JMenuItem("Start 5");
		plusMinus =new JMenuItem("+/-");
		points = new JMenuItem("Points");
		minutes = new JMenuItem("Minutes");
		
		layoutMenu.add(position);
		layoutMenu.add(start);
		layoutMenu.add(plusMinus);
		layoutMenu.add(points);
		layoutMenu.add(minutes);
		
		
		mainPanel = new JPanel();
		this.add(mainPanel);
		
		singleGameCanvas = new SingleGameCanvas(gameIndex, database, singleGameCanvasWidth, singleGameCanvasHeight);
		mainPanel.add(singleGameCanvas);
	}
	
}
