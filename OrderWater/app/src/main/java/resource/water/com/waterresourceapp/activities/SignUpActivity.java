package resource.water.com.waterresourceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class SignUpActivity extends CommonActivity implements View.OnClickListener, View.OnKeyListener, AdapterView.OnItemSelectedListener {

    private Button mBtnRegister;
    private EditText mEdtMobileNumber, mEdtPassword;
    private Spinner mSpUserType;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);
        initUi();
        setCategoryData();
        listeners();
    }

    private void listeners() {

        mBtnRegister.setOnClickListener(this);
    }

    private void initUi() {

        mBtnRegister = (Button) findViewById(R.id.signUpRegister);
        mEdtMobileNumber = (EditText) findViewById(R.id.mobileNumber);
        mEdtPassword = (EditText) findViewById(R.id.signUpPassword);
        mSpUserType = (Spinner)findViewById(R.id.sp_category);
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
        } else if (userType.equalsIgnoreCase("Select User Type")) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.valid_usertype), Toast.LENGTH_SHORT).show();
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
            backendlessUser.setProperty("contact", mEdtMobileNumber.getText().toString());
            backendlessUser.setProperty("email", "hari.m558@gmail.com");
            backendlessUser.setProperty("usertype", userType);
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
    private void setCategoryData() {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpUserType.setAdapter(adapter);
        mSpUserType.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String str = (String) adapterView.getItemAtPosition(i);
        userType = str;

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
