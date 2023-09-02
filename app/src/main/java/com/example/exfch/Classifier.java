package com.example.exfch;

import static org.tensorflow.lite.support.image.ops.ResizeOp.ResizeMethod.NEAREST_NEIGHBOR;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Pair;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.Tensor;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.model.Model;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Classifier {
    Context context;
    private static final String MODEL_NAME = "saved_model.tflite";
    private static final String LABEL_FILE = "labels.txt";
    Interpreter interpreter = null;

    Model model;

    private List<String> labels;

    int modelInputWidth, modelInputHeight, modelInputChannel;
    int modelOutputClasses;

    private static final int INPUT_SIZE = 128; // 입력 이미지 크기
    private static final int NUM_CLASSES = 4; // 클래스 수

    float[][][][] inputArray = new float[1][INPUT_SIZE][INPUT_SIZE][3];

    TensorImage inputImage;
    TensorBuffer outputBuffer;

    public Classifier(Context context){
        this.context = context;
    }

    public void init() throws IOException {
        model = Model.createModel(context, MODEL_NAME);
        initModelShape();
        labels = FileUtil.loadLabels(context, LABEL_FILE);
    }
    private Interpreter getInterpreter() throws IOException {
        ByteBuffer model = loadModelFile(MODEL_NAME);
        model.order(ByteOrder.nativeOrder());
        return new Interpreter(model);
    }

    private ByteBuffer loadModelFile(String modelName) throws IOException {
        AssetManager am = context.getAssets();
        AssetFileDescriptor afd = am.openFd(modelName);
        FileInputStream fis = new FileInputStream(afd.getFileDescriptor());
        FileChannel fc = fis.getChannel();
        long startOffset = afd.getStartOffset();
        long declaredLength = afd.getDeclaredLength();

        return fc.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void initModelShape() {
        Tensor inputTensor = model.getInputTensor(0);
        int[] inputShape = inputTensor.shape();
        modelInputChannel = inputShape[0];
        modelInputWidth = inputShape[1];
        modelInputHeight = inputShape[2];

        inputImage = new TensorImage(inputTensor.dataType());

        Tensor outputTensor = model.getOutputTensor(0);
        outputBuffer = TensorBuffer.createFixedSize(outputTensor.shape(), outputTensor.dataType());
    }

    private Bitmap convertBitmapToARGB8888(Bitmap bitmap) {
        return bitmap.copy(Bitmap.Config.ARGB_8888, true);
    }

    private TensorImage loadImage(final Bitmap bitmap) {
        if(bitmap.getConfig() != Bitmap.Config.ARGB_8888) {
            inputImage.load(convertBitmapToARGB8888(bitmap));
        } else {
            inputImage.load(bitmap);
        }

        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeOp(modelInputWidth, modelInputHeight, NEAREST_NEIGHBOR))
                        .add(new NormalizeOp(0.0f, 255.0f))
                        .build();

        return imageProcessor.process(inputImage);
    }

    private Bitmap resizeBitmap(Bitmap bitmap) {
        return Bitmap.createScaledBitmap(bitmap, modelInputWidth, modelInputHeight, false);
    }

    private ByteBuffer convertBitmapToGrayByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bitmap.getByteCount());
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int pixel : pixels) {
            int r = pixel >> 16 & 0xFF;
            int g = pixel >> 8 & 0xFF;
            int b = pixel & 0xFF;

            float avgPixelValue = (r + g + b) / 3.0f;
            float normalizedPixelValue = avgPixelValue / 255.0f;

            byteBuffer.putFloat(normalizedPixelValue);
        }

        return byteBuffer;
    }

    private Bitmap normalizeImage(Bitmap bitmap, float[][][][] inputArray) {
        for (int i = 0; i < INPUT_SIZE; i++) {
            for (int j = 0; j < INPUT_SIZE; j++) {
                int pixelValue = bitmap.getPixel(i, j);
                inputArray[0][i][j][0] = (Color.red(pixelValue) - 127.5f) / 127.5f;
                inputArray[0][i][j][1] = (Color.green(pixelValue) - 127.5f) / 127.5f;
                inputArray[0][i][j][2] = (Color.blue(pixelValue) - 127.5f) / 127.5f;
            }
        }

        return bitmap;
    }

    public Pair<String, Float> classify(Bitmap image) {
        inputImage = loadImage(image);

        Object[] inputs = new Object[]{inputImage.getBuffer()};
        Map<Integer, Object> outputs = new HashMap<>();
        outputs.put(0, outputBuffer.getBuffer().rewind());

        model.run(inputs, outputs);

        Map<String, Float> output = new TensorLabel(labels, outputBuffer).getMapWithFloatValue();

        return argmax(output);
    }

    private Pair<String, Float> argmax(Map<String, Float> map) {
        String maxKey = "";
        float maxVal = -1;

        for(Map.Entry<String, Float> entry : map.entrySet()) {
            float f = entry.getValue();
            if(f > maxVal) {
                maxKey = entry.getKey();
                maxVal = f;
            }
        }

        return new Pair<> (maxKey, maxVal);
    }

    public void finish() {
        if(model != null)
            model.close();
    }
}
