package resource.water.com.waterresourceapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import resource.water.com.waterresourceapp.R;
import resource.water.com.waterresourceapp.model.Order;
import resource.water.com.waterresourceapp.util.Const;
import resource.water.com.waterresourceapp.util.Utils;

/**
 * Created by hari on 28/7/16.
 */

public class OrderPaymentActivity extends CommonActivity implements View.OnClickListener {

    private TextView  mTxtQuantity;
    private TextView  mTxtOrderType;
    private TextView  mTxtAddress;
    private Button mFinalOrderSubmit;
    private String orderType,address,quantity;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderpayment);

        getIntentData();
        init();
        listener();
        setIntentData();

    }

    private void listener() {

        mFinalOrderSubmit.setOnClickListener(this);
    }

    private void setIntentData() {

        mTxtQuantity.setText(quantity);
        mTxtOrderType.setText(orderType);
        mTxtAddress.setText(address);
    }

    private void getIntentData() {

        Bundle bundle = getIntent().getExtras();

        orderType = bundle.getString(Const.CUS_TYPE);
        address = bundle.getString(Const.ADDRESS);
        quantity = bundle.getString(Const.QUANTTIY);

    }

    private void init() {


        mTxtQuantity =  (TextView)findViewById(R.id.waterCanQuantity);
        mTxtOrderType = (TextView)findViewById(R.id.orderType);
        mTxtAddress =   (TextView)findViewById(R.id.addressInfo);
        mFinalOrderSubmit = (Button)findViewById(R.id.finalSubmitOrder);
    }

    @Override
    public void onClick(View view) {


        if(view == mFinalOrderSubmit){

            confirmationDialog();
        }
    }

    private void confirmationDialog() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.order_confirm_msg);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                saveOrder();
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        }).create().show();
    }

    private void saveOrder(){

    if(validate()) {

        Utils.showProgressBar(this,"Please wait...");
        Order order = new Order();
        order.setAddress(mTxtAddress.getText().toString());
        order.setQuantity(mTxtQuantity.getText().toString());
        order.setOrderType(mTxtOrderType.getText().toString());

        Backendless.Persistence.save(order, new AsyncCallback<Order>() {
            @Override
            public void handleResponse(Order order) {

                Utils.hideProgressBar();

                Toast.makeText(getApplicationContext(), " Your Order is placed successfully..", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(getApplicationContext(), "Fail" + backendlessFault.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
    private boolean validate() {

        if (mTxtAddress.getText().toString().length()<=0 || mTxtAddress.getText().toString() == null) {
            Toast.makeText(this, getResources().getString(R.string.valid_destination), Toast.LENGTH_SHORT).show();
            return false;
        } else if (mTxtQuantity.getText().toString().length()<=0 || mTxtQuantity.getText().toString().length()>4) {
            Toast.makeText(this, getResources().getString(R.string.valid_quantity), Toast.LENGTH_SHORT).show();
            return false;
        } else if (mTxtOrderType.toString().length()<=0 || mTxtOrderType == null) {
            Toast.makeText(this, getResources().getString(R.string.valid_order), Toast.LENGTH_SHORT).show();
            return false;
        } else {

            return true;
        }
    }

}
