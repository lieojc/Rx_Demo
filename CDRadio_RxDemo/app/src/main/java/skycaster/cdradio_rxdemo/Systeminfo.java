package skycaster.cdradio_rxdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import utils.SlideAdapter;
import utils.SlideMenu;

/**
 * Created by 郑力嘉 on 2016/12/14.
 */
public class Systeminfo extends Activity{

    private SlideMenu slideMenu;
    private ImageView menuImge;
    private TextView tx1,tx2;
    private ListView listviewslide;
    private ListView listviewsecond;
    private ArrayList<String>slidelist=new ArrayList<>();
    private ArrayList<String>slidelist2=new ArrayList<>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.systeminfo);
        slideMenu = (SlideMenu) findViewById(R.id.slide_menu);

        menuImge = (ImageView) findViewById(R.id.title_bar_menu_btn);
        listviewslide = (ListView) findViewById(R.id.listViewslide);
        listviewsecond=(ListView)findViewById(R.id.listViewsecond);
        SlideAdapter slideAdapter=new SlideAdapter(Systeminfo.this,slidelist);
        SlideAdapter sldeAdapter2=new SlideAdapter(Systeminfo.this,slidelist2);
        listviewslide.setAdapter(slideAdapter);
        listviewsecond.setAdapter(sldeAdapter2);
        slidelist.add("测试页面");
        slidelist2.add("关于我们");
        menuImge.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (slideMenu.isMainScreenShowing()) {
                    slideMenu.openMenu();
                } else {
                    slideMenu.closeMenu();
                }
            }
        });


        listviewslide.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent();
                intent.setClass(Systeminfo.this, MainActivity.class);
                startActivity(intent);
            }

        });

        listviewsecond.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent();
                intent.setClass(Systeminfo.this, Systeminfo.class);
                startActivity(intent);
            }

        });

    }

}
