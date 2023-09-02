package com.example.exfch;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Pair;
import android.widget.ImageView;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.Tensor;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class ImageClassifier {
    Context context;
    Interpreter interpreter = null;

    private static final String MODEL_NAME = "saved_model.tflite";

    int modelInputWidth, modelInputHeight, modelInputchannel;
    int modelOutputClasses;

    public ImageClassifier(Context context) {
        this.context = context;
    }

    public void init() throws IOException {
        ByteBuffer model = loadModelFile(MODEL_NAME);
        model.order(ByteOrder.nativeOrder());
        interpreter = new Interpreter(model);

        initModelShape();
    }

    public Pair<Integer, Float> Classify(Bitmap image) {
        ByteBuffer buffer = convertBitmapToGrayByteBuffer(resizeBitmap((image)));

        float[][] result = new float[1][modelOutputClasses];

        interpreter.run(buffer, result);

        return argmax(result[0]);
    }

    private Pair<Integer, Float> argmax(float[] array) {
        int argmax = 0;

        float max = array[0];
        for (int i = 1; i < array.length; i++) {
            float f = array[i];
            if(f > max) {
                argmax = i;
                max = f;
            }
        }

        return new Pair<>(argmax, max);
    }

    private void initModelShape() {
        Tensor inputTensor = interpreter.getInputTensor(0);
        int[] inputShape = inputTensor.shape();
        modelInputchannel = inputShape[0];
        modelInputWidth = inputShape[1];
        modelInputHeight = inputShape[2];

        Tensor outputTensor = interpreter.getOutputTensor(0);
        int[] outputShape = outputTensor.shape();
        modelOutputClasses = outputShape[1];
    }

    private Bitmap resizeBitmap(Bitmap bitmap) {
        return Bitmap.createScaledBitmap(bitmap, modelInputWidth, modelInputHeight, false);
    }

    private ByteBuffer loadModelFile(String modelPath) throws IOException {
        AssetManager am = context.getAssets();
        AssetFileDescriptor afd = am.openFd(modelPath);
        FileInputStream fis = new FileInputStream(afd.getFileDescriptor());
        FileChannel fc = fis.getChannel();
        long startOffset = afd.getStartOffset();
        long declaredLength = afd.getDeclaredLength();

        return fc.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    //ARGB를 BrayScale로 변환 + Bitmap을 ByteBuffer 포맷으로 변환
    private ByteBuffer convertBitmapToGrayByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bitmap.getByteCount());
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for(int pixel : pixels) {
            int r = pixel >> 16 & 0xFF;
            int g = pixel >> 8 & 0xFF;
            int b = pixel & 0xFF;

            float avgPixelValue = (r + g + b) / 3.0f;
            float normalizedPixelValue = avgPixelValue / 255.0f;

            byteBuffer.putFloat(normalizedPixelValue);
        }

        return byteBuffer;
    }

    public void finish() {
        if(interpreter != null) {
            interpreter.close();
        }
    }
}
