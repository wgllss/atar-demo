package com.atar.fragments;

/**
 * 
 */

import android.annotation.SuppressLint;
import android.fragment.CommonFragment;
import android.interfaces.NetWorkCallListener;
import android.os.Handler;
import android.os.Message;
import android.reflection.NetWorkMsg;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.atar.activitys.R;
import com.atar.common.CommonLoading;
import com.atar.enums.EnumMsgWhat;
import com.atar.widgets.refresh.DataDispose;
import com.atar.widgets.refresh.OnHandlerDataListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

/**
 *****************************************************************************************************************************************************************************
 * 淘股吧刷新Fragment
 * @author :Atar
 * @createTime:2014-6-3上午11:08:30
 * @version:1.0.0
 * @modifyTime:
 * @modifyAuthor:
 * @description: 一步解决实现各种控件刷新 同里带有操作数据handler
 *****************************************************************************************************************************************************************************
 */
@SuppressLint("Recycle")
public class AtarRefreshFragment<T extends PullToRefreshBase<V>, V extends View> extends CommonFragment implements OnHandlerDataListener<T, V>, NetWorkCallListener, OnClickListener {
	private T t;
	private DataDispose<T, V> mDataDispose;
	private boolean isFirstLoad = true;
	private boolean isSwitchViewData;

	private TextView txtToast;

	@Override
	public void onDataReceive(Message msg, T t) {
		CommonLoading.dissMissLoading();
	}

	@Override
	public void NetWorkCall(NetWorkMsg msg) {
		CommonLoading.dissMissLoading();
		onStopRefresh();
	}

	@Override
	public void onDataReceive(int msgWhat) {
		if (mDataDispose != null) {
			Message msg = new Message();
			msg.what = msgWhat;
			mDataDispose.onDataReceive(msg);
		}
	}

	@Override
	public void onDataReceive(int msgWhat, Object obj) {
		if (mDataDispose != null) {
			Message msg = new Message();
			msg.what = msgWhat;
			msg.obj = obj;
			mDataDispose.onDataReceive(msg);
		}
	}

	@Override
	public void onDataReceive(int msgWhat, int msg1, int msg2) {
		if (mDataDispose != null) {
			Message msg = new Message();
			msg.what = msgWhat;
			msg.arg1 = msg1;
			msg.arg2 = msg2;
			mDataDispose.onDataReceive(msg);
		}
	}

	@Override
	public void onDataReceive(int msgWhat, int msg1, int msg2, Object obj) {
		if (mDataDispose != null) {
			Message msg = new Message();
			msg.what = msgWhat;
			msg.arg1 = msg1;
			msg.arg2 = msg2;
			msg.obj = obj;
			mDataDispose.onDataReceive(msg);
		}
	}

	@Override
	public void setRefreshing() {
		if (t != null) {
			t.setRefreshing();
		}
	}

	@Override
	public void onStopRefresh() {
		if (mDataDispose != null) {
			mDataDispose.onStopRefresh();
		}
	}

	@Override
	public DataDispose<T, V> getDataDispose() {
		return mDataDispose;
	}

	@Override
	public boolean isPullDownRefresh() {
		return (t != null ? t.getCurrentMode() : Mode.PULL_FROM_START) == Mode.PULL_FROM_START;
	}

	@Override
	public boolean isFirstLoad() {
		return isFirstLoad;
	}

	@Override
	public void setIsFirstLoad(boolean isFirstLoad) {
		this.isFirstLoad = isFirstLoad;
	}

	@Override
	public T getPullView() {
		return t;
	}

	@Override
	public V getRefreshView() {
		return t != null ? t.getRefreshableView() : null;
	}

	@Override
	public void setRefreshMode(Mode mode) {
		if (t != null) {
			t.setMode(mode);
		}
	}

	@Override
	public boolean isSwitchData() {
		return isSwitchViewData;
	}

	@Override
	public void setIsSwitchViewData(boolean isSwitchViewData) {
		this.isSwitchViewData = isSwitchViewData;
	}

	/**
	 * 设置toast提示内容
	 * @author :Atar
	 * @createTime:2014-11-14下午4:21:23
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param strToastContent
	 * @description:
	 */
	public void setToastText(String strToastContent) {
		if (txtToast != null) {
			txtToast.setText(strToastContent);
			txtToast.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 隐藏toast
	 * @author :Atar
	 * @createTime:2014-11-19下午9:01:47
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @description:
	 */
	public void setToastTextGone() {
		if (txtToast != null) {
			txtToast.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置toast提示
	 * @author :Atar
	 * @createTime:2015-6-3下午1:42:15
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param txtToast
	 * @description:
	 */
	public void setTextView(TextView txtToast) {
		this.txtToast = txtToast;
	}

	/**
	 * 设置刷新控件
	 * @author :Atar
	 * @createTime:2015-4-3上午10:19:26
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param t
	 * @description:
	 */
	protected void setRefreshView(T t) {
		this.t = t;
		if (mDataDispose == null) {
			mDataDispose = new DataDispose<T, V>(t, this);
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		initUI(isVisibleToUser);
	}

	private static Handler handler;

	protected void initUI(final boolean isVisibleToUser) {
		if (handler == null) {
			handler = new Handler();
		}
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (t == null || getActivity() == null || !isVisibleToUser) {
					initUI(isVisibleToUser);
				} else {
					if (isFirstLoad()) {
						onDataReceive(EnumMsgWhat.LOAD_FROM_SQL);
						setIsFirstLoad(false);
					}
				}
			}
		}, 100);
	}

	@Override
	public void onClick(View v) {
	}

	public void onSwitchUI(int which, int which2, String changeID) {
	}

	public void onRefreshUI(int which, int which2, Object obj) {
	}

	@Override
	public void OnChangeSkin(int skinType) {
		super.OnChangeSkin(skinType);
		if (getPullView() != null && getActivity() != null) {
			if (getPullView().getHeaderLayout() != null) {
				getPullView().getHeaderLayout().setRefreshingLabel(getResources().getString(R.string.refreshing_waiting));
				getPullView().getHeaderLayout().setPullLabel(getResources().getString(R.string.pull_to_start_refresh));
				getPullView().getHeaderLayout().setReleaseLabel(getResources().getString(R.string.pull_to_start_reset));
				// getPullView().getHeaderLayout().setRefreshingDrawable(GlobeSettings.refreshImg[skinType]);
			}
			if (getPullView().getFooterLayout() != null) {
				getPullView().getFooterLayout().setRefreshingLabel(getResources().getString(R.string.refreshing_waiting));
				getPullView().getFooterLayout().setPullLabel(getResources().getString(R.string.pull_to_up_load_more));
				getPullView().getFooterLayout().setReleaseLabel(getResources().getString(R.string.pull_to_up_reset));
				// getPullView().getFooterLayout().setRefreshingDrawable(GlobeSettings.refreshImg[skinType]);
			}
			//
			// if (getPullView().getHeaderLayout() != null && getPullView().getHeaderLayout().getHeaderText() != null) {
			// getPullView().getHeaderLayout().setHeaderTextColor(getActivity().getResources().obtainTypedArray(R.array.txt_day_grey_night_greyblack_color).getColor(skinType, 0));
			// }
			// if (getPullView().getFooterLayout() != null && getPullView().getFooterLayout().getHeaderText() != null) {
			// getPullView().getFooterLayout().setHeaderTextColor(getActivity().getResources().obtainTypedArray(R.array.txt_day_grey_night_greyblack_color).getColor(skinType, 0));
			// }
			// if (getPullView().getHeaderLayout() != null && getPullView().getHeaderLayout().getSubHeaderText() != null) {
			// getPullView().getHeaderLayout().setSubHeaderTextColor(getActivity().getResources().obtainTypedArray(R.array.txt_day_grey_night_greyblack_color).getColor(skinType, 0));
			// }
			// if (getPullView().getFooterLayout() != null && getPullView().getFooterLayout().getSubHeaderText() != null) {
			// getPullView().getFooterLayout().setSubHeaderTextColor(getActivity().getResources().obtainTypedArray(R.array.txt_day_grey_night_greyblack_color).getColor(skinType, 0));
			// }
			// if (getPullView().getHeaderLayout() != null) {
			// getPullView().getHeaderLayout().setRefreshBgColor(getActivity().getResources().obtainTypedArray(R.array.refresh_bg_color).getColor(skinType, 0));
			// }
			// if (getPullView().getFooterLayout() != null) {
			// getPullView().getFooterLayout().setRefreshBgColor(getActivity().getResources().obtainTypedArray(R.array.refresh_bg_color).getColor(skinType, 0));
			// }
		}
		// LoadUtil.setTextColor(getActivity(), txtToast, R.array.txt_day_grey_night_greyblack_color, skinType);
	}
}