package cfks.bugjump.ghosteclipse.ui;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import jiesheng.系统操作;

public class WatermarkCanvas extends View {
    private String username = "";
    private Paint paint;
    
    public WatermarkCanvas(Context ctx,String username){
        super(ctx);
        this.username = "§a§lUser: " + username;
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        paint = new Paint(1);
        paint.setColor(0x6C1D6C);
        paint.setTextSize(150);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(1.0f);
        paint.setAlpha(90);//透明度35%
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // TODO: Implement this method
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        int centralPointX = getMeasuredWidth()/2;
        int centralPointY = getMeasuredHeight()/2;
        String line1 = "GhostEclipse";
        canvas.drawText(line1,centralPointX-getTextWidth(line1)/2,centralPointY-getTextHeight(line1),this.paint);
        canvas.drawText(this.username,centralPointX-getTextWidth(this.username)/2,centralPointY+getTextHeight(this.username),this.paint);
    }
    
    private int getTextHeight(String text){
        Rect rect = new Rect();
        this.paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }
    
    private int getTextWidth(String text){
        Rect rect = new Rect();
        this.paint.getTextBounds(text, 0, text.length(), rect);
        return rect.width();
    }
}