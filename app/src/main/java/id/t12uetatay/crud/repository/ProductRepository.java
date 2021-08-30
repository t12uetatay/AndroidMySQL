package id.t12uetatay.crud.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.t12uetatay.crud.api.ApiService;
import id.t12uetatay.crud.api.RetrofitService;
import id.t12uetatay.crud.models.Product;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {
    private boolean error;

    public ProductRepository() {
    }

    public LiveData<List<Product>> getProduct() {
        Call<List<Product>> data = RetrofitService.getInstance().getapi().getProducts();
        final MutableLiveData<List<Product>> liveData = new MutableLiveData<>();
        data.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                liveData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                // TODO better error handling in part
                liveData.setValue(null);
            }
        });

        return liveData;
    }

    public boolean insert(Product product){
        Call<Product> call = RetrofitService
                .getInstance()
                .getapi()
                .postProduct(
                        product.getProductName(),
                        product.getPrice(),
                        product.getDescription(),
                        product.getPic()
                );
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                error = false;
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                error = true;
            }
        });

        return error;
    }

    public boolean update(Product product, String pic_name){
        Call<Product> call = RetrofitService
                .getInstance()
                .getapi()
                .putProduct(
                        product.getProductId(),
                        product.getProductName(),
                        product.getPrice(),
                        product.getDescription(),
                        product.getPic(),
                        pic_name
                );
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                error = false;
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                error = true;
            }
        });

        return error;
    }

    public boolean delete(long id){
        Call<Product> call = RetrofitService
                .getInstance()
                .getapi()
                .deleteProduct(id);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                error = false;
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                error = true;
            }
        });

        return error;
    }

    private void simulateDelay() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
