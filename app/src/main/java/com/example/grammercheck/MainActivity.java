package com.example.grammercheck;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    EditText input;
    TextView output;
    Button submit;
    String text;

    //api key
    //vJTW1KUmyAxAEzAy
    //link
    //https://api.textgears.com/grammar?key=DEMO_KEY&text=I+is+an+engeneer!&language=en-GB
    String FirstPart="https://api.textgears.com/grammar?key=vJTW1KUmyAxAEzAy&text=";
    String Last_Part="!&language=en-GB";
    //https://api.textgears.com/grammar?key=vJTW1KUmyAxAEzAy&text=I+laik+too+drink+coffee+everi+day.!&language=en-GB

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input=findViewById(R.id.input);
        output=findViewById(R.id.output);
        submit=findViewById(R.id.submit);
        output.setGravity(Gravity.CENTER_HORIZONTAL);
        output.setGravity(Gravity.CENTER_VERTICAL);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text=input.getText().toString();
                String url=FirstPart+text+Last_Part;
                url=url.replace(' ','+');
                Toast.makeText(MainActivity.this, "Submit Pressed Please Wait", Toast.LENGTH_SHORT).show();
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                       // Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        try {
                            List<String> error=new ArrayList<String>();
                            Map mp=new HashMap();

                            JSONObject respon=response.getJSONObject("response");
                            JSONArray errors=respon.getJSONArray("errors");
                            //Toast.makeText(MainActivity.this, Integer.toString(errors.length()), Toast.LENGTH_SHORT).show();
                            if(errors.length()==0)
                            {
                                output.setText(text);
                                return;
                            }


                            for(int i=0;i<errors.length();i++)
                            {
                                JSONObject SingleError= (JSONObject) errors.get(i);
                                String s=SingleError.getString("bad");
                                int x=SingleError.getInt("offset");
                                mp.put(x,s);
                                error.add(new String(s));

                               // Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                            }
                            SpannableStringBuilder builder = new SpannableStringBuilder();
                            List<Integer> list=new ArrayList<Integer>();

                            Set set=mp.entrySet();//Converting to Set so that we can traverse
                            Iterator itr=set.iterator();
                            while(itr.hasNext()){
                                //Converting to Map.Entry so that we can get key and value separately
                                Map.Entry entry=(Map.Entry)itr.next();
                                int x= (int) entry.getKey();
                                String s= (String) entry.getValue();
                                for(int i=x;i>=0;i--)
                                {
                                    if(text.substring(i,i+s.length()).equals(s))
                                    {
                                        list.add(i);
                                        break;
                                    }
                                }

                            }
                            for(int i=0;i<text.length();)
                            {
                                if(i==list.get(0))
                                {
                                    String s=error.get(0);
                                    SpannableString span=new SpannableString(s);
                                    span.setSpan(new ForegroundColorSpan(Color.RED),0,s.length(),0);
                                    builder.append(span);
                                    list.remove(0);
                                    error.remove(0);
                                    i+=s.length();
                                    continue;

                                }
                                String temp=text.substring(i,i+1);
                                // Toast.makeText(MainActivity.this, temp, Toast.LENGTH_SHORT).show();
                                SpannableString span=new SpannableString(temp);
                                span.setSpan(new ForegroundColorSpan(Color.RED),0,0,0);
                                builder.append(span);
                                i++;

                            }
                            output.setText(builder);

                           //Naive Logic
                           /* for(int i=0;i<text.length()&&!error.isEmpty();)
                            {
                                String s=error.get(0);

                                //output.setText(s);
                                //Toast.makeText(MainActivity.this, Integer.toString(s.length())+" "+s, Toast.LENGTH_SHORT).show();
                              //  Toast.makeText(MainActivity.this, text.substring(i,i+s.length()), Toast.LENGTH_SHORT).show();

                                if(s.length()==1&&text.substring(i,i).equals(s))
                                {
                                    SpannableString span=new SpannableString(s);
                                    span.setSpan(new ForegroundColorSpan(Color.RED),0,0,0);
                                    builder.append(span);
                                    error.remove(0);
                                   // Toast.makeText(MainActivity.this, text.substring(i,i), Toast.LENGTH_SHORT).show();

                                    i++;
                                    continue;
                                }
                                if(text.substring(i,i+s.length()).equals(s))
                                {
                                    //Toast.makeText(MainActivity.this, text.substring(i,i+s.length()), Toast.LENGTH_SHORT).show();
                                    String T=text.substring(i,i+s.length());
                                    SpannableString span=new SpannableString(T);
                                    span.setSpan(new ForegroundColorSpan(Color.RED),0,T.length(),0);
                                    builder.append(span);

                                    i+=s.length();
                                    error.remove(0);
                                    continue;
                                }
                                String temp=text.substring(i,i+1);
                               // Toast.makeText(MainActivity.this, temp, Toast.LENGTH_SHORT).show();
                                SpannableString span=new SpannableString(temp);
                                span.setSpan(new ForegroundColorSpan(Color.RED),0,0,0);
                                builder.append(span);

                                i++;


                            }

                            output.setText(builder);*/




                        }catch (Exception e){
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                queue.add(request);

            }

        });

    }
}