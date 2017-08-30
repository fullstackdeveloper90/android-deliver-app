package com.mobileappsprn.alldealership;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.zxing.Result;
import com.mobileappsprn.alldealership.utilities.ApplicationVariables;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by FuGenX-10 on 25-05-2016.
 */
public class CarScannerActivity extends Activity implements View.OnClickListener, ZXingScannerView.ResultHandler {


    Activity activityRef;
    private ZXingScannerView mScannerView;
    private static final int ZBAR_CAMERA_PERMISSION = 1;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        // Obtain the shared Tracker instance.
        ApplicationVariables application = (ApplicationVariables) getApplication();
        mTracker    =   application.getDefaultTracker();
        Log.v("FLOW", "CAME HERE : CarScannerActivity");
        activityRef = CarScannerActivity.this;
        takeCameraPermission();

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };
        contentFrame.addView(mScannerView);
    }

    @Override
    public void onClick(View v) {

    }

    public void takeCameraPermission() {
        if (ContextCompat.checkSelfPermission(activityRef, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
        }
    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case ZBAR_CAMERA_PERMISSION:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "Camera permission granted to use the QR Scanner", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
//                }
//                return;
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();
        if(mTracker != null) {
            mTracker.setScreenName(getClass().getSimpleName());
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }


    @Override
    public void handleResult(final Result rawResult) {
        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!rawResult.getText().toString().equalsIgnoreCase(null) &&
                        !rawResult.getBarcodeFormat().toString().equalsIgnoreCase(null)) {
                    String code = rawResult.getText().toString();
                    String format = rawResult.getBarcodeFormat().toString();
                    Log.v("Code ", code + " :: " + format);

                    Intent dataIntent = new Intent();
                    dataIntent.putExtra("SCAN_RESULT", code);
                    dataIntent.putExtra("SCAN_RESULT_TYPE", format);
                    setResult(Activity.RESULT_OK, dataIntent);
                    finish();

//                    Intent intent = new Intent(CarScannerActivity.this, QRScannerWebViewActivity.class);
//                    intent.putExtra("qrScanUrl", code);
//                    intent.putExtra("title","");
//                    intent.putExtra("type", "Fav");
//                    startActivity(intent);

                    mScannerView.stopCamera();
                    finish();
                } else {
                    mScannerView.resumeCameraPreview(CarScannerActivity.this);
                }

            }
        }, 2000);
    }


    private static class CustomViewFinderView extends ViewFinderView {
        public static final String TRADE_MARK_TEXT = "";
        public static final int TRADE_MARK_TEXT_SIZE_SP = 40;
        public final Paint PAINT = new Paint();

        public CustomViewFinderView(Context context) {
            super(context);
            init();
        }

        public CustomViewFinderView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            PAINT.setColor(Color.WHITE);
            PAINT.setAntiAlias(true);
            float textPixelSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    TRADE_MARK_TEXT_SIZE_SP, getResources().getDisplayMetrics());
            PAINT.setTextSize(textPixelSize);
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            drawTradeMark(canvas);
        }

        private void drawTradeMark(Canvas canvas) {
            Rect framingRect = getFramingRect();
            float tradeMarkTop;
            float tradeMarkLeft;
            if (framingRect != null) {
                tradeMarkTop = framingRect.bottom + PAINT.getTextSize() + 10;
                tradeMarkLeft = framingRect.left;
            } else {
                tradeMarkTop = 10;
                tradeMarkLeft = canvas.getHeight() - PAINT.getTextSize() - 10;
            }
            canvas.drawText(TRADE_MARK_TEXT, tradeMarkLeft, tradeMarkTop, PAINT);
        }
    }
}
