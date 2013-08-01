package com.wise.Data;

import java.util.ArrayList;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.OverlayItem;
import com.baidu.mapapi.Projection;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

public class CarItemizedOverlay extends ItemizedOverlay<OverlayItem>{
	ArrayList<OverlayItem> mapOverlays = new ArrayList<OverlayItem>();
	GeoPoint p;
	String str;
	int picSize;
	public CarItemizedOverlay(Drawable defaDrawable,GeoPoint p,String str,int PicSize){
		this(defaDrawable);
		this.p = p;
		this.str = str;
		this.picSize = PicSize;
	}

	public CarItemizedOverlay(Drawable defaultMarker) {
		super(boundCenter(defaultMarker));
	}
	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow,long when) {
		super.draw(canvas, mapView, shadow);  
		Point point = new Point();
		Projection projection = mapView.getProjection();
		projection.toPixels(p, point);
		Paint paint1 = new Paint();
		paint1.setARGB(255, 0, 0, 0);
		paint1.setTextSize(picSize);
		paint1.setAntiAlias(true);
		canvas.drawText(str, point.x + 15, point.y -8, paint1);	
		return true;  
	}
	@Override
	protected OverlayItem createItem(int i) {
		return mapOverlays.get(i);
	}

	@Override
	public int size() {
		return mapOverlays.size();
	}
	// Ìí¼Ó±ê¼Ç
	public void addOverLay(OverlayItem overLay) {
		mapOverlays.add(overLay);
		this.populate();
	}
}