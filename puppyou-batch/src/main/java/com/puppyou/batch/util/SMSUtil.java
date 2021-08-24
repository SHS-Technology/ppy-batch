package com.puppyou.batch.util;

import java.io.IOException;

import com.puppyou.batch.util.thirdparty.alimtalk.api.APIInit;
import com.puppyou.batch.util.thirdparty.alimtalk.model.request.Message;
import com.puppyou.batch.util.thirdparty.alimtalk.model.response.MessageModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SMSUtil {

	
	public static String sendSMS(String phone,String content) throws Exception {
//        groupId : G4V20210312165359FNGLF3KN7R7LRGY
//        messageId : M4V20210312165359O3XEACQ5JDGMTN2
        // 전송할 메시지는 변수를 치환하여 입력해 줍니다.  예) #{이름}님 가입을 환영합니다.  ---> 홍길동님 가입을 환영합니다.
        // 변수 이외의 내용은 100% 일치해야 하며, 단 줄내림은 마음껏 하실 수 있습니다. 예) #{이름}님 가입을 환영합니다. ----> 홍길동님\n\n가입을\n\n환영합니다.
//        Message message = new Message("01066090201", "0269527560", "김가람님의 인증번호는 131312 입니다.", kakaoOptions);
//		KakaoOptions kakaoOptions = new KakaoOptions("KA01PF210312024322631MIfuVPPGvvr", "KA01TP210416094107477Mj544ToepVD", false, null, null);
        Message message = new Message(phone , "0269527560", content );
        
        Call<MessageModel> api = APIInit.getAPI().sendMessage(APIInit.getHeaders(), message);
        api.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<MessageModel> call, Response<MessageModel> response) {
                // 성공 시 200이 출력됩니다.
                if (response.isSuccessful()) {
                    System.out.println("statusCode : " + response.code());
                    MessageModel body = response.body();
                    System.out.println("groupId : " + body.getGroupId());
                    System.out.println("messageId : " + body.getMessageId());
                    System.out.println("to : " + body.getTo());
                    System.out.println("from : " + body.getFrom());
                    System.out.println("type : " + body.getType());
                    System.out.println("statusCode : " + body.getStatusCode());
                    System.out.println("statusMessage : " + body.getStatusMessage());
                    System.out.println("customFields : " + body.getCustomFields());
                } else {
                    try {
                        System.out.println(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<MessageModel> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
        return "SUCCESS";
	}
}
