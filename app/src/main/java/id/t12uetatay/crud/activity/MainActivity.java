package id.t12uetatay.crud.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import id.t12uetatay.crud.R;
import id.t12uetatay.crud.helpers.Space;
import id.t12uetatay.crud.adapter.ProductAdapter;
import id.t12uetatay.crud.models.Product;
import id.t12uetatay.crud.viewmodel.ProductViewModel;

public class MainActivity extends AppCompatActivity implements ProductAdapter.AdapterListener {
    private ProductViewModel viewModel;
    private ProductAdapter mAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        viewModel = ViewModelProviders.of(this).get(ProductViewModel.class);
        mAdapter = new ProductAdapter(this, this);
        refreshLayout = findViewById(R.id.swipeRefresh);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL,false);
        //set layout manager as gridLayoutManager
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.addItemDecoration(new Space(2, 3));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Product product = null;
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                intent.putExtra("data", product);
                startActivity(intent);
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

    }

    public void onResume(){
        super.onResume();
        refreshContent();
    }

    private void refreshContent(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
                viewModel.getProjectListObservable().observe(MainActivity.this, new Observer<List<Product>>() {
                    @Override
                    public void onChanged(@Nullable List<Product> products) {
                        if (products != null) {
                            mAdapter.setDataList(products);
                            mAdapter.notifyDataSetChanged();
                            refreshLayout.setRefreshing(false);
                        }
                    }
                });
            }
        },3000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(long lapangId, int position) {

    }

    @Override
    public void onActionMoreClick(Product product, ImageView imageView) {
        popupShowMenu(product, imageView);
    }

    private static final String POPUP_CONSTANT = "mPopup";
    private static final String POPUP_FORCE_SHOW_ICON = "setForceShowIcon";

    public void popupShowMenu(Product product, ImageView imageView){
        PopupMenu popup = new PopupMenu(this, imageView);
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals(POPUP_CONSTANT)) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(POPUP_FORCE_SHOW_ICON, boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        popup.getMenuInflater().inflate(R.menu.menu_popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_modify) {
                    Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                    intent.putExtra("data", product);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.action_remove) {
                    new MaterialAlertDialogBuilder(MainActivity.this)
                            .setTitle("Alert!")
                            .setMessage("Are you sure to remove "+product.getProductName()+"?")
                            .setPositiveButton("Yes, Remove", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (!viewModel.delete(product.getProductId())){
                                        refreshContent();
                                    }
                                }
                            })
                            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .show();
                    return true;
                } else {
                    return onMenuItemClick(item);
                }

            }
        });
        popup.show();

    }
}