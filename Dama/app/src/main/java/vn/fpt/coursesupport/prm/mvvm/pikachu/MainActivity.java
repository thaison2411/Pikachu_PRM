package vn.fpt.coursesupport.prm.mvvm.pikachu;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import vn.fpt.coursesupport.prm.mvvm.pikachu.databinding.ActivityMainBinding;
import vn.fpt.coursesupport.prm.mvvm.pikachu.viewmodel.PikachuViewModel;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private PikachuViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        int[] pikachuResources = new int[]{
                R.drawable.empty_tile,
                R.drawable.pikachu_1,
                R.drawable.pikachu_2,
                R.drawable.pikachu_3,
                R.drawable.pikachu_4,
                R.drawable.pikachu_5,
                R.drawable.pikachu_6,
                R.drawable.pikachu_7,
                R.drawable.pikachu_8,
                R.drawable.pikachu_9,
                R.drawable.pikachu_10
        };

        PikachuViewModel.Factory factory = new PikachuViewModel.Factory(1, pikachuResources);
        viewModel = new ViewModelProvider(this, factory).get(PikachuViewModel.class);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
    }
}