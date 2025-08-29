package cfks.bugjump.ghosteclipse;

import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import cfks.bugjump.ghosteclipse.ui.fragment.SectionsPagerAdapter;
import cfks.bugjump.ghosteclipse.databinding.ActivityMainBinding;
import cfks.bugjump.ghosteclipse.wfb0f52bbeb47d2a2120e8a648e0fb487;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        wfb0f52bbeb47d2a2120e8a648e0fb487.Start(this);
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        binding.viewPager.setAdapter(sectionsPagerAdapter);
        binding.tabs.setupWithViewPager(binding.viewPager);
        ImmersionBar.with(this)
        .transparentBar()
        .fullScreen(true)
        .fitsSystemWindows(true)
        .hideBar(BarHide.FLAG_HIDE_BAR)
        .init();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 114514, Menu.NONE, "Settings").setIcon(R.drawable.ic_settings).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem arg0) {
        // TODO: Implement this method
        if(arg0.getItemId() == 114514){
            Utils.toActivity(this,SettingsActivity.class);
        }
        return super.onOptionsItemSelected(arg0);
    }
}