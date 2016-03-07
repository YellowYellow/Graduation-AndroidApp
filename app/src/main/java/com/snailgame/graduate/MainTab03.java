package com.snailgame.graduate;
 
import android.annotation.SuppressLint;  
import android.os.Bundle; 
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


@SuppressLint("NewApi")
public class MainTab03 extends Fragment
{

	TextView a;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View newsLayout = inflater.inflate(R.layout.main_tab_03, container, false);
		return newsLayout;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState); 
	  
	} 
}
