package app.ec.com.apppa.LayerView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import app.ec.com.apppa.FotoListViewModel;
import app.ec.com.apppa.Helpers.FirebaseHelper;
import app.ec.com.apppa.Helpers.PermissionManager;
import app.ec.com.apppa.LayerModel.Album;
import app.ec.com.apppa.R;
import app.ec.com.apppa.RecyclerViews.FotoLineAdapter;
import app.ec.com.apppa.databinding.ActivityFotoListBinding;

import static app.ec.com.apppa.Helpers.FirebaseHelper.getInstance;

public class FotoListActivity extends AppCompatActivity {

    private ActivityFotoListBinding binding;
    private FotoListViewModel fotoListViewModel;
    private RecyclerView mFotoRecyclerView;
    private static final int PHOTO_REQUEST_CODE = 102;
    private int posAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // instancia o viewModel
        fotoListViewModel = new FotoListViewModel();

        // vincula a view ao viewModel
        binding = DataBindingUtil.setContentView(this, R.layout.activity_foto_list);
        binding.setViewModel(fotoListViewModel);
        binding.executePendingBindings();

        setupFotoRecycler();
        loadObservables();

        posAlbum = getIntent().getIntExtra("POS", 0);
        fotoListViewModel.onCreate(posAlbum);

        // Toolbar - botão voltar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupFotoRecycler(){

        mFotoRecyclerView = (RecyclerView) findViewById(R.id.fotoRecyclerView);

        LinearLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mFotoRecyclerView.setLayoutManager(layoutManager);

    }

    public void loadObservables() {
        binding.getViewModel().album.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Log.e("eccc", "VIEW onChange");
                Album album = binding.getViewModel().album.get();
                FotoLineAdapter mAdapter = new FotoLineAdapter(album.retImagensReverse(), posAlbum);
                mFotoRecyclerView.setAdapter(mAdapter);
            }
        });
    }

    public void onInsClick(View view){
        fotoListViewModel.onInsClick(FotoListActivity.this, FotoListActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            fotoListViewModel.insFotoFB();
        }
    }

    public void onCompartilharClick(View view){
        fotoListViewModel.onCompartilharClick(FotoListActivity.this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // volta para a Activity anterior
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
