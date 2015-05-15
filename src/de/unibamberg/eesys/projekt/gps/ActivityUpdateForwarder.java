package de.unibamberg.eesys.projekt.gps;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.location.ActivityRecognitionResult;

public class ActivityUpdateForwarder extends IntentService {
	public static final String INTENT_ACTION = "ActivityRecognitionUpdate";

	public ActivityUpdateForwarder() {
		super("ActivityUpdateForwarder");
	}
	
	@Override
	 protected void onHandleIntent(Intent intent) {
	     if (ActivityRecognitionResult.hasResult(intent)) {
	         ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
	         
			int activityType = result.getMostProbableActivity().getType();
			int confidence = result.getMostProbableActivity().getConfidence();
			 
			Intent intentUpdate = new Intent();
			intentUpdate.setAction(INTENT_ACTION);
			intentUpdate.addCategory(Intent.CATEGORY_DEFAULT);
			
			intentUpdate.putExtra("activityType", activityType);
			intentUpdate.putExtra("confidence", confidence);
			sendBroadcast(intentUpdate);
	     }
	     
	     
	 }
	

    
	


	
	
}
