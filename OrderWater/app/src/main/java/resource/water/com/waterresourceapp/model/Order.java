package resource.water.com.waterresourceapp.model;

/**
 * Created by hari on 4/8/16.
 */

public class Order {

    private String address,orderType,quantity;
    private String objectId;

    public String getAddress() {
        return address;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
