package com.snailgame.graduate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;


public class FragmentMainActivity extends FragmentActivity implements OnClickListener
{
	private MainTab02 mTab02;
	private MainTab03 mTab03;

	  private static FragmentMainActivity application;
	  public static FragmentMainActivity getInstance() {
	        return application;
	    }
	/**
	 * �ײ��ĸ���ť
	 */
	private Button mEnter;
	private LinearLayout mTabBtnFrd;
	private LinearLayout mTabBtnAddress;
	/**
	 * ���ڶ�Fragment���й���
	 */
	private FragmentManager fragmentManager;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);
		initViews();
		fragmentManager = getSupportFragmentManager();
		setTabSelection(1);
		application = this;
	}



	private void initViews() {

		mTabBtnFrd = (LinearLayout) findViewById(R.id.id_tab_bottom_friend);
		mTabBtnAddress = (LinearLayout) findViewById(R.id.id_tab_bottom_contact);
		mEnter = (Button) findViewById(R.id.EnterList);

		mTabBtnFrd.setOnClickListener(this);
		mTabBtnAddress.setOnClickListener(this);
		mEnter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(FragmentMainActivity.this, Plant_list.class));
			}
		});
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.id_tab_bottom_friend:
			setTabSelection(1);
			break;
		case R.id.id_tab_bottom_contact:
			setTabSelection(2);
			break;
		default:
			break;
		}
	}

	/**
	 * ��ݴ����index����������ѡ�е�tabҳ��
	 *
	 */
	@SuppressLint("NewApi")
	private void setTabSelection(int index)
	{
		// ���ð�ť
		resetBtn();
		// ����һ��Fragment����
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		hideFragments(transaction);
		switch (index)
		{
		case 1:
			((ImageButton) mTabBtnFrd.findViewById(R.id.btn_tab_bottom_friend))
					.setImageResource(R.drawable.tab_find_frd_pressed);
			if (mTab02 == null)
			{
				mTab02 = new MainTab02();
				transaction.add(R.id.id_content, mTab02);
			} else
			{
				transaction.show(mTab02);
			}
			break;
		case 2:
			((ImageButton) mTabBtnAddress.findViewById(R.id.btn_tab_bottom_contact))
					.setImageResource(R.drawable.tab_address_pressed);
			if (mTab03 == null)
			{
				mTab03 = new MainTab03();
				transaction.add(R.id.id_content, mTab03);
			} else
			{
				transaction.show(mTab03);
			}
			break;
		}
		transaction.commit();
	}

	/**
	 * �������е�ѡ��״̬��
	 */
	private void resetBtn()
	{
		((ImageButton) mTabBtnFrd.findViewById(R.id.btn_tab_bottom_friend))
				.setImageResource(R.drawable.tab_find_frd_normal);
		((ImageButton) mTabBtnAddress.findViewById(R.id.btn_tab_bottom_contact))
				.setImageResource(R.drawable.tab_address_normal);
	}

	/**
	 * �����е�Fragment����Ϊ����״̬��
	 *
	 * @param transaction
	 *            ���ڶ�Fragmentִ�в���������
	 */
	@SuppressLint("NewApi")
	private void hideFragments(FragmentTransaction transaction)
	{
		if (mTab02 != null)
		{
			transaction.hide(mTab02);
		}
		if (mTab03 != null)
		{
			transaction.hide(mTab03);
		}

	}

}
