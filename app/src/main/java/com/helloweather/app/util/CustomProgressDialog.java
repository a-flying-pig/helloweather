package com.helloweather.app.util;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.helloweather.app.R;

/**
 * Created by Administrator on 2016-12-1.
 * 自定义透明的圆形进度条
 */
public class CustomProgressDialog {
    /**
     *  
     *
     * @brief   得到自定义的progressDialog（简述）
     * @param   context（）
     * @param   msg（）
     * @return  返回Dialog对象（return描述返回值）
     */
    public static Dialog getCustomProgressDialog(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null); // 得到加载View
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view); // 加载布局
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img); // main.xml中的ImageView
        TextView tipTextView = (TextView) v.findViewById(R.id.tip_text_view); // 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.load_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg); // 设置加载信息

        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog); // 创建自定义样式dialog
        loadingDialog.setCancelable(false); // 不可以用“返回键”取消
        // 设置布局
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        return loadingDialog;
    }
}
