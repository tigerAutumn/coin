import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
public class OkHttpTest {

    public String[] accessKeyList = {"f5cb72cd2809303436866ffd284f9f9d","bf18da33f4011c6dc52c839a4688bd3b","029ca634fd5aa600f6393d6fc423fc48","029ca634fd5aa600f6393d6fc423fc48"};


    @Test
    public void test() {

        //创建OkHttpClient实例对象
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        //创建Request对象
        for (String string : accessKeyList) {
            Request request = new Request.Builder()
                    .url("http://data.fixer.io/api/latest?access_key="+string)
                    .get()
                    .build();

            //执行Request请求
            //异步请求
            /*okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //请求失败
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //请求成功
                    Log.d("TestOkHttp",response.body().string());
                }
            });*/
            //同步请求
            Response response;
            try {
                response = okHttpClient.newCall(request).execute();
                JSONObject jsonObject = JSONObject.parseObject(response.body().string());
                if (!jsonObject.containsKey("rates")) {
                    continue;
                }
                JSONObject rates = jsonObject.getJSONObject("rates");
                BigDecimal bigDecimal = rates.getBigDecimal("CNY");
                BigDecimal bigDecimal2 = rates.getBigDecimal("USD");
                BigDecimal bg = bigDecimal.divide(bigDecimal2, bigDecimal.ROUND_HALF_UP);

                break;
            } catch (IOException e) {
               // logger.info("getCNYValue exeception :{}", e);
               continue;
            }
        }
    }



}
