
public class AdGroupPair {
	private int advertiser;
	private int group;
	
	public AdGroupPair(int a, int g) {
		this.setAdvertiser(a);
		this.setGroup(g);
	}

	public int getAdvertiser() {
		return advertiser;
	}

	public void setAdvertiser(int advertiser) {
		this.advertiser = advertiser;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}
	
	@Override
	public String toString() {
		return "(" + getAdvertiser() + ", " + getGroup() + ")";
	}
}
