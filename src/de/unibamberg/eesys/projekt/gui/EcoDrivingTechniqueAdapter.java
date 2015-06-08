package de.unibamberg.eesys.projekt.gui;

import de.unibamberg.eesys.projekt.R;
import de.unibamberg.eesys.projekt.businessobjects.EcoDrivingScore;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class EcoDrivingTechniqueAdapter extends ArrayAdapter<EcoDrivingScore> {
  private final Context context;
  private final EcoDrivingScore[] values;

  public EcoDrivingTechniqueAdapter(Context context, EcoDrivingScore[] values2) {
    super(context, -1, values2);
    this.context = context;
    this.values = values2;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View rowView = inflater.inflate(R.layout.progressbar_item, parent, false);
    
    TextView dividingLabel= (TextView) rowView.findViewById(R.id.dividingLabel);
    String dividingText = values[position].getDividingText(); 
    if (dividingText.isEmpty())
    	dividingLabel.setVisibility(0);
    else 
    	dividingLabel.setText(values[position].getDividingText());    
    
    TextView descr = (TextView) rowView.findViewById(R.id.progressBarDescr);
    descr.setText(values[position].getTechniqueName());
    
    ProgressBar progressBar = (ProgressBar) rowView.findViewById(R.id.progressBar1);
    progressBar.setProgress(values[position].getProgress());    

    return rowView;
  }
}