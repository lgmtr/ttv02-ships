package de.haw.ttv.main;

import de.uniba.wiai.lspi.chord.data.ID;

public class Sector {

	private ID start;
	private ID end;

	public Sector(ID start, ID end) {
		this.start = start;
		this.end = end;
	}
	
	public Sector() {
		this(null, null);
	}

	public ID getStart() {
		return start;
	}

	public void setStart(ID start) {
		this.start = start;
	}

	public ID getEnd() {
		return end;
	}

	public void setEnd(ID end) {
		this.end = end;
	}
	
	@Override
	public String toString(){
		return "==============================================================================================\n"
				+ "Sector Start: " + start + " \nSector End: " + end + 
				"\n==============================================================================================\n";
	}

}
