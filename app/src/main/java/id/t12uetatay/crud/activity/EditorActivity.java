package id.t12uetatay.crud.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import id.t12uetatay.crud.R;
import id.t12uetatay.crud.api.RetrofitService;
import id.t12uetatay.crud.models.Product;
import id.t12uetatay.crud.viewmodel.ProductViewModel;

public class EditorActivity extends AppCompatActivity {
    private MaterialButton button;
    private ImageView imageView;
    private CardView card;
    private static final int REQUEST_CODE_CHOOSE = 23;
    private TextInputEditText editText1;
    private TextInputEditText editText2;
    private TextInputEditText editText3;
    private TextInputLayout textInputLayout1;
    private TextInputLayout textInputLayout2;
    private TextInputLayout textInputLayout3;
    private ProductViewModel viewModel;
    private Bitmap bitmap = null;
    private Product product=null;
    private boolean ch=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Toolbar toolbar = findViewById(R.id.toolbar);
        button = findViewById(R.id.btn);
        imageView = findViewById(R.id.imageView);
        card = findViewById(R.id.card);
        editText1 = findViewById(R.id.editext1);
        editText2 = findViewById(R.id.editext2);
        editText3 = findViewById(R.id.editext3);
        textInputLayout1 = findViewById(R.id.textInputLayout1);
        textInputLayout2 = findViewById(R.id.textInputLayout2);
        textInputLayout3 = findViewById(R.id.textInputLayout3);
        viewModel = ViewModelProviders.of(this).get(ProductViewModel.class);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        card.setVisibility(View.GONE);
        product = (Product) getIntent().getSerializableExtra("data");
        if (product!=null){
            card.setVisibility(View.VISIBLE);
            button.setEnabled(true);
            editText1.setText(product.getProductName());
            editText2.setText(String.valueOf(product.getPrice()));
            editText3.setText(product.getDescription());
            Glide.with(getApplicationContext())
                    .load(RetrofitService.base_url_image+product.getPic())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }

        findViewById(R.id.browse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RxPermissions rxPermissions = new RxPermissions(EditorActivity.this);
                rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .subscribe(aBoolean -> {
                            if (aBoolean) {
                                startAction();
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.permission_request_denied, Toast.LENGTH_LONG)
                                        .show();
                            }
                        }, Throwable::printStackTrace);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()){
                    if (product!=null){
                        putProduct(product.getProductId(), product.getPic());
                    } else {
                        postProduct();
                    }
                }
            }
        });
    }

    private void postProduct(){
        if (viewModel.insert(new Product(
                0,
                editText1.getText().toString(),
                Long.parseLong(editText2.getText().toString()),
                editText3.getText().toString(),
                getStringImage(bitmap)
        ))){
            Toast.makeText(getApplicationContext(), "Failed uploaded!", Toast.LENGTH_SHORT).show();
        } else {
            editText1.setText(null);
            editText2.setText(null);
            editText3.setText(null);
            card.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Uploaded successfull!", Toast.LENGTH_SHORT).show();
        }
    }

    private void putProduct(long id, String pic_name){
        String pic="";
        if (ch) {
            pic = getStringImage(bitmap);
        }
        if (viewModel.update(new Product(
                id,
                editText1.getText().toString(),
                Long.parseLong(editText2.getText().toString()),
                editText3.getText().toString(),
                pic
        ), pic_name)){
            Toast.makeText(getApplicationContext(), "Failed Modify!", Toast.LENGTH_SHORT).show();
        } else {
            onBackPressed();
            Toast.makeText(getApplicationContext(), "Modify successfull!", Toast.LENGTH_SHORT).show();
        }
    }

    private void startAction(){
        Matisse.from(EditorActivity.this)
                .choose(MimeType.ofImage(), false)
                .theme(R.style.Matisse_Dracula)
                .countable(true)
                .capture(true)
                .captureStrategy(
                        new CaptureStrategy(true, "com.zhihu.matisse.sample.fileprovider", "test"))
                .maxSelectable(1)
                //.addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(
                        getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .setOnSelectedListener((uriList, pathList) -> {
                    //Log.e("onSelected", "onSelected: pathList=" + pathList);
                })
                .showSingleMediaType(true)
                .originalEnable(true)
                .maxOriginalSize(10)
                .autoHideToolbarOnSingleTap(true)
                .setOnCheckedListener(isChecked -> {
                    //Log.e("isChecked", "onCheck: isChecked=" + isChecked);
                })
                .forResult(REQUEST_CODE_CHOOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            Uri uri = Uri.fromFile(new File(Matisse.obtainPathResult(data).get(0)));
            Crop(uri);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                if (result.getUri()!=null){
                    ch=true;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    card.setVisibility(View.VISIBLE);
                    button.setEnabled(true);
                    Glide.with(getApplicationContext())
                            .load(result.getUri().toString())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imageView);
                }

            }
        }
    }

    private void Crop(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setAspectRatio(3,4)
                .start(this);
    }

    public boolean validate() {
        boolean valid = false;
        String name = editText1.getText().toString();
        String price = editText2.getText().toString();
        String desc = editText3.getText().toString();
        if (name.isEmpty()) {
            valid = false;
            textInputLayout1.setError("Product name required.");
        } else {
            valid = true;
            textInputLayout1.setError(null);
        }

        if (price.isEmpty()) {
            valid = false;
            textInputLayout2.setError("Price required.");
        } else {
            valid = true;
            textInputLayout2.setError(null);
        }


        if (desc.isEmpty()) {
            valid = false;
            textInputLayout3.setError("Description required.");
        } else {
            valid = true;
            textInputLayout3.setError(null);
        }

        return valid;
    }

    public String getStringImage(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageByteArray, Base64.DEFAULT);
    }

}