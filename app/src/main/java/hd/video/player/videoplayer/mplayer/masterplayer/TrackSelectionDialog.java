package hd.video.player.videoplayer.mplayer.masterplayer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.trackselection.TrackSelectionOverride;
import com.google.android.exoplayer2.trackselection.TrackSelectionParameters;
import com.google.android.exoplayer2.ui.TrackSelectionView;
import com.google.android.material.tabs.TabLayout;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class TrackSelectionDialog extends DialogFragment {
    public static final ImmutableList<Integer> TRACK_TYPE_LIST = ImmutableList.of(2, 1, 3);
    private DialogInterface.OnClickListener positiveClickListener;
    private DialogInterface.OnDismissListener dismissListener;
    public final SparseArray<TrackSelectionViewFragment> trackSelectionFragments = new SparseArray<>();
    public final ArrayList<Integer> trackTypeOrder = new ArrayList<>();
    private int dialogTitleResourceId;

    public interface TrackSelectionListener {
        void onTrackSelectionConfirmed(TrackSelectionParameters selectionParameters);
    }

    public static boolean hasValidTracks(Player player) {
        return hasValidTracks(player.getCurrentTracks());
    }

    public static boolean hasValidTracks(Tracks currentTracks) {
        UnmodifiableIterator<Tracks.Group> trackGroupsIterator = currentTracks.getGroups().iterator();
        while (trackGroupsIterator.hasNext()) {
            if (TRACK_TYPE_LIST.contains(trackGroupsIterator.next().getType())) {
                return true;
            }
        }
        return false;
    }

    public static TrackSelectionDialog createForPlayer(Player player, DialogInterface.OnDismissListener onDismissCallback) {
        int titleResId = R.string.track_selection_title;
        Tracks currentTracks = player.getCurrentTracks();
        TrackSelectionParameters selectionParameters = player.getTrackSelectionParameters();
        return createForTrackData(
                titleResId,
                currentTracks,
                selectionParameters,
                true,
                false,
                new TrackSelectionListener() {
                    @Override
                    public void onTrackSelectionConfirmed(TrackSelectionParameters selectionParameters) {
                        player.setTrackSelectionParameters(selectionParameters);
                    }
                },
                onDismissCallback
        );
    }

    public static TrackSelectionDialog createForTrackData(
            int titleResId,
            Tracks currentTracks,
            TrackSelectionParameters selectionParameters,
            boolean showAdaptiveOptions,
            boolean allowMultipleOverrides,
            TrackSelectionListener trackSelectionListener,
            DialogInterface.OnDismissListener onDismissCallback) {

        TrackSelectionDialog dialog = new TrackSelectionDialog();
        dialog.initialize(
                currentTracks,
                selectionParameters,
                titleResId,
                showAdaptiveOptions,
                allowMultipleOverrides,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int whichButton) {
                        TrackSelectionParameters.Builder paramsBuilder = selectionParameters.buildUpon();
                        for (int trackType : TRACK_TYPE_LIST) {
                            paramsBuilder.setTrackTypeDisabled(trackType, dialog.isTrackTypeDisabled(trackType));
                            paramsBuilder.clearOverridesOfType(trackType);
                            for (TrackSelectionOverride override : dialog.getTrackOverrides(trackType).values()) {
                                paramsBuilder.addOverride(override);
                            }
                        }
                        trackSelectionListener.onTrackSelectionConfirmed(paramsBuilder.build());
                    }
                },
                onDismissCallback
        );
        return dialog;
    }

    public TrackSelectionDialog() {
        setRetainInstance(true);
    }

    private void initialize(
            Tracks currentTracks,
            TrackSelectionParameters selectionParameters,
            int titleResId,
            boolean showAdaptiveOptions,
            boolean allowMultipleOverrides,
            DialogInterface.OnClickListener positiveClickCallback,
            DialogInterface.OnDismissListener onDismissCallback) {

        this.dialogTitleResourceId = titleResId;
        this.positiveClickListener = positiveClickCallback;
        this.dismissListener = onDismissCallback;

        for (int trackType : TRACK_TYPE_LIST) {
            List<Tracks.Group> filteredTrackGroups = new ArrayList<>();
            for (Tracks.Group group : currentTracks.getGroups()) {
                if (group.getType() == trackType) {
                    filteredTrackGroups.add(group);
                }
            }

            if (!filteredTrackGroups.isEmpty()) {
                TrackSelectionViewFragment fragment = new TrackSelectionViewFragment();
                fragment.init(
                        filteredTrackGroups,
                        selectionParameters.disabledTrackTypes.contains(trackType),
                        selectionParameters.overrides,
                        showAdaptiveOptions,
                        allowMultipleOverrides
                );
                this.trackSelectionFragments.put(trackType, fragment);
                this.trackTypeOrder.add(trackType);
            }
        }
    }

    public boolean isTrackTypeDisabled(int trackType) {
        TrackSelectionViewFragment fragment = this.trackSelectionFragments.get(trackType);
        return fragment != null && fragment.isDisabled;
    }

    public Map<TrackGroup, TrackSelectionOverride> getTrackOverrides(int trackType) {
        TrackSelectionViewFragment fragment = this.trackSelectionFragments.get(trackType);
        return fragment == null ? Collections.emptyMap() : fragment.overrides;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AppCompatDialog dialog = new AppCompatDialog(getActivity(), R.style.TrackSelectionDialogThemeOverlay);
        dialog.setTitle(this.dialogTitleResourceId);
        return dialog;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        this.dismissListener.onDismiss(dialogInterface);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.track_selection_dialog, container, false);
        TabLayout tabLayout = rootView.findViewById(R.id.track_selection_dialog_tab_layout);
        ViewPager viewPager = rootView.findViewById(R.id.track_selection_dialog_view_pager);
        TextView cancelButton = rootView.findViewById(R.id.track_selection_dialog_cancel_button);
        AppCompatButton confirmButton = rootView.findViewById(R.id.track_selection_dialog_ok_button);

        viewPager.setAdapter(new FragmentAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setVisibility(this.trackSelectionFragments.size() <= 1 ? View.GONE : View.VISIBLE);

        cancelButton.setOnClickListener(view -> dismiss());
        confirmButton.setOnClickListener(view -> {
            positiveClickListener.onClick(getDialog(), DialogInterface.BUTTON_POSITIVE);
            dismiss();
        });

        return rootView;
    }

    public static String getTrackTypeDisplayName(Resources resources, int trackType) {
        switch (trackType) {
            case 1:
                return resources.getString(R.string.exo_track_selection_title_audio);
            case 2:
                return resources.getString(R.string.exo_track_selection_title_video);
            case 3:
                return resources.getString(R.string.exo_track_selection_title_text);
            default:
                throw new IllegalArgumentException("Unsupported track type");
        }
    }

    private final class FragmentAdapter extends FragmentPagerAdapter {
        public FragmentAdapter(FragmentManager fragmentManager) {
            super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        public Fragment getItem(int position) {
            return TrackSelectionDialog.this.trackSelectionFragments.get(TrackSelectionDialog.this.trackTypeOrder.get(position));
        }

        public int getCount() {
            return TrackSelectionDialog.this.trackTypeOrder.size();
        }

        public CharSequence getPageTitle(int position) {
            return TrackSelectionDialog.getTrackTypeDisplayName(
                    TrackSelectionDialog.this.getResources(),
                    TrackSelectionDialog.this.trackTypeOrder.get(position)
            );
        }
    }

    public static final class TrackSelectionViewFragment extends Fragment implements TrackSelectionView.TrackSelectionListener {
        private boolean allowAdaptiveSelections;
        private boolean allowMultipleOverrides;
        boolean isDisabled;
        Map<TrackGroup, TrackSelectionOverride> overrides;
        private List<Tracks.Group> trackGroups;

        public TrackSelectionViewFragment() {
            // Retain instance across configuration changes
            setRetainInstance(true);
        }

        /**
         * Initializes the fragment with the necessary data.
         *
         * @param list                    The list of track groups to display.
         * @param isDisabled              Whether the track type is disabled.
         * @param overrides               Existing track selection overrides.
         * @param allowAdaptiveSelections Whether adaptive selections are allowed.
         * @param allowMultipleOverrides  Whether multiple overrides are allowed.
         */
        public void init(List<Tracks.Group> list, boolean isDisabled, Map<TrackGroup, TrackSelectionOverride> overrides, boolean allowAdaptiveSelections, boolean allowMultipleOverrides) {
            this.trackGroups = list;
            this.isDisabled = isDisabled;
            this.allowAdaptiveSelections = allowAdaptiveSelections;
            this.allowMultipleOverrides = allowMultipleOverrides;
            // Filter overrides to match the track groups
            this.overrides = new HashMap<>(TrackSelectionView.filterOverrides(overrides, list, allowMultipleOverrides));
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Inflate the layout for the fragment
            View view = inflater.inflate(R.layout.exo_track_selection_dialog, container, false);

            // Find and initialize the TrackSelectionView
            TrackSelectionView trackSelectionView = view.findViewById(R.id.exo_track_selection_view);
            trackSelectionView.setShowDisableOption(true); // Show the "Disable" option
            trackSelectionView.setAllowMultipleOverrides(this.allowMultipleOverrides);
            trackSelectionView.setAllowAdaptiveSelections(this.allowAdaptiveSelections);

            // Initialize the track selection view with data
            trackSelectionView.init(this.trackGroups, this.isDisabled, this.overrides, null, this);
            return view;
        }

        /**
         * Called when track selection changes.
         *
         * @param isDisabled Whether the track type is disabled.
         * @param overrides  Updated track selection overrides.
         */
        @Override
        public void onTrackSelectionChanged(boolean isDisabled, Map<TrackGroup, TrackSelectionOverride> overrides) {
            this.isDisabled = isDisabled;
            this.overrides = overrides;
        }
    }
}
