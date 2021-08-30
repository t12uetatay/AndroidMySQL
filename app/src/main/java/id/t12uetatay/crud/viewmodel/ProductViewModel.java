package id.t12uetatay.crud.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import id.t12uetatay.crud.models.Product;
import id.t12uetatay.crud.repository.ProductRepository;
import io.github.cdimascio.dotenv.Dotenv;

public class ProductViewModel extends AndroidViewModel {
    private ProductRepository productRepository;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        productRepository = new ProductRepository();
    }

    public LiveData<List<Product>> getProjectListObservable() {
        return productRepository.getProduct();
    }

    public boolean insert(Product product){
        return productRepository.insert(product);
    }

    public boolean update(Product product, String pic_name){
        return productRepository.update(product, pic_name);
    }

    public boolean delete(long id){
        return productRepository.delete(id);
    }
}
