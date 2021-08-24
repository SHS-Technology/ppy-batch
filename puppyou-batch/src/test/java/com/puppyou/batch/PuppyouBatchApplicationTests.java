package com.puppyou.batch;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

@SpringBootTest
class PuppyouBatchApplicationTests {
    private FirebaseOptions option;
    private Firestore db; 
    private final static String PATH = "D:\\sts4\\ppy-batch\\puppyou-batch\\src\\test\\java\\com\\puppyou\\batch\\puppyou-6ed5b-firebase-adminsdk-pidt0-3097d699ea.json";

    public static void main( String[] args ) {
    	
    	PuppyouBatchApplicationTests app = new PuppyouBatchApplicationTests();
        try {
            app.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void init() throws Exception{
        FileInputStream refreshToken = new FileInputStream(PATH);
        option = new FirebaseOptions.Builder()
        		  .setCredentials(GoogleCredentials.fromStream(refreshToken))
        		  .setDatabaseUrl("https://puppyou-6ed5b-default-rtdb.firebaseio.com")
        		  .build();
        FirebaseApp.initializeApp(option);
        db = FirestoreClient.getFirestore();
        db.collection("purchings").addSnapshotListener( (target, exception)->{
            System.out.println(" - select start - ");
            target.forEach( item->{
                System.out.println("primary id : "+item.getId() + "  ||  value : " + item.getData());
            });
            System.out.println(" - select end - ");
        });
        WriteBatch batch = db.batch();
        Map<String, Object> update = new HashMap<String, Object>();
        update.put("chattingRoomId", "HELLO-WORLD9");
        DocumentReference nycRef = db.collection("purchings").document("matchingNo_40");
        batch.update(nycRef, update);
        DocumentReference nycRef2 = db.collection("purchings").document("matchingNo_41");
        batch.update(nycRef2, update);
        
        batch.commit();
    }
    
	@SuppressWarnings("deprecation")
	@Test
	void test() throws IOException, InterruptedException, ExecutionException {
		System.out.println(" - ################################### START TEST ############################################### - ");
		

		
		InputStream serviceAccount = new FileInputStream("D:\\\\sts4\\\\ppy-batch\\\\puppyou-batch\\\\src\\\\test\\\\java\\\\com\\\\puppyou\\\\batch\\\\puppyou-6ed5b-firebase-adminsdk-pidt0-3097d699ea.json");
		GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
		FirebaseOptions option = new FirebaseOptions.Builder()
		    .setCredentials(credentials)
		    .build();
		FirebaseApp firebaseApp = null;
	    List<FirebaseApp> firebaseApps = FirebaseApp.getApps();
	    if(firebaseApps!=null && !firebaseApps.isEmpty()){
	        for(FirebaseApp app : firebaseApps){
	            if(app.getName().equals(FirebaseApp.DEFAULT_APP_NAME))
	                firebaseApp = app;
	        }
	    }
	    else{
	      firebaseApp = FirebaseApp.initializeApp(option);
	    }

		Firestore db = FirestoreClient.getFirestore();
		
		DocumentReference  abc =	db.collection("purchings").document("matchingNo_4");
		ApiFuture<DocumentSnapshot> future = abc.get();
		DocumentSnapshot document = future.get();
		System.out.println("##########" + document.getData());

//		db.collection("purchings").addSnapshotListener( (target, exception)->{
//			System.out.println(" - select start - ");
//			target.forEach( item->{
//				System.out.println("primary id : "+item.getId() + "  ||  value : " + item.getData());
//			});
//			System.out.println(" - select end - ");
//		});
//		Map<String, Object> update = new HashMap<String, Object>();
//		update.put("chattingRoomId", "HELLO-WORLD9");
//		ApiFuture<WriteResult> initialResult2 =db.collection("purchings").document("matchingNo_40").update(update);
//		try {
//			initialResult2.get();
//		}catch (Exception e) {
//			System.out.println("catch");
//		}
//		
//		WriteBatch batch = db.batch();
//		DocumentReference nycRef = db.collection("purchings").document("matchingNo_40");
//		batch.update(nycRef, update);
//		DocumentReference nycRef2 = db.collection("purchings").document("matchingNo_4");
//		batch.update(nycRef2, update);
//		
//		batch.commit();
//		ApiFuture<List<WriteResult>> initialResult = batch.commit();
//		for(int i = 0; i < initialResult.get().size(); i ++) {
//			WriteResult result = initialResult.get().get(i);
//			System.out.println(initialResult.get().get(i).toString());
//		}
		
		// Confirm that data has been successfully saved by blocking on the operation

		System.out.println(" - ################################### START END ############################################### - ");
//	       String PATH = "D:\\sts4\\ppy-batch\\puppyou-batch\\src\\test\\java\\com\\puppyou\\batch\\puppyou-6ed5b-firebase-adminsdk-pidt0-3097d699ea.json";
//		FileInputStream refreshToken = new FileInputStream(PATH);
//        option = new FirebaseOptions.Builder()
//        		  .setCredentials(GoogleCredentials.fromStream(refreshToken))
//        		  .setDatabaseUrl("https://puppyou-6ed5b-default-rtdb.firebaseio.com")
//        		  .build();
//        FirebaseApp.initializeApp(option);
//        db = FirestoreClient.getFirestore();
	}

}
