package jp.yokomark.audiosample;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
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

public class MainActivity extends ActionBarActivity implements AudioManager.OnAudioFocusChangeListener {
	private static final HashMap<String, String> MAP = new HashMap<String, String>();

	private static final List<Mode> MODES = new ArrayList<Mode>();
	@InjectView(R.id.streams) Spinner streamSpinner;
	@InjectView(R.id.modes) Spinner modeSpinner;
	@InjectView(R.id.text) EditText input;
	@InjectView(R.id.volume_bar) SeekBar volumeBar;
	private TextToSpeech tts;
	private AudioManager am;
	private boolean ready;

	static {
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

		AudioStreams.Entry str = (AudioStreams.Entry) streamSpinner.getSelectedItem();
		volumeBar.setMax(am.getStreamMaxVolume(str.value));
		volumeBar.setProgress(am.getStreamVolume(str.value));
		volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				AudioStreams.Entry str = (AudioStreams.Entry) streamSpinner.getSelectedItem();
				am.setStreamVolume(str.value, progress, AudioManager.FLAG_PLAY_SOUND);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
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

	@OnClick(R.id.button)
	public void onStartSpeech() {
		if (!ready) {
			return;
		}

		tts.speak(input.getText().toString(), TextToSpeech.QUEUE_ADD, MAP);
	}

	@OnClick(R.id.show_media_routes)
	public void onShowMediaRoutes() {
		MediaRouteListFragment.show(this);
	}

	@OnCheckedChanged(R.id.check)
	public void onAudioFocusSettingChange(boolean isChecked) {
		AudioStreams.Entry str = (AudioStreams.Entry) streamSpinner.getSelectedItem();
		if (isChecked) {
			am.requestAudioFocus(MainActivity.this, str.value, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
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
		AudioStreams.Entry str = AudioStreams.getStreams().get(pos);
		volumeBar.setMax(am.getStreamMaxVolume(str.value));
		volumeBar.setProgress(am.getStreamVolume(str.value));
		MAP.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(str.value));
	}

	@OnItemSelected(R.id.modes)
	public void onModeSelected(int pos) {
		Mode mode = MODES.get(pos);
		am.setMode(mode.value);
	}

	/* package */ SpinnerAdapter prepareStreamAdapter() {
		return new ArrayAdapter<AudioStreams.Entry>(
				this, android.R.layout.simple_list_item_1, android.R.id.text1, AudioStreams.getStreams());
	}

	/* package */ SpinnerAdapter prepareModeAdapter() {
		return new ArrayAdapter<Mode>(this, android.R.layout.simple_list_item_1, android.R.id.text1, MODES);
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
