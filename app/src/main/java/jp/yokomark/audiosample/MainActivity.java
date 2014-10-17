package jp.yokomark.audiosample;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity implements AudioManager.OnAudioFocusChangeListener {
	private final static HashMap<String, String> MAP = new HashMap<String, String>();
	TextToSpeech tts;
	AudioManager am;
	boolean ready;
	int stream;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				ready = TextToSpeech.SUCCESS == status;
			}
		});
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!ready) {
					return;
				}

				tts.speak("Speeeeeeeeeeeech!!", TextToSpeech.QUEUE_ADD, MAP);
			}
		});
		((CheckBox) findViewById(R.id.check)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					am.requestAudioFocus(MainActivity.this, stream, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
				} else {
					am.abandonAudioFocus(MainActivity.this);
				}
			}
		});
		((CheckBox) findViewById(R.id.use_bluetooth_sco)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				am.setBluetoothScoOn(isChecked);
			}
		});
		((CheckBox) findViewById(R.id.use_speakerphone)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				am.setSpeakerphoneOn(isChecked);
			}
		});
		((Spinner) findViewById(R.id.streams)).setAdapter(new ArrayAdapter<Stream>(
				this, android.R.layout.simple_list_item_1, android.R.id.text1, getStreams()));
		((Spinner) findViewById(R.id.streams)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Stream str = (Stream) parent.getItemAtPosition(position);
				stream = str.stream;
				MAP.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(str.stream));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
	}

	@Override
	protected void onDestroy() {
		tts.shutdown();
		am.abandonAudioFocus(MainActivity.this);
		super.onDestroy();
	}

	@Override
	public void onAudioFocusChange(int focusChange) {}

	private List<Stream> getStreams() {
		List<Stream> streams = new ArrayList<Stream>();
		streams.add(new Stream(AudioManager.STREAM_VOICE_CALL, "STREAM_VOICE_CALL"));
		streams.add(new Stream(AudioManager.STREAM_SYSTEM, "STREAM_SYSTEM"));
		streams.add(new Stream(AudioManager.STREAM_RING, "STREAM_RING"));
		streams.add(new Stream(AudioManager.STREAM_MUSIC, "STREAM_MUSIC"));
		streams.add(new Stream(AudioManager.STREAM_ALARM, "STREAM_ALARM"));
		streams.add(new Stream(AudioManager.STREAM_NOTIFICATION, "STREAM_NOTIFICATION"));
		streams.add(new Stream(AudioManager.STREAM_DTMF, "STREAM_DTMF"));
		return streams;
	}

	static class Stream {
		final int stream;
		final String label;

		public Stream(int stream, String label) {
			this.stream = stream;
			this.label = label;
		}

		@Override
		public String toString() {
			return label;
		}
	}
}
