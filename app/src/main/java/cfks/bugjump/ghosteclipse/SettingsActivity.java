package cfks.bugjump.ghosteclipse;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import cfks.bugjump.ghosteclipse.databinding.ActivitySettingsBinding;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Settings");
        ImmersionBar.with(this)
        .transparentBar()
        .fullScreen(true)
        .fitsSystemWindows(true)
        .hideBar(BarHide.FLAG_HIDE_BAR)
        .init();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem arg0) {
        // TODO: Implement this method
        if(arg0.getItemId() == android.R.id.home){
            Utils.toActivity(this,MainActivity.class);
            return true;
        }
        return super.onOptionsItemSelected(arg0);
    }
    
    @Override
    public boolean onKeyDown(int arg0, KeyEvent arg1) {
        // TODO: Implement this method
        if(arg0 == KeyEvent.KEYCODE_BACK){
            Utils.toActivity(this,MainActivity.class);
            return true;
        }
        return super.onKeyDown(arg0, arg1);
    }
}
