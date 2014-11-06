package jp.yokomark.audiosample;

import android.media.AudioManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author KeishinYokomaku
 */
public class AudioStreams {
	public static final String TAG = AudioStreams.class.getSimpleName();
	private static final List<Entry> STREAMS = new ArrayList<Entry>();

	static {
		STREAMS.add(new Entry(AudioManager.STREAM_VOICE_CALL, "STREAM_VOICE_CALL"));
		STREAMS.add(new Entry(AudioManager.STREAM_SYSTEM, "STREAM_SYSTEM"));
		STREAMS.add(new Entry(AudioManager.STREAM_RING, "STREAM_RING"));
		STREAMS.add(new Entry(AudioManager.STREAM_MUSIC, "STREAM_MUSIC"));
		STREAMS.add(new Entry(AudioManager.STREAM_ALARM, "STREAM_ALARM"));
		STREAMS.add(new Entry(AudioManager.STREAM_NOTIFICATION, "STREAM_NOTIFICATION"));
		STREAMS.add(new Entry(6, "STREAM_BLUETOOTH_SCO")); // hidden by default, see AudioSystem
		STREAMS.add(new Entry(7, "STREAM_SYSTEM_ENFORCED")); // hidden by default, see AudioSystem
		STREAMS.add(new Entry(AudioManager.STREAM_DTMF, "STREAM_DTMF"));
		STREAMS.add(new Entry(9, "STREAM_TTS")); // hidden by default, see AudioSystem
	}

	public static List<Entry> getStreams() {
		return Collections.unmodifiableList(STREAMS);
	}

	public static Entry findByStreamId(int stream) {
		for (Entry entry : STREAMS) {
			if (entry.value == stream) {
				return entry;
			}
		}
		throw new IllegalArgumentException("no such stream");
	}

	/* package */ static class Entry {
		final int value;
		final String label;

		public Entry(int value, String label) {
			this.value = value;
			this.label = label;
		}

		@Override
		public String toString() {
			return label;
		}
	}
}
