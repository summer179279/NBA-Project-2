package datamodel;

import java.util.ArrayList;
import java.util.HashMap;


//for one game
public class SingleGame {
	static java.text.DecimalFormat df = new java.text.DecimalFormat("#");  
	static java.text.DecimalFormat df_2 = new java.text.DecimalFormat("#.00"); 
	static java.text.DecimalFormat df_1 = new java.text.DecimalFormat("#.0"); 
	
	public int index;
	public String date;
	public int leftTeamIndex;
	public int rightTeamIndex;
	public int leftScore;
	public int rightScore;
	public boolean overtime = false;
	public int maxLeftDiff = 0;
	public int maxRightDiff = 0;
	
	public ArrayList<Event> eventList = new ArrayList<Event>();
	public ArrayList<SinglePlayer> leftPlayers = new ArrayList<SinglePlayer>();
	public ArrayList<SinglePlayer> rightPlayers = new ArrayList<SinglePlayer>();
	public HashMap<String, SinglePlayer> leftPlayersMap = new HashMap<String, SinglePlayer>();
	public HashMap<String, SinglePlayer> rightPlayersMap = new HashMap<String, SinglePlayer>();
	
	public int quarter;
	public int timesTied;
	public int leadChanges;
	public String duration;
	
	int[] leftQuarterScore, rightQuarterScore;
	
	//����team�ĸ�������
	// left team attribute
	public HashMap<String, Float> leftTeamAttrMap = new HashMap<String, Float>();

	// right team attribute
	public HashMap<String, Float> rightTeamAttrMap = new HashMap<String, Float>();

	//���Ľ��������Ǳ�������
	public int getMaxQuarter () {
		return eventList.get(eventList.size() - 1).quarter;
	}
	//����ʱ�������Ǳ���ʱ��
	public int getMaxTimeIndex () {
		
		int q = 4;
		if (eventList.size() >= 1) {
			q = eventList.get(eventList.size() - 1).quarter;
		}
		if (q == 4) {
			return 4 * 12 * 60;
		} else {
			return (q - 4) * 5 * 60 + 4 * 12 * 60;
		}
	}
	
	//result�����һ�����Լ������ֵ
	public float[] getAttrValueByType (int teamIndex, String type) {
		float[] result = new float[2];
		
		if (type.equals("Points")) {
			result[0] = leftTeamAttrMap.get("points");
			result[1] = rightTeamAttrMap.get("points");
		} else if (type.equals("Field Goal")) {
			result[0] = leftTeamAttrMap.get("field_goals_made");
			result[1] = rightTeamAttrMap.get("field_goals_made");
		} else if (type.equals("FGA")) {
			result[0] = leftTeamAttrMap.get("field_goals_att");
			result[1] = rightTeamAttrMap.get("field_goals_att");
		} else if (type.equals("FGP%")) {
			result[0] = leftTeamAttrMap.get("field_goals_pct");
			result[1] = rightTeamAttrMap.get("field_goals_pct");
		} else if (type.equals("2-Points")) {
			result[0] = leftTeamAttrMap.get("two_points_made");
			result[1] = rightTeamAttrMap.get("two_points_made");
		} else if (type.equals("2PA")) {
			result[0] = leftTeamAttrMap.get("two_points_att");
			result[1] = rightTeamAttrMap.get("two_points_att");
		} else if (type.equals("2PP%")) {
			result[0] = leftTeamAttrMap.get("two_points_pct");
			result[1] = rightTeamAttrMap.get("two_points_pct");
		} else if (type.equals("3-Points")) {
			result[0] = leftTeamAttrMap.get("three_points_made");
			result[1] = rightTeamAttrMap.get("three_points_made");
		} else if (type.equals("3PA")) {
			result[0] = leftTeamAttrMap.get(" three_points_att");
			result[1] = rightTeamAttrMap.get(" three_points_att");
		} else if (type.equals("3PP%")) {
			result[0] = leftTeamAttrMap.get("three_points_pct");
			result[1] = rightTeamAttrMap.get("three_points_pct");
		} else if (type.equals("Free Throw")) {
			result[0] = leftTeamAttrMap.get("free_throws_made");
			result[1] = rightTeamAttrMap.get("free_throws_made");
		} else if (type.equals("FTA")) {
			result[0] = leftTeamAttrMap.get("free_throws_att");
			result[1] = rightTeamAttrMap.get("free_throws_att");
		} else if (type.equals("FTP%")) {
			result[0] = leftTeamAttrMap.get("free_throws_pct");
			result[1] = rightTeamAttrMap.get("free_throws_pct");
		} else if (type.equals("Rebound")) {
			result[0] = leftTeamAttrMap.get("rebounds");
			result[1] = rightTeamAttrMap.get("rebounds");
		} else if (type.equals("Offense RB")) {
			result[0] = leftTeamAttrMap.get("offensive_rebounds");
			result[1] = rightTeamAttrMap.get("offensive_rebounds");
		} else if (type.equals("Defense RB")) {
			result[0] = leftTeamAttrMap.get("defensive_rebounds");
			result[1] = rightTeamAttrMap.get("defensive_rebounds");
		} else if (type.equals("Assist")) {
			result[0] = leftTeamAttrMap.get("assists");
			result[1] = rightTeamAttrMap.get("assists");
		} else if (type.equals("Steal")) {
			result[0] = leftTeamAttrMap.get("steals");
			result[1] = rightTeamAttrMap.get("steals");
		} else if (type.equals("Block")) {
			result[0] = leftTeamAttrMap.get("blocks");
			result[1] = rightTeamAttrMap.get("blocks");
		} else if (type.equals("Turnover")) {
			result[0] = leftTeamAttrMap.get("turnovers");
			result[1] = rightTeamAttrMap.get("turnovers");
		} else if (type.equals("Personal Foul")) {
			result[0] = leftTeamAttrMap.get("personal_fouls");
			result[1] = rightTeamAttrMap.get("personal_fouls");
		} 
		if (teamIndex == rightTeamIndex) {
			float temp = result[0];
			result[0] = result[1];
			result[1] = temp;
		}
		return result;
	}
	
	
	//����left��Ա�����ֵ
	public String getLeftPlayerMax (String attrType) {
		float maxValue = -Float.MAX_VALUE;
		String result = "";
		if (attrType.equals("minutes")) {
			for (int i = 0; i < leftPlayers.size(); i++) {
				if (maxValue <= SingleGame.translateTimeStringToInt(leftPlayers.get(i).mins)) {
					maxValue = SingleGame.translateTimeStringToInt(leftPlayers.get(i).mins);
					result = leftPlayers.get(i).mins;
				}
			}
		} else {
			for (int i = 0; i < leftPlayers.size(); i++) {
				if (leftPlayers.get(i).attrMap.containsKey(attrType)) {

					if (maxValue <= leftPlayers.get(i).attrMap.get(attrType)) {
						maxValue = leftPlayers.get(i).attrMap.get(attrType);
						result = df.format(maxValue);
						if (attrType.equals("two_points_pct")) {
							result = df_1.format(maxValue * 100);
						}
						if (attrType.equals("three_points_pct")) {
							result = df_1.format(maxValue);
						}
						
					}
				}
			}
		}
		return result;
		
	}

	//����right��Ա���ֵ
	public String getRightPlayerMax (String attrType) {
		float maxValue = -Float.MAX_VALUE;
		String result = "";
		if (attrType.equals("minutes")) {
			for (int i = 0; i < rightPlayers.size(); i++) {
				if (maxValue <= SingleGame.translateTimeStringToInt(rightPlayers.get(i).mins)) {
					maxValue = SingleGame.translateTimeStringToInt(rightPlayers.get(i).mins);
					result = rightPlayers.get(i).mins;
				}
			}
		} else {
			for (int i = 0; i < rightPlayers.size(); i++) {
				if (rightPlayers.get(i).attrMap.containsKey(attrType)) {
					if (maxValue <= rightPlayers.get(i).attrMap.get(attrType)) {
						maxValue = rightPlayers.get(i).attrMap.get(attrType);
						result = df.format(maxValue);
						if (attrType.equals("two_points_pct")) {
							result = df_1.format(maxValue * 100);
						}
						if (attrType.equals("three_points_pct")) {
							result = df_1.format(maxValue);
						}
					}
				}
			}
		}
		return result;
	}
	
	public static float translateTimeStringToInt (String time) {
		if (time == null) {
			return 0f;
		}
		String[] array = time.split(":");
		int mins = Integer.parseInt(array[0]);
		int sec = Integer.parseInt(array[1]);
		return mins * 60 + sec;
	}
	
	//����Ч��
	public void setPlayersEfficiency () {
		int maxTimeIndex = getMaxTimeIndex();
		int lastTimeIndex, curTimeIndex;
		ArrayList<Efficiency> effList;
		for (int i = 0; i < leftPlayers.size(); i++) {
			effList = leftPlayers.get(i).getEffList();
			lastTimeIndex = 0;
			for (int j = 0; j < effList.size(); j++) {
				curTimeIndex = (int) (lastTimeIndex + effList.get(j).ratio * maxTimeIndex);
				effList.get(j).setTimeIndex(lastTimeIndex, curTimeIndex);
				lastTimeIndex = curTimeIndex;
			}
		}
		
		for (int i = 0; i < rightPlayers.size(); i++) {
			effList = rightPlayers.get(i).getEffList();
			lastTimeIndex = 0;
			for (int j = 0; j < effList.size(); j++) {
				curTimeIndex = (int) (lastTimeIndex + effList.get(j).ratio * maxTimeIndex);
				effList.get(j).setTimeIndex(lastTimeIndex, curTimeIndex);
				lastTimeIndex = curTimeIndex;
			}
		}
	}
	
	public boolean isLeftWin () {
		return leftScore > rightScore ? true : false;
	}
	public int getWinTeamIndex () {
		return isLeftWin() ? leftTeamIndex : rightTeamIndex;
	}
	public int getAwayTeam () {
		return leftTeamIndex;
	}
	public int getScoreDiff () {
		return Math.abs(leftScore - rightScore);
	}
	public int getDirectScoreDiff () {
		return leftScore - rightScore;
	}
	public ArrayList<Event> getEventList () {
		return this.eventList;
	}
	public ArrayList<SinglePlayer> getLeftPlayers () {
		return leftPlayers;
	}
	public ArrayList<SinglePlayer> getRightPlayers () {
		return rightPlayers;
	}
	public int getMaxScoreDiff () {
		return maxLeftDiff >= maxRightDiff ? maxLeftDiff : maxRightDiff;
	}
}
