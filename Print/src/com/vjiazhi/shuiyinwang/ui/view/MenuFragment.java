
package com.vjiazhi.shuiyinwang.ui.view;
import com.umeng.update.UmengUpdateAgent;
import com.vjiazhi.yinji.R;
import com.vjiazhi.shuiyinwang.ui.AboutAppActivity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class MenuFragment extends Fragment implements View.OnClickListener {

	private View currentView;
	private RelativeLayout jieshao;
	private RelativeLayout fankui;
	private RelativeLayout gengxin;


	public View getCurrentView() {
		return currentView;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		currentView = inflater.inflate(R.layout.slidingpane_menu_layout,
				container, false);

		jieshao = (RelativeLayout) currentView.findViewById(R.id.zimingxing);
		fankui = (RelativeLayout) currentView.findViewById(R.id.yijian);
		gengxin = (RelativeLayout) currentView.findViewById(R.id.gengxin);

		jieshao.setOnClickListener(this);
		fankui.setOnClickListener(this);
		gengxin.setOnClickListener(this);
		
		
		return currentView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// ((ImgMainActivity) getActivity()).getSlidingPaneLayout().closePane();
		if (v.getId() == R.id.zimingxing) {
			Intent intent = new Intent(getActivity(), AboutAppActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.gengxin) {
			UmengUpdateAgent.forceUpdate(getActivity());
		} else if (v.getId() == R.id.yijian) {
//			L.l("===888888888888888888888s===ument:======"+mUmengFeedBack);
//			try
//			{
//			mUmengFeedBack.startFeedbackActivity();
//			}catch(Exception e)
//			{
//				L.l("===999:"+e.getMessage());
//				
//			}
			if(uMeng!=null)
			{
				uMeng.onClickPosition(0);
			}
		}
	}
	
	public interface UmengInterface {
		public void onClickPosition(int position);
	}
	
	UmengInterface uMeng=null;
	
	public void setUmengInterface(UmengInterface u)
	{
		uMeng=u;
	}
	
	
}
