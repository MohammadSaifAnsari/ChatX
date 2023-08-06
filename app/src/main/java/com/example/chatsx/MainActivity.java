package com.example.chatsx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.chatsx.Adapter.ViewPagerAdapter;
import com.example.chatsx.Fragment.ChatsFragment;
import com.example.chatsx.Model.UserModel;
import com.example.chatsx.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import com.firebase.ui.database.FirebaseRecyclerOptions;

public class MainActivity extends AppCompatActivity {


    ActivityMainBinding activityMainBinding;
    FirebaseAuth firebaseAuth;

    ViewPagerAdapter viewPagerAdapter;

    private String[] titles = new String[]{"Chats", "Calls"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        //Adding toolbar to main activity
        Toolbar toolbar = activityMainBinding.chatTool;
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();


        //Setting view pager adapter for tab layout
        viewPagerAdapter = new ViewPagerAdapter(this);
        activityMainBinding.viewPager2.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(activityMainBinding.tabLayout, activityMainBinding.viewPager2,
                ((tab, position) -> tab.setText(titles[position]))).attach();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.Search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

//                Bundle mBundle = new Bundle();
//                mBundle.putString("mText",
//                        newText);
//                new ChatsFragment().setArguments(mBundle);

                return false;
            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.Setting) {
            Toast.makeText(this, "Opening Setting", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.Logout) {
            firebaseAuth.signOut();
            Intent intent = new Intent(MainActivity.this, SignIn.class);
            startActivity(intent);
        }
        if (id == R.id.Profile) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //Exit app on back pressed
        Intent a = new Intent(Intent.ACTION_MAIN); //Launches home screen
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}