package resource.water.com.waterresourceapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import resource.water.com.waterresourceapp.NavigationMainActivity;
import resource.water.com.waterresourceapp.R;
import resource.water.com.waterresourceapp.util.Utils;

/**
 * Created by hari on 27/7/16.
 */

public class LogInActivity extends CommonActivity implements View.OnClickListener, View.OnKeyListener {

    public static final String APPLICATION_SECRET_KEY = "B79091D3-9FF6-B9D7-FF99-A466DBB3F800";
    public static final String APPLICATION_ID = "44E38069-4ADB-5077-FF34-C551D6D41200";
    private Button mBtnLoginSubmit;
    private TextView mTxtRegister, mTxtForgotPassword;
    private EditText mEdtMobile, mEdtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        Backendless.initApp(this, APPLICATION_ID, APPLICATION_SECRET_KEY, "v1");
        initUi();
        listeners();
    }

    private void listeners() {

        mBtnLoginSubmit.setOnClickListener(this);
        mTxtForgotPassword.setOnClickListener(this);
        mTxtRegister.setOnClickListener(this);
        mEdtPassword.setOnKeyListener(this);
    }

    private void initUi() {

        mBtnLoginSubmit = (Button) findViewById(R.id.loginSubmit);
        mEdtMobile = (EditText) findViewById(R.id.loginMobile);
        mEdtPassword = (EditText) findViewById(R.id.loginPassword);
        mTxtForgotPassword = (TextView) findViewById(R.id.loginForgotpassword);
        mTxtRegister = (TextView) findViewById(R.id.loginNewRegister);
    }

    @Override
    public void onClick(View view) {

        if (view == mBtnLoginSubmit) {
            Utils.showProgressBar(this,"Please wait...");
            signInRequest();

        } else if (view == mTxtForgotPassword) {

            forgotPassword();

        } else if (view == mTxtRegister) {

            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        }
    }

    private void signInRequest() {
        if (validate()) {
           Backendless.UserService.login(mEdtMobile.getText().toString(), mEdtPassword.getText().toString(), new AsyncCallback<BackendlessUser>() {
                @Override
                public void handleResponse(BackendlessUser backendlessUser) {

                   // Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), NavigationMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    Utils.hideProgressBar();
                }
                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    Utils.hideProgressBar();
                    if(Integer.parseInt(backendlessFault.getCode()) == 3003) {
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.not_existed), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void forgotPassword() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.forgotTitle);
        EditText mEdtEmailId = new EditText(this);
        builder.setView(mEdtEmailId);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        }).create().show();

    }

    private boolean validate() {

        if (!Utils.isValidPhoneNumber(mEdtMobile.getText().toString())) {
            Utils.hideProgressBar();
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.valid_phone), Toast.LENGTH_SHORT).show();
            mEdtMobile.requestFocus();
            return false;
        } else if (!Utils.isValidPassword(mEdtPassword.getText().toString())) {
            Utils.hideProgressBar();
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.valid_password), Toast.LENGTH_SHORT).show();
            mEdtPassword.requestFocus();
            return false;
        } else {

            return true;
        }
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_ENTER:

                    signInRequest();

                    return true;
                default:
                    break;
            }
        }

        return false;
    }
}
