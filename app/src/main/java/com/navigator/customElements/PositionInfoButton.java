package com.navigator.customElements;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.navigator.R;

/**
 * Created by tony on 23.10.16.
 */

public class PositionInfoButton extends LinearLayout {
    private TextView text;
    private Button button;

    public PositionInfoButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.button_layout, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        text = (TextView) findViewById(R.id.tv);
        button = (Button) findViewById(R.id.btnAdd);
    }
}
