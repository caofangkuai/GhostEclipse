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
import cfks.bugjump.ghosteclipse.databinding.FragmentNbeBinding;
import com.google.android.material.snackbar.Snackbar;
import com.kongzue.dialogx.dialogs.PopTip;
import jiesheng.共享数据;
import jiesheng.文件操作;
import jiesheng.算术运算;

/**
 * A placeholder fragment containing a simple view.
 */
public class NBEFragment extends Fragment {
  private FragmentNbeBinding binding;
  private String[] minecraftVersionsList = {"3.4.5.272725"};
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
    binding = FragmentNbeBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

    binding.email.setText(共享数据.取文本("NBE_email"));
    binding.pwd.setText(共享数据.取文本("NBE_pwd"));
    binding.randomAccount.setOnClickListener(v->{
        //随机小号
        String[] neteaseAccounts = 文件操作.读入资源文件(ctx,"neteaseAccounts.txt").split("\n");
        String neteaseAccount = neteaseAccounts[算术运算.取随机数(0,neteaseAccounts.length - 1)];
        binding.email.setText(neteaseAccount.split("----")[0]);
        binding.pwd.setText(neteaseAccount.split("----")[1]);
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
        if(binding.email.getText().toString().isEmpty() || binding.pwd.getText().toString().isEmpty()){
            PopTip.show("Please enter your email and password").iconError();
            return;
        }
        if(minecraftVersion.isEmpty()){
            PopTip.show("Please select the Minecraft version").iconError();
            return;
        }
        if(binding.roomCode.getText().toString().isEmpty()){
            PopTip.show("Please enter the room code").iconError();
            return;
        }
        Utils.toGameActivity(act,"NBE");
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
      共享数据.置文本("NBE_email",binding.email.getText().toString());
      共享数据.置文本("NBE_pwd",binding.pwd.getText().toString());
  }
}