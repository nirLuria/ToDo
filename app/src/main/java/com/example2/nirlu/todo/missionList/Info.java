package com.example2.nirlu.todo.missionList;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;package com.example22.nluria.missionList;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by nluria on 5/21/2017.
 */

public class Info extends AppCompatActivity
{

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
        textView = (TextView)findViewById(R.id.info);
        String str="כל הזכויות שמורות לניר לוריא.\n" +
                "אם יש לכם הצעות לשיפור, אנא צרו עמי קשר:\n" +
                "2nirluria@gmail.com";
        textView.setText(str);
        textView.setTextSize(18);


        optionBar();
    }


    private void optionBar()
    {
        ActionBar ab = getSupportActionBar();
        ab.setLogo(R.drawable.logo_brown);
        ab.setDisplayUseLogoEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setTitle("2Do");
        ab.setBackgroundDrawable(new ColorDrawable(Color.argb(252,352,524,525)));

    }
}
