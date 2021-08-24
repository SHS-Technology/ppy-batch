package com.puppyou.batch.util.thirdparty.alimtalk.api;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class APIInit {
    private static Retrofit retrofit;
    private static SolapiMsgV4 messageService;

    public static String getHeaders(){
        try {
            String apiKey = "NCSVKYMBAIXHDSVC";
            String apiSecret = "DOBKQ0ZGNAVJPTRNQRBPNE8FNBQV2YVS";
            String salt = UUID.randomUUID().toString().replaceAll("-", "");
//            String date = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toString().split("\\[")[0];
            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            String signature = new String(Hex.encodeHex(sha256_HMAC.doFinal((date + salt).getBytes(StandardCharsets.UTF_8))));
            return "HMAC-SHA256 ApiKey=" + apiKey + ", Date=" + date + ", salt=" + salt + ", signature=" + signature;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SolapiMsgV4 getAPI() {
        if (messageService == null) {
            setRetrofit();
            messageService = retrofit.create(SolapiMsgV4.class);
        }
        return messageService;
    }

//    public static SolapiImgApi getImageAPI() {
//        if (imageService == null) {
//            setRetrofit();
//            imageService = retrofit.create(SolapiImgApi.class);
//        }
//        return imageService;
//    }

    public static void setRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        Request 시 로그가 필요하면 추가하세요.
//        interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        String domain = "api.solapi.com";
        String protocol = "https";
        String prefix = "/";

//        try {
//            Ini ini = new Ini(new File("config.ini"));
//            if (ini.get("SERVER", "domain") != null) domain = ini.get("SERVER", "domain");
//            if (ini.get("SERVER", "protocol") != null) protocol = ini.get("SERVER", "protocol");
//            if (ini.get("SERVER", "prefix") != null) prefix = ini.get("SERVER", "prefix");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(protocol + "://" + domain + prefix)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }
}
