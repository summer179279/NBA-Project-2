package datamodel;

import java.util.ArrayList;
import java.util.HashMap;

public class SinglePlayer {
	public String name;
	String lastName, firstName;
	public String pos;
	public int jersey = 0;
	public String mins;


	//list for a single player
	ArrayList<Efficiency> effList;
	ArrayList<Event> eventList;
	public HashMap<String, Float> attrMap = new HashMap<String, Float>();

	public SinglePlayer(String name) {
		this.name = name;
		effList = new ArrayList<Efficiency>();
		eventList = new ArrayList<Event>();
	}
	
	public void addEfficiency (float ratio, int value) {
		this.effList.add(new Efficiency(ratio, value));
	}
	
	public ArrayList<Efficiency> getEffList () {
		return effList;
	}
	
	public ArrayList<Event> getEventList () {
		return eventList;
	}
	
	public String getName () {
		return name;
	}
	
	public int getJersey () {
		return this.jersey;
	}
	
	public boolean atCourtWithinTime (int startTimeIndex, int endTimeIndex) {
		int middleTimeIndex = (startTimeIndex + endTimeIndex) / 2;
		for (int i = 0; i < effList.size(); i++) {
			if (effList.get(i).value != 999 
					&& effList.get(i).getStartTimeIndex() <= middleTimeIndex 
					&& effList.get(i).getEndTimeIndex() >= middleTimeIndex) {
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<Integer> getTimeListByTypeWithinTime (String type, int startTimeIndex, int endTimeIndex) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		if (type.equals("points")) {
			for (int i = 0; i < eventList.size(); i++) {
				if (eventList.get(i) instanceof MadeEvent) {
					MadeEvent tempEvent = eventList.get(i).getMadeInstance();
					if (tempEvent.actionPlayer.equals(this.name)
							&& tempEvent.timeIndex >= startTimeIndex && tempEvent.timeIndex < endTimeIndex) {
						result.add(tempEvent.timeIndex);					
					}
				}
			}
		} else if(type.equals("twoPts")) {
			for (int i = 0; i < eventList.size(); i++) {
				if (eventList.get(i) instanceof MadeEvent) {
					MadeEvent tempEvent = eventList.get(i).getMadeInstance();
					if (tempEvent.actionPlayer.equals(this.name)
							&& tempEvent.timeIndex >= startTimeIndex && tempEvent.timeIndex < endTimeIndex
							&& tempEvent.getPoint()==2) {
						result.add(tempEvent.timeIndex);
					}
				}
			}			
		} else if(type.equals("threePts")) {
			for (int i = 0; i < eventList.size(); i++) {
				if (eventList.get(i) instanceof MadeEvent) {
					MadeEvent tempEvent = eventList.get(i).getMadeInstance();
					if (tempEvent.actionPlayer.equals(this.name)
							&& tempEvent.timeIndex >= startTimeIndex && tempEvent.timeIndex < endTimeIndex
							&& tempEvent.getPoint()==3) {
						result.add(tempEvent.timeIndex);
					}
				}
			}			
		} else if(type.equals("free")) {
			for (int i = 0; i < eventList.size(); i++) {
				if (eventList.get(i) instanceof MadeEvent) {
					MadeEvent tempEvent = eventList.get(i).getMadeInstance();
					if (tempEvent.actionPlayer.equals(this.name)
							&& tempEvent.timeIndex >= startTimeIndex && tempEvent.timeIndex < endTimeIndex
							&& tempEvent.getPoint()==1) {
						result.add(tempEvent.timeIndex);
					}
				}
			}			
		}  else if(type.equals("ofRbd")) {
			for (int i = 0; i < eventList.size(); i++) {
				if (eventList.get(i) instanceof ReboundEvent) {
					ReboundEvent tempEvent = eventList.get(i).getReboundInstance();
					if (tempEvent.actionPlayer.equals(this.name)
							&& tempEvent.timeIndex >= startTimeIndex && tempEvent.timeIndex < endTimeIndex
							&& !tempEvent.isDefensive) {
						result.add(tempEvent.timeIndex);
					}
				}
			}			
		} else if(type.equals("defRbd")) {
			for (int i = 0; i < eventList.size(); i++) {
				if (eventList.get(i) instanceof ReboundEvent) {
					ReboundEvent tempEvent = eventList.get(i).getReboundInstance();
					if (tempEvent.actionPlayer.equals(this.name)
							&& tempEvent.timeIndex >= startTimeIndex && tempEvent.timeIndex < endTimeIndex
							&& tempEvent.isDefensive) {
						result.add(tempEvent.timeIndex);
					}
				}
			}			
		} else if(type.equals("turnover")) {
		 	for (int i = 0; i < eventList.size(); i++) {
				if (eventList.get(i) instanceof TurnoverEvent) {
					TurnoverEvent tempEvent = eventList.get(i).getTurnoverInstance();
					if (tempEvent.actionPlayer.equals(this.name)
							&& tempEvent.timeIndex >= startTimeIndex && tempEvent.timeIndex < endTimeIndex) {
						result.add(tempEvent.timeIndex);
					}
				}
			}			
		} else if(type.equals("miss")) {
		 	for (int i = 0; i < eventList.size(); i++) {
				if (eventList.get(i) instanceof MissEvent) {
					MissEvent tempEvent = eventList.get(i).getMissInstance();
					if (tempEvent.actionPlayer.equals(this.name)
							&& tempEvent.timeIndex >= startTimeIndex && tempEvent.timeIndex < endTimeIndex) {
						result.add(tempEvent.timeIndex);
					}
				}
			}			
		}		
		return result;
	}
	
	
	public HashMap<String,Integer> getStatByTypeWithinTime (String type, int startTimeIndex, int endTimeIndex) {
		HashMap<String,Integer> result = new HashMap<String,Integer>();
		if(type.equals("ast")) {
			for (int i = 0; i < eventList.size(); i++) {
				if (eventList.get(i) instanceof MadeEvent) {
					MadeEvent tempEvent = eventList.get(i).getMadeInstance();
					if (tempEvent.assistPlayer!=null
							&& tempEvent.assistPlayer.equals(this.name)
							&& tempEvent.timeIndex >= startTimeIndex && tempEvent.timeIndex < endTimeIndex) {
						result.put(tempEvent.actionPlayer, tempEvent.timeIndex);
					}
				}
			}			
		} else if(type.equals("foul")) {
		 	for (int i = 0; i < eventList.size(); i++) {
				if (eventList.get(i) instanceof FoulEvent) {
					FoulEvent tempEvent = eventList.get(i).getFoulInstance();
					if (tempEvent.actionPlayer.equals(this.name)
							&& tempEvent.timeIndex >= startTimeIndex && tempEvent.timeIndex < endTimeIndex) {
						result.put(tempEvent.foulPlayer, tempEvent.timeIndex);
					}
				}
			}			
		} else if(type.equals("stl")) {
			for (int i = 0; i < eventList.size(); i++) {
				if (eventList.get(i) instanceof TurnoverEvent) {
					TurnoverEvent tempEvent = eventList.get(i).getTurnoverInstance();
					if (tempEvent.stealPlayer!=null
							&& tempEvent.stealPlayer.equals(this.name)
							&& tempEvent.timeIndex >= startTimeIndex && tempEvent.timeIndex < endTimeIndex) {
						result.put(tempEvent.actionPlayer, tempEvent.timeIndex);
					}
				}
			}		
		} else if(type.equals("blk")) {
			for (int i = 0; i < eventList.size(); i++) {
				if (eventList.get(i) instanceof MissEvent) {
					MissEvent tempEvent = eventList.get(i).getMissInstance();
					if (tempEvent.blockPlayer!=null
							&& tempEvent.blockPlayer.equals(this.name)
							&& tempEvent.timeIndex >= startTimeIndex && tempEvent.timeIndex < endTimeIndex) {
						result.put(tempEvent.actionPlayer, tempEvent.timeIndex);
					}
				}
			}		
		}
		return result;
	}
	
	@Override
	public boolean equals (Object o) {
		if (! (o instanceof SinglePlayer)) {
			return false;
		}
		SinglePlayer pgs = (SinglePlayer) o;
		if (pgs.lastName.equals(this.lastName)
				&& pgs.firstName.equals(this.firstName)
				&& pgs.jersey == this.jersey) {
			return true;
		}
		
		return false;
		
	}
}
