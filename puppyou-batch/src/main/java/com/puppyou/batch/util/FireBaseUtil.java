package com.puppyou.batch.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

public class FireBaseUtil {
	
	public static List<FirebaseApp> firebaseApps ;
	
	@SuppressWarnings("deprecation")
	public static String fireStoreByPurchings() throws IOException {
		System.out.println(" - ################################### START TEST ############################################### - ");
		
		InputStream serviceAccount = new FileInputStream("D:\\\\sts4\\\\ppy-batch\\\\puppyou-batch\\\\src\\\\test\\\\java\\\\com\\\\puppyou\\\\batch\\\\puppyou-6ed5b-firebase-adminsdk-pidt0-3097d699ea.json");
		GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
		FirebaseOptions option = new FirebaseOptions.Builder()
		    .setCredentials(credentials)
		    .build();
		FirebaseApp.initializeApp(option);

		Firestore db = FirestoreClient.getFirestore();

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
		System.out.println(" - ################################### START END ############################################### - ");
//	       String PATH = "D:\\sts4\\ppy-batch\\puppyou-batch\\src\\test\\java\\com\\puppyou\\batch\\puppyou-6ed5b-firebase-adminsdk-pidt0-3097d699ea.json";
//		FileInputStream refreshToken = new FileInputStream(PATH);
//        option = new FirebaseOptions.Builder()
//        		  .setCredentials(GoogleCredentials.fromStream(refreshToken))
//        		  .setDatabaseUrl("https://puppyou-6ed5b-default-rtdb.firebaseio.com")
//        		  .build();
//        FirebaseApp.initializeApp(option);
//        db = FirestoreClient.getFirestore();
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public static Firestore connectionFireStore(String fileName) throws IOException {
		ClassPathResource classPathResource = new ClassPathResource("json/" + fileName);
		System.out.println("##path: " + classPathResource.getPath());
//		String path = FireBaseUtil.class.getResource("").getPath();
//		System.out.println(path);
//		InputStream serviceAccount = new FileInputStream(classPathResource.getPath());
		GoogleCredentials credentials = GoogleCredentials.fromStream(classPathResource.getInputStream());
		FirebaseOptions option = new FirebaseOptions.Builder()
		    .setCredentials(credentials)
		    .build();
//		FirebaseApp.initializeApp(option);

		FirebaseApp firebaseApp = null;
	    firebaseApps = FirebaseApp.getApps();
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
		return db;
	}

}
