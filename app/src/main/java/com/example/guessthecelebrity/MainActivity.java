package com.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> celebURLs=new ArrayList<String>();
    ArrayList<String> celebNames=new ArrayList<String>();
    int chosenCeleb=0;
    String[] answers = new String[4];
    int locationOfCorrectAnswers=0;
    int incorrectAnswerLocation=0;
    ImageView imageView;
    Button button1,button2,button3,button4;

    public void takeAnswers(View view)
    {
        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswers))){
            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Wrong! It was "+celebNames.get(chosenCeleb), Toast.LENGTH_SHORT).show();
        }
        try {
            newQuestion();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    int j,i;
    public void newQuestion() throws ExecutionException, InterruptedException {
        Random rand = new Random();
        chosenCeleb= rand.nextInt(celebNames.size());
        imageDownload image = new imageDownload();
        Bitmap celebPhoto = image.execute(celebURLs.get(chosenCeleb)).get();
        imageView.setImageBitmap(celebPhoto);

        locationOfCorrectAnswers = rand.nextInt(4);

        for(i=0;i<4;i++)
        {
            if(locationOfCorrectAnswers==i)
            {
                answers[i]=celebNames.get(chosenCeleb);
            }
            else
            {
                incorrectAnswerLocation = rand.nextInt(celebURLs.size());
                while(incorrectAnswerLocation==chosenCeleb)
                {
                    incorrectAnswerLocation = rand.nextInt(celebURLs.size());
                }
                for(j=0;j<i;j++)
                {
                    if(answers[i]==celebNames.get(incorrectAnswerLocation))
                        incorrectAnswerLocation = rand.nextInt(celebURLs.size());
                }
                answers[i]=celebNames.get(incorrectAnswerLocation);
            }
        }
        button1.setText(answers[0]);
        button2.setText(answers[1]);
        button3.setText(answers[2]);
        button4.setText(answers[3]);

    }
    public class imageDownload  extends AsyncTask<String, Void, Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }
    }
    public class DownloadHTML extends AsyncTask<String , Void, String>{
        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url ;
            HttpURLConnection connection=null;
            try{
                url =new URL(urls[0]);
                connection=(HttpURLConnection)url.openConnection();
                InputStream in =connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data =reader.read();
                while(data!=-1)
                {
                char current = (char) data;
                result+=current;
                data=reader.read();
                }
                return result;
            }
            catch(Exception e)
            {
            e.printStackTrace();
            return "Something Went Wrong";
            }
        }


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DownloadHTML task =new DownloadHTML();
        imageView = findViewById(R.id.imageView);
        button1=findViewById(R.id.opt1);
        button2=findViewById(R.id.opt2);
        button3=findViewById(R.id.opt3);
        button4=findViewById(R.id.opt4);
        String result="";
        try {
        result=task.execute("http://www.posh24.se/kandisar").get();
        if(result.equals("Something Went Wrong"))
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        else {
            String[] splitResult = result.split("<div class=\"listedArticles\">");
            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while (m.find()) {

                celebURLs.add(m.group(1));
            }
            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);

            while (m.find()) {

                celebNames.add(m.group(1));
            }
        newQuestion();
        }


        }
        catch(Exception e)
        {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        }
        }

