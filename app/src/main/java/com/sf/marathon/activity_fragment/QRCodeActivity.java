/*Copyright ©2015 TommyLemon(https://github.com/TommyLemon)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/

package com.sf.marathon.activity_fragment;

import android.net.Uri;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import java.io.File;
import com.sf.marathon.R;
import com.sf.marathon.model.User;
import zuo.biao.library.base.BaseActivity;
import zuo.biao.library.interfaces.OnBottomDragListener;
import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.manager.CacheManager;
import zuo.biao.library.ui.WebViewActivity;
import zuo.biao.library.util.CommonUtil;
import zuo.biao.library.util.DownloadUtil;
import zuo.biao.library.util.JSON;
import zuo.biao.library.util.Log;
import zuo.biao.library.util.SettingUtil;
import zuo.biao.library.util.StringUtil;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.zxing.WriterException;
import com.zxing.encoding.EncodingHandler;

/**二维码界面Activity
 * @author Lemon
 */
public class QRCodeActivity extends BaseActivity implements OnBottomDragListener {
	private static final String TAG = "QRCodeActivity";

	//启动方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


	/**启动这个Activity的Intent
	 * @param context
	 * @param userId
	 * @return
	 */
	public static Intent createIntent(Context context, long userId) {
		return new Intent(context, QRCodeActivity.class).
				putExtra(INTENT_ID, userId);
	}

	//启动方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	@Override
	public Activity getActivity() {
		return this;
	}

	private long userId = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qrcode_activity, this);

		intent = getIntent();
		userId = intent.getLongExtra(INTENT_ID, userId);

		//功能归类分区方法，必须调用<<<<<<<<<<
		initView();
		initData();
		initEvent();
		//功能归类分区方法，必须调用>>>>>>>>>>

	}


	//UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


	private ImageView ivQRCodeHead;
	private TextView tvQRCodeName;

	private ImageView ivQRCodeCode;
	private View ivQRCodeProgress;
	@Override
	public void initView() {//必须调用
		autoSetTitle();
		
		ivQRCodeHead = findView(R.id.ivQRCodeHead);
		tvQRCodeName = findView(R.id.tvQRCodeName);

		ivQRCodeCode = findView(R.id.ivQRCodeCode);
		ivQRCodeProgress = findView(R.id.ivQRCodeProgress);
	}


	//UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>










	//Data数据区(存在数据获取或处理代码，但不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


	private User user;
	@Override
	public void initData() {//必须调用
		
		ivQRCodeProgress.setVisibility(View.VISIBLE);
		runThread(TAG + "initData", new Runnable() {

			@Override
			public void run() {

				user = CacheManager.getInstance().get(User.class, "" + userId);
				if (user == null) {
					user = new User(userId);
				}
				runUiThread(new Runnable() {
					@Override
					public void run() {
						Glide.with(context).load(user.getHead()).into(ivQRCodeHead);
						tvQRCodeName.setText(StringUtil.getTrimedString(
								StringUtil.isNotEmpty(user.getName(), true)
								? user.getName() : user.getPhone()));
					}
				});

				setQRCode(user);
			}
		});

	}

	private Bitmap qRCodeBitmap;
	protected void setQRCode(User user) {
		if (user == null) {
			Log.e(TAG, "setQRCode  user == null" +
					" || StringUtil.isNotEmpty(user.getPhone(), true) == false >> return;");
			return;
		}

		try {
			qRCodeBitmap = EncodingHandler.createQRCode(JSON.toJSONString(user)
					, (int) (2 * getResources().getDimension(R.dimen.qrcode_size)));
		} catch (WriterException e) {
			e.printStackTrace();
			Log.e(TAG, "initData  try {Bitmap qrcode = EncodingHandler.createQRCode(contactJson, ivQRCodeCode.getWidth());" +
					" >> } catch (WriterException e) {" + e.getMessage());
		}

		runUiThread(new Runnable() {
			@Override
			public void run() {
					ivQRCodeProgress.setVisibility(View.GONE);
					ivQRCodeCode.setImageBitmap(qRCodeBitmap);						
			}
		});	
	}

	//Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








	//Event事件区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	@Override
	public void initEvent() {//必须调用

	}

	//系统自带监听方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	@Override
	public void onDragBottom(boolean rightToLeft) {
		if (rightToLeft) {

			return;
		}

		finish();
	}



	//类相关监听<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


	@Override
	protected void onDestroy() {
		super.onDestroy();

		ivQRCodeProgress = null;
		ivQRCodeCode = null;
		user = null;

		if (qRCodeBitmap != null) {
			if (qRCodeBitmap.isRecycled() == false) {
				qRCodeBitmap.recycle();
			}
			qRCodeBitmap = null;
		}
		if (ivQRCodeCode != null) {
			ivQRCodeCode.setImageBitmap(null);
			ivQRCodeCode = null;
		}
	}


	//类相关监听>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	//系统自带监听方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


	//Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








	//内部类,尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



	//内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

  /**关于界面
   * @author Lemon
   */
  public static class AboutActivity extends BaseActivity implements OnClickListener,
      OnLongClickListener, OnBottomDragListener {
    private static final String TAG = "AboutActivity";

    //启动方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
      return new Intent(context, AboutActivity.class);
    }

    //启动方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    @Override
    public Activity getActivity() {
      return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.about_activity, this);

      //功能归类分区方法，必须调用<<<<<<<<<<
      initView();
      initData();
      initEvent();
      //功能归类分区方法，必须调用>>>>>>>>>>

      if (SettingUtil.isOnTestMode) {
        showShortToast("测试服务器\n" + com.sf.marathon.util.HttpRequest.URL_BASE);
      }


      //仅测试用
      com.sf.marathon.util.HttpRequest.translate("library", 0, new OnHttpResponseListener() {

        @Override
        public void onHttpResponse(int requestCode, String resultJson, Exception e) {
          showShortToast("测试Http请求:翻译library结果为\n" + resultJson);
        }
      });

    }

    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private ImageView ivAboutGesture;

    private TextView tvAboutAppInfo;

    private ImageView ivAboutQRCode;
    @Override
    public void initView() {

      ivAboutGesture = findView(R.id.ivAboutGesture);
      ivAboutGesture.setVisibility(SettingUtil.isFirstStart ? View.VISIBLE : View.GONE);
      if (SettingUtil.isFirstStart) {
        ivAboutGesture.setImageResource(R.drawable.gesture_left);
      }

      tvAboutAppInfo = findView(R.id.tvAboutAppInfo);

      ivAboutQRCode = findView(R.id.ivAboutQRCode, this);
    }


    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>










    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initData() {

      tvAboutAppInfo.setText(com.sf.marathon.application.DemoApplication.getInstance().getAppName()
          + "\n" + com.sf.marathon.application.DemoApplication.getInstance().getAppVersion());

      setQRCode();
    }


    private Bitmap qRCodeBitmap;
    /**显示二维码
     */
    protected void setQRCode() {
      runThread(TAG + "setQRCode", new Runnable() {

        @Override
        public void run() {

          try {
            qRCodeBitmap = EncodingHandler.createQRCode(com.sf.marathon.util.Constant.APP_DOWNLOAD_WEBSITE
                , (int) (2 * getResources().getDimension(R.dimen.qrcode_size)));
          } catch (WriterException e) {
            e.printStackTrace();
            android.util.Log.e(TAG, "initData  try {Bitmap qrcode = EncodingHandler.createQRCode(contactJson, ivContactQRCodeCode.getWidth());" +
                " >> } catch (WriterException e) {" + e.getMessage());
          }

          runUiThread(new Runnable() {
            @Override
            public void run() {
              ivAboutQRCode.setImageBitmap(qRCodeBitmap);
            }
          });
        }
      });
    }

    /**下载应用
     */
    private void downloadApp() {
      showProgressDialog("正在下载...");
      runThread(TAG + "downloadApp", new Runnable() {
        @Override
        public void run() {
          File file = DownloadUtil
              .downLoadFile(context, "ZBLibraryDemo", ".apk", com.sf.marathon.util.Constant.APP_DOWNLOAD_WEBSITE);
          dismissProgressDialog();
          DownloadUtil.openFile(context, file);
        }
      });
    }

    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








    //Event事件区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initEvent() {

      findView(R.id.llAboutMainTabActivity).setOnClickListener(this);
      findView(R.id.llAboutZBLibraryMainActivity).setOnClickListener(this);

      findView(R.id.llAboutUpdate).setOnClickListener(this);
      findView(R.id.llAboutShare).setOnClickListener(this);
      findView(R.id.llAboutComment).setOnClickListener(this);

      findView(R.id.llAboutDeveloper, this).setOnLongClickListener(this);
      findView(R.id.llAboutWeibo, this).setOnLongClickListener(this);
      findView(R.id.llAboutContactUs, this).setOnLongClickListener(this);
    }

    //系统自带监听方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void onDragBottom(boolean rightToLeft) {
      if (rightToLeft) {
        toActivity(WebViewActivity.createIntent(context, "博客", com.sf.marathon.util.Constant.APP_OFFICIAL_BLOG));

        ivAboutGesture.setImageResource(R.drawable.gesture_right);
        return;
      }

      if (SettingUtil.isFirstStart) {
        runThread(TAG + "onDragBottom", new Runnable() {
          @Override
          public void run() {
            android.util.Log.i(TAG, "onDragBottom  >> SettingUtil.putBoolean(context, SettingUtil.KEY_IS_FIRST_IN, false);");
            SettingUtil.putBoolean(SettingUtil.KEY_IS_FIRST_START, false);
          }
        });
      }

      finish();
    }

    @Override
    public void onClick(View v) {
      switch (v.getId()) {
      case R.id.llAboutMainTabActivity:
        startActivity(MainTabActivity.createIntent(context).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        overridePendingTransition(R.anim.bottom_push_in, R.anim.hold);

        enterAnim = exitAnim = R.anim.null_anim;
        finish();
        break;
      case R.id.llAboutZBLibraryMainActivity:
        startActivity(com.sf.marathon.DEMO.DemoMainActivity.createIntent(context));
        overridePendingTransition(R.anim.bottom_push_in, R.anim.hold);
        break;

      case R.id.llAboutUpdate:
        toActivity(WebViewActivity.createIntent(context, "更新日志", com.sf.marathon.util.Constant.UPDATE_LOG_WEBSITE));
        break;
      case R.id.llAboutShare:
        CommonUtil
            .shareInfo(context, getString(R.string.share_app) + "\n 点击链接直接查看ZBLibrary\n" + com.sf.marathon.util.Constant.APP_DOWNLOAD_WEBSITE);
        break;
      case R.id.llAboutComment:
        showShortToast("应用未上线不能查看");
        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://details?id=" + getPackageName())));
        break;

      case R.id.llAboutDeveloper:
        toActivity(WebViewActivity.createIntent(context, "开发者", com.sf.marathon.util.Constant.APP_DEVELOPER_WEBSITE));
        break;
      case R.id.llAboutWeibo:
        toActivity(WebViewActivity.createIntent(context, "博客", com.sf.marathon.util.Constant.APP_OFFICIAL_BLOG));
        break;
      case R.id.llAboutContactUs:
        CommonUtil.sendEmail(context, com.sf.marathon.util.Constant.APP_OFFICIAL_EMAIL);
        break;

      case R.id.ivAboutQRCode:
        downloadApp();
        break;
      default:
        break;
      }
    }

    @Override
    public boolean onLongClick(View v) {
      switch (v.getId()) {
      case R.id.llAboutDeveloper:
        CommonUtil.copyText(context, com.sf.marathon.util.Constant.APP_DEVELOPER_WEBSITE);
        return true;
      case R.id.llAboutWeibo:
        CommonUtil.copyText(context, com.sf.marathon.util.Constant.APP_OFFICIAL_BLOG);
        return true;
      case R.id.llAboutContactUs:
        CommonUtil.copyText(context, com.sf.marathon.util.Constant.APP_OFFICIAL_EMAIL);
        return true;
      default:
        break;
      }
      return false;
    }



    //系统自带监听方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








    //内部类,尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



    //内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

  }
}