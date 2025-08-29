package cfks.bugjump.ghosteclipse.ui.fragment;

import android.app.Activity;
import android.content.Context;
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

import cfks.bugjump.ghosteclipse.R;
import cfks.bugjump.ghosteclipse.Utils;
import cfks.bugjump.ghosteclipse.databinding.FragmentJeBinding;
import com.google.android.material.snackbar.Snackbar;
import com.kongzue.dialogx.dialogs.PopTip;
import jiesheng.共享数据;

/**
 * A placeholder fragment containing a simple view.
 */
public class JEFragment extends Fragment {
  private FragmentJeBinding binding;
  private String[] minecraftVersionsList = {"1.20.6"};
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
    共享数据.初始化数据(ctx,"MinecraftInfo");
    binding = FragmentJeBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

    binding.offlineName.setText(共享数据.取文本("JE_offlineName"));
    binding.ip.setText(共享数据.取文本("JE_ip"));
    binding.port.setText(共享数据.取文本("JE_port","25565"));
    binding.microsoft.setChecked(!共享数据.取逻辑("JE_isOffline"));
    binding.offline.setChecked(共享数据.取逻辑("JE_isOffline"));
    binding.offline.setOnCheckedChangeListener((buttonView, isChecked) -> {
      if (isChecked) {
        binding.microsoft.setChecked(false);
      }
    });
    binding.microsoft.setOnCheckedChangeListener((buttonView, isChecked) -> {
      if (isChecked) {
        binding.offline.setChecked(false);
      }
    });
    binding.loginMicrosoft.setOnClickListener(v->{
        //微软登录
        PopTip.show("This login method is not supported for the time being").iconError();
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
        if(!binding.offline.isChecked() && !binding.microsoft.isChecked()){
            PopTip.show("Please select the login method").iconError();
            return;        
        }
        if(binding.offline.isChecked() && binding.offlineName.getText().toString().isEmpty()){
            PopTip.show("Please enter the offline name").iconError();
            return;
        }
        if(binding.microsoft.isChecked()){
            PopTip.show("This login method is not supported for the time being").iconError();
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
        Utils.toGameActivity(act,"JE");
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
      共享数据.置文本("JE_offlineName",binding.offlineName.getText().toString());
      共享数据.置文本("JE_ip",binding.ip.getText().toString());
      共享数据.置文本("JE_port",binding.port.getText().toString());
      共享数据.置逻辑("JE_isOffline",binding.offline.isChecked());
  }
}