package resource.water.com.waterresourceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class SignUpActivity extends CommonActivity implements View.OnClickListener, View.OnKeyListener {

    private Button mBtnRegister;
    private EditText mEdtMobileNumber, mEdtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);

        initUi();
        listeners();
    }

    private void listeners() {

        mBtnRegister.setOnClickListener(this);
    }

    private void initUi() {

        mBtnRegister = (Button) findViewById(R.id.signUpRegister);
        mEdtMobileNumber = (EditText) findViewById(R.id.mobileNumber);
        mEdtPassword = (EditText) findViewById(R.id.signUpPassword);
        mEdtPassword.setOnKeyListener(this);
        mEdtMobileNumber.requestFocus();

    }

    @Override
    public void onClick(View view) {

        if (view == mBtnRegister) {

            signUpRequest();
        }
    }

    private boolean validate() {

        if (!Utils.isValidPhoneNumber(mEdtMobileNumber.getText().toString())) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.valid_phone), Toast.LENGTH_SHORT).show();
            mEdtMobileNumber.requestFocus();
            return false;
        } else if (!Utils.isValidPassword(mEdtPassword.getText().toString())) {
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

                    signUpRequest();

                    return true;
                default:
                    break;
            }
        }
        return false;
    }

    private void signUpRequest() {

        if (validate()) {
            Utils.showProgressBar(this, "Please Wait...");
            BackendlessUser backendlessUser = new BackendlessUser();
            backendlessUser.setProperty("mobilenumber", mEdtMobileNumber.getText().toString());
            backendlessUser.setPassword(mEdtPassword.getText().toString());
            Backendless.UserService.register(backendlessUser, new AsyncCallback<BackendlessUser>() {
                @Override
                public void handleResponse(BackendlessUser backendlessUser) {

                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                    Utils.hideProgressBar();
                    Intent intent = new Intent(SignUpActivity.this, NavigationMainActivity.class);
                    startActivity(intent);
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {

                    Toast.makeText(getApplicationContext(), "Fails" + backendlessFault.getCode(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
