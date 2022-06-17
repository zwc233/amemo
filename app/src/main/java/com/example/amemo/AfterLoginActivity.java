package com.example.amemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.amemo.ui.group.GroupFragment;
import com.example.amemo.ui.group.InGroupActivity;
import com.example.amemo.ui.memo.MemoFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.amemo.databinding.ActivityAfterLoginBinding;

public class AfterLoginActivity extends AppCompatActivity {

    private ActivityAfterLoginBinding binding;

    Receiver receiver;
    IntentFilter filter;

    class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case "Invitation":
                    updateGroups();
                    Toast.makeText(context,
                            R.string.invited_to_new_group,
                            Toast.LENGTH_SHORT).show();
                    break;
                case "New Member":
                    updateGroups();
                    Toast.makeText(context,
                            R.string.new_member_to_group,
                            Toast.LENGTH_SHORT).show();
                    break;
                case "New Memo":
                    updateGroups();
                    updateMemos();
                    Toast.makeText(context,
                            R.string.new_memo_created,
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setStatusBar(this);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
        binding = ActivityAfterLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        receiver = new Receiver();
        filter = new IntentFilter();
        filter.addAction("Invitation");
        filter.addAction("New Memo");

        registerReceiver(receiver, filter);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_memo, R.id.navigation_group, R.id.navigation_settings)
                .build();
        NavController navController;
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_after_login);
        navController = navHostFragment.getNavController();
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_after_login);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        ImageButton btnIcon = findViewById(R.id.iconButton);
        btnIcon.setOnClickListener(v ->{
            Intent mainIntent = new Intent(AfterLoginActivity.this, UserInfoActivity.class);
            AfterLoginActivity.this.startActivity(mainIntent);
            overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
        });

        ImageButton btnAddNewGroup = findViewById(R.id.addNewGroup);
        btnAddNewGroup.setOnClickListener(v ->{
            CreateNewGroupDialog customBottomDialog = new CreateNewGroupDialog(AfterLoginActivity.this);
            customBottomDialog.show();
            overridePendingTransition(R.anim.anim_slide_in_bottom,R.anim.no_anim);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(receiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    public void updateGroups() {
        GroupFragment groupFragment = (GroupFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_group);
        if (groupFragment != null) {
            groupFragment.updateGroups();
        }
    }

    public void updateMemos() {
        MemoFragment memoFragment = (MemoFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_memo);
        if (memoFragment != null) {
            memoFragment.updateMemos();
        }
    }
}