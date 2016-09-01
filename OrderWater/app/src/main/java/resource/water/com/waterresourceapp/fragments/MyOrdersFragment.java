package resource.water.com.waterresourceapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.async.callback.BackendlessCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

import java.util.List;

import resource.water.com.waterresourceapp.R;
import resource.water.com.waterresourceapp.activities.CommonActivity;
import resource.water.com.waterresourceapp.activities.CommonFragment;
import resource.water.com.waterresourceapp.adapters.OrdersAdapter;
import resource.water.com.waterresourceapp.model.Order;
import resource.water.com.waterresourceapp.util.Utils;

/**
 * Created by hari on 27/7/16.
 */

public class MyOrdersFragment extends CommonFragment {

    private ListView orderList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_order_list,container,false);
        orderList = (ListView) view.findViewById(R.id.orderList);
        Utils.showProgressBar(getActivity(),"Please wait...");
        getAllOrdersList();
        return view;
    }

    private void getAllOrdersList() {

        String id = Backendless.UserService.CurrentUser().getObjectId();
        String whereClause = "ownerId = '"+id+"'" ;
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause( whereClause );
        Backendless.Persistence.of( Order.class ).find( dataQuery,
                new AsyncCallback<BackendlessCollection<Order>>(){
                    @Override
                    public void handleResponse( BackendlessCollection<Order> orderBackendlessCollection )
                    {
                        // the "foundContact" collection now contains instances of the Contact class.
                        // each instance represents an object stored on the server.

                        List<Order> str = orderBackendlessCollection.getData();

                        for (int i = 0; i < str.size(); i++) {

                            System.out.println("=======DATA" + str.get(i).getAddress());
                        }
                        Utils.hideProgressBar();
                        OrdersAdapter ordersAdapter = new OrdersAdapter(getActivity(), str);
                        orderList.setAdapter(ordersAdapter);
                        ordersAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void handleFault( BackendlessFault fault )
                    {
                        // an error has occurred, the error code can be retrieved with fault.getCode()
                    }
                });

                /*Backendless.Persistence.of(Order.class).find(new AsyncCallback<BackendlessCollection<Order>>() {
                    @Override
                    public void handleResponse(BackendlessCollection<Order> orderBackendlessCollection) {

                        List<Order> str = orderBackendlessCollection.getData();

                        for (int i = 0; i < str.size(); i++) {

                            System.out.println("=======DATA" + str.get(i).getAddress());
                        }
                        Utils.hideProgressBar();
                        OrdersAdapter ordersAdapter = new OrdersAdapter(getActivity(), str);
                        orderList.setAdapter(ordersAdapter);
                        ordersAdapter.notifyDataSetChanged();


                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {

                    }
                });*/
    }
}
