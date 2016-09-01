package resource.water.com.waterresourceapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import resource.water.com.waterresourceapp.R;
import resource.water.com.waterresourceapp.activities.CommonFragment;

/**
 * Created by hari on 18/8/16.
 */

public class MyProfileFragment extends CommonFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile,container,false);


        forgotPassword();

        return view;
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
}
