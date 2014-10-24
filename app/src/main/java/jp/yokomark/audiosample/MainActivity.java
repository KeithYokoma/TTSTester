package jp.yokomark.audiosample;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemSelected;

public class MainActivity extends Activity implements AudioManager.OnAudioFocusChangeListener {
	private static final HashMap<String, String> MAP = new HashMap<String, String>();
	private static final List<Stream> STREAMS = new ArrayList<Stream>();
	private static final List<Mode> MODES = new ArrayList<Mode>();
	@InjectView(R.id.streams) Spinner streamSpinner;
	@InjectView(R.id.modes) Spinner modeSpinner;
	@InjectView(R.id.text) EditText input;
	private TextToSpeech tts;
	private AudioManager am;
	private boolean ready;
	private int stream;

	static {
		STREAMS.add(new Stream(AudioManager.STREAM_VOICE_CALL, "STREAM_VOICE_CALL"));
		STREAMS.add(new Stream(AudioManager.STREAM_SYSTEM, "STREAM_SYSTEM"));
		STREAMS.add(new Stream(AudioManager.STREAM_RING, "STREAM_RING"));
		STREAMS.add(new Stream(AudioManager.STREAM_MUSIC, "STREAM_MUSIC"));
		STREAMS.add(new Stream(AudioManager.STREAM_ALARM, "STREAM_ALARM"));
		STREAMS.add(new Stream(AudioManager.STREAM_NOTIFICATION, "STREAM_NOTIFICATION"));
		STREAMS.add(new Stream(AudioManager.STREAM_DTMF, "STREAM_DTMF"));

		MODES.add(new Mode(AudioManager.MODE_CURRENT, "MODE_CURRENT"));
		MODES.add(new Mode(AudioManager.MODE_INVALID, "MODE_INVALID"));
		MODES.add(new Mode(AudioManager.MODE_IN_CALL, "MODE_IN_CALL"));
		MODES.add(new Mode(AudioManager.MODE_IN_COMMUNICATION, "MODE_IN_COMMUNICATION"));
		MODES.add(new Mode(AudioManager.MODE_RINGTONE, "MODE_RINGTONE"));
		MODES.add(new Mode(AudioManager.MODE_NORMAL, "MODE_NORMAL"));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.inject(this);

		tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				ready = TextToSpeech.SUCCESS == status;
			}
		});
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		streamSpinner.setAdapter(prepareStreamAdapter());
		modeSpinner.setAdapter(prepareModeAdapter());
	}

	@Override
	protected void onDestroy() {
		tts.shutdown();
		am.abandonAudioFocus(MainActivity.this);
		super.onDestroy();
	}

	@Override
	public void onAudioFocusChange(int focusChange) {}

	@OnClick(R.id.button)
	public void onStartSpeech() {
		if (!ready) {
			return;
		}

		tts.speak(input.getText().toString(), TextToSpeech.QUEUE_ADD, MAP);
	}

	@OnCheckedChanged(R.id.check)
	public void onAudioFocusSettingChange(boolean isChecked) {
		if (isChecked) {
			am.requestAudioFocus(MainActivity.this, stream, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
		} else {
			am.abandonAudioFocus(MainActivity.this);
		}
	}

	@OnCheckedChanged(R.id.use_bluetooth_sco)
	public void onBluetoothScoSettingChange(boolean isChecked) {
		am.setBluetoothScoOn(isChecked);
	}

	@OnCheckedChanged(R.id.use_speakerphone)
	public void onSpeakerphoneSettingChange(boolean isChecked) {
		am.setSpeakerphoneOn(isChecked);
	}

	@OnItemSelected(R.id.streams)
	public void onStreamSelected(int pos) {
		Stream str = STREAMS.get(pos);
		stream = str.value;
		MAP.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(str.value));
	}

	@OnItemSelected(R.id.modes)
	public void onModeSelected(int pos) {
		Mode mode = MODES.get(pos);
		am.setMode(mode.value);
	}

	/* package */ SpinnerAdapter prepareStreamAdapter() {
		return new ArrayAdapter<Stream>(this, android.R.layout.simple_list_item_1, android.R.id.text1, STREAMS);
	}

	/* package */ SpinnerAdapter prepareModeAdapter() {
		return new ArrayAdapter<Mode>(this, android.R.layout.simple_list_item_1, android.R.id.text1, MODES);
	}

	/* package */ static class Stream {
		final int value;
		final String label;

		public Stream(int value, String label) {
			this.value = value;
			this.label = label;
		}

		@Override
		public String toString() {
			return label;
		}
	}

	/* package */ static class Mode {
		final int value;
		final String label;

		public Mode(int value, String label) {
			this.value = value;
			this.label = label;
		}

		@Override
		public String toString() {
			return label;
		}
	}
}
