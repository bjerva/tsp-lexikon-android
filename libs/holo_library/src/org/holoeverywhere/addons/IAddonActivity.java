
package org.holoeverywhere.addons;

import org.holoeverywhere.app.Activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public abstract class IAddonActivity extends IAddonBase {
    private final Activity activity;

    public IAddonActivity(Activity activity) {
        this.activity = activity;
    }

    public boolean addContentView(View view, LayoutParams params) {
        return false;
    }

    public boolean closeOptionsMenu() {
        return false;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return false;
    }

    public View findViewById(int id) {
        return null;
    }

    public Activity getActivity() {
        return activity;
    }

    public boolean invalidateOptionsMenu() {
        return false;
    }

    public void onConfigurationChanged(Configuration newConfig) {

    }

    public void onCreate(Bundle savedInstanceState) {

    }

    public boolean onCreatePanelMenu(int featureId, android.view.Menu menu) {
        return false;
    }

    public void onDestroy() {

    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
        return false;
    }

    public boolean onMenuOpened(int featureId, android.view.Menu menu) {
        return false;
    }

    public void onPanelClosed(int featureId, android.view.Menu menu) {

    }

    public void onPause() {

    }

    public void onPostCreate(Bundle savedInstanceState) {

    }

    public void onPostResume() {

    }

    public boolean onPreparePanel(int featureId, View view, android.view.Menu menu) {
        return false;
    }

    public void onSaveInstanceState(Bundle outState) {

    }

    public void onStop() {

    }

    public void onTitleChanged(CharSequence title, int color) {

    }

    public boolean openOptionsMenu() {
        return false;
    }

    public boolean requestWindowFeature(int featureId) {
        return false;
    }

    public boolean setContentView(View view, LayoutParams params) {
        return false;
    }
}
