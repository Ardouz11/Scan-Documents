package com.ScanDocuments;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.util.List;
import java.util.regex.Pattern;

public class SearchForPatterns {

/* This part for processing SimCard*/
static void runTextRecognitionSimCard(Bitmap mSelectedImage) {

        InputImage image = InputImage.fromBitmap(mSelectedImage, 0);
        TextRecognizer recognizer = TextRecognition.getClient();
       // mSimCardButton.setEnabled(false);
        recognizer.process(image)
                .addOnSuccessListener(
                        new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text texts) {
                               // mSimCardButton.setEnabled(true);
                              //  Log.d("The full text ",texts.getText().toString());
                                SearchForPatterns.processTextRecognitionResultSimCard(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                               // mSimCardButton.setEnabled(true);
                                e.printStackTrace();
                            }
                        });
    }
     static void processTextRecognitionResultSimCard(Text texts) {

        List<Text.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
           // showToast("No text found");
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
    }
    /* This part for processing IDCard */
    static void runTextRecognition(Bitmap mSelectedImage) {

        InputImage image = InputImage.fromBitmap(mSelectedImage, 0);
        TextRecognizer recognizer = TextRecognition.getClient();
       // mIDCardButton.setEnabled(false);
        recognizer.process(image)
                .addOnSuccessListener(
                        new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text texts) {
                             //   mIDCardButton.setEnabled(true);
                                Log.d("The full text ",texts.getText().toString());
                                SearchForPatterns.processTextRecognitionResult(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                               // mIDCardButton.setEnabled(true);
                                e.printStackTrace();
                            }
                        });
    }
     static List<Text.Line> processLines(List<Text.Line> lines){
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
     static List<Text.Line> testOfLength(List<Text.Line> lines){
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
     static void processTextRecognitionResult(Text texts) {

        List<Text.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
          //  showToast("No text found");
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
                if(flag){
                    if(Pattern.matches("[0-9].*[0-9]",lines.get(j).getText())){
                        flag=false;
                        Log.i("DOB is ",lines.get(j).getText().toString());
                    }
                }
                /* This one for getting CIN */
                if(Pattern.matches("[A-Z].*[0-9].*",lines.get(j).getText())){
                    Log.i("CIN is ",lines.get(j).getText().toString());
                }
                /* This one for getting LName and FName */
                if (Pattern.matches("[A-Z].*[A-Z]", lines.get(j).getText())) {
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

        }}
    /* This part is for processing back side of the ID Card */
    static void runTextRecognitionBackSide(Bitmap bitmap) {

        InputImage image = InputImage.fromBitmap(bitmap, 0);

        TextRecognizer recognizer = TextRecognition.getClient();
      //  mIDCardButton.setEnabled(false);
        recognizer.process(image)
                .addOnSuccessListener(
                        new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text texts) {
                              //  mIDCardButton.setEnabled(true);
                                Log.d("The full text ",texts.getText().toString());
                                processTextRecognitionResultBackSide(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                //mIDCardButton.setEnabled(true);
                                e.printStackTrace();
                            }
                        });
    }

     static void processTextRecognitionResultBackSide(Text texts) {

        List<Text.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            //showToast("No text found");
            return;
        }
        for (int i = 1; i < blocks.size(); i++) {
            List<Text.Line> lines = blocks.get(i).getLines();
            lines=processLines(lines);

            for (int j = 0; j < lines.size(); j++) {
                List<Text.Element> elements = lines.get(j).getElements();
                        /*
                        This patterns for matching Address  */

                if(Pattern.matches("Adresse.*",lines.get(j).getText())){
                    Log.i("Adresse is ",lines.get(j).getText().toString().replace("Adresse","").toUpperCase());
                }


            }
        }

    }


}
