package de.unibamberg.eesys.projekt.businessobjects;

public class EcoDrivingProgress {
	
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
	
	public EcoDrivingProgress(String techniqueName, int progress, String dividingText) {
		this.techniqueName = techniqueName;
		this.dividingText = dividingText;
		this.progress = progress;
	}	
	
	public EcoDrivingProgress(String techniqueName, int progress) {
		this.techniqueName = techniqueName;
		this.dividingText = dividingText;
		this.progress = progress;
	}		
	
	

}
