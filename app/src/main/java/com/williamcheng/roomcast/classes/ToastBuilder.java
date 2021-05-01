package com.williamcheng.roomcast.classes;

import android.content.Context;
import android.widget.Toast;

public class ToastBuilder {
    private final Context context;

    public ToastBuilder(Context context) {
        this.context = context;
    }

    public void createToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
