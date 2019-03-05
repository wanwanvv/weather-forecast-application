package mg.studio.weatherappdesign;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new DownloadUpdate().execute();
    }

    public void btnClick(View view) {
        new DownloadUpdate().execute();
        Toast.makeText(getApplicationContext(),"Weather has updated",Toast.LENGTH_LONG).show();

    }


    private class DownloadUpdate extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = "http://v.juhe.cn/weather/index?cityname=%E9%87%8D%E5%BA%86&dtype=json&format=2&key=bd318ae49ffa63d1b02977e8862d82e0";
            HttpURLConnection urlConnection = null;
            BufferedReader reader;

            try {
                URL url = new URL(stringUrl);
                //创建URL实例
                // Create the request to get the information from the server, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Mainly needed for debugging
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                //The temperature
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String temperature) {
            //Update the temperature displayed
            //date=new ArrayList<Weather>();
            String todayTemperature=new String(" ");
            String todayDate=new String (" ");
            String todayWeek=new String(" ");
            String location=new String(" ");
            String futureWeek[]={"星期一","星期二","星期三","星期四","星期五"};
            String futureWeather[]={"小雨","小雨","小雨","小雨","小雨"};
            String weeks[]={"mon","tue","wed","thur","fri","sat","sun"};
            String futureTemperature[]={"10℃","10℃","10℃","10℃","10℃"};
            int weekChoice[]={1,2,3,4,5};
            ImageView imageView[]=new ImageView[5];
            imageView[0]=findViewById(R.id.img_weather_condition);
            imageView[1]=findViewById(R.id.image1);
            imageView[2]=findViewById(R.id.image2);
            imageView[3]=findViewById(R.id.image3);
            imageView[4]=findViewById(R.id.image4);
            int weatherCases[]={1,2,3,4,5};
            if(temperature!=null){
                JsonParser parser=new JsonParser();//json解析器
                JsonObject obj=(JsonObject) parser.parse(temperature);
                /*获取返回状态码*/
                String resultcode=obj.get("resultcode").getAsString();
                /*如果状态码是200说明返回数据成功*/
                if(resultcode!=null&&resultcode.equals("200")){
                    JsonObject resultObj=obj.get("result").getAsJsonObject();
                    JsonObject todayWeatherArray=resultObj.get("sk").getAsJsonObject();
                    JsonObject todayLists=resultObj.get("today").getAsJsonObject();
                    JsonArray futureWeatherArray=resultObj.get("future").getAsJsonArray();
                    JsonObject todayEdate=futureWeatherArray.get(0).getAsJsonObject();
                    todayDate=todayEdate.get("date").getAsString();
                    todayTemperature=new String(todayWeatherArray.get("temp").getAsString());
                    location=todayLists.get("city").getAsString();
                    for(int i=0;i<=4;i++)
                    {
                        JsonObject weatherObject=futureWeatherArray.get(i).getAsJsonObject();
                        futureWeek[i]=weatherObject.get("week").getAsString();
                        futureWeather[i]=weatherObject.get("weather").getAsString();
                        futureTemperature[i]=weatherObject.get("temperature").getAsString();

                        if(futureWeather[i].contains("雨"))
                        {
                            weatherCases[i]=1;
                        }
                        else
                            if(futureWeather[i].contains("阴"))
                            {
                                weatherCases[i]=2;
                            }
                            else if(futureWeather[i].contains("多云"))
                            {
                                weatherCases[i]=3;
                            }
                            else
                            {
                                weatherCases[i]=4;
                            }

                    }
                }
            }
            for(int i=0;i<=4;i++)
            {
                switch (futureWeek[i])
                {
                    case "星期一":
                        weekChoice[i]=0;
                        break;
                    case "星期二":
                        weekChoice[i]=1;
                        break;
                    case "星期三":
                        weekChoice[i]=2;
                        break;
                    case "星期四":
                        weekChoice[i]=3;
                        break;
                    case "星期五":
                        weekChoice[i]=4;
                        break;
                    case "星期六":
                        weekChoice[i]=5;
                        break;
                    case "星期日":
                        weekChoice[i]=6;
                        break;

                }
            }
            switch (futureWeek[0])
            {

                case "星期一":
                    todayWeek="MONDAY";
                    break;
                case "星期二":
                    todayWeek="TUESDAY";
                    break;
                case "星期三":
                    todayWeek="WEDNESDAY";
                    break;
                case "星期四":
                    todayWeek="THURSDAY";
                    break;
                case "星期五":
                    todayWeek="FRIDAY";
                    break;
                case "星期六":
                    todayWeek="SATURDAY";
                    break;
                case "星期日":
                    todayWeek="SUNDAY";
                    break;

            }
            ((TextView) findViewById(R.id.title)).setText(todayWeek);
            ((TextView) findViewById(R.id.temperature_of_the_day)).setText(todayTemperature);
            ((TextView) findViewById(R.id.tv_date)).setText(todayDate);
            //((TextView) findViewById(R.id.tv_location)).setText(location);
            ((TextView) findViewById(R.id.week1)).setText(weeks[weekChoice[1]]);
            ((TextView) findViewById(R.id.week2)).setText(weeks[weekChoice[2]]);
            ((TextView) findViewById(R.id.week3)).setText(weeks[weekChoice[3]]);
            ((TextView) findViewById(R.id.week4)).setText(weeks[weekChoice[4]]);

            ((TextView) findViewById(R.id.temperature1)).setText(futureTemperature[1]);
            ((TextView) findViewById(R.id.temperature2)).setText(futureTemperature[2]);
            ((TextView) findViewById(R.id.temperature3)).setText(futureTemperature[3]);
            ((TextView) findViewById(R.id.temperature4)).setText(futureTemperature[4]);

            for(int i=0;i<=4;i++)
            {
                switch (weatherCases[i])
                {
                    case 1:
                        imageView[i].setImageDrawable(getResources().getDrawable(R.drawable.rainy_small));
                        break;
                    case 2:
                        imageView[i].setImageDrawable(getResources().getDrawable(R.drawable.windy_small));
                        break;
                    case 3:
                        imageView[i].setImageDrawable(getResources().getDrawable(R.drawable.partly_sunny_small));
                        break;
                    case 4:
                        imageView[i].setImageDrawable(getResources().getDrawable(R.drawable.sunny_small));
                        break;


                }
            }
        }
    }
}
