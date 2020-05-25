package com.example.mytranslator;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    DbHandler db;
    ImageButton add;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db=new DbHandler(MainActivity.this);
        add = (ImageButton) findViewById(R.id.imagebutton_);
        final LinearLayout lm = (LinearLayout) findViewById(R.id.layout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(MainActivity.this,Main2Activity.class);
                startActivity(in);
            }
        });
        Cursor rs = db.getData();
        rs.moveToFirst();
        for(int i=0; i<rs.getCount(); i++)
        {
            final int cid = rs.getInt(0);
            final String oc = rs.getString(1);
            final String tc = rs.getString(2);
            rs.moveToNext();
            // Create LinearLayout
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.HORIZONTAL);

            // Create TextView
            TextView t = new TextView(this);
            int id = getResources().getIdentifier("drawable/rounded_edittext", "xml","com.example.mytranslator");

            ll.setBackground(getDrawable(id));

            t.setWidth(750);
            t.setPadding(25,25,25,25);
            t.setText(oc + " \n " + tc );
            ll.addView(t);
            // Create Button
            final ImageButton b = new ImageButton(this);
            int bid = getResources().getIdentifier("drawable/del", "png","com.example.mytranslator");
            b.setImageDrawable(getDrawable(bid));
            // Give button an ID
            b.setId(i);
            b.setLayoutParams(params);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.delete(cid);
                    Intent in=new Intent(MainActivity.this,MainActivity.class);
                    startActivity(in);
                }
            });
            t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this,Main2Activity.class);
                    i.putExtra("oc",oc);
                    i.putExtra("tc",tc);
                    startActivity(i);
                }
            });
            //Add button to LinearLayout
            ll.addView(b);
            //Add button to LinearLayout defined in XML
            lm.addView(ll);
        }
        if(!rs.isClosed())
        {
            rs.close();
        }
    }
}
