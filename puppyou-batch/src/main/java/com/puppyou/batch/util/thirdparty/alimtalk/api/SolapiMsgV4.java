package com.puppyou.batch.util.thirdparty.alimtalk.api;

import com.puppyou.batch.util.thirdparty.alimtalk.model.request.Message;
import com.puppyou.batch.util.thirdparty.alimtalk.model.response.MessageModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

// 문서 : https://docs.solapi.com/rest-api-reference/message-api-v4
// 일부 API는 Query Parameter를 추가로 사용할 수 있습니다.
public interface SolapiMsgV4 {
    // 심플 메시지
    @POST("messages/v4/send")
    Call<MessageModel> sendMessage(@Header("Authorization") String auth,
                                   @Body Message message);
}
