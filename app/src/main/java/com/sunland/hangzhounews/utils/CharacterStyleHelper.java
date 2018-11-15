package com.sunland.hangzhounews.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.widget.TextView;

public class CharacterStyleHelper {
    public static void setNewsTitleText(Context context, TextView textView, String content, int textSize, int colorId, int selectedColor){
        ColorStateList xpp1= context.getResources().getColorStateList(colorId);
        ColorStateList xpp2= context.getResources().getColorStateList(selectedColor);
        SpannableStringBuilder ssb=new SpannableStringBuilder(content);
        ssb.setSpan(new TextAppearanceSpan("serif", 0, textSize, xpp1,xpp2),0,content.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        textView.setText(ssb);
    }


}
