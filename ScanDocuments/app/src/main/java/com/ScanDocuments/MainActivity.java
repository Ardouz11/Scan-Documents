// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.ScanDocuments;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ImageView mImageView;
    private Button mIDCardButton;
    private Button mSimCardButton;
    private Bitmap mSelectedImage;
    // Max width (portrait mode)
    private Integer mImageMaxWidth;
    // Max height (portrait mode)
    private Integer mImageMaxHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.image_view);

        mIDCardButton = findViewById(R.id.button_idcard);
        mSimCardButton = findViewById(R.id.button_simcard);

        mIDCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchForPatterns.runTextRecognitionBackSide(mSelectedImage);
            }
        });
        mSimCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchForPatterns.runTextRecognitionSimCard(mSelectedImage);
            }
        });
        Spinner dropdown = findViewById(R.id.spinner);
        String[] items = new String[]{"Test Image 1 (Text)", "Test Image 2 (Face)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout
                .simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);
    }
/* This part for processing SimCard
    private void runTextRecognitionSimCard() {

            InputImage image = InputImage.fromBitmap(mSelectedImage, 0);
            TextRecognizer recognizer = TextRecognition.getClient();
        mSimCardButton.setEnabled(false);
            recognizer.process(image)
                    .addOnSuccessListener(
                            new OnSuccessListener<Text>() {
                                @Override
                                public void onSuccess(Text texts) {
                                    mSimCardButton.setEnabled(true);
                                    Log.d("The full text ",texts.getText().toString());
                                    processTextRecognitionResultSimCard(texts);
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Task failed with an exception
                                    mSimCardButton.setEnabled(true);
                                    e.printStackTrace();
                                }
                            });
        }
    private void processTextRecognitionResultSimCard(Text texts) {

            List<Text.TextBlock> blocks = texts.getTextBlocks();
            if (blocks.size() == 0) {
                showToast("No text found");
                return;
            }
            boolean flag_serial=true;
            for (int i = 0; i < blocks.size(); i++) {
                List<Text.Line> lines = blocks.get(i).getLines();
                for (int j = 0; j < lines.size(); j++) {
                    List<Text.Element> elements = lines.get(j).getElements();
                    for (int k = 0; k < elements.size(); k++) {

                        if(elements.get(k).getText().length()>=10 && elements.get(k).getText().matches("[0-9]+")){

                            if(elements.get(k).getText().length()==10) {
                                Log.d("Tele Number ", elements.get(k).getText().toString());


                            }
                            else if(flag_serial) {
                                flag_serial=false;
                                Log.d("Serial Number ", elements.get(k).getText().toString());
                            }
                        }

                }}
            }
    }*/

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    // Functions for loading images from app assets.

    // Returns max image width, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private Integer getImageMaxWidth() {
        if (mImageMaxWidth == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxWidth = mImageView.getWidth();
        }

        return mImageMaxWidth;
    }

    // Returns max image height, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private Integer getImageMaxHeight() {
        if (mImageMaxHeight == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxHeight =
                    mImageView.getHeight();
        }

        return mImageMaxHeight;
    }

    // Gets the targeted width / height.
    private Pair<Integer, Integer> getTargetedWidthHeight() {
        int targetWidth;
        int targetHeight;
        int maxWidthForPortraitMode = getImageMaxWidth();
        int maxHeightForPortraitMode = getImageMaxHeight();
        targetWidth = maxWidthForPortraitMode;
        targetHeight = maxHeightForPortraitMode;
        return new Pair<>(targetWidth, targetHeight);
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
       // mGraphicOverlay.clear();
        switch (position) {
            case 0:
                mSelectedImage = getBitmapFromAsset(this, "Simcard_1.jpg");
                break;
            case 1:
                // Whatever you want to happen when the third item gets selected
                mSelectedImage = getBitmapFromAsset(this, "Backside.jpg");
                break;
        }
        if (mSelectedImage != null) {
            // Get the dimensions of the View
            Pair<Integer, Integer> targetedSize = getTargetedWidthHeight();

            int targetWidth = targetedSize.first;
            int maxHeight = targetedSize.second;

            // Determine how much to scale down the image
            float scaleFactor =
                    Math.max(
                            (float) mSelectedImage.getWidth() / (float) targetWidth,
                            (float) mSelectedImage.getHeight() / (float) maxHeight);

            Bitmap resizedBitmap =
                    Bitmap.createScaledBitmap(
                            mSelectedImage,
                            (int) (mSelectedImage.getWidth() / scaleFactor),
                            (int) (mSelectedImage.getHeight() / scaleFactor),
                            true);

            mImageView.setImageBitmap(resizedBitmap);
            mSelectedImage = resizedBitmap;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream is;
        Bitmap bitmap = null;
        try {
            is = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

/* This part for processing IDCard
private void runTextRecognition() {

    InputImage image = InputImage.fromBitmap(mSelectedImage, 0);
    TextRecognizer recognizer = TextRecognition.getClient();
    mIDCardButton.setEnabled(false);
    recognizer.process(image)
            .addOnSuccessListener(
                    new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text texts) {
                            mIDCardButton.setEnabled(true);
                            Log.d("The full text ",texts.getText().toString());
                            processTextRecognitionResult(texts);
                        }
                    })
            .addOnFailureListener(
                    new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Task failed with an exception
                            mIDCardButton.setEnabled(true);
                            e.printStackTrace();
                        }
                    });
}
    private List<Text.Line> processLines(List<Text.Line> lines){
        boolean tag=false;
        for(int i=0;i<lines.size();i++){
            if(Pattern.matches(".*MAROC",lines.get(i).getText()) ||Pattern.matches("CARTE.*",lines.get(i).getText())
                    || Pattern.matches(".*[àä].*",lines.get(i).getText())
                    ||Pattern.matches("[a-z].*",lines.get(i).getText())||Pattern.matches("[0-9]\\s.*",lines.get(i).getText())
                    ||Pattern.matches(".*[~!@#$%^&*()_+'{}\\\\[\\\\]:;,<>?-].*",lines.get(i).getText())


            ){
                tag=true;
            }
            if(tag){
                lines.remove(i);

            }
        }
        return  lines;
    }
    private List<Text.Line> testOfLength(List<Text.Line> lines){
        int length=0;
        for(int i=0;i<lines.size();i++){
            for(int k=0;k<lines.get(i).getElements().size();k++){
                length=+lines.get(i).getElements().get(k).getText().length();
            }
            if(length<3||lines.get(i).getElements().size()>2){
                lines.remove(i);
            }

        }
        return lines;

    }
    private void processTextRecognitionResult(Text texts) {

        List<Text.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            showToast("No text found");
            return;
        }
        boolean flag=true,flag_name=true;
        for (int i = 1; i < blocks.size(); i++) {
            List<Text.Line> lines = blocks.get(i).getLines();
            lines=processLines(lines);
            lines= testOfLength(lines);

            for (int j = 0; j < lines.size(); j++) {
                List<Text.Element> elements = lines.get(j).getElements();
                        /*
                        This patterns for matching DOB  */
                /*if(flag){
                    if(Pattern.matches("[0-9].*[0-9]",lines.get(j).getText())){
                        flag=false;
                        Log.i("DOB is ",lines.get(j).getText().toString());
                    }
                }*/
                /* This one for getting CIN */
               /* if(Pattern.matches("[A-Z].*[0-9].*",lines.get(j).getText())){
                    Log.i("CIN is ",lines.get(j).getText().toString());
                }*/
                /* This one for getting LName and FName */
              /*  if (Pattern.matches("[A-Z].*[A-Z]", lines.get(j).getText())) {
                    if(flag_name) {
                        Log.i("FName is ", lines.get(j).getText().toString());
                        flag_name=false;
                    }
                    else {
                        Log.i("LName is ", lines.get(j).getText().toString());
                        flag_name=true;
                    }
                }
            }

        }}*/
        /* This part is for processing back side of the ID Card
        private void runTextRecognitionBackSide(Bitmap bitmap) {

            InputImage image = InputImage.fromBitmap(bitmap, 0);

            TextRecognizer recognizer = TextRecognition.getClient();
            mIDCardButton.setEnabled(false);
            recognizer.process(image)
                    .addOnSuccessListener(
                            new OnSuccessListener<Text>() {
                                @Override
                                public void onSuccess(Text texts) {
                                    mIDCardButton.setEnabled(true);
                                    Log.d("The full text ",texts.getText().toString());
                                    processTextRecognitionResultBackSide(texts);
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Task failed with an exception
                                    mIDCardButton.setEnabled(true);
                                    e.printStackTrace();
                                }
                            });
        }

    private void processTextRecognitionResultBackSide(Text texts) {

        List<Text.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            showToast("No text found");
            return;
        }
        for (int i = 1; i < blocks.size(); i++) {
            List<Text.Line> lines = blocks.get(i).getLines();
            lines=processLines(lines);

            for (int j = 0; j < lines.size(); j++) {
                List<Text.Element> elements = lines.get(j).getElements();
                        /*
                        This patterns for matching Address  */

                  /*  if(Pattern.matches("Adresse.*",lines.get(j).getText())){
                        Log.i("Adresse is ",lines.get(j).getText().toString().replace("Adresse","").toUpperCase());
                    }


                }
            }

        }*/
}


