package com.theappwelt.vhelp.utilities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import com.theappwelt.vhelp.R;

import pl.droidsonroids.gif.GifImageView;

public class TransparentProgressDialog extends Dialog {

    private AnimationDrawable animation;

    public TransparentProgressDialog(Context context) {
        super(context, R.style.TransparentProgressDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_custom_progress_layout);

        GifImageView la = findViewById(R.id.loader);
    }

}