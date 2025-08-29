package cfks.bugjump.ghosteclipse.ui;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import jiesheng.系统操作;

public class SmallWatermarkCanvas extends View {
    private Paint paint;
    
    public SmallWatermarkCanvas(Context ctx){
        super(ctx);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        paint = new Paint(1);
        paint.setColor(0xA89B55);
        paint.setTextSize(50);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(1.0f);
        paint.setAlpha(90);//透明度35%
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // TODO: Implement this method
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        String text = "GhostEclipse v" + getVersionName(getContext());
        canvas.drawText(text,10,getMeasuredHeight()-10-getTextHeight(text),this.paint);
    }
    
    private String getVersionName(Context ctx){
        try {
			PackageManager packageManager = ctx.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(ctx.getPackageName(), 0);
			return packageInfo.versionName;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
    }
    
    private int getTextHeight(String text){
        Rect rect = new Rect();
        this.paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }
}