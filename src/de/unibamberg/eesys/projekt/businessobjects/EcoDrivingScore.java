package de.unibamberg.eesys.projekt.businessobjects;

public class EcoDrivingScore implements Comparable {
	
	private boolean visible = true; 
	
	public String getTechniqueName() {
		return techniqueName;
	}

	public void setTechniqueName(String techniqueName) {
		this.techniqueName = techniqueName;
	}

	public String getDividingText() {
		return dividingText;
	}

	public void setDividingText(String dividingText) {
		this.dividingText = dividingText;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	private String techniqueName; 
	private String dividingText = "";
	private int progress;
	
	public EcoDrivingScore(String techniqueName, int progress, String dividingText) {
		this.techniqueName = techniqueName;
		this.dividingText = dividingText;
		this.progress = progress;
	}	
	
	public EcoDrivingScore(String techniqueName, int progress) {
		this.techniqueName = techniqueName;
		this.dividingText = dividingText;
		this.progress = progress;
	}

	@Override
	/** allow sorting by progress 
	 * 
	 * Array will be sorted in DESCENDING order 
	 */
		public int compareTo(Object another) {
		
			if (progress < ((EcoDrivingScore) another).getProgress())
				return 1;
			else if (progress > ((EcoDrivingScore) another).getProgress())
				return -1; 
			else return 0;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	

}
