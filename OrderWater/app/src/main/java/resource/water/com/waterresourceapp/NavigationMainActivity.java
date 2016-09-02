package resource.water.com.waterresourceapp;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.ArrayList;
import java.util.List;

import resource.water.com.waterresourceapp.activities.CommonActivity;
import resource.water.com.waterresourceapp.activities.LogInActivity;
import resource.water.com.waterresourceapp.fragments.HomeFragment;
import resource.water.com.waterresourceapp.fragments.MyOrdersFragment;
import resource.water.com.waterresourceapp.fragments.MyProfileFragment;
import resource.water.com.waterresourceapp.model.PermissionsModel;
import resource.water.com.waterresourceapp.util.Utils;

/**
 * Created by hari on 18/8/16.
 */

public class NavigationMainActivity extends CommonActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final int REQUEST_CODE_PERMISSIONS = 101;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private  List<PermissionsModel> permissionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
        checkPermissions();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        initNavigationDrawer();
        if (savedInstanceState == null) {
            onNavigationItemSelected(navigationView.getMenu().getItem(0));
            navigationView.getMenu().getItem(0).setChecked(true);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.home_fragment:
                HomeFragment home = new HomeFragment();
                loadFragment(home);
                break;
            case R.id.orders_fragment:
                MyOrdersFragment orders = new MyOrdersFragment();
                loadFragment(orders);
                drawerLayout.closeDrawers();
                break;
            case R.id.profile_fragment:
                MyProfileFragment profileFragment = new MyProfileFragment();
                loadFragment(profileFragment);
                drawerLayout.closeDrawers();
                break;
            case R.id.logout_fragment:
                logout();
                drawerLayout.closeDrawers();
                break;
            default:
                break;
        }
        return true;
    }

    public void initNavigationDrawer() {

        View header = navigationView.getHeaderView(0);
        TextView tv_email = (TextView) header.findViewById(R.id.tv_email);
        tv_email.setText("raj.amalw@learn2crack.com");
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void loadFragment(Fragment fragment) {
        drawerLayout.closeDrawers();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

    }

    public void logout() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.logout);
        // EditText mEdtEmailId = new EditText(this);
        // builder.setView(mEdtEmailId);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Utils.showProgressBar(NavigationMainActivity.this, "Please Wait...");
                Backendless.UserService.logout(new AsyncCallback<Void>() {
                    public void handleResponse(Void response) {

                        Utils.hideProgressBar();
                        // user has been logged out.
                        Intent intent = new Intent(NavigationMainActivity.this, LogInActivity.class);
                        startActivity(intent);
                    }

                    public void handleFault(BackendlessFault fault) {
                        // something went wrong and logout failed, to get the error code call fault.getCode()
                    }
                });

                dialogInterface.dismiss();
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        }).create().show();

    }

    private void checkPermissions() {

        int locationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        permissionsList = new ArrayList<>();
        ArrayList<String> list = new ArrayList<>();

        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            PermissionsModel permissionsModel2 = new PermissionsModel();
            permissionsModel2.setMessage(getResources().getString(R.string.location));
            permissionsModel2.setPermissions(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionsList.add(permissionsModel2);
            list.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!list.isEmpty()) {
            ActivityCompat.requestPermissions(this, list.toArray(new String[list.size()]), REQUEST_CODE_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS: {

                for (int i = 0; i < permissions.length; i++) {

                   if (grantResults[i] == PackageManager.PERMISSION_DENIED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                permissions[i])) {
                            showPermissionDialog(requestCode,permissionsList.get(i).getMessage(), permissions[i]);
                        }else {
                            Toast.makeText(this, "else "+permissionsList.get(i).getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showPermissionDialog(final int requestCode, String message, final String permission) {

        new android.support.v7.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(NavigationMainActivity.this, new String[]{permission}, requestCode);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .create()
                .show();
    }
}
