package com.navigator.customElements;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.navigator.R;


/**
 * Created by tony on 23.10.16.
 */

public class MarkerPlaceInfoFrame extends RelativeLayout {
    private TextView textView;
    private Button buttonView;
    private Context context;

    private static final String TAG = MarkerPlaceInfoFrame.class.getSimpleName();
    private final OnClickListener buttonListener = new OnClickListener() {
        public void onClick(View view) {
            Log.i(TAG, "Clicked custom field!");
        }
    };

    public MarkerPlaceInfoFrame(Context context) {
        super(context);
        this.context = context;
        initComponent();
    }

    private void initComponent() {
        Log.i(TAG, "Create MarkerPlaceInfoFrame!");
        LayoutInflater inflater = LayoutInflater.from(context);
//                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.button_layout, this);
        buttonView = (Button) v.findViewById(R.id.btnAdd);
        buttonView.setOnClickListener(buttonListener);
        textView = (TextView) v.findViewById(R.id.tv);
    }

    public void setInforPlace(String info) {
        if (textView != null) {
            textView.setText(info);
        }
    }

    public void setColor(int color) {
        if (textView != null) {
            textView.setTextColor(color);
        }
    }

    public Button getButtonView() {
        return buttonView;
    }

    public TextView getTextView() {
        return textView;
    }

}
