package de.haw.ttv.main;

import de.uniba.wiai.lspi.chord.data.ID;

public class Sector {
	
	private ID start;
	private ID end;
	private ID middle;
	
	private boolean firedAt; //true wenn beschossen

	public Sector(ID start, ID end) {
		this.start = start;
		this.end = end;
		calcMiddle();
		this.setFiredAt(false);
	}

	public Sector() {
		this(null, null);
	}
	
	private void calcMiddle() {
		if(start.compareTo(end) < 0){
			setMiddle(start.add(start.subtract(end).divide(2)));
		}else{
			setMiddle(end.add(end.subtract(start).divide(2)));
		}
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
				+ "Sector Start: " + start.toBigInteger() + " \nSector End:   " + end.toBigInteger() + 
				"\n==============================================================================================\n";
	}

	public boolean isFiredAt() {
		return firedAt;
	}

	public void setFiredAt(boolean firedAt) {
		this.firedAt = firedAt;
	}

	public ID getMiddle() {
		return middle;
	}

	public void setMiddle(ID middle) {
		this.middle = middle;
	}

}
