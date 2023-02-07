package com.example.provacippi;

import static android.provider.MediaStore.Images.Media.getBitmap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.provacippi.ml.SavedModel384;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity2 extends AppCompatActivity{

    private ImageView imageView;
    Bitmap b = null;
    String[] labels =new String[6];
    String result;
    TextView nome;
    TextView descrizione;
    TextView curiosità;
    ImageView cassonetto;


    @Override
    protected void onCreate(Bundle savedInstanceState) { //mostra l'immagine scattata e/o presa dalla libreria
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        imageView = findViewById(R.id.imageView2);
        Uri imageUri = (Uri) getIntent().getExtras().get("uri");
        nome = findViewById(R.id.result);
        descrizione = findViewById(R.id.descrizione);
        curiosità = findViewById(R.id.curiosita);

        //label per il risultato
        try {
            int i=0;
            BufferedReader buff= new BufferedReader(new InputStreamReader(getAssets().open("labels.txt")));
            String line;
            while ((line = buff.readLine()) != null) {
               labels[i] = line;
               i++;
            }
            buff.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            b = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            imageView.setImageURI(imageUri);
            //imageView.setImageBitmap(handleSamplingAndRotationBitmap(this, imageUri));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onPredictButton(View view) throws IOException {
        try {
            SavedModel384 model = SavedModel384.newInstance(this.getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 384, 384, 3}, DataType.FLOAT32);

            //scalare le immagini nella dimensione di input della rete
            Bitmap resizedBitmap= this.resizeBitmap(b, 384, 384);
            TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
            tensorImage.load(resizedBitmap);

            //preprocessing: normalizzazione 0-255
            this.preProcessingImage(tensorImage);
            inputFeature0.loadBuffer(tensorImage.getBuffer()); //BUFFER ( Argomento) Bitmap.createScaledBitmap())

            SavedModel384.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            //scelta del valore massimo di probabilità restituito
            int max = this.getMax(outputFeature0.getFloatArray());
            result= labels[max];
            Risultato r = new Risultato(result);

            nome.setText(r.getLabel());
            descrizione.setText(r.getDescrizione());
            curiosità.setText(r.getFrase());
            // Releases model resources if no longer used.
            model.close();

        } catch (IOException e) {
            // TODO Handle the exception
        }

    }



    private Bitmap resizeBitmap(Bitmap bitmap, int width, int height){
        return Bitmap.createScaledBitmap(bitmap, width, height, false);

    }

    private TensorImage preProcessingImage(TensorImage tensorImage){
        ImageProcessor.Builder imageProcessor= new ImageProcessor.Builder().add(new NormalizeOp(0.0f, 255.0f));
        ImageProcessor img= imageProcessor.build();
        img.process(tensorImage);
        return tensorImage;
    }

    private int getMax(float[] arr){
        int index=0;
        float max= 0;
        int len= arr.length -1;
        for(int i=0; i<len; i++){
            if(arr[i]> max){
                index= i;
                max= arr[i];
            }

        }
        return index;

    }

    private Bitmap handleSamplingAndRotationBitmap(Context context, Uri uri) throws IOException {
        //uso delle options della BitmapFactory per gestire le dimensioni dell'immagine (operazioni di decoding)
        BitmapFactory.Options options = new  BitmapFactory.Options();

        //decoding: lettura delle dimensioni dell'immagine prima della costruzione e della allocazione della bitmap
        options.inJustDecodeBounds= true;
        InputStream imageStream= context.getContentResolver().openInputStream(uri);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        //valuto se l'immagine è troppo grande o pesante rispetto alle dimensioni 384x384. Se cos' fosse
        //con calculate in sample size ottengo un'immagine più piccola

        options.inSampleSize= calculateInSampleSize(options, 384, 384);

        //decodifica dell'immagine gestita concorrentemente con le opzioni di riferimento
        options.inJustDecodeBounds= false;
        imageStream = context.getContentResolver().openInputStream(uri);
        Bitmap picture = BitmapFactory.decodeStream(imageStream, null, options);

        //rotazione dell'immagine se necessaria
        picture= rotateIfRequired(context, picture, uri);
        return picture;


    }

    private int calculateInSampleSize(BitmapFactory.Options options, float recWidth, float recHeight){
        //recupero delle dimensioni delle immagini
        float height = options.outHeight;
        float width = options.outWidth;
        float inSampleSize = 1;

        //se l'immagine è troppo grande calcolo i rapporti di cui sono scalate
        if(height > recHeight || width > recWidth){
            float heightRatio= height/ recHeight; //potrebbe esserci un problema perchè forse devono essere float
            float widthRatio= width/ recWidth;

            //scelgo il rapporto minore come valore di inSampleSize
            if(heightRatio < widthRatio){
                inSampleSize= heightRatio;
            }else{
                inSampleSize=widthRatio;
            }


        }
        return (int) inSampleSize;
    }

    private Bitmap rotateIfRequired(Context context, Bitmap picture, Uri pictureUri) throws IOException {
        InputStream input = context.getContentResolver().openInputStream(pictureUri);
        ExifInterface ei = new ExifInterface(input);
        switch (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)){
            case ExifInterface.ORIENTATION_ROTATE_90 :
                return rotate(picture, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(picture, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(picture, 270);
            default:
                return picture;
        }
    }

    private Bitmap rotate(Bitmap picture, float degree){
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap res= Bitmap.createBitmap(picture, 0, 0, picture.getWidth(), picture.getHeight(), matrix, true);
        picture.recycle();
        return res;
    }















}
