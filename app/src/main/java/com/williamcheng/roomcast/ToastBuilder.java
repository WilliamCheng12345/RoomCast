package com.williamcheng.roomcast;

import android.content.Context;
import android.widget.Toast;

public class ToastBuilder {
    Context context;

    public ToastBuilder(Context context) {
        this.context = context;
    }

    public void createToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
