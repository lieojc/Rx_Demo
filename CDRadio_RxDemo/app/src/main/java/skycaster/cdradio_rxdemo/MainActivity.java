package skycaster.cdradio_rxdemo;

import android.app.Activity;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.skycaster.jniutils.JniHelper;
import com.skycaster.l_cdradiorx.bases.BaseActivity;
import com.skycaster.l_cdradiorx.bases.BaseApplication;
import com.skycaster.l_cdradiorx.beans.AckBean;
import com.skycaster.l_cdradiorx.beans.DSP;
import com.skycaster.l_cdradiorx.manager.DSPManager;
import com.skycaster.l_cdradiorx.receiver.DSPSensor;
import com.skycaster.l_cdradiorx.utils.RequestUtils;
import com.skycaster.l_cdradiorx.utils.ToastUtil;
import com.skycaster.l_cdradiorx.utils.UsbUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import utils.BizDataAdapter;
import utils.LocalFil;
import utils.SlideAdapter;
import utils.SlideMenu;
import utils.TimingUtils;

/**
 * Created by 郑力嘉 on 2016/12/14.
 */


public class MainActivity extends BaseActivity {

    private SlideMenu slideMenu;
    private DSPManager mDSPManager;
    private JniHelper jniHelper; //so库操作类
    private static AckBean ackBean;//
    private static RequestUtils.RequestCallBack rc;//
    private Button btn_startDevice;
    private ToggleButton btn_startBussiness;//业务数据开始操作按钮


    private Button btn_refreshdevice; //刷新设备操作按钮
    private Button btn_cleardata; //清除数据操作按钮
    private Button btn_resetConnection;//重置按钮
    private RadioGroup radiogroupfomat;//hex与ascii的切换radiogroup
    private ImageView menuImg; //SlideMenu的ImageView
    private TextView tv_activate_feedback; //信息反馈显示
    private ArrayList<byte[]>bizDataList=new ArrayList<>();
    private ArrayList<String>listslideadd=new ArrayList<>();
    private ArrayList<String>listslidesecond=new ArrayList<>();
    private TextView tv_missCount;
    private TextView tv_dataFileName;//文件存储名
    private TextView tv_dataPassTime;//计时TextView
    private TextView tv_bizDataCount;
    private UsbDevice usbDevice;
    private UsbInterface usbInterface;
    private EditText edt_setFreq;
    private EditText edt_toneLeft;
    private EditText edt_toneRight;
    private double freq=0.98;
    private int toneLeft=36;
    private int toneRight=45;
    private DSP dsp;
    private Handler handler=new Handler();
    private LinkedList<byte[]> daqData=new LinkedList<>();
    private byte[] temp=new byte[4106];
    private byte[] bizData=new byte[64];
//    private ArrayList<byte[]> bizData =new ArrayList<>();
   private BizDataAdapter bizDataAdapter=new BizDataAdapter(this,bizDataList);

    private StringBuffer stringBuffer;
    private StringBuffer stringBuffer2;
    private ListView listView;
    private ListView listviewslide;
    private ListView listviewsecond;
    private ListView lstv_bizData;
    private List<String> list=new ArrayList<>();
    private LayoutInflater inflater;

    private boolean isbulk;
    private StringBuffer hex;
    private boolean isclose=false;
    private DSPSensor dspSensor;
    private long data_count;
    //private byte[] bytes=new byte[4106];
   // private  ConvertHexToString ch;


    public static void startActivity(Activity context){
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected int getContentView() {

        return R.layout.activity_main;
    }

    @Override
    protected void initView() {


     //   btn_stopBussiness = (Button) findViewById(R.id.stopbussiness);
        btn_startBussiness=(ToggleButton)findViewById(R.id.startbusiness);

      //  btn_savefile=(Button)findViewById(R.id.safefile);
        btn_refreshdevice=(Button)findViewById(R.id.relaxdevice);
        btn_cleardata=(Button)findViewById(R.id.cleardata);
        tv_activate_feedback= (TextView) findViewById(R.id.main_tv_activate_feedback);
        tv_missCount=(TextView)findViewById(R.id.tiaozhennumber);
        tv_dataFileName=(TextView)findViewById(R.id.txwritename);
        tv_bizDataCount= (TextView) findViewById(R.id.activity_biz_tv_biz_data_count);
        edt_setFreq= (EditText) findViewById(R.id.widget_dsp_config2_edt_freq);
        edt_toneLeft= (EditText) findViewById(R.id.widget_dsp_config2_edt_tone_left);
        edt_toneRight= (EditText) findViewById(R.id.widget_dsp_config2_edt_tone_right);
     //   tv_bussiness=(TextView)findViewById(R.id.textViewbusiness);
        slideMenu = (SlideMenu) findViewById(R.id.slide_menu);
         menuImg = (ImageView) findViewById(R.id.title_bar_menu_btn);
        listView = (ListView) findViewById(R.id.testListView);
        listviewslide = (ListView) findViewById(R.id.listViewslide);
        listviewsecond=(ListView)findViewById(R.id.listViewsecond);
        radiogroupfomat=(RadioGroup)findViewById(R.id.radiogroupformat);
        tv_dataPassTime= (TextView) findViewById(R.id.activity_biz_tv_file_time_phrase);
        btn_resetConnection= (Button) findViewById(R.id.activity_biz_btn_reset_connection);

    }

    @Override
    protected void initData() {
       mDSPManager=DSPManager.getDSPManager();
        if(BaseApplication.getDsp()!=null){
            mDSPManager.getDeviceVersionInfo(new RequestUtils.RequestCallBack() {
                @Override
                public void onGetAckBean(AckBean ackBean) {
                    tv_activate_feedback.setText(ackBean.getVersionInfo());
                }
            });
        }
        radiogroupfomat.check(R.id.hexradio);
        listView.setAdapter(bizDataAdapter);
        SlideAdapter slideAdapter1 = new SlideAdapter(MainActivity.this,listslideadd);
        SlideAdapter slideAdapter2 = new SlideAdapter(MainActivity.this,listslidesecond);
        listviewslide.setAdapter(slideAdapter1);
        listviewsecond.setAdapter(slideAdapter2);
        listslideadd.add("测试页面");
        listslidesecond.add("关于我们");
//        listviewslide.setAdapter((new ArrayAdapter<String>(this,
//                R.layout.layout_menu));
    }

    @Override
    protected void initListener() {

     /*   btn_startDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isSuccess = mDSPManager.startDevice();
                if (isSuccess) {
                    tv_activate_feedback.setText("启动成功");
                } else {
                    tv_activate_feedback.setText("启动失败");
                    ToastUtil.showToast("请点击刷新设备");
                }
            }
        });*/

   /*     btn_stopDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isSuccess = mDSPManager.apiStopDevice();
                if(isSuccess){
                    tv_activate_feedback.setText("停止成功");
                }else {
                    tv_activate_feedback.setText("停止失败");
                }
            }
        });
*/



     /*   btn_startBussiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                boolean isSuccess = mDSPManager.apiGetService(serviceData2, 64, (byte) 33, new DSPManager.ServiceDataListener() {

                    @Override
                    public void preTask() {

                        listView.setAdapter(bizDataAdapter);
                        bizDataAdapter.changeFormat(true);
                    }

                    @Override
                    public void onGetBandData(byte[] bytes, int i) {

                    }

                    @Override
                    public void onReceiveData() {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                stringBuffer2 = new StringBuffer();
                                for (int i = 0; i < serviceData2.length; i++) {
                                    stringBuffer2.append(Integer.toHexString(serviceData2[i] & 0x00FF) + " ");

                                }
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // tv_bussiness.setText(stringBuffer.toString().trim());
                                        daqData.add(serviceData2);

                                        bizDataList.add(serviceData2);
                                      *//*  if(list.size()>10){
                                            list.remove(0);
                                        }*//*
                                        bizDataAdapter.notifyDataSetChanged();

                                    }
                                });

                            }
                        }).start();
                    }

                    @Override
                    public void onPacketMiss(final int missCount) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                tv_missCount.setText(missCount + "");
                            }
                        });
                    }

                    @Override
                    public void onServiceStop() {
                        tv_activate_feedback.post(new Runnable() {
                            @Override
                            public void run() {
                                tv_activate_feedback.setText("结束成功");
                            }
                        });
                    }
                });
                if(isSuccess){
                    tv_activate_feedback.setText("开始成功");
                }else {
                    tv_activate_feedback.setText("开始失败");
                }
            }
        });*/
/*

        btn_transferAsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                boolean isSuccess = mDSPManager.apiGetService(serviceData, 64, (byte) 33, new DSPManager.ServiceDataListener() {

                    @Override
                    public void preTask() {

                        listView.setAdapter(bizDataAdapter);
                        bizDataAdapter.changeFormat(false);
                    }

                    @Override
                    public void onGetBandData(byte[] bytes, int i) {

                    }

                    @Override
                    public void onReceiveData() {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                stringBuffer = new StringBuffer();

                                for (int i = 0; i < serviceData.length; i++) {
                                    stringBuffer.append(Integer.toHexString(serviceData[i] & 0x00FF) + " ");

                                }

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // tv_bussiness.setText(stringBuffer.toString().trim());
                                        //    ch.convertHexToString(hex);


                                        daqData.add(serviceData);

                                        bizDataList.add(serviceData);
                                      */
/*  if(list.size()>10){
                                            list.remove(0);
                                        }*//*

                                        bizDataAdapter.notifyDataSetChanged();
                                        //  daqData.add(list.clone());


                                    }
                                });

                            }
                        }).start();
                    }

                    @Override
                    public void onPacketMiss(int i) {

                    }

                    @Override
                    public void onServiceStop() {
                        tv_activate_feedback.post(new Runnable() {
                            @Override
                            public void run() {
                                tv_activate_feedback.setText("结束成功");
                            }
                        });
                    }
                });
                if(isSuccess){
                    tv_activate_feedback.setText("开始成功");
                }else {
                    tv_activate_feedback.setText("开始失败");
                }


            }
        });
*/



        //启动/停止业务数据
        btn_startBussiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_startBussiness.isChecked()) {
                   String s_freq = edt_setFreq.getText().toString().trim();
                    if (!TextUtils.isEmpty(s_freq)) {
                        freq = Double.valueOf(s_freq);
                        if (freq >= 108 || freq <= 88) {
                            ToastUtil.showToast("频点数值必须在88-108之间");
                            btn_startBussiness.setChecked(false);
                            return;
                        }
                    }
                    String s_toneLeft = edt_toneLeft.getText().toString().trim();
                    if (!TextUtils.isEmpty(s_toneLeft)) {
                        toneLeft = Integer.valueOf(s_toneLeft);
                    }
                    String s_toneRight = edt_toneRight.getText().toString().trim();
                    if (!TextUtils.isEmpty(s_toneLeft)) {
                        toneRight = Integer.valueOf(s_toneRight);
                    }
//                    dspManager.setFreq((int) (freq * 100));
//                    dspManager.setToneLeft(toneLeft);
//                    dspManager.setToneRight(toneRight);
                    final boolean isSuccess = mDSPManager.apiOpenCDRadio((int) (freq * 100), toneLeft, toneRight);
                    BaseApplication.post(new Runnable() {
                        @Override
                        public void run() {
                            if (isSuccess) {
                                ToastUtil.showToast("设置参数成功");
                            } else {
                                ToastUtil.showToast("设置参数失败");
                            }
                        }
                    });
                    boolean isServiceOn =  mDSPManager.apiGetService(bizData, bizData.length, (byte) 33, new DSPManager.ServiceDataListener() {
                        @Override
                        public void preTask() {
                            bizDataList.clear();
                            bizDataAdapter.notifyDataSetChanged();
                            tv_dataFileName.setText(LocalFil.prepareFile("busydata"));
                            TimingUtils.startTiming(new TimingUtils.TimeUpdater() {
                                @Override
                                public void onUpdate(long s) {
                                    tv_dataPassTime.setText(s + "");
                                }
                            });
                            data_count = 0;
                            tv_bizDataCount.setText("接受数量：0");
                            tv_missCount.setText("跳帧数量:0");

                        }

                        @Override
                        public void onGetBandData(byte[] bandData, int frameId) {

                        }


                        public void onReceiveData(final byte[] date) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    bizDataList.add(date.clone());
                                    if (bizDataList.size() > 20) {
                                        bizDataList.remove(0);
                                    }
                                    bizDataAdapter.notifyDataSetChanged();
                                    listView.smoothScrollToPosition(bizDataList.size() - 1);
                                    data_count++;
                                    tv_bizDataCount.setText("接受数量：" + data_count);
                                    LocalFil.writeNewFile(bizData.clone(), 0, bizData.length);
                                }
                            });
                        }

                        @Override
                        public void onPacketMiss(final int missCount) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    tv_missCount.setText("跳帧数量" + missCount);
                                }
                            });

                        }

                        @Override
                        public void onServiceStop() {
                            TimingUtils.stopTiming();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    btn_startBussiness.setChecked(false);

                                }
                            });
                        }
                    });


                    if(isServiceOn){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToast("业务数据启动成功");
                            }
                        });
                    }else {
                        btn_startBussiness.setChecked(false);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToast("业务数据启动失败");
                            }
                        });
                    }

                } else {
                    if (mDSPManager.apiStopService()) {
                        ToastUtil.showToast("业务数据停止成功");
                    } else {
                        ToastUtil.showToast("业务数据停止失败");
                    }
                }
            }
        });















/*

        btn_stopBussiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(!mDSPManager.apiStopService()){
                            tv_activate_feedback.post(new Runnable() {
                                @Override
                                public void run() {
                                    tv_activate_feedback.setText("结束失败");
                                }
                            });
                        }
                    }
                }).start();
            }

        });
*/

   /*     btn_savefile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    if(daqData.size()==0){
                        ToastUtil.showToast("无数据");
                        return;
                    }
                    LocalFil.prepareTunerFile();
                    for(byte[] data:daqData){
                        LocalFil.writeTunerFile(data);
                    }
                LocalFil.stopWriting();
                    daqData.clear();
                    ToastUtil.showToast("写入完成！");


            }
        });*/
   /*     btn_refreshdevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean isSuccess= UsbUtil.detectUSB();
                if(isSuccess){
                    setDspCommuParams(BaseApplication.getDsp());
                    tv_activate_feedback.setText("" + usbDevice.getVendorId());
                    tv_activate_feedback.setText("" + usbDevice.getProductId());
                }else {
                    tv_activate_feedback.setText("null");
                    tv_activate_feedback.setText("null");
                }
            }
        });
*/
        btn_resetConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UsbUtil.resetConnection()) {
                    ToastUtil.showToast("重置成功");
                } else {
                    ToastUtil.showToast("重置失败");
                }
            }
        });





        btn_refreshdevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UsbUtil.detectUSB()){
                    mDSPManager.getDeviceVersionInfo(new RequestUtils.RequestCallBack() {
                        @Override
                        public void onGetAckBean(AckBean ackBean) {
                            tv_activate_feedback.setText(ackBean.getVersionInfo());
                        }
                    });
                }else {
                    tv_activate_feedback.setText("DSP链接失败，请重新尝试插拔设备");
                }
            }
        });


        btn_cleardata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                daqData.clear();
                bizDataList.clear();
                listView.setAdapter(bizDataAdapter);

                //   List<String> list=new ArrayList<>();
            }
        });


        menuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (slideMenu.isMainScreenShowing()) {
                    slideMenu.openMenu();
                } else {
                    slideMenu.closeMenu();
                }


            }
        });





        radiogroupfomat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton child = (RadioButton) radiogroupfomat.getChildAt(0);
                if (child.isChecked()) {
                    bizDataAdapter.changeFormat(true);
                } else {
                    bizDataAdapter.changeFormat(false);
                }
            }
        });

        listviewslide.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent();
                intent.setClass(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }

        });

        listviewsecond.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent();
                intent.setClass(MainActivity.this, Systeminfo.class);
                startActivity(intent);
            }

        });
        }










 /*   public static int hexStringToAlgorism(String hex) {
        hex = hex.toUpperCase();
        int max = hex.length();
        int result = 0;
        for (int i = max; i > 0; i--) {
            char c = hex.charAt(i - 1);
            int algorism = 0;
            if (c >= '0' && c <= '9') {
                algorism = c - '0';
            } else {
                algorism = c - 55;
            }
            result += Math.pow(16, max - i) * algorism;
        }
        return result;
    }*/
/*
    public static String AsciiStringToString(String content) {
        String result = "";
        int length = content.length() / 2;
        for (int i = 0; i < length; i++) {
            String c = content.substring(i * 2, i * 2 + 2);
            int a = hexStringToAlgorism(c);
            char b = (char) a;
            String d = String.valueOf(b);
            result += d;
        }
        return result;
    }

    public static String hexStringToString(String hexString, int encodeType) {
        String result = "";
        int max = hexString.length() / encodeType;
        for (int i = 0; i < max; i++) {
            char c = (char) hexStringToAlgorism(hexString
                    .substring(i * encodeType, (i + 1) * encodeType));
            result += c;
        }
        return result;
    }*/


    public static String bytetoString(byte[] bytearray) {
        String result = "";
        char temp;

        int length = bytearray.length;
        for (int i = 0; i < length; i++) {
            temp = (char) bytearray[i];
            result += temp;
        }
        return result;
    }
}
