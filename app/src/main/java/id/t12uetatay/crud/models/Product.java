package id.t12uetatay.crud.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Product implements Serializable {
    @SerializedName("product_id")
    @Expose
    long productId;

    @SerializedName("product_name")
    @Expose
    String productName;

    @SerializedName("price")
    @Expose
    long price;

    @SerializedName("description")
    @Expose
    String description;

    @SerializedName("pic")
    @Expose
    String pic;

    public Product(long productId, String productName, long price, String description, String pic){
        this.productId=productId;
        this.productName=productName;
        this.price=price;
        this.description=description;
        this.pic=pic;
    }


    public long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public long getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getPic() {
        return pic;
    }

}
