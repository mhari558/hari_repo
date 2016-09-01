package resource.water.com.waterresourceapp.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import resource.water.com.waterresourceapp.R;
import resource.water.com.waterresourceapp.model.Order;

/**
 * Created by hari on 4/8/16.
 */

public class OrdersAdapter extends BaseAdapter {
    Context context;
    List<Order> str;

    public OrdersAdapter(Context context, List<Order> str) {

        this.context =context;
        this.str =str;
    }

    @Override
    public int getCount() {
        return str.size();
    }

    @Override
    public Object getItem(int i) {
        return str.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        View view ;
        if(convertView == null){

           view = inflater.inflate(R.layout.custom_order,null);
        }else{
            view = convertView;
        }

        Order order = str.get(i);

        ((TextView)view.findViewById(R.id.waterCanQuantity)).setText(order.getQuantity());
        ((TextView)view.findViewById(R.id.orderType)).setText(order.getOrderType());
        ((TextView)view.findViewById(R.id.addressInfo)).setText(order.getAddress());

        return view;
    }
}
