package id.t12uetatay.crud.api;

import java.util.List;

import id.t12uetatay.crud.models.Product;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ApiService {

    //get all product
    @GET("products")
    Call<List<Product>> getProducts();

    //add new product
    @FormUrlEncoded
    @POST("products/add")
    Call<Product> postProduct(@Field("product_name") String product_name,
                              @Field("price") long price,
                              @Field("description") String description,
                              @Field("pic") String pic);
    //modify product
    @FormUrlEncoded
    @PUT("products/update")
    Call<Product> putProduct(@Field("product_id") long id,
                             @Field("product_name") String product_name,
                             @Field("price") long price,
                             @Field("description") String description,
                             @Field("pic") String pic,
                             @Field("pic_name") String change);
    //remove
    @FormUrlEncoded
    @POST("products/delete")
    Call<Product> deleteProduct(@Field("product_id") long id);
}
