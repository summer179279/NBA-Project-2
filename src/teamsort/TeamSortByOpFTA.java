package teamsort;

import java.util.Comparator;

import datamodel.Team;

public class TeamSortByOpFTA implements Comparator<Team> {
	@Override
	public int compare (Team o1, Team o2) {
		
		if (o1.opfta < o2.opfta) {
			return 1;
		} else if (o1.opfta == o2.opfta) {
			return 0;
		} else {
			return -1;
		}
	}
}