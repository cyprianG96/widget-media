package com.mobica.widgets.weather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobica.widgets.HttpRequestThread;
import com.mobica.widgets.R;
import com.mobica.widgets.utils.LocationUtils;
import com.mobica.widgets.utils.SharedPrefsUtils;
import com.mobica.widgets.views.OvalButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WeatherConfigureFragment extends DialogFragment implements View.OnClickListener {

    private final String LOCATION_ACTION = "com.mobica.widgets.weather.LOCATION";

    private Callback mListener;

    private int appWidgetId;
    private AppCompatEditText searchEdit;
    private WeatherSearchAdapter adapter;
    private AppCompatTextView currentLocation;

    private String searchRequestValue = "";
    private Runnable searchRequestRunnable;

    private BroadcastReceiver broadcastReceiver;

    public WeatherConfigureFragment() { }

    static WeatherConfigureFragment newInstance(Callback listener) {
        WeatherConfigureFragment settingsFragment = new WeatherConfigureFragment();
        settingsFragment.mListener = listener;
        return settingsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.Dialog);
        searchRequestRunnable = new Runnable() {
            @Override
            public void run() {
                if (!searchRequestValue.isEmpty()) {
                    new HttpRequestThread("http://api.apixu.com/v1/search.json?key=851f9a23ff23485d9b383147192406&q=" + searchRequestValue).start(new HttpRequestThread.OnHttpRequestListener() {
                        @Override
                        public void onHttpResponseOk(final String response) {
                            searchEdit.post(new Runnable() {
                                @Override
                                public void run() {
                                    updateSearchList(response);
                                }
                            });
                        }
                        @Override
                        public void onHttpResponseError(int errorCode, String response) { }
                    });
                } else {
                    adapter.clearData();
                }
            }
        };
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("ZXC", intent.toString());
                if (intent.getAction().equals(LOCATION_ACTION)) {
                    PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    String location = LocationUtils.getCurrentLocation(getContext(), pendingIntent);
                    if (!location.isEmpty()) currentLocation.setText(location);
                }
            }
        };
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent intent = getActivity().getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mListener != null) {
            mListener.onCloseWeatherConfigure();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRoot = inflater.inflate(R.layout.fragment_weather_configure, container, false);
        OvalButton mOvalButtonClose = mRoot.findViewById(R.id.ovalButton_Settings_Close);
        mOvalButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return mRoot;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        searchEdit = view.findViewById(R.id.searchEdit);
        adapter = new WeatherSearchAdapter();
        RecyclerView searchList = view.findViewById(R.id.searchList);
        currentLocation = view.findViewById(R.id.currentLocation);
        currentLocation.setOnClickListener(this);
        searchList.setAdapter(adapter);
        searchList.setLayoutManager(new LinearLayoutManager(getContext()));
        view.findViewById(R.id.clearButton).setOnClickListener(this);
        view.findViewById(R.id.selectButton).setOnClickListener(this);
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (searchRequestRunnable != null) searchEdit.removeCallbacks(searchRequestRunnable);
                searchRequestValue = editable == null ? "" : editable.toString();
                searchEdit.postDelayed(searchRequestRunnable, 500L);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(LOCATION_ACTION));

        Intent intent = new Intent(LOCATION_ACTION);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        String location = LocationUtils.getCurrentLocation(getContext(), pendingIntent);
        if (!location.isEmpty()) currentLocation.setText(location);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    private void updateSearchList(String json) {
        try {
            ArrayList<WeatherSearchItem> searchItems = new ArrayList<>();
            JSONArray res = new JSONArray(json);
            for (int i = 0; i < res.length(); ++i) {
                WeatherSearchItem item = new WeatherSearchItem();
                JSONObject obj = res.getJSONObject(i);
                item.location = obj.getString("name");
                searchItems.add(item);
            }
            adapter.updateData(searchItems);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.clearButton) {
            searchEdit.setText("");
            adapter.clearData();
        } else if (v.getId() == R.id.selectButton) {
            WeatherSearchItem item = adapter.getSelected();
            if (item != null) {
                SharedPrefsUtils.saveLocationForWidget(v.getContext(), appWidgetId, item.location);
                Weather.updateAppWidget(v.getContext(), appWidgetId);
            }
            dismiss();
        } else if (v.getId() == R.id.currentLocation) {
            searchEdit.setText(currentLocation.getText());
        }
    }

    public interface Callback {
        void onCloseWeatherConfigure();
    }
}
