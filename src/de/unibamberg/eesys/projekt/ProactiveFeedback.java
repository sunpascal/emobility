package de.unibamberg.eesys.projekt;

public class ProactiveFeedback {
	
	// <b>Top speeds. </b>At 110 km/h, driving 20km/h faster consumes 25% for energy.\n\n
	private static String feedbackTopSpeed = "If you drive 20 km/h slower, you will consume 25% less energy.";
	private static String feedbackConstantSpeed = "Driving at constant speed can reduce energy consumption by 20%.";
	
	public String getFeedbackIfAvailable() {
		
		// if speed is > 130 km/h 
			return feedbackTopSpeed;
			
		// if speed variation is > ....  
//			return feedbackConstantSpeed;			
		
	}

}
