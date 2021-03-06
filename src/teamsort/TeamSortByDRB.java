package teamsort;

import java.util.Comparator;

import datamodel.Team;

public class TeamSortByDRB implements Comparator<Team> {
	@Override
	public int compare (Team o1, Team o2) {
		
		if (o1.drb < o2.drb) {
			return 1;
		} else if (o1.drb == o2.drb) {
			return 0;
		} else {
			return -1;
		}
	}
}
