package resource.water.com.waterresourceapp.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.google.gson.Gson;

import resource.water.com.waterresourceapp.R;
import resource.water.com.waterresourceapp.activities.CommonFragment;
import resource.water.com.waterresourceapp.activities.LogInActivity;
import resource.water.com.waterresourceapp.util.Const;
import resource.water.com.waterresourceapp.util.Utils;

import static android.R.attr.password;

/**
 * Created by hari on 18/8/16.
 */

public class MyProfileFragment extends CommonFragment implements View.OnClickListener {

    private Button mBtnUpdatePassword;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

         view = inflater.inflate(R.layout.fragment_profile,container,false);

        initUI();
        listeners();
        forgotPassword();

        return view;
    }

    private void initUI() {
        mBtnUpdatePassword = (Button)view.findViewById(R.id.update_password);
    }
    private void listeners() {

        mBtnUpdatePassword.setOnClickListener(this);
    }

    private void forgotPassword() {

        Backendless.UserService.restorePassword( "123", new AsyncCallback<Void>()
        {
            public void handleResponse( Void response )
            {
                // Backendless has completed the operation - an email has been sent to the user
            }

            public void handleFault( BackendlessFault fault )
            {
                // password revovery failed, to get the error code call fault.getCode()
            }
        });
    }

    @Override
    public void onClick(View view) {

        if(view == mBtnUpdatePassword){

             updatePasswordDialog();

        }
    }

    private void updatePasswordDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.update_password);
        final EditText mEdtEmailId = new EditText(getActivity());
       // final EditText mEdtEmailId = new EditText(getActivity());
        builder.setView(mEdtEmailId);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Utils.showProgressBar(getActivity(),"Please wait...");

                upddatePasswordRequest();

                dialogInterface.dismiss();
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        }).create().show();

    }

    private void upddatePasswordRequest() {

       String password = Utils.getStringFromSharedPreferences(getActivity(),"Password");
       String userId =  Utils.getStringFromSharedPreferences(getActivity(),"UserId");

        Backendless.UserService.login(userId,password, new AsyncCallback<BackendlessUser>()
        {
            public void handleResponse( BackendlessUser user )
            {
                // user has been logged in, now user properties can be updated
                user.setPassword("hari.m558");
                Backendless.UserService.update( user, new AsyncCallback<BackendlessUser>()
                {
                    public void handleResponse( BackendlessUser user )
                    {

                        Utils.hideProgressBar();
                        Toast.makeText(getActivity(),"Your password has been changed successfully... ",Toast.LENGTH_SHORT).show();
                        // user has been updated
                    }

                    public void handleFault( BackendlessFault fault )
                    {
                        Toast.makeText(getActivity(),"Failed"+fault.getMessage(),Toast.LENGTH_SHORT).show();
                        // user update failed, to get the error code call fault.getCode()
                    }
                });
            }

            public void handleFault( BackendlessFault fault )
            {
                Toast.makeText(getActivity(),"Failed"+fault.getMessage(),Toast.LENGTH_SHORT).show();
                // login failed, to get the error code call fault.getCode()
            }
        });
    }
}
