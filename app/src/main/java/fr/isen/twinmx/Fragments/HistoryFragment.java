package fr.isen.twinmx.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.isen.twinmx.activities.MainActivity;
import fr.isen.twinmx.R;
import fr.isen.twinmx.activities.MotoFormActivity;
import fr.isen.twinmx.database.MotoRepository;
import fr.isen.twinmx.database.listeners.MotoListener;
import fr.isen.twinmx.TMApplication;
import fr.isen.twinmx.database.model.Moto;
import fr.isen.twinmx.ui.adapters.MotosAdapter;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


/**
 * Created by pierredfc.
 */

public class HistoryFragment extends Fragment implements MotoListener.OnCreateMotoCallback, View.OnClickListener {

    private View rootview;

    @BindView(R.id.history_recycler)
    RecyclerView historyView;

    @BindView(R.id.no_history_text)
    TextView noHistoryView;

    private MotosAdapter motosAdapter;
    private RealmResults<Moto> motoFinder;
    private MainActivity activity;

    private Paint p = new Paint();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null)
        {
            this.getActivity().findViewById(R.id.fab).setOnClickListener(this);
        }
    }

    public static HistoryFragment newInstance(MainActivity activity, FloatingActionButton fab) {
        HistoryFragment f = new HistoryFragment();
        f.activity = activity;
        fab.setOnClickListener(f);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.rootview = inflater.inflate(R.layout.fragment_history, container, false);

        ButterKnife.bind(this, rootview);

        ((AppCompatActivity) this.getActivity()).getSupportActionBar().setTitle(getString(R.string.bnav_history));

        this.historyView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(TMApplication.getContext());
        this.historyView.setLayoutManager(layoutManager);
        this.historyView.setAdapter(this.motosAdapter = new MotosAdapter(new ArrayList<Moto>(0), (MainActivity) this.getActivity()));
        this.initSwipe();
        return this.rootview;
    }

    @Override
    public void onResume() {
        super.onResume();
        //demo(MotoRepository.getInstance());
        if (this.motoFinder != null) this.motoFinder.removeChangeListeners();
        this.motoFinder = MotoRepository.getInstance().findAllAsync(new RealmChangeListener<RealmResults<Moto>>() {
            @Override
            public void onChange(RealmResults<Moto> items) {
                onMotoResponseReceived(items);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.motoFinder != null) this.motoFinder.removeChangeListeners();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSuccess()
    {

    }

    @Override
    public void onFailure()
    {

    }

    public void onMotoResponseReceived(List<Moto> motos) {
        if (motos != null && !motos.isEmpty()) {
            this.motosAdapter.setItems(motos);
        }
    }

    private void initSwipe(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT){
                    motosAdapter.removeItem(position);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX <= 0){
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white_24dp);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                    else
                    {
                       return;
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(this.historyView);
    }

    // On Floating Action Button click
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this.getActivity(), MotoFormActivity.class);
        startActivity(intent);
    }
}
