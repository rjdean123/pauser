package com.deanslab.pauserfinal;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

import java.io.File;
import java.io.IOException;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

public class ListenerService extends Service implements RecognitionListener {
	
	private String NAME;
	private SpeechRecognizer recognizer;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		NAME = "";
		try {
			NAME = intent.getStringExtra("NAME");
		} catch (Exception e) {
			//text.setText("ERROR: " + e.getMessage());
		}
		
		//initializing recognizer in background
        new AsyncTask<Void, Void, Exception>() {
        	ProgressDialog dialog;
        	
        	@Override
        	protected void onPreExecute() {
        		dialog = new ProgressDialog(getApplicationContext());
        		dialog.setMessage("Initializing ...");
        	}
        	
        	@Override
        	protected Exception doInBackground(Void... params) {
        		try {
        			Assets assets = new Assets(ListenerService.this);
        			File assetDir = assets.syncAssets();
        			setupRecognizer(assetDir);
        		} catch (IOException e) {
        			return e;
        		}
        		return null;
        	}
        	
        	@Override
        	protected void onPostExecute(Exception result) {
        		if (result != null) {
        			//((TextView) findViewById(R.id.textField)).setText("Initialization Failed: " + result);
        		} else {
        			//((TextView) findViewById(R.id.textField)).setText("Listening ...");
        			switchSearch("namesearch");
        		}
        		dialog.dismiss();
        	}
        }.execute();
    }
	
	@Override
	public void onDestroy() {
		try {
			recognizer.stop();
		} catch (Exception e) {
			
		}
		super.onDestroy();
	}

	@Override
	public void onBeginningOfSpeech() {
		
	}

	@Override
	public void onEndOfSpeech() {
		
	}

	@Override
	public void onPartialResult(Hypothesis hypothesis) {
		String text = hypothesis.getHypstr();
    	if (text.equalsIgnoreCase(NAME)) {
    		pauseCalled();
    	}		
	}

	@Override
	public void onResult(Hypothesis hypothesis) {
    	if (hypothesis != null) {
    		String text = hypothesis.getHypstr();
    		if (text.equalsIgnoreCase(NAME)) {
    			pauseCalled();
    		}
    	}		
	}
	
    private void switchSearch(String searchName) {
    	try {
    		recognizer.stop();
    		recognizer.startListening(searchName);
    	} catch (Exception e) {
    		Log.e("ERROR", e.getMessage());
    	}
    }
    
    private void setupRecognizer(File assetsDir) {
    	File modelsDir = new File (assetsDir, "models");
        recognizer = defaultSetup()
                .setAcousticModel(new File(modelsDir, "hmm/en-us-semi"))
                .setDictionary(new File(modelsDir, "dict/cmu07a.dic"))
                .setRawLogDir(assetsDir).setKeywordThreshold(1e-7f)
                .getRecognizer();
        recognizer.addListener(this);
        
        recognizer.addKeyphraseSearch("namesearch", NAME);
    }
    
    private void pauseCalled() {
    	//((TextView) findViewById(R.id.textField)).setText("PAUSED");
    	AudioManager mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager.isMusicActive()) {
        	Intent i = new Intent("com.android.music.musicservicecommand");
   	    	i.putExtra("command", "pause");
   	    	ListenerService.this.sendBroadcast(i);
   	   	}
        switchSearch("namesearch");
    }

}
