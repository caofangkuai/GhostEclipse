package cfks.bugjump.ghosteclipse.ui.floatingView;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class SqWindowManagerFloatView extends DragViewLayout {
    public SqWindowManagerFloatView(final Context context, final int floatImgId,final ImageView.OnClickListener onclick) {
        super(context);
        setClickable(true);
        final ImageView floatView = new ImageView(context);
        floatView.setImageResource(floatImgId);
        floatView.setOnClickListener(onclick);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        addView(floatView, params);
    }
}