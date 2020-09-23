package com.hardware.sample.widget;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hardware.sample.R;
import com.utils.AppUtils;
import com.widget.draw.MoveDrawAreaView;

public class MoveDarwActivity extends Activity implements View.OnClickListener {

    /**
     * 年月日的集合    [20160726, 20160725, 20160724]
     */


    /**
     * Called when the activity is first created.
     */

    private Button btnErase;
    private MoveDrawAreaView mdavArea;
    private Button btnDraw;
    private ImageView csvv_surface;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载MainView
        setContentView(R.layout.activity_move_draw);
        mdavArea = (MoveDrawAreaView) findViewById(R.id.mdav_area);
        btnErase= (Button) findViewById(R.id.btn_erase);
        btnDraw= (Button) findViewById(R.id.btn_draw);
        csvv_surface= (ImageView) findViewById(R.id.csvv_surface);
        initValue();
    }

    private void initValue() {
        int viewWidth = AppUtils.getWidth(this);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mdavArea.getLayoutParams();
        layoutParams.width = viewWidth;
        layoutParams.height = viewWidth * 9 / 16;

        RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) csvv_surface.getLayoutParams();
        layoutParams2.width = viewWidth;
        layoutParams2.height = viewWidth * 9 / 16;
        mdavArea.setWidthNumX(22);
        mdavArea.setHeighNumY(18);
        mdavArea.setLayoutParams(layoutParams);
        csvv_surface.setLayoutParams(layoutParams2);
        mdavArea.setVisibility(View.VISIBLE);
        mdavArea.redraw();
        btnErase.setOnClickListener(this );
        btnDraw.setOnClickListener(this );
        mdavArea.setType(MoveDrawAreaView.Type.Darw);
    }

    public void onBack(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dc_left_arrow_layout:

                break;

            case R.id.btn_erase:
                if (mdavArea.getType().equals(MoveDrawAreaView.Type.Normal)) {
                    return;
                }
                if (!mdavArea.getType().equals(MoveDrawAreaView.Type.Clean)) {
                    mdavArea.setType(MoveDrawAreaView.Type.Clean);
                    btnErase.setBackgroundResource(R.drawable.move_draw_erase_select_v);
                } else {
                    mdavArea.setType(MoveDrawAreaView.Type.Darw);
                    btnErase.setBackgroundResource(R.drawable.move_draw_erase_v);
                }

                break;

            case R.id.btn_draw:
//                if (mdavArea.getType().equals(MoveDrawAreaView.Type.Normal)){
//                    return;
//                }
                mdavArea.redraw();
                break;
        }
    }
}