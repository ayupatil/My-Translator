package com.example.mytranslator;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.TextRecognizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;


import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class Main2Activity extends AppCompatActivity {

    ImageButton mic, translate, clear, speak, cam, swap, copy, paste, share, save;
    EditText e1, e2;
    TextToSpeech t1;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    Context context = this;
    String lan1="en", lan2="en";
    int i1,i2;
    String l1,l2;
    private Spinner spinner, translationSpinner;
    DbHandler db;


    private static final String LOG_TAG = "Text API";
    private static final int PHOTO_REQUEST = 10;
    private Uri imageUri;
    private TextRecognizer detector;
    private static final int REQUEST_WRITE_PERMISSION = 20;
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_RESULT = "result";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Intent i = getIntent();


        db=new DbHandler(Main2Activity.this);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        clear = (ImageButton) findViewById(R.id.imageButton);
        cam = (ImageButton) findViewById(R.id.imagebutton1);
        translate = (ImageButton) findViewById(R.id.imageButton2);
        mic = (ImageButton) findViewById(R.id.imageButton3);
        speak = (ImageButton) findViewById(R.id.imageButton4);
        swap = (ImageButton) findViewById(R.id.imageView);
        copy = (ImageButton) findViewById(R.id.imageButton6);
        paste = (ImageButton) findViewById(R.id.imageButton8);
        share = (ImageButton) findViewById(R.id.imagebutton5);
        save = (ImageButton) findViewById(R.id.imageButton7);

        e1 = (EditText) findViewById(R.id.editText);
        e2 = (EditText) findViewById(R.id.editText2);

        spinner = (Spinner) findViewById(R.id.spinner);
        translationSpinner = (Spinner) findViewById(R.id.spinner1);

        if(i!=null)
        {
            e1.setText(i.getStringExtra("oc"));
            e2.setText(i.getStringExtra("tc"));

        }


        if (savedInstanceState != null) {
            imageUri = Uri.parse(savedInstanceState.getString(SAVED_INSTANCE_URI));
            e1.setText(savedInstanceState.getString(SAVED_INSTANCE_RESULT));
        }
        detector = new TextRecognizer.Builder(getApplicationContext()).build();


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,R.array.languages,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        translationSpinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                i1 = position;
                lan1 = getResources().getStringArray(R.array.codes)[i1];
                l1 = getResources().getStringArray(R.array.gcodes)[i1];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        translationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                i2 = position;
                lan2 = getResources().getStringArray(R.array.codes)[i2];
                l2 = getResources().getStringArray(R.array.gcodes)[i2];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(new Locale("en"));
                }
            }
        });




        mic.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                AudioManager audioManger = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                AudioDeviceInfo[] audioDevices = audioManger.getDevices(AudioManager.GET_DEVICES_ALL);
                for(AudioDeviceInfo deviceInfo : audioDevices)
                {

                    if(!(deviceInfo.getType()==AudioDeviceInfo.TYPE_WIRED_HEADPHONES||deviceInfo.getType()==AudioDeviceInfo.TYPE_WIRED_HEADSET))

                    {
                        t1.speak("Please plug your headphones to use this feature!", TextToSpeech.QUEUE_FLUSH, null);
                        Toast.makeText(Main2Activity.this, "Please plug your headphones to use this feature!", Toast.LENGTH_SHORT).show();
                    }

                    else
                    {
                        final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, l1);


                        startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
                    }
                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                e1.setText("");
            }
        });

        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String s = null;
                try {
                    s = Translate(e1.getText().toString(),lan1+"-"+lan2);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                e2.setText(s);

            }
        });

        speak.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {



                t1.setLanguage(new Locale(l2));

                t1.speak(e2.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);

            }
        });

        cam.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                ActivityCompat.requestPermissions(Main2Activity.this, new
                        String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);

            }
        });

        swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setSelection(i2);
                translationSpinner.setSelection(i1);

                String temp = lan1;
                lan1 = lan2;
                lan2 = temp;

            }
        });

        final ClipboardManager myClipboard;
        myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipData myClip;
                String text = e2.getText().toString();
                myClip = ClipData.newPlainText("text", text);
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(Main2Activity.this, "Copied", Toast.LENGTH_SHORT).show();
            }
        });

        paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipData mytext = myClipboard.getPrimaryClip();
                ClipData.Item item = mytext.getItemAt(0);
                e1.setText(item.getText().toString());
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,e2.getText().toString());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.addContent(new Content(e1.getText().toString(),e2.getText().toString()));
                Intent in=new Intent(Main2Activity.this,MainActivity.class);
                startActivity(in);
            }
        });


    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {

                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    e1.setText(e1.getText().toString()+" "+result.get(0));
                }

                String temp = e1.getText().toString();

                e1.setText(temp);



                break;
            }

        }


        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
            launchMediaScanIntent();
            try {
                Bitmap bitmap = decodeBitmapUri(this, imageUri);
                if (detector.isOperational() && bitmap != null) {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> textBlocks = detector.detect(frame);

                    String lines = "";

                    for (int index = 0; index < textBlocks.size(); index++) {
                        //extract scanned text blocks here
                        TextBlock tBlock = textBlocks.valueAt(index);
                        for (Text line : tBlock.getComponents()) {
                            //extract scanned text lines here
                            lines = lines + line.getValue() + "\n";

                        }
                    }
                    if (textBlocks.size() == 0) {
                        e1.setText("Scan Failed: Found nothing to scan");
                    } else {

                        e1.setText(e1.getText() + lines + "\n");
                    }
                } else {
                    e1.setText("Could not set up the detector!");
                }
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT).show();
            }
        }
    }


    String Translate(String textToBeTranslated,String languagePair) throws IOException {

        String jsonString;

        //Set up the translation call URL
        String yandexKey = "trnsl.1.1.20190306T162340Z.fa15e86654f0fcd7.efe2f6ecb8b0abdf15d231ee340901f1d2ea6191";
        String yandexUrl = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=" + yandexKey + "&text=" + textToBeTranslated + "&lang=" + languagePair;
        URL yandexTranslateURL = new URL(yandexUrl);

        //Set Http Conncection, Input Stream, and Buffered Reader
        HttpURLConnection httpJsonConnection = (HttpURLConnection) yandexTranslateURL.openConnection();
        InputStream inputStream = httpJsonConnection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        //Set string builder and insert retrieved JSON result into it
        StringBuilder jsonStringBuilder = new StringBuilder();
        while ((jsonString = bufferedReader.readLine()) != null) {
            jsonStringBuilder.append(jsonString + "\n");
        }

        //Close and disconnect
        bufferedReader.close();
        inputStream.close();
        httpJsonConnection.disconnect();

        //Making result human readable
        String resultString = jsonStringBuilder.toString().trim();
        resultString = resultString.substring(resultString.indexOf('[')+1);
        resultString = resultString.substring(0,resultString.indexOf("]"));
        resultString = resultString.substring(resultString.indexOf("\"")+1);
        resultString = resultString.substring(0,resultString.indexOf("\""));

        return resultString;


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    Toast.makeText(Main2Activity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }



    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "picture.jpg");
        imageUri = FileProvider.getUriForFile(Main2Activity.this,
                BuildConfig.APPLICATION_ID + ".provider", photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PHOTO_REQUEST);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (imageUri != null) {
            outState.putString(SAVED_INSTANCE_URI, imageUri.toString());
            outState.putString(SAVED_INSTANCE_RESULT, e1.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        int targetW = 600;
        int targetH = 600;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeStream(ctx.getContentResolver()
                .openInputStream(uri), null, bmOptions);
    }


}
