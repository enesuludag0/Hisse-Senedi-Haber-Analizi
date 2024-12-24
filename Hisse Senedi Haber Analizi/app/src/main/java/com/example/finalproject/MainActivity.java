package com.example.finalproject;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.finalproject.databinding.MainActivityBinding;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {
    private MainActivityBinding binding;
    private final int ID_PIYASALAR = 1;
    private final int ID_HABERLER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        binding.bottomNavigation.add(new MeowBottomNavigation.Model(ID_PIYASALAR, R.drawable.stocks));
        binding.bottomNavigation.add(new MeowBottomNavigation.Model(ID_HABERLER, R.drawable.news));

        binding.bottomNavigation.show(ID_HABERLER, true);

        binding.bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                return null;
            }
        });

        binding.bottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                Fragment selectedFragment = null;
                if (model.getId() == ID_PIYASALAR) {
                    selectedFragment = new StockFragment();
                } else if (model.getId() == ID_HABERLER) {
                    selectedFragment = new NewsFragment();
                }

                replaceFragment(selectedFragment);

                return null;
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_main, fragment, null)
                .commit();
    }

}
