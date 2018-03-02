package br.senai.sp.informatica.mobile.bikemobi.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import br.senai.sp.informatica.mobile.bikemobi.R;

/**
 * Created by 43255815886 on 27/02/2018.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;

    public abstract void inicializarComponentes();

    protected void setupToolBar() {
        Toolbar toolbar = (Toolbar)
                findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    protected void setupNavDrawer() {

        final ActionBar actionBar = getSupportActionBar();

        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_18dp);

        actionBar.setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        if (navigationView != null && drawerLayout != null) {
            navigationView.setNavigationItemSelectedListener(onNavSelectedItem());
        }
    }

    private NavigationView.OnNavigationItemSelectedListener onNavSelectedItem() {
        return new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);

                drawerLayout.closeDrawers();

                onNavDrawerItemSelected(item);

                return true;
            }
        };


    }

    private void onNavDrawerItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_drawer_historico:
                Intent intentGestor = new Intent(this, HistoricoActivity.class);
                startActivity(intentGestor);
                finish();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (drawerLayout != null) {
                    // abre o nav drawer
                    openDrawer();
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    protected void openDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom()) {
                assert ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)) != null;
                ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

}
