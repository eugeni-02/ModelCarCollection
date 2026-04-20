package com.example.modelcarcollection;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.modelcarcollection.database.CarDatabase;
import com.example.modelcarcollection.model.Car;
import com.google.common.util.concurrent.ListenableFuture;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ScanActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final String API_KEY = BuildConfig.GEMINI_API_KEY;

    private PreviewView previewView;
    private TextView scanStatus, resultConfidence;
    private LinearLayout resultCard;
    private Button btnCapture;
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;

    private String detectedName = "";
    private String detectedDetail = "";
    private String detectedValue = "";
    private String detectedBrand = "";
    private String detectedScale = "";
    private String detectedYear = "";
    private String detectedColor = "";
    private String detectedYearOfRelease = "";

    private boolean carIdentified = false;

    private android.widget.EditText editName, editBrand, editScale, editYear,
            editYearOfRelease, editColor, editValue;

    private String capturedPhotoPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        previewView = findViewById(R.id.camera_preview);
        scanStatus = findViewById(R.id.scan_status);
        resultCard = findViewById(R.id.result_card);
        resultConfidence = findViewById(R.id.result_confidence);
        btnCapture = findViewById(R.id.btn_capture);
        editName = findViewById(R.id.edit_name);
        editBrand = findViewById(R.id.edit_brand);
        editScale = findViewById(R.id.edit_scale);
        editYear = findViewById(R.id.edit_year);
        editYearOfRelease = findViewById(R.id.edit_year_of_release);
        editColor = findViewById(R.id.edit_color);
        editValue = findViewById(R.id.edit_value);

        cameraExecutor = Executors.newSingleThreadExecutor();

        findViewById(R.id.btn_cancel).setOnClickListener(v -> finish());

        btnCapture.setOnClickListener(v -> {
            if (carIdentified) {
                saveCar();
            } else {
                takePhoto();
            }
        });

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                int rotation = getWindowManager().getDefaultDisplay().getRotation();
                imageCapture = new ImageCapture.Builder()
                        .setTargetRotation(rotation)
                        .build();

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this,
                        CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture);
            } catch (Exception e) {
                Toast.makeText(this, "Camera error: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhoto() {
        if (imageCapture == null) return;

        scanStatus.setText("Analyzing...");
        btnCapture.setEnabled(false);

        File photoFile = new File(getFilesDir(), "car_" + System.currentTimeMillis() + ".jpg");
        ImageCapture.OutputFileOptions options =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(options, cameraExecutor,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        identifyWithGemini(photoFile);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        runOnUiThread(() -> {
                            scanStatus.setText("Photo failed. Try again.");
                            btnCapture.setEnabled(true);
                        });
                    }
                });
        capturedPhotoPath = photoFile.getAbsolutePath();
    }

    private void identifyWithGemini(File photoFile) {
        try {
            // Read and encode photo
            FileInputStream fis = new FileInputStream(photoFile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) baos.write(buffer, 0, len);
            fis.close();
            String base64Image = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);

            // Build prompt
            String prompt = "You are an expert model car identifier. Look at this image and identify the model car. " +
                    "Respond ONLY with a JSON object in this exact format, no other text:\n" +
                    "{\n" +
                    "  \"name\": \"Full model name e.g. Ferrari 250 GTO\",\n" +
                    "  \"brand\": \"Manufacturer e.g. Hot Wheels\",\n" +
                    "  \"scale\": \"Scale e.g. 1:64\",\n" +
                    "  \"year\": \"Year of real car e.g. 1962\",\n" +
                    "  \"color\": \"Color e.g. Red\",\n" +
                    "  \"yearOfRelease\": \"Year this model was released e.g. 2003\",\n" +
                    "  \"marketValue\": \"Estimated market value e.g. $25-$60\",\n" +
                    "  \"confidence\": \"Match confidence e.g. 94%\"\n" +
                    "}";

            // Build Gemini request body
            JSONObject textPart = new JSONObject();
            textPart.put("text", prompt);

            JSONObject inlineData = new JSONObject();
            inlineData.put("mime_type", "image/jpeg");
            inlineData.put("data", base64Image);

            JSONObject imagePart = new JSONObject();
            imagePart.put("inline_data", inlineData);

            JSONArray parts = new JSONArray();
            parts.put(imagePart);
            parts.put(textPart);

            JSONObject content = new JSONObject();
            content.put("parts", parts);

            JSONArray contents = new JSONArray();
            contents.put(content);

            JSONObject requestBody = new JSONObject();
            requestBody.put("contents", contents);

            // Send request to Gemini
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY;

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("content-type", "application/json")
                    .post(RequestBody.create(
                            requestBody.toString(),
                            MediaType.parse("application/json")))
                    .build();

            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                runOnUiThread(() -> {
                    if (response.code() == 429) {
                        scanStatus.setText("Too many requests. Please wait and try again.");
                    } else if (response.code() == 503) {
                        scanStatus.setText("AI servers are busy. Please try again in a few seconds.");
                    } else {
                        scanStatus.setText("API error " + response.code() + ": " + errorBody);
                    }
                    btnCapture.setEnabled(true);
                });
                return;
            }
            String responseBody = response.body().string();

            // Parse Gemini response
            JSONObject json = new JSONObject(responseBody);
            String text = json
                    .getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

            // Clean and parse JSON
            text = text.trim();
            if (text.startsWith("```")) {
                text = text.replaceAll("```json", "").replaceAll("```", "").trim();
            }

            JSONObject result = new JSONObject(text);
            detectedName = result.optString("name", "Unknown");
            detectedBrand = result.optString("brand", "Unknown");
            if (detectedBrand.toLowerCase().contains("hot wheels")) {
                detectedBrand = "Hot Wheels";
            }
            detectedScale = result.optString("scale", "Unknown");
            detectedYear = result.optString("year", "Unknown");
            detectedColor = result.optString("color", "Unknown");
            detectedYearOfRelease = result.optString("yearOfRelease", "Unknown");
            detectedValue = result.optString("marketValue", "Unknown");
            String confidence = result.optString("confidence", "");
            detectedDetail = detectedYear + " · " + detectedBrand + " · " + detectedColor;

            runOnUiThread(() -> showResult(confidence));

        } catch (Exception e) {
            String errorMsg = e.getMessage();
            runOnUiThread(() -> {
                if (errorMsg != null && errorMsg.contains("429")) {
                    scanStatus.setText("Too many requests. Please wait a minute and try again.");
                } else {
                    scanStatus.setText("Error: " + errorMsg);
                }
                btnCapture.setEnabled(true);
            });
        }
    }

    private void showResult(String confidence) {
        editName.setText(detectedName);
        editBrand.setText(detectedBrand);
        editScale.setText(detectedScale);
        editYear.setText(detectedYear);
        editYearOfRelease.setText(detectedYearOfRelease);
        editColor.setText(detectedColor);
        editValue.setText(detectedValue);
        resultConfidence.setText(confidence);
        resultCard.setVisibility(View.VISIBLE);
        scanStatus.setText("Car identified! Edit if needed.");
        btnCapture.setText("Add to Collection");
        btnCapture.setEnabled(true);
        carIdentified = true;
    }

    private void saveCar() {
        Car car = new Car();
        car.name = editName.getText().toString().trim();
        car.brand = editBrand.getText().toString().trim();
        car.scale = editScale.getText().toString().trim();
        car.year = editYear.getText().toString().trim();
        car.yearOfRelease = editYearOfRelease.getText().toString().trim();
        car.color = editColor.getText().toString().trim();
        car.marketValue = editValue.getText().toString().trim();
        car.trend = "";
        car.dateAdded = System.currentTimeMillis();
        car.imageUrl = capturedPhotoPath;

        Executors.newSingleThreadExecutor().execute(() -> {
            CarDatabase.getInstance(this).carDao().insert(car);
            runOnUiThread(() -> {
                Toast.makeText(this, car.name + " added!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}