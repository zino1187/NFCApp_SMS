package com.example.zino.nfcapp;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    String TAG=this.getClass().getName();
    NfcAdapter nfcAdapter;
    SmsManager smsManager;

    TextView txt_msg;

    /*sms 관련*/
    String toNum="010-2867-9055";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt_msg = (TextView)findViewById(R.id.txt_msg);

        /*새로운 액티비티를 계속 띄우는지 확인*/
        Log.d(TAG,"onCreate()"+this);

        checkNfc();
        getDataFromTag();
        smsManager = SmsManager.getDefault();
    }

    /*-----------------------------------------------------------------
     nfc 사용이 절대적으로 중요하지 않을 경우엔 AndroidManifest.xml
     에서 처리하지 않고, 코드상에서 지원여부를 체크해도 된다.
    -----------------------------------------------------------------*/
    public void checkNfc(){
        nfcAdapter=NfcAdapter.getDefaultAdapter(this);

        if(nfcAdapter == null){
            Toast.makeText(this,"이 디바이스는 nfc를 지원하지 않습니다",Toast.LENGTH_LONG).show();
            finish();
        }else{
            Toast.makeText(this,"nfc 사용 가능",Toast.LENGTH_LONG).show();
        }
    }

    public void getDataFromTag(){
        Intent intent = getIntent();
        Log.d(TAG, "Intent is "+intent);

        Parcelable[] parcelables=intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        if(parcelables == null){
            return;
        }
        //bar.setVisibility(View.GONE);

        for(int a=0;a<parcelables.length;a++){
            NdefMessage ndefMessage=(NdefMessage)parcelables[a];

            NdefRecord[] records=ndefMessage.getRecords();

            for(int i=0;i<records.length;i++){
                String data=decode(records[i].getPayload());
                txt_msg.append(data+"\n");
                if(data.equals("mommy")){
                    /*문자 보내기*/
                    smsManager.sendTextMessage(toNum, null, "your chid has arrived school", null, null);
                }
            }
        }

    }

    public String decode(byte[] buf) {
        String strText = "";
        String textEncoding = ((buf[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
        int langCodeLen = buf[0] & 0077;

        try {
            strText = new String(buf, langCodeLen + 1, buf.length - langCodeLen - 1, textEncoding);
        } catch (Exception e) {
            Log.d("tag1", e.toString());
        }
        return strText;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode ==2){
            if(permissions.length > 0 && grantResults[0]!=PackageManager.PERMISSION_DENIED){
                sendSMS(toNum,"아이가 학교에 도착했어요");
            }else{
                Toast.makeText(this,"SMS 권한을 부여해야 사용이 가능합니다",Toast.LENGTH_LONG).show();
            }
        }
    }


    /*문자 전송 메서드*/
    public void send(){
        if(checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED){
            String[] permissions={Manifest.permission.SEND_SMS};

            requestPermissions(permissions, 2);
        }else{
            sendSMS(toNum,"아이가 학교에 도착했어요");
        }
    }


    public void sendSMS(String dest, String text){
        smsManager.sendTextMessage(dest, null,text, null, null);
    }


    @Override
    protected void onStart() {
        super.onStart();
        //Log.d(TAG,"onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume()");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.d(TAG,"onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Log.d(TAG,"onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Log.d(TAG,"onDestroy()");
    }
}
