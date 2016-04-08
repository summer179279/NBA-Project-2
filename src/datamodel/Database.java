package datamodel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
public class Database {
	public static final int gameNum = 82;
	public static final int teamNum = 30;
	public static final int[] teamColor = {
		227,53,59,  
		1,126,70, 
		0,0,0, 
		244,122,32, 
		170,19,50, 
		128,23,54, 
		0,48,106, 
		250,184,39, 
		182,30,46, 
		251,198,45, 
		183,10,47, 
		253,205,23,
		200,19,61, 
		249,186,33, 
		91,118,173, 
		149,1,43, 
		1,57,12,
		7,72,126, 
		0,41,90,
		245,109,26, 
		11,125,195,  
		1,119,190,
		0,88,191, 
		241,167,5, 
		225,58,62, 
		61,58,149, 
		100,100,100, 
		205,16,65, 
		0,56,11, 
		180,94,40};
	
	int count = 0;
	
	//��������
	public static String[] attrNames = {
		"Field Goal", "FGA", "FGP%",
		"2-Points", "2PA", "2PP%",
		"3-Points", "3PA", "3PP%",
		"Free Throw", "FTA", "FTP%",
		"Rebound", "Offense RB", "Defense RB",
		"Points", "Assist", "Steal", "Block", "Turnover", "Personal Foul",};
	
	//(k, v) ������ȫ���԰� Map �����е� value ���� key �ĸ�������ϵͳ������ key �Ĵ洢λ��֮��value ��֮���������Ｔ�ɡ�
	static public Team[] teams;
	public HashMap<String, Integer> teamIndex;
	public HashMap<Integer, Team> teamsMap;
	public HashMap<Integer, SingleGame> gameMap;
	public ArrayList<SingleGame> singleGameList;
	public ArrayList<WinLostCellData> winLostCellList;
	public int maxScoreDiff = Integer.MIN_VALUE;
	public int minScoreDiff = Integer.MAX_VALUE;
	
	public float maxfg = -Float.MAX_VALUE, maxfga = -Float.MAX_VALUE, maxfgp = -Float.MAX_VALUE;
	public float max_3p = -Float.MAX_VALUE, max_3pa = -Float.MAX_VALUE, max_3pp = -Float.MAX_VALUE;
	public float max_2p = -Float.MAX_VALUE, max_2pa = -Float.MAX_VALUE, max_2pp = -Float.MAX_VALUE;
	public float maxft= -Float.MAX_VALUE, maxfta= -Float.MAX_VALUE, maxftp;
	public float maxorb= -Float.MAX_VALUE, maxdrb= -Float.MAX_VALUE, maxtrb= -Float.MAX_VALUE;
	public float maxast= -Float.MAX_VALUE, maxstl= -Float.MAX_VALUE, maxblk= -Float.MAX_VALUE, maxtov= -Float.MAX_VALUE, maxpf= -Float.MAX_VALUE;
	public float maxpts= -Float.MAX_VALUE;
	
	public float minfg = Float.MAX_VALUE, minfga = Float.MAX_VALUE, minfgp = Float.MAX_VALUE;
	public float min_3p = Float.MAX_VALUE, min_3pa = Float.MAX_VALUE, min_3pp = Float.MAX_VALUE;
	public float min_2p = Float.MAX_VALUE, min_2pa = Float.MAX_VALUE, min_2pp = Float.MAX_VALUE;
	public float minft= Float.MAX_VALUE, minfta= Float.MAX_VALUE, minftp = Float.MAX_VALUE;
	public float minorb= Float.MAX_VALUE, mindrb= Float.MAX_VALUE, mintrb= Float.MAX_VALUE;
	public float minast= Float.MAX_VALUE, minstl= Float.MAX_VALUE, minblk= Float.MAX_VALUE, mintov= Float.MAX_VALUE, minpf= Float.MAX_VALUE;
	public float minpts= Float.MAX_VALUE;
	
	public Database () {
		teams = new Team[teamNum];
		teamIndex = new HashMap<String, Integer>();
		teamsMap = new HashMap<Integer, Team>();
		gameMap = new HashMap<Integer, SingleGame>();
		singleGameList = new ArrayList<SingleGame>();
		winLostCellList = new ArrayList<WinLostCellData>();
	}
	
	public Database (String teamNameFile, String winLostFile, String gameStatFile, String allGameDataFile, String efficiencyFile, String summaryFolder) {
		this();
		readTeamNames(teamNameFile);
		readWinLostCellData(winLostFile);
		readAllGameStatData(gameStatFile);
		setGameIndexofCellData();
		readGameEvent(allGameDataFile);
		readPlayerEfficiency(efficiencyFile);
		setPlayerEvents();
		setAllGameSummary(summaryFolder);
		
		System.out.println("Database read data finished!");
	}
	public void setAllGameSummary (String folderName) {
		File folder = new File(folderName);
		String[] fileNames = folder.list();
		for (int i = 0; i < fileNames.length; i++) {
			//System.out.println(fileNames[i]);
			setSingleGameSummary(folderName + "/" + fileNames[i]);
		}
		
	}
	
	public void setSingleGameSummary (String fileName) {
		SAXReader reader = new SAXReader();
		File file = new File(fileName);
		try {
			Document doc = reader.read(file);
			Element root = doc.getRootElement();
			Element teamRight = root.elements().get(1);
			Element teamLeft = root.elements().get(2);
			String teamHomeName = teamRight.attributeValue("name");
			teamHomeName = teamHomeName.split(" ")[teamHomeName.split(" ").length - 1];
			String teamAwayName = teamLeft.attributeValue("name");
			teamAwayName = teamAwayName.split(" ")[teamAwayName.split(" ").length - 1];
			int homePoint = Integer.parseInt(teamRight.attributeValue("points"));
			int awayPoint = Integer.parseInt(teamLeft.attributeValue("points"));
			SingleGame tempGame = null;
			int index = 0;
			for (; index < singleGameList.size(); index++) {
				tempGame = singleGameList.get(index);
				if (teamsMap.get(tempGame.leftTeamIndex).name.equals(teamAwayName) &&
						teamsMap.get(tempGame.rightTeamIndex).name.equals(teamHomeName) &&
						tempGame.leftScore == awayPoint && tempGame.rightScore == homePoint) {
					count++;
					break;
				}
			}
			
			if (index == singleGameList.size()) {
				System.out.println(fileName);
				System.out.println(teamAwayName + ":" + teamHomeName);
			} else {

				//tempGame.quarter = Integer.parseInt(root.attributeValue("quarter"));
				if (root.attributeValue("times_tied") != null) {
					tempGame.timesTied = Integer.parseInt(root.attributeValue("times_tied"));
				}
				if (root.attributeValue("lead_changes") != null) {
					tempGame.leadChanges = Integer.parseInt(root.attributeValue("lead_changes"));
				}
				if (root.attributeValue("duration") != null) {
					tempGame.duration = root.attributeValue("duration");
				}
				
				
				
				// read right team, home
				List<Element> quarterScore = teamRight.elements().get(0).elements();
				tempGame.quarter = quarterScore.size();
				tempGame.rightQuarterScore = new int[quarterScore.size()];
				for (int i = 0; i < quarterScore.size(); i++) {
					tempGame.rightQuarterScore[i] = Integer.parseInt(quarterScore.get(i).attributeValue("points"));
				}
				Element stat = teamRight.elements().get(1);
				Attribute tempAtt;
				String attName;
				float tempValue;
				for (int i = 0; i < stat.attributes().size(); i++) {
					tempAtt = stat.attributes().get(i);
					attName = tempAtt.getQName().getName();
					if (attName.equals("minutes") == false) {
						tempValue = Float.parseFloat(tempAtt.getValue());
						tempGame.rightTeamAttrMap.put(attName, tempValue);
					}
				}
				Element player;
					SinglePlayer tempSinglePlayer;
					String firstName, lastName, pName;
					List<Element> playerList;
					if (teamRight.elements().size() >= 3) {	
						
						 playerList = teamRight.elements().get(2).elements();
					
				
					for (int i = 0; i < playerList.size(); i++) {
						player = playerList.get(i);
						firstName = player.attributeValue("first_name").split(" ")[0];
						lastName = player.attributeValue("last_name").split(" ")[0];
						pName = firstName.substring(0,1) + "." + lastName;
						if (tempGame.rightPlayersMap.containsKey(pName)) {
							tempSinglePlayer = tempGame.rightPlayersMap.get(pName);
							if (firstName != null)
								tempSinglePlayer.firstName = firstName;
							if (lastName != null)
								tempSinglePlayer.lastName = lastName;
							tempSinglePlayer.pos = player.attributeValue("position");
							if (player.attributeValue("jersey_number") != null) {
								tempSinglePlayer.jersey = Integer.parseInt(player.attributeValue("jersey_number"));
							}
							
							for (int k = 0; k < player.element("statistics").attributes().size(); k++) {
								tempAtt = player.element("statistics").attributes().get(k);
								attName = tempAtt.getQName().getName();
								if (attName.equals("minutes") == false) {
									tempValue = Float.parseFloat(tempAtt.getValue());
									tempSinglePlayer.attrMap.put(attName, tempValue);
								} else {
									tempSinglePlayer.mins = tempAtt.getValue();
								}
							}
						}
					}
				}
				
				
				// read left team, left
				quarterScore = teamLeft.elements().get(0).elements();
				tempGame.leftQuarterScore = new int[tempGame.quarter];
				for (int i = 0; i < quarterScore.size(); i++) {
					tempGame.leftQuarterScore[i] = Integer.parseInt(quarterScore.get(i).attributeValue("points"));
				}
				stat = teamLeft.elements().get(1);

				for (int i = 0; i < stat.attributes().size(); i++) {
					tempAtt = stat.attributes().get(i);
					attName = tempAtt.getQName().getName();
					if (attName.equals("minutes") == false) {
						tempValue = Float.parseFloat(tempAtt.getValue());
						tempGame.leftTeamAttrMap.put(attName, tempValue);
					}
				}
				if (teamLeft.elements().size() >= 3) {
				playerList = teamLeft.elements().get(2).elements();

				for (int i = 0; i < playerList.size(); i++) {
					player = playerList.get(i);
					firstName = player.attributeValue("first_name").split(" ")[0];
					lastName = player.attributeValue("last_name").split(" ")[0];
					pName = firstName.substring(0,1) + "." + lastName;
					if (tempGame.leftPlayersMap.containsKey(pName)) {
						tempSinglePlayer = tempGame.leftPlayersMap.get(pName);
						if (firstName != null)
							tempSinglePlayer.firstName = firstName;
						if (lastName != null)
							tempSinglePlayer.lastName = lastName;
						tempSinglePlayer.pos = player.attributeValue("position");
						if (player.attributeValue("jersey_number") != null) {
							tempSinglePlayer.jersey = Integer.parseInt(player.attributeValue("jersey_number"));
						}
						
						
						for (int k = 0; k < player.element("statistics").attributes().size(); k++) {
							tempAtt = player.element("statistics").attributes().get(k);
							attName = tempAtt.getQName().getName();
							if (attName.equals("minutes") == false) {
								tempValue = Float.parseFloat(tempAtt.getValue());
								tempSinglePlayer.attrMap.put(attName, tempValue);
							} else {
								tempSinglePlayer.mins = tempAtt.getValue();
							}
						}
					}
				}
				}
				
			}
			
			
			
			//System.out.println(count);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void setPlayerEvents () {
		SingleGame curGame;
		ArrayList<Event> eventList;
		for (int i = 0; i < singleGameList.size(); i++) {
			curGame = singleGameList.get(i);
			eventList = curGame.getEventList();
			for (int j = 0; j < eventList.size(); j++) {
				if (curGame.leftPlayersMap.containsKey(eventList.get(j).actionPlayer)) {
					curGame.leftPlayersMap.get(eventList.get(j).actionPlayer).eventList.add(eventList.get(j));
				} else if (curGame.rightPlayersMap.containsKey(eventList.get(j).actionPlayer)) {
					curGame.rightPlayersMap.get(eventList.get(j).actionPlayer).eventList.add(eventList.get(j));
				}
				if (eventList.get(j) instanceof MadeEvent) {
					MadeEvent tempEvent = (MadeEvent) eventList.get(j);
					if (tempEvent.assisted == true) {
						if (curGame.leftPlayersMap.containsKey(tempEvent.assistPlayer)) {
							curGame.leftPlayersMap.get(tempEvent.assistPlayer).eventList.add(eventList.get(j));
						} else if (curGame.rightPlayersMap.containsKey(tempEvent.assistPlayer)) {
							curGame.rightPlayersMap.get(tempEvent.assistPlayer).eventList.add(eventList.get(j));
						}
					}
				} else if (eventList.get(j) instanceof MissEvent) {
					MissEvent tempEvent = (MissEvent) eventList.get(j);
					if (tempEvent.blocked == true) {
						if (curGame.leftPlayersMap.containsKey(tempEvent.blockPlayer)) {
							curGame.leftPlayersMap.get(tempEvent.blockPlayer).eventList.add(eventList.get(j));
						} else if (curGame.rightPlayersMap.containsKey(tempEvent.blockPlayer)) {
							curGame.rightPlayersMap.get(tempEvent.blockPlayer).eventList.add(eventList.get(j));
						}
					}
				} else if (eventList.get(j) instanceof TurnoverEvent) {
					TurnoverEvent tempEvent = (TurnoverEvent) eventList.get(j);
					if (tempEvent.stole == true) {
						if (curGame.leftPlayersMap.containsKey(tempEvent.stealPlayer)) {
							curGame.leftPlayersMap.get(tempEvent.stealPlayer).eventList.add(eventList.get(j));
						} else if (curGame.rightPlayersMap.containsKey(tempEvent.stealPlayer)) {
							curGame.rightPlayersMap.get(tempEvent.stealPlayer).eventList.add(eventList.get(j));
						}
					}
				}
			}

		}
	}
	
	public float[] getMinMaxByType (String type) {
		float[] result = new float[2];
		
		if (type.equals("Points")) {
			result[0] = maxpts;
			result[1] = minpts;
		} else if (type.equals("Field Goal")) {
			result[0] = maxfg;
			result[1] = minfg;
		} else if (type.equals("FGA")) {
			result[0] = maxfga;
			result[1] = minfga;
		} else if (type.equals("FGP%")) {
			result[0] = maxfgp;
			result[1] = minfgp;
		} else if (type.equals("2-Points")) {
			result[0] = max_2p;
			result[1] = min_2p;
		} else if (type.equals("2PA")) {
			result[0] = max_2pa;
			result[1] = min_2pa;
		} else if (type.equals("2PP%")) {
			result[0] = max_2pp;
			result[1] = min_2pp;
		} else if (type.equals("3-Points")) {
			result[0] = max_3p;
			result[1] = min_3p;
		} else if (type.equals("3PA")) {
			result[0] = max_3pa;
			result[1] = min_3pa;
		} else if (type.equals("3PP%")) {
			result[0] = max_3pp;
			result[1] = min_3pp;
		} else if (type.equals("Free Throw")) {
			result[0] = maxft;
			result[1] = minft;
		} else if (type.equals("FTA")) {
			result[0] = maxfta;
			result[1] = minfta;
		} else if (type.equals("FTP%")) {
			result[0] = maxftp;
			result[1] = minftp;
		} else if (type.equals("Rebound")) {
			result[0] = maxtrb;
			result[1] = mintrb;
		} else if (type.equals("Offense RB")) {
			result[0] = maxorb;
			result[1] = minorb;
		} else if (type.equals("Defense RB")) {
			result[0] = maxdrb;
			result[1] = mindrb;
		} else if (type.equals("Assist")) {
			result[0] = maxast;
			result[1] = minast;
		} else if (type.equals("Steal")) {
			result[0] = maxstl;
			result[1] = minstl;
		} else if (type.equals("Block")) {
			result[0] = maxblk;
			result[1] = minblk;
		} else if (type.equals("Turnover")) {
			result[0] = maxtov;
			result[1] = mintov;
		} else if (type.equals("Personal Foul")) {
			result[0] = maxpf;
			result[1] = minpf;
		} 
		return result;
	}
	
	public void readTeamAvgData (String fileName) {
		File file = null;
		FileReader fr = null;
		BufferedReader br = null;
		
		try {
			
			file = new File(fileName);
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			
			br.readLine();
			String line;
			String[] array;
			for (int i = 0; i < teamNum; i++) {
				line = br.readLine();
				array = line.split(",");
				teams[i].fg = Float.parseFloat(array[4]) / 82;
				if (maxfg < teams[i].fg) {
					maxfg = teams[i].fg;
				}
				if (minfg > teams[i].fg) {
					minfg = teams[i].fg;
				}
			
				teams[i].fga = Float.parseFloat(array[5]) / 82;
				if (maxfga < teams[i].fga) {
					maxfga = teams[i].fga;
				}
				if (minfga > teams[i].fga) {
					minfga = teams[i].fga;
				}
			
				teams[i].fgp = Float.parseFloat(array[6]);
				if (maxfgp < teams[i].fgp) {
					maxfgp = teams[i].fgp;
				}
				if (minfgp > teams[i].fgp) {
					minfgp = teams[i].fgp;
				}
			
				teams[i]._3p = Float.parseFloat(array[7])/ 82;
				if (max_3p < teams[i]._3p) {
					max_3p = teams[i]._3p;
				}
				if (min_3p > teams[i]._3p) {
					min_3p = teams[i]._3p;
				}
			
				teams[i]._3pa = Float.parseFloat(array[8])/ 82;
				if (max_3pa < teams[i]._3pa) {
					max_3pa = teams[i]._3pa;
				}
				if (min_3pa > teams[i]._3pa) {
					min_3pa = teams[i]._3pa;
				}
			
				teams[i]._3pp = Float.parseFloat(array[9]);
				if (max_3pp < teams[i]._3pp) {
					max_3pp = teams[i]._3pp;
				}
				if (min_3pp > teams[i]._3pp) {
					min_3pp = teams[i]._3pp;
				}
			
				teams[i]._2p = Float.parseFloat(array[10])/ 82;
				if (max_2p < teams[i]._2p) {
					max_2p = teams[i]._2p;
				}
				if (min_2p > teams[i]._2p) {
					min_2p = teams[i]._2p;
				}
			
				teams[i]._2pa = Float.parseFloat(array[11])/ 82;
				if (max_2pa < teams[i]._2pa) {
					max_2pa = teams[i]._2pa;
				}
				if (min_2pa > teams[i]._2pa) {
					min_2pa = teams[i]._2pa;
				}
			
				teams[i]._2pp = Float.parseFloat(array[12]);
				if (max_2pp < teams[i]._2pp) {
					max_2pp = teams[i]._2pp;
				}
				if (min_2pp > teams[i]._2pp) {
					min_2pp = teams[i]._2pp;
				}
			
				teams[i].ft = Float.parseFloat(array[13])/ 82;
				if (maxft < teams[i].ft) {
					maxft = teams[i].ft;
				}
				if (minft > teams[i].ft) {
					minft = teams[i].ft;
				}
			
				teams[i].fta = Float.parseFloat(array[14])/ 82;
				if (maxfta < teams[i].fta) {
					maxfta = teams[i].fta;
				}
				if (minfta > teams[i].fta) {
					minfta = teams[i].fta;
				}
			
				teams[i].ftp = Float.parseFloat(array[15]);
				if (maxftp < teams[i].ftp) {
					maxftp = teams[i].ftp;
				}
				if (minftp > teams[i].ftp) {
					minftp = teams[i].ftp;
				}
			
				teams[i].orb = Float.parseFloat(array[16])/ 82;
				if (maxorb < teams[i].orb) {
					maxorb = teams[i].orb;
				}
				if (minorb > teams[i].orb) {
					minorb = teams[i].orb;
				}
			
				teams[i].drb = Float.parseFloat(array[17])/ 82;
				if (maxdrb < teams[i].drb) {
					maxdrb = teams[i].drb;
				}
				if (mindrb > teams[i].drb) {
					mindrb = teams[i].drb;
				}
			
				teams[i].trb = Float.parseFloat(array[18])/ 82;
				if (maxtrb < teams[i].trb) {
					maxtrb = teams[i].trb;
				}
				if (mintrb > teams[i].trb) {
					mintrb = teams[i].trb;
				}
			
				teams[i].ast = Float.parseFloat(array[19])/ 82;
				if (maxast < teams[i].ast) {
					maxast = teams[i].ast;
				}
				if (minast > teams[i].ast) {
					minast = teams[i].ast;
				}
			
				teams[i].stl = Float.parseFloat(array[20])/ 82;
				if (maxstl < teams[i].stl) {
					maxstl = teams[i].stl;
				}
				if (minstl > teams[i].stl) {
					minstl = teams[i].stl;
				}
			
				teams[i].blk = Float.parseFloat(array[21])/ 82;
				if (maxblk < teams[i].blk) {
					maxblk = teams[i].blk;
				}
				if (minblk > teams[i].blk) {
					minblk = teams[i].blk;
				}
			
				teams[i].tov = Float.parseFloat(array[22])/ 82;
				if (maxtov < teams[i].tov) {
					maxtov = teams[i].tov;
				}
				if (mintov > teams[i].tov) {
					mintov = teams[i].tov;
				}
			
				teams[i].pf = Float.parseFloat(array[23])/ 82;
				if (maxpf < teams[i].pf) {
					maxpf = teams[i].pf;
				}
				if (minpf > teams[i].pf) {
					minpf = teams[i].pf;
				}
			
				teams[i].pts = Float.parseFloat(array[25]);
				if (maxpts < teams[i].pts) {
					maxpts = teams[i].pts;
				}
				if (minpts > teams[i].pts) {
					minpts = teams[i].pts;
				}
			
			}
			
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}
	
	public void readTeamOppoAvgData (String fileName) {
		File file = null;
		FileReader fr = null;
		BufferedReader br = null;
		
		try {
			
			file = new File(fileName);
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			
			br.readLine();
			String line;
			String[] array;
			for (int i = 0; i < teamNum; i++) {
				line = br.readLine();
				array = line.split(",");
				teams[i].opfg = Float.parseFloat(array[4])/ 82;
				if (maxfg < teams[i].opfg) {
					maxfg = teams[i].opfg;
				}
				if (minfg > teams[i].opfg) {
					minfg = teams[i].opfg;
				}
				
				teams[i].opfga = Float.parseFloat(array[5])/ 82;
				if (maxfga < teams[i].opfga) {
					maxfga = teams[i].opfga;
				}
				if (minfga > teams[i].opfga) {
					minfga = teams[i].opfga;
				}
				teams[i].opfgp = Float.parseFloat(array[6]);
				if (maxfgp < teams[i].opfgp) {
					maxfgp = teams[i].opfgp;
				}
				if (minfgp > teams[i].opfgp) {
					minfgp = teams[i].opfgp;
				}
			
				teams[i].op_3p = Float.parseFloat(array[7])/ 82;
				if (max_3p < teams[i].op_3p) {
					max_3p = teams[i].op_3p;
				}
				if (min_3p > teams[i].op_3p) {
					min_3p = teams[i].op_3p;
				}
			
				teams[i].op_3pa = Float.parseFloat(array[8])/ 82;
				if (max_3pa < teams[i].op_3pa) {
					max_3pa = teams[i].op_3pa;
				}
				if (min_3pa > teams[i].op_3pa) {
					min_3pa = teams[i].op_3pa;
				}
			
				teams[i].op_3pp = Float.parseFloat(array[9]);
				if (max_3pp < teams[i].op_3pp) {
					max_3pp = teams[i].op_3pp;
				}
				if (min_3pp > teams[i].op_3pp) {
					min_3pp = teams[i].op_3pp;
				}
				teams[i].op_2p = Float.parseFloat(array[10])/ 82;
				if (max_2p < teams[i].op_2p) {
					max_2p = teams[i].op_2p;
				}
				if (min_2p > teams[i].op_2p) {
					min_2p = teams[i].op_2p;
				}
				teams[i].op_2pa = Float.parseFloat(array[11])/ 82;
				if (max_2pa < teams[i].op_2pa) {
					max_2pa = teams[i].op_2pa;
				}
				if (min_2pa > teams[i].op_2pa) {
					min_2pa = teams[i].op_2pa;
				}
			
				teams[i].op_2pp = Float.parseFloat(array[12]);
				if (max_2pp < teams[i].op_2pp) {
					max_2pp = teams[i].op_2pp;
				}
				if (min_2pp > teams[i].op_2pp) {
					min_2pp = teams[i].op_2pp;
				}
				teams[i].opft = Float.parseFloat(array[13])/ 82;
				if (maxft < teams[i].opft) {
					maxft = teams[i].opft;
				}
				if (minft > teams[i].opft) {
					minft = teams[i].opft;
				}
			
				teams[i].opfta = Float.parseFloat(array[14])/ 82;
				if (maxfta < teams[i].opfta) {
					maxfta = teams[i].opfta;
				}
				if (minfta > teams[i].opfta) {
					minfta = teams[i].opfta;
				}
				teams[i].opftp = Float.parseFloat(array[15]);
				if (maxftp < teams[i].opftp) {
					maxftp = teams[i].opftp;
				}
				if (minftp > teams[i].opftp) {
					minftp = teams[i].opftp;
				}
				teams[i].oporb = Float.parseFloat(array[16])/ 82;
				if (maxorb < teams[i].oporb) {
					maxorb = teams[i].oporb;
				}
				if (minorb > teams[i].oporb) {
					minorb = teams[i].oporb;
				}
				teams[i].opdrb = Float.parseFloat(array[17])/ 82;
				if (maxdrb < teams[i].opdrb) {
					maxdrb = teams[i].opdrb;
				}
				if (mindrb > teams[i].opdrb) {
					mindrb = teams[i].opdrb;
				}
				teams[i].optrb = Float.parseFloat(array[18])/ 82;
				if (maxtrb < teams[i].optrb) {
					maxtrb = teams[i].optrb;
				}
				if (mintrb > teams[i].optrb) {
					mintrb = teams[i].optrb;
				}
				teams[i].opast = Float.parseFloat(array[19])/ 82;
				if (maxast < teams[i].opast) {
					maxast = teams[i].opast;
				}
				if (minast > teams[i].opast) {
					minast = teams[i].opast;
				}
				teams[i].opstl = Float.parseFloat(array[20])/ 82;
				if (maxstl < teams[i].opstl) {
					maxstl = teams[i].opstl;
				}
				if (minstl > teams[i].opstl) {
					minstl = teams[i].opstl;
				}
				teams[i].opblk = Float.parseFloat(array[21])/ 82;
				if (maxblk < teams[i].opblk) {
					maxblk = teams[i].opblk;
				}
				if (minblk > teams[i].opblk) {
					minblk = teams[i].opblk;
				}
				teams[i].optov = Float.parseFloat(array[22])/ 82;
				if (maxtov < teams[i].optov) {
					maxtov = teams[i].optov;
				}
				if (mintov > teams[i].optov) {
					mintov = teams[i].optov;
				}
				teams[i].oppf = Float.parseFloat(array[23])/ 82;
				if (maxpf < teams[i].oppf) {
					maxpf = teams[i].oppf;
				}
				if (minpf > teams[i].oppf) {
					minpf = teams[i].oppf;
				}
				teams[i].oppts = Float.parseFloat(array[25]);
				if (maxpts < teams[i].oppts) {
					maxpts = teams[i].oppts;
				}
				if (minpts > teams[i].oppts) {
					minpts = teams[i].oppts;
				}
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}
		
	public void readTeamNames (String fileName) {
		File file = null;
		FileReader fr = null;
		BufferedReader br = null;
		
		try {
			
			file = new File(fileName);
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			
			// reader file content
			if (winLostCellList == null) {
				System.out.println("winLostCellList is null");
				return;
			} 
			String line = br.readLine();
			String[] array;
			String[] arrayForCityName;
			int index = 0;
			while ((line = br.readLine()) != null) {
				array = line.split(",");
				arrayForCityName = array[1].split(" ");
				
				teams[index] = new Team(arrayForCityName[0], arrayForCityName[arrayForCityName.length - 1], array[array.length - 1]);
				teams[index].index = index;
				teamIndex.put(array[1], index);
				teamsMap.put(index, teams[index]);
				
				teams[index].setOverall(splitStub(array[2]));
				teams[index].setHome(splitStub(array[3]));
				teams[index].setRoad(splitStub(array[4]));
				teams[index].setEastOppo(splitStub(array[5]));
				teams[index].setWestOppo(splitStub(array[6]));
				teams[index].setPreAllStar(splitStub(array[13]));
				teams[index].setPostAllStar(splitStub(array[14]));
				teams[index].setMarginLess3(splitStub(array[15]));
				teams[index].setMarginMore10(splitStub(array[16]));
				teams[index].setOct(splitStub(array[17]));
				teams[index].setNov(splitStub(array[18]));
				teams[index].setDec(splitStub(array[19]));
				teams[index].setJan(splitStub(array[20]));
				teams[index].setFeb(splitStub(array[21]));
				teams[index].setMar(splitStub(array[22]));
				teams[index].setApr(splitStub(array[23]));
				
				index++;
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}
	
	public void readWinLostCellData (String fileName) {
		File file = null;
		FileReader fr = null;
		BufferedReader br = null;
		
		try {
			
			file = new File(fileName);
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			
			// reader file content
			if (winLostCellList == null) {
				System.out.println("winLostCellList is null");
				return;
			} 
			String line = br.readLine();
			String[] array;
			int[] win_lost;
			int leftTeamIndex = 0, topTeamIndex;
			
			while ((line = br.readLine()) != null) {
				array = line.split(",");
				for (int i = 2; i < array.length; i++) {
					WinLostCellData tempCellData = new WinLostCellData();
					topTeamIndex = i - 2;
					if (leftTeamIndex == topTeamIndex) {
						tempCellData.leftTeam = leftTeamIndex;
						tempCellData.topTeam = leftTeamIndex;
					} else {
						tempCellData.leftTeam = leftTeamIndex;
						tempCellData.topTeam = topTeamIndex;
						win_lost = splitStub(array[i]);
						tempCellData.leftWin = win_lost[0];
						tempCellData.topWin = win_lost[1];
					}
					winLostCellList.add(tempCellData);
				}
				leftTeamIndex++;
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
		
	}
	
	private int[] splitStub (String str) {
		int[] result = new int[2];
		String[] array = str.split("-");
		result[0] = Integer.parseInt(array[0]);
		result[1] = Integer.parseInt(array[1]);
		return result;
	}
	
	public void readAllGameStatData (String fileName) {
		File file = null;
		FileReader fr = null;
		BufferedReader br = null;
		
		try {
			
			file = new File(fileName);
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			
			// reader file content
			if (winLostCellList == null) {
				System.out.println("winLostCellList is null");
				return;
			} 
			String line = br.readLine();
			String[] array;
			int gameIndex = 0;
			int scoreDiff;
			while ((line = br.readLine()) != null) {
				array = line.split(",");
				SingleGame tempStatData = new SingleGame();
				tempStatData.index = gameIndex;
				tempStatData.date = array[0];
				tempStatData.leftTeamIndex = teamIndex.get(array[2]);
				this.teamsMap.get(tempStatData.leftTeamIndex).gameIndex.add(gameIndex);
				
				tempStatData.leftScore = Integer.parseInt(array[3]);
				tempStatData.rightTeamIndex = teamIndex.get(array[4]);
				this.teamsMap.get(tempStatData.rightTeamIndex).gameIndex.add(gameIndex);
				
				tempStatData.rightScore = Integer.parseInt(array[5]);
				if (array.length >= 7 && array[6].equals("OT")) {
					tempStatData.overtime = true;
				}
				singleGameList.add(tempStatData);
				gameMap.put(tempStatData.index, tempStatData);
				scoreDiff = Math.abs(tempStatData.leftScore - tempStatData.rightScore);
				if (maxScoreDiff < scoreDiff) {
					maxScoreDiff = scoreDiff;
				}
				if (minScoreDiff > scoreDiff) {
					minScoreDiff = scoreDiff;
				}
				gameIndex++;
			}
			
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}
	
	public void readGameEvent (String fileName) {
		File file = null;
		FileReader fr = null;
		BufferedReader br = null;
		
		try {
			
			file = new File(fileName);
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			
			String line;
			String[] array;
			
			int gameIndex;  // 0
			int quarter;    // 1
			int timeIndex;  // 2
			int curLeftScore; // 4
			int curRightScore; // 5
			int actionTeamIndex;
			String actionTeam; //8
			String actionPlayer; //9
			String actionType; // 10
				
			SingleGame tempStatData;
			
			int scoreDiff = 0;
			
			while ((line = br.readLine()) != null) {
				array = line.split(",");
				if (array[8].equals("start") || array[8].endsWith("end")) continue;
				gameIndex = Integer.parseInt(array[0]);
				tempStatData = singleGameList.get(gameIndex);
				
				quarter = Integer.parseInt(array[1]);
				timeIndex = Integer.parseInt(array[2]);
				curLeftScore = Integer.parseInt(array[4]);
				curRightScore = Integer.parseInt(array[5]);
				
				scoreDiff = curLeftScore - curRightScore;
				if (scoreDiff >= 0) {
					if (tempStatData.maxLeftDiff < scoreDiff) {
						tempStatData.maxLeftDiff = scoreDiff;
					}
				} else {
					if (tempStatData.maxRightDiff < -scoreDiff) {
						tempStatData.maxRightDiff = -scoreDiff;
					}
				}

				
				//System.out.println(gameIndex + " : " + timeIndex);
				
				
				actionTeam = array[8];
				if (actionTeam.startsWith("LA")) {
					actionTeam = array[8].split(" ")[1];
					if (actionTeam.equals(teamsMap.get(tempStatData.leftTeamIndex).name)) {
						actionTeamIndex = tempStatData.leftTeamIndex;
					} else {
						actionTeamIndex = tempStatData.rightTeamIndex;
					}
				} else {
					if (actionTeam.startsWith(teamsMap.get(tempStatData.leftTeamIndex).city)) {
						actionTeamIndex = tempStatData.leftTeamIndex;
					} else {
						actionTeamIndex = tempStatData.rightTeamIndex;
					}
					
				}
				actionPlayer = array[9];
				actionType = array[10];
				
				if (actionType.equals("made")) {
					MadeEvent event = new MadeEvent(gameIndex, actionType, actionPlayer, quarter, timeIndex, actionTeamIndex, curLeftScore, curRightScore);
					int point = 0, distance;
					if (array[11].equals("free") || array[11].equals("clear") || array[11].equals("flagrant")) {
						point = 1;
					} else if (array[11].equals("technical")) {
						point = 2;  // this is technical miss
					} else {
						if (array[11].equals("2-pt")) {
							point = 2;
						} else if (array[11].equals("3-pt")) {
							point = 3;
						}
						
						if (array[12].equals("rim")) {
							distance = -1;
						} else {
							distance = Integer.parseInt(array[12]);
						}
						event.setDistance(distance);
						
						// length > 13 means there is assist
						if (array.length > 13) {
							event.setAssistedPlayer(array[13]);
						}
						
					}
					event.setPoint(point);
					tempStatData.eventList.add(event);
					
				} else if (actionType.equals("miss")) {
					MissEvent event = new MissEvent(gameIndex, actionType, actionPlayer, quarter, timeIndex, actionTeamIndex, curLeftScore, curRightScore);
					int point = 0, distance;
					if (array[11].equals("free") || array[11].equals("clear") || array[11].equals("flagrant")) {
						point = 1;
					} else if (array[11].equals("technical")) {
						point = 2;  // this is technical miss
					} else {
						if (array[11].equals("2-pt")) {
							point = 2;
						} else if (array[11].equals("3-pt")) {
							point = 3;
						}
						
						if (array[12].equals("rim")) {
							distance = -1;
						} else {
							distance = Integer.parseInt(array[12]);
						}
						event.setDistance(distance);
						
						// length > 13 means there is assist
						if (array.length > 13) {
							event.setBlockPlayer(array[13]);
						}
						
					}
					event.setPoint(point);
					tempStatData.eventList.add(event);
					
					
				} else if (actionType.equals("rebound")) {
					ReboundEvent event = new ReboundEvent(gameIndex, actionType, actionPlayer, quarter, timeIndex, actionTeamIndex, curLeftScore, curRightScore);
					event.setReboundType(array[11]);
					tempStatData.eventList.add(event);
					
				} else if (actionType.equals("foul")) {
					FoulEvent event = new FoulEvent(gameIndex, actionType, actionPlayer, quarter, timeIndex, actionTeamIndex, curLeftScore, curRightScore);
					event.setFoulType(array[11]);
					if (array.length > 13) {
						event.setFoulPlayer(array[13]);
					}
					tempStatData.eventList.add(event);
				} else if (actionType.equals("enter")) {
					EnterEvent event = new EnterEvent(gameIndex, actionType, actionPlayer, quarter, timeIndex, actionTeamIndex, curLeftScore, curRightScore);
					event.setLeftPlayer(array[13]);
					tempStatData.eventList.add(event);
				} else if (actionType.equals("turnover")) {
					TurnoverEvent event = new TurnoverEvent(gameIndex, actionType, actionPlayer, quarter, timeIndex, actionTeamIndex, curLeftScore, curRightScore);
					event.setTurnoverType(array[11]);
					if (array.length > 13) {
						event.setStealPlayer(array[13]);
					}
					tempStatData.eventList.add(event);
				} else if (actionType.equals("timeout")) {
					TimeoutEvent event = new TimeoutEvent(gameIndex, actionType, actionPlayer, quarter, timeIndex, actionTeamIndex, curLeftScore, curRightScore);
					event.setTimeoutType(array[11]);
					tempStatData.eventList.add(event);
				}
			}
			
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}

	public void setGameIndexofCellData () {
		if (singleGameList.size() == 0 || winLostCellList.size() == 0) {
			System.out.println("singleGameList or winLostCellList not added!");
			return;
		}
		SingleGame tempStatData;
		WinLostCellData tempCellData;
		for (int i = 0; i < singleGameList.size(); i++) {
			tempStatData = singleGameList.get(i);
			for (int j = 0; j < winLostCellList.size(); j++) {
				tempCellData = winLostCellList.get(j);
				if ((tempCellData.leftTeam == tempStatData.leftTeamIndex && tempCellData.topTeam == tempStatData.rightTeamIndex) 
						|| (tempCellData.topTeam == tempStatData.leftTeamIndex && tempCellData.leftTeam == tempStatData.rightTeamIndex)) {
					tempCellData.gameIndex.add(tempStatData.index);
				}
			}
		}
	}
	
	public void readPlayerEfficiency (String folderName) {
		File folder = new File(folderName);
		String[] fileNames = folder.list();
		int index;
		for (int i = 0; i < fileNames.length; i++) {
			index = Integer.parseInt(fileNames[i].split("-")[0]);
			readSingleGameEfficiency(index, folderName + "/" + fileNames[i]);
		}
		
	}
				
	public void readSingleGameEfficiency (int gameIndex, String fileName) {
		File file = null;
		FileReader fr = null;
		BufferedReader br = null;
		
		try {
			
			file = new File(fileName);
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			
			SingleGame game = gameMap.get(gameIndex);
			String line = br.readLine();
			String[] array = line.split(",");
			String leftTeamName = array[0];
			String plaryerName = array[1].split(" ")[0].substring(0,1) + "." + array[1].split(" ")[1];
			SinglePlayer tempPlayer = new SinglePlayer(plaryerName);
			/*tempPlayer.firstName = array[1].split(" ")[0];
			tempPlayer.lastName = array[1].split(" ")[1];*/
			float sum = 0;
			for (int i = 2; i < array.length; i += 2) {
				sum += Float.parseFloat(array[i].substring(0, array[i].indexOf('p')));
			}
			float tempWidth;
			int tempValue;
			for (int i = 2; i < array.length; i+= 2) {
				tempWidth = Float.parseFloat(array[i].substring(0, array[i].indexOf('p')));
				if (array[i + 1].equals("null")) {
					tempValue = 999;
				} else {
					tempValue = Integer.parseInt(array[i + 1]);
				}
				
				tempPlayer.addEfficiency(1.0f * tempWidth / sum, tempValue);
			}
			game.leftPlayers.add(tempPlayer);
			game.leftPlayersMap.put(tempPlayer.name, tempPlayer);
			
			while ((line = br.readLine()) != null) {
				array = line.split(",");
				plaryerName = array[1].split(" ")[0].substring(0,1) + "." + array[1].split(" ")[1];
				tempPlayer = new SinglePlayer(plaryerName);
				
				sum = 0;
				for (int i = 2; i < array.length; i += 2) {
					sum += Float.parseFloat(array[i].substring(0, array[i].indexOf('p')));
				}
				for (int i = 2; i < array.length; i+= 2) {
					tempWidth = Float.parseFloat(array[i].substring(0, array[i].indexOf('p')));
					if (array[i + 1].equals("null")) {
						tempValue = 999;
					} else {
						tempValue = Integer.parseInt(array[i + 1]);
					}
					tempPlayer.addEfficiency(1.0f * tempWidth / sum, tempValue);
				}
				if (array[0].equals(leftTeamName)) {
					game.leftPlayers.add(tempPlayer);
					game.leftPlayersMap.put(tempPlayer.name, tempPlayer);
				} else {
					game.rightPlayers.add(tempPlayer);
					game.rightPlayersMap.put(tempPlayer.name, tempPlayer);
				}
			}
			
			game.setPlayersEfficiency();
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
		
		
	}
	
	public void writePlayererData (String fileName) {
		File writeFile = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		
		try {
			writeFile = new File(fileName);
			fw = new FileWriter(writeFile);
			bw = new BufferedWriter(fw);
			
			
			
			System.out.println("Write Finish!");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
/******** read file template **********************************************************
	File file = null;
	FileReader fr = null;
	BufferedReader br = null;
	
	try {
		
		file = new File(fileName);
		fr = new FileReader(file);
		br = new BufferedReader(fr);
		
		// reader file content
		if (winLostCellList == null) {
			System.out.println("winLostCellList is null");
			return;
		} 
		String line;
		while ((line = br.readLine()) != null) {
			
		}
		
		
	} catch (IOException ioe) {
		ioe.printStackTrace();
	} finally {
		if (br != null) {
			try {
				br.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
		if (fr != null) {
			try {
				fr.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
*************************************************************************************/
}
