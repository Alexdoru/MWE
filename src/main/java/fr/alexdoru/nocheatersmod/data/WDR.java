package fr.alexdoru.nocheatersmod.data;

import java.util.ArrayList;

public class WDR {
	
	public long timestamp;
	public ArrayList<String> hacks;

	public WDR(long timestamp, ArrayList<String> hacks) {
		
		this.timestamp = timestamp;
		this.hacks = hacks;
		
	}
	
	/**
	 * Compares the timestamp of timestamped reports
	 * @param wdr
	 * @return
	 */
	public int compareTo(WDR wdr) {
		return compare(this, wdr);
	}
	
	/**
	 * Compares the timestamp of timestamped reports
	 * @param wdr1
	 * @param wdr2
	 * @return
	 */
	private static int compare(WDR wdr1, WDR wdr2) {
		
		Long x = Long.parseLong(wdr1.hacks.get(3));
		Long y = Long.parseLong(wdr2.hacks.get(3));
		
		return Long.compare(x, y);
	}

	public int compareToInvert(WDR wdr) {
		return compare(wdr, this);
	}
	
	public boolean isOnlyStalking() {
		return this.hacks.size() == 1 && this.hacks.get(0).contains("stalk");
	}
	
}