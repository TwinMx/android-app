package fr.isen.twinmx.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import fr.isen.twinmx.R;
import fr.isen.twinmx.TMApplication;

/**
 * Created by cdupl on 9/26/2016.
 */

public class TMSnackBar {

    private static Snackbar bluetoothSnackBar = null;

    private static Snackbar makeTop(View view, CharSequence charSequence, int duration, int background, int textColor, int actionColor, int drawable) {
        final Snackbar snackbar = Snackbar.make(view, charSequence, duration);
        final View snackView = snackbar.getView();
        final CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) snackView.getLayoutParams();
        params.gravity = Gravity.TOP;
        snackView.setLayoutParams(params);

        snackbar.getView().setBackgroundColor(background);

        // Changing message text color
        snackbar.setActionTextColor(textColor);

        // Changing action button text color
        final View sbView = snackbar.getView();
        final TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
        textView.setTextColor(actionColor);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setCompoundDrawablePadding(50);

        return snackbar;
    }

    public static Snackbar makeBluetooth(Context context, View view, View.OnClickListener listener) {
        final Resources r = TMApplication.getContext().getResources();
        TMSnackBar.bluetoothSnackBar = makeTop(view, r.getString(R.string.start_bt), Snackbar.LENGTH_INDEFINITE, context.getResources().getColor(R.color.blue500), Color.WHITE, Color.WHITE, R.drawable.ic_bluetooth_white_24dp);
        TMSnackBar.bluetoothSnackBar.setAction(r.getString(R.string.yes),listener);
        return TMSnackBar.bluetoothSnackBar;
    }
}
