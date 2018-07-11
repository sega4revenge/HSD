package com.finger.hsd.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.finger.hsd.R;
import com.finger.hsd.model.Date_Ex;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Scanner_HSD_Activity extends AppCompatActivity {

    SurfaceView surfaceView;
    TextView tx;
    int moc = 0;
    CameraSource cameraSource;
    final int perID = 1001;
    String regexNumber = "([0-9/])";
    String dateCreate = "";
    String dateUse = "";
    StringBuffer DateNumber = new StringBuffer();
    ArrayList<Date_Ex> arrDate = new ArrayList<Date_Ex>();
    long mdateCreate = 0 ;
    long mdateUse= 0;
    boolean checkDate= false;
    MaterialDialog dialog;
    int mOldNum = 0;
    ImageView arrow_back;
    String[] regex = {"(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)"
            ,"(0?[1-9]|[12][0-9]|3[01])(0?[1-9]|1[012])((19|20)\\d\\d)"
            ,"(0?[1-9]|[12][0-9]|3[01])(0?[1-9]|1[012])([12][0-9])"
            ,"(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/([12][0-9])"
            ,"(0?[1-9]|[12][0-9]|3[01]) (0?[1-9]|1[012]) ((19|20)\\d\\d)"
            ,"(0?[1-9]|[12][0-9]|3[01]) (0?[1-9]|1[012]) ([12][0-9])"
            ,"(0?[1-9]|[12][0-9]|3[01]).(0?[1-9]|1[012]).((19|20)\\d\\d)"
            ,"((19|20)\\d\\d).(0?[1-9]|1[012]).(0?[1-9]|[12][0-9]|3[01])"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner__hsd);

        surfaceView = (SurfaceView) findViewById(R.id.SurfaceView);
        tx = (TextView) findViewById(R.id.objectWord);
        arrow_back= (ImageView) findViewById(R.id.arrow_back);
        arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraSource.stop();
                finish();
            }
        });
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(Scanner_HSD_Activity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Scanner_HSD_Activity.this,new String[]{Manifest.permission.CAMERA},perID);
                        return;
                    }
                    cameraSource.start(surfaceView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();

            }
        });

        TextRecognizer textRecognizer = new TextRecognizer.Builder(Scanner_HSD_Activity.this).build();
        if (!textRecognizer.isOperational()) {
//            Log.d("ERROR", "Error!Permission");
            tx.setText("Error!Permission");
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;

            cameraSource = new CameraSource.Builder(Scanner_HSD_Activity.this, textRecognizer)
                    .setAutoFocusEnabled(true)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedFps(12.0f)
                    .setRequestedPreviewSize(width, height)
                    .build();

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if(items.size() != 0 ){
                        tx.post(new Runnable() {
                            @Override
                            public void run() {
                                for(int i = moc ; i < items.size(); i++) {
                                    int countNumber = 0;
                                    boolean checkDate2 = false;
                                    TextBlock item = items.valueAt(i);
                                    String mResult = item.getValue().trim();
                                    if(mResult.length()>=6){
                                        for(int ii=0;ii<mResult.length();ii++){
                                            countNumber = mResult.length()-ii;
                                            if(String.valueOf(mResult.charAt(ii)).matches(regexNumber) && countNumber>=6){
                                                for(int iii = (ii+9);iii>=(ii+5);iii--){
                                                    try {
                                                        if(checkDATE(mResult.substring(ii,iii)) != -1){ //!arrDate.contains(new Date_Ex(convertDateTime(mResult.substring(ii,iii),checkDATE(mResult.substring(ii,iii))),mResult.substring(ii,iii)))
                                                            if(DateNumber.indexOf(mResult.substring(ii,iii)) == -1){
                                                                DateNumber.append(" , "+mResult.substring(ii,iii));
                                                                arrDate.add(new Date_Ex(convertDateTime(mResult.substring(ii,iii),checkDATE(mResult.substring(ii,iii))),mResult.substring(ii,iii)));
                                                                checkDate2 = true;
                                                                break;
                                                            }else{
                                                            }
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                if(checkDate2){
                                                    break;
                                                }
                                            }
                                        }
                                        if(arrDate.size()>0 && mOldNum != arrDate.size()){
                                            Time time = new Time();
                                            time.setToNow();
                                            int countCreate = 0;
                                            int countUse = 0;
                                            ArrayList<Date_Ex> arrCreate = new ArrayList<>();
                                            ArrayList<Date_Ex> arrUse = new ArrayList<>();
                                            for(int is = mOldNum ; is <arrDate.size();is++){
                                                if(mdateCreate == 0 && arrDate.get(mOldNum).getDateMili() < time.toMillis(true)){
                                                    mdateCreate = arrDate.get(mOldNum).getDateMili();
                                                    dateCreate = arrDate.get(mOldNum).getDate();
                                                }
                                                else
                                                if(mdateUse == 0 && arrDate.get(mOldNum).getDateMili() > time.toMillis(true)){

                                                    mdateUse = arrDate.get(mOldNum).getDateMili();
                                                    dateUse = arrDate.get(mOldNum).getDate();

                                                    Intent returnIntent = new Intent();
                                                    returnIntent.putExtra("dateChoose",dateUse);
                                                    try {
                                                        returnIntent.putExtra("getType",checkDATE(dateUse));
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    returnIntent.putExtra("miliseconDate",mdateUse);
                                                    setResult(1000,returnIntent);
                                                    finish();

                                                }else{
                                                    if(mdateCreate != 0 && arrDate.get(mOldNum).getDateMili() < time.toMillis(true)){
                                                        countCreate++;
                                                        arrCreate.add(new Date_Ex(mdateCreate,dateCreate));
                                                        arrCreate.add(new Date_Ex(arrDate.get(mOldNum).getDateMili(),arrDate.get(mOldNum).getDate()));
                                                        if(checkDate){
                                                            dialog.dismiss();
                                                        }
                                                    }
                                                    if(mdateUse != 0 && arrDate.get(mOldNum).getDateMili() > time.toMillis(true && !checkDate)){
                                                        countUse++;
                                                        arrCreate.add(new Date_Ex(mdateUse,dateUse));
                                                        arrUse.add(new Date_Ex(arrDate.get(mOldNum).getDateMili(),arrDate.get(mOldNum).getDate()));
                                                        if(checkDate){
                                                            dialog.dismiss();
                                                        }
                                                    }
                                                }
                                            }
                                            if(countCreate!=0)
                                            {
                                                ShowDialog(0,arrCreate);
                                            }
                                            if(countUse!=0)
                                            {
                                                ShowDialog(1,arrUse);
                                            }
                                            mOldNum = arrDate.size();
                                        }
                                    }
                                }

                                String strDaytext = getResources().getString(R.string.nsx)+" "+dateCreate+
                                        "\n"+getResources().getString(R.string.hsd)+ " "+dateUse;
                                tx.setText(strDaytext);

                            }
                            // }
                        });
                    }
                }
            });
        }

    }
    public void ShowDialog(final int type,final ArrayList<Date_Ex> arr){
        checkDate = true;
        String title = "";
        ArrayList<String> arrDate = new ArrayList<>();
        for (Date_Ex data: arr) {
            arrDate.add(data.getDate());
        }
        if(type==0){
            title = getResources().getString(R.string.where_is_expire);
        }else{
            title = getResources().getString(R.string.where_is_expired_time);
        }
        MaterialDialog.Builder buidle =   new MaterialDialog.Builder(Scanner_HSD_Activity.this)
                .limitIconToDefaultSize()
                .title(title)
                .positiveText(getResources().getString(R.string.ok))
                .cancelable(false)
                .items(arrDate)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        dialog.setSelectedIndex(which);
                        return true;
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog2, @NonNull DialogAction which) {
                        if(type == 0){
                            dateCreate = arr.get(dialog2.getSelectedIndex()).getDate();
                            mdateCreate = arr.get(dialog2.getSelectedIndex()).getDateMili();
                        }else{
                            dateUse = arr.get(dialog2.getSelectedIndex()).getDate();
                            mdateUse = arr.get(dialog2.getSelectedIndex()).getDateMili();
                        }
                        String strDaytext = getResources().getString(R.string.nsx)+" "+dateCreate+
                                "\n"+getResources().getString(R.string.hsd)+ " "+dateUse;
                        tx.setText(strDaytext);
                        checkDate = false;
                        dialog.dismiss();
                    }
                });
        dialog = buidle.build();
        dialog.show();
    }





    public long converTypeDate(SimpleDateFormat sdf, String date){
        long timeInMilliseconds = 0;
        try {
            Date mDate = sdf.parse(date);
            timeInMilliseconds = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }
    public int checkDATE(String s) throws Exception
    {
        int num_instance = -1;

        for(int i=0 ; i<regex.length;i++){
            if(s.matches(regex[i])){
                num_instance = i;
                break;
            }

        }
        return num_instance;
    }
    public long convertDateTime(String date,int type){
        long milisecontime = 0;
        switch (type){
            case 0:
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                milisecontime = converTypeDate(sdf,date);
                break;
            case 1:
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("ddMMyyyy");
                milisecontime = converTypeDate(sdf2,date);
                break;
            case 2:
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf3 = new SimpleDateFormat("ddMMyy");
                milisecontime = converTypeDate(sdf3,date);
                break;
            case 3:
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf4 = new SimpleDateFormat("dd/MM/yy");
                milisecontime = converTypeDate(sdf4,date);
                break;
            case 4:
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf5 = new SimpleDateFormat("dd MM yyyy");
                milisecontime = converTypeDate(sdf5,date);
                break;
            case 5:
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf6 = new SimpleDateFormat("dd MM yy");
                milisecontime = converTypeDate(sdf6,date);
                break;
            default:
        }
        return milisecontime;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case perID:
            {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED ){
                        return;
                    }
                    try{
                        cameraSource.start(surfaceView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}