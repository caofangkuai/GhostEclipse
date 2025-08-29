package cfks.bugjump.ghosteclipse.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import cfks.bugjump.ghosteclipse.BEMicrosoftLoginActivity;
import cfks.bugjump.ghosteclipse.R;
import cfks.bugjump.ghosteclipse.Utils;
import cfks.bugjump.ghosteclipse.databinding.FragmentBeBinding;
import cfks.bugjump.ghosteclipse.relay.AccountManager;
import com.google.android.material.snackbar.Snackbar;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.dialogs.MessageMenu;
import com.kongzue.dialogx.dialogs.PopTip;
import com.kongzue.dialogx.interfaces.OnMenuItemClickListener;
import com.kongzue.dialogx.interfaces.OnMenuItemSelectListener;
import dev.sora.relay.session.listener.xbox.XboxDeviceInfo;
import java.util.ArrayList;
import java.util.List;
import jiesheng.共享数据;

/**
 * A placeholder fragment containing a simple view.
 */
public class BEFragment extends Fragment {
  private FragmentBeBinding binding;
  private String[] minecraftVersionsList = {"1.21.90"};
  private String minecraftVersion = "";
  private Context ctx;
  private Activity act;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    ctx = getContext();
    act = getActivity();
    binding = FragmentBeBinding.inflate(inflater, container, false);
    View root = binding.getRoot();
    共享数据.初始化数据(ctx,"MinecraftInfo");
    binding.ip.setText(共享数据.取文本("BE_ip"));
    binding.port.setText(共享数据.取文本("BE_port","19132"));
    if(AccountManager.accounts.size() > 0){
        binding.loginMicrosoft.setText("You have logged in");
        binding.loginMicrosoft.setEnabled(false);
    }
    binding.loginMicrosoft.setOnClickListener(v->{
        //微软登录
        List<String> channels = new ArrayList<String>();
        XboxDeviceInfo.Companion.getDevices().values().forEach(device->{
           channels.add(device.getDeviceType());
        });
        MessageMenu.build()
        .setTitle("Please select the login channel")
        .setMenuList(channels.toArray(new String[channels.size()]))
        .setOnMenuItemClickListener(new OnMenuItemClickListener(){
            @Override
            public boolean onClick(Object arg0, CharSequence arg1, int index) {
                // TODO: Implement this method
                Intent intent = new Intent();
                intent.setClass(act, BEMicrosoftLoginActivity.class);
                intent.putExtra("channel",channels.get(index));
                act.startActivity(intent);
                act.finish();
                act.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return false;
            }
        })
        .show();
    });
	binding.mcVersion.setAdapter(new ArrayAdapter<String>(ctx,android.R.layout.simple_spinner_dropdown_item,minecraftVersionsList));
    binding.mcVersion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            minecraftVersion = minecraftVersionsList[pos];
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {}
    });
    binding.connect.setOnClickListener(v->{
        if(AccountManager.accounts.size() <= 0) {
        	PopTip.show("Please login to your Microsoft account").iconError();
            return;
        }
        if(minecraftVersion.isEmpty()){
            PopTip.show("Please select the Minecraft version").iconError();
            return;
        }
        if(binding.ip.getText().toString().isEmpty() || binding.port.getText().toString().isEmpty()){
            PopTip.show("Please enter the server ip and port").iconError();
            return;
        }
        Utils.toGameActivity(act,"BE");
    });
    return root;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
    
  @Override
  public void onPause() {
      super.onPause();
      // TODO: Implement this method
      共享数据.置文本("BE_ip",binding.ip.getText().toString());
      共享数据.置文本("BE_port",binding.port.getText().toString());
  }
}