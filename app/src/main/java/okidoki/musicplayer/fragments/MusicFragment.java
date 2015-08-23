package okidoki.musicplayer.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import org.w3c.dom.Text;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okidoki.musicplayer.MainActivity;
import okidoki.musicplayer.R;
import okidoki.musicplayer.classes.Music;
import okidoki.musicplayer.classes.MusicList;
import okidoki.musicplayer.fragments.dummy.DummyContent;
/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class MusicFragment extends Fragment implements AbsListView.OnItemClickListener  {

    private OnFragmentInteractionListener mListener;
    public static MusicList musicList;
    private musicListViewAdapter musicListAdapter;
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;

    // TODO: Rename and change types of parameters
    public static MusicFragment newInstance() {
        MusicFragment fragment = new MusicFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static MusicFragment newInstance(String param1, int i) {
        MusicFragment fragment = new MusicFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_SECTION_NUMBER, i);
        fragment.setArguments(args);
        return fragment;
    }
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MusicFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musicList = null;
        mParam1 = getArguments().getString(ARG_PARAM1);
        if(mParam1 == "GLOBAL" )
            musicList = MainActivity.globalMusicList;
        if(mParam1 == "ALBUM" ){
            musicList = MainActivity.localalbum.getMusicList();
            musicList.setName(MainActivity.localalbum.getArtist());
        }
        if(mParam1 == "LIST" ) {
            musicList = MainActivity.playmusiclist;
        }

        musicListAdapter = new musicListViewAdapter(MainActivity.mainActivity, musicList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(
                R.layout.fragment_music_list, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.frag_music_list_list);
        TextView playlistTitle = (TextView) rootView.findViewById(R.id.musicListName);

        playlistTitle.setText(musicList.getName());
        TextView playlistDuration = (TextView) rootView.findViewById(R.id.totalTimeMusicList);
        long duration = 0;
        for(int i = 0; i < musicList.musicListSize();i++)
            duration+= musicList.getMusicFromList(i).getDuration();

        playlistDuration.setText(String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(duration),
                TimeUnit.MILLISECONDS.toMinutes(duration) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))));

        listView.setFastScrollEnabled(true);
        listView.setAdapter((ListAdapter) musicListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                musicList = null;
                if(mParam1 == "GLOBAL" )
                    musicList = MainActivity.globalMusicList;
                if(mParam1 == "ALBUM" ){
                    musicList = MainActivity.localalbum.getMusicList();
                    musicList.setName(MainActivity.localalbum.getArtist());
                }
                if(mParam1 == "LIST" ) {
                    musicList = MainActivity.playmusiclist;
                    //Toast.makeText(MainActivity.mainActivity,MainActivity.playmusiclist.getMusicFromList(2).getTitle(),Toast.LENGTH_SHORT).show();
                }

                PlayerFragment.musicPlayer.playSong(position,true,true);
                PlayerFragment.playImageButton.setImageResource(R.drawable.ic_pause);
                miniPlayerFragment.updatePlayImage();
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*((MainActivity) getActivity()).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        MainActivity.localalbum = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        //mListener.onFragmentInteraction(listOfMusic.);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MusicFragment newInstance(int sectionNumber) {
        MusicFragment fragment = new MusicFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
}

class musicListViewAdapter extends BaseAdapter {
    Context context;
    MusicList adapterMusicList;
    public musicListViewAdapter(Context context, MusicList musicList){
        this.context = context;
        adapterMusicList = musicList;
    }
    @Override
    public int getCount() {
        if(adapterMusicList != null)
            return adapterMusicList.getMusicList().size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return adapterMusicList.getMusicFromList(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.song_item,parent,false);
        }
        else{
            row = convertView;
        }
        TextView titleTextView = (TextView) row.findViewById(R.id.songTitleSongList);
        TextView artistTextView = (TextView) row.findViewById(R.id.songArtistSongList);
        ImageView imageView = (ImageView) row.findViewById(R.id.albumArtSongList);

        titleTextView.setText(adapterMusicList.getMusicFromList(position).getTitle());
        artistTextView.setText(adapterMusicList.getMusicFromList(position).getArtist().getName());
        imageView.setImageBitmap(adapterMusicList.getMusicFromList(position).getAlbum().getBitmap());

        return row;
    }
}