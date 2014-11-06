package jp.yokomark.audiosample;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.media.MediaRouter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;

/**
 * @author KeishinYokomaku
 */
public class MediaRouteListFragment extends DialogFragment implements DialogInterface.OnClickListener {
	public static final String TAG = MediaRouteListFragment.class.getSimpleName();
	ArrayAdapter<MediaRouter.RouteInfo> adapter;
	MediaRouter mediaRouter;

	public MediaRouteListFragment() {}

	public static void show(FragmentActivity activity) {
		MediaRouteListFragment fragment = new MediaRouteListFragment();
		fragment.show(activity.getSupportFragmentManager(), TAG);
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mediaRouter = MediaRouter.getInstance(getActivity());
		adapter = new RoutesAdapter(getActivity(), mediaRouter.getRoutes());
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
				.setTitle(R.string.title_media_routes)
				.setAdapter(adapter, this);
		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		MediaRouter.RouteInfo info = adapter.getItem(which);
		mediaRouter.selectRoute(info);
	}

	/* package */ static class RoutesAdapter extends ArrayAdapter<MediaRouter.RouteInfo> {
		public RoutesAdapter(Context context, List<MediaRouter.RouteInfo> items) {
			super(context, R.layout.list_item_media_routes, R.id.route_id, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
			MediaRouter.RouteInfo info = getItem(position);
			TextView name = ButterKnife.findById(view, R.id.route_name);
			TextView desc = ButterKnife.findById(view ,R.id.route_desc);
			TextView id = ButterKnife.findById(view, R.id.route_id);
			TextView stream = ButterKnife.findById(view, R.id.route_stream);
			name.setText(position + ": " +info.getName());
			stream.setText("Stream: " + AudioStreams.findByStreamId(info.getPlaybackStream()).label);
			desc.setText("Description: "+ info.getDescription());
			id.setText("ID: " + info.getId());
			return view;
		}
	}
}
