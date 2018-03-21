package com.example.jeran.splittr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeran.splittr.helper.CircleImageView;
import com.example.jeran.splittr.helper.InternetUtils;
import com.example.jeran.splittr.helper.JsonCallAsync;
import com.example.jeran.splittr.helper.LinkUtils;
import com.example.jeran.splittr.helper.PackageManagerUtils;
import com.example.jeran.splittr.helper.PermissionUtils;
import com.example.jeran.splittr.helper.ResponseBin;
import com.example.jeran.splittr.helper.ResponseListener;
import com.example.jeran.splittr.helper.ToastUtils;
import com.example.jeran.splittr.helper.UtilityMethods;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/* This is the activity where the users can see his expense. */

public class LandingActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static boolean doubleBackToExitPressedOnce = false;

    private NavigationView navigationView;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private FloatingActionMenu fabMenu;
    private TextView navName, navEmail;
    private FloatingActionButton fab1, fab2;
    public static String email = "", name = "";
    private CircleImageView navPic;


    //Initializations for Camera Perms & Image processing
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;
    private static final String CLOUD_VISION_API_KEY = "AIzaSyA_ABH_tP7LaRF8UvSM-cj0voEFWDPh4Zk";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        findViewsById();
        loadUserDetails();
        setUpToolBar();
        setUpFabButton();
        setUpNavigationDrawer();
        setUpHomeFragment();
    }

    private void loadUserDetails() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        LandingActivity.email = sharedPreferences.getString(getString(R.string.USER_EMAIL), "");
        LandingActivity.name = sharedPreferences.getString(getString(R.string.USER_NAME), "");
    }

    private void findViewsById() {
        fabMenu = (FloatingActionMenu) findViewById(R.id.fab);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navPic = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.nav_pic);
        navName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_name);
        navEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_email);
        fab1 = ((FloatingActionButton) findViewById(R.id.fab1));
        fab2 = ((FloatingActionButton) findViewById(R.id.fab2));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.landing_screen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addFriend:
                UtilityMethods.insertFragment(AddFriendsFragment.newInstance(), R.id.content_frame, LandingActivity.this);
                fabMenu.close(true);
                fabMenu.setVisibility(View.GONE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (LandingActivity.doubleBackToExitPressedOnce) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            } else {
                LandingActivity.doubleBackToExitPressedOnce = true;

                Toast.makeText(LandingActivity.this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        LandingActivity.doubleBackToExitPressedOnce = false;
                    }
                }, 3000);
            }
        }
    }

    private void setUpToolBar() {
        setSupportActionBar(toolbar);
    }

    private void setUpFabButton() {
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
    }

    private void setUpNavigationDrawer() {
        Picasso.with(LandingActivity.this)
                .load(LinkUtils.PROFILE_PIC_PATH + LandingActivity.email + ".png")
                .into(navPic);
        navName.setText(LandingActivity.name);
        navEmail.setText(LandingActivity.email);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setUpHomeFragment() {
        UtilityMethods.insertFragment(LandingFragment.newInstance(), R.id.content_frame, LandingActivity.this);
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab1:
                UtilityMethods.insertFragment(EnterBillFragment.newInstance(), R.id.content_frame, LandingActivity.this);
                fabMenu.close(true);
                fabMenu.setVisibility(View.GONE);
                break;
            case R.id.fab2:
                AlertDialog.Builder builder = new AlertDialog.Builder(LandingActivity.this);
                builder
                        .setMessage(R.string.dialog_select_prompt)
                        .setPositiveButton(R.string.dialog_select_gallery, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startGalleryChooser();
                            }
                        })
                        .setNegativeButton(R.string.dialog_select_camera, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startCamera();
                            }
                        });
                builder.create().show();
                fabMenu.close(true);
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
                UtilityMethods.insertFragment(LandingFragment.newInstance(), R.id.content_frame, LandingActivity.this);
                fabMenu.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_feedback:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"jaiswaldiksha07@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback on Splittr App");
                startActivity(Intent.createChooser(intent, "Choose an App to send the email"));
                break;
            case R.id.nav_contact_us:
                Intent intent1 = new Intent(Intent.ACTION_SEND);
                intent1.setType("message/rfc822");
                intent1.putExtra(Intent.EXTRA_EMAIL, new String[]{"jaiswaldiksha07@gmail.com"});
                intent1.putExtra(Intent.EXTRA_SUBJECT, "Splittr App");
                startActivity(Intent.createChooser(intent1, "Choose an App to send the email"));
                break;
            case R.id.nav_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Splittr App");
                startActivity(shareIntent);
                break;
            case R.id.nav_logout:
                showLogOutDialog();
                break;
        }

        // set item as selected to persist highlight
        item.setChecked(true);
        // close drawer when item is tapped
        drawer.closeDrawers();

        // Add code here to update the UI based on the item selected
        // For example, swap UI fragments here

        return true;
    }

    private void showLogOutDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        JSONObject deleteGcmTokenData = new JSONObject();

                        try {
                            deleteGcmTokenData.put("email", LandingActivity.email);
                        } catch (JSONException e) {
                            Log.d("Splittr", e.toString());
                        }

                        ResponseListener responseListener = new ResponseListener() {
                            @Override
                            public void setOnResponseListener(ResponseBin responseBin) {
                                if (responseBin != null && responseBin.getResponse() != null) {
                                    String response = responseBin.getResponse();
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String result = jsonObject.getString("result");

                                        if (result.equals("success")) {
                                            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.putBoolean(getString(R.string.USER_LOGIN), false);
                                            editor.putString(getString(R.string.USER_NAME), null);
                                            editor.putString(getString(R.string.USER_EMAIL), null);
                                            editor.commit();

                                            startActivity(new Intent(LandingActivity.this, LoginActivity.class));
                                            finish();
                                        } else if (result.equals("failed")) {
                                            ToastUtils.showToast(getApplicationContext(), "Unable to log you out from server. Please try again.", false);
                                        }

                                    } catch (JSONException e) {
                                        Toast.makeText(LandingActivity.this, "Error occurred", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        };

                        if (InternetUtils.hasConnection(LandingActivity.this)) {
                            new JsonCallAsync(LandingActivity.this, "deleteGcmTokenRequest", deleteGcmTokenData.toString(), LinkUtils.DELETE_TOKEN_URL, responseListener, true, "GET").execute();
                        } else {
                            ToastUtils.showToast(LandingActivity.this, "Unable to connect. Please check your Internet connection.", false);
                        }


                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    // Code for the Image capture and  Processing starts here

    public void startGalleryChooser() {
        if (PermissionUtils.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                    GALLERY_IMAGE_REQUEST);
        }
    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uploadImage(data.getData());
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            uploadImage(photoUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                    startGalleryChooser();
                }
                break;
        }
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                1200);

                callCloudVision(bitmap);
                //mMainImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                Log.d("Splittr", "Image picking failed because " + e.getMessage());
                Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d("Splittr", "Image picker gave us a null image.");
            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    private void callCloudVision(final Bitmap bitmap) throws IOException {
        // Switch text to loading
        //mImageDetails.setText(R.string.loading_message);
        final ProgressDialog progressDialog = new ProgressDialog(LandingActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer requestInitializer =
                            new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                                /**
                                 * We override this so we can inject important identifying fields into the HTTP
                                 * headers. This enables use of a restricted cloud platform API key.
                                 */
                                @Override
                                protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                                        throws IOException {
                                    super.initializeVisionRequest(visionRequest);

                                    String packageName = getPackageName();
                                    visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                                    String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                                    visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                                }
                            };

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);

                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature desiredFeature = new Feature();
                            desiredFeature.setType("DOCUMENT_TEXT_DETECTION");
//                            Feature desiredFeature = new Feature();
//                            desiredFeature.setType("LABEL_DETECTION");
                            //desiredFeature.setMaxResults(10);
                            add(desiredFeature);
                        }});

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d("Splittr", "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();

                    CapturedItemsFragment capturedItemsFragment = CapturedItemsFragment.newInstance();
                    Bundle args = new Bundle();
                    args.putSerializable("capturedItems", convertToHashMap(response));
                    capturedItemsFragment.setArguments(args);

//                    fabMenu.close(true);
//                    fabMenu.setVisibility(View.GONE);
                    UtilityMethods.insertFragment(capturedItemsFragment, R.id.content_frame, LandingActivity.this);

                } catch (GoogleJsonResponseException e) {
                    Log.d("Splittr", "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d("Splittr", "failed to make API request because of other IOException " +
                            e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }


            protected void onPostExecute(String result) {
                progressDialog.dismiss();

            }
        }.execute();
    }

    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private HashMap<String, Double> convertToHashMap(BatchAnnotateImagesResponse response) {
        int counter = 0;

        String message = "I found these things:\n\n";
        boolean itemStartingPoint = false;
        boolean priceStartingPoint = false;
        HashMap<String, Double> itemsMap = new HashMap<String, Double>();
        List<EntityAnnotation> texts = response.getResponses().get(0).getTextAnnotations();
        ArrayList<String> itemsList = new ArrayList<String>();
        if (texts != null) {
            for (EntityAnnotation text : texts) {

                String input = text.getDescription().trim();
                String lines[] = input.split("\\r?\\n");
                // Log.e(TAG,input.trim());
                //  Log.e(TAG, Integer.toString(lines.length));
                for (String line : lines) {
                    String currentLine = line.trim();
                    if (currentLine.startsWith("Cashier:")) {
                        Log.d("Splittr", currentLine);
                        Log.d("Splittr", Integer.toString(currentLine.length()));
                        itemStartingPoint = true;
                        continue;
                    }
                    if (itemStartingPoint && currentLine.length() <= 4) {
                        Log.d("Splittr", currentLine);
                        Log.d("Splittr", Integer.toString(currentLine.length()));
                        itemStartingPoint = false;
                        priceStartingPoint = true;
                    }
                    if (counter >= itemsList.size()) {
                        priceStartingPoint = false;
                    }
                    if (priceStartingPoint && currentLine.length() <= 4 && counter < itemsList.size()) {
                        Log.d("Splittr", currentLine);
                        Log.d("Splittr", Integer.toString(currentLine.length()));
                        Double cost = Double.parseDouble(currentLine.trim());
                        Log.d("Splittr", itemsList.get(counter));
                        Log.d("Splittr", cost.toString());
                        if (cost <= 0.0) {
                            counter++;
                            continue;
                        }
                        itemsMap.put(itemsList.get(counter++), cost);

                        Log.d("Splittr", "Counter Size -->" + counter + " Itemlist size " + itemsList.size());
                    }

                    if (itemStartingPoint && input.length() > 5) {
                        Log.d("Splittr", currentLine);
                        Log.d("Splittr", Integer.toString(currentLine.length()));
                        String item = currentLine.substring(2, currentLine.length());
                        itemsList.add(item);
                        Log.d("Splittr", "Added ---> " + item);
                        //itemsMap.put(item,0.0);
                    }
                }
                for (String itm : itemsMap.keySet()) {
                    String key = itm.toString();
                    String value = itemsMap.get(itm).toString();
                    Log.d("Splittr", key + " ===== " + value);
                }

                return itemsMap;
            }


        }
        return itemsMap;
    }
}