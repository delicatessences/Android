package fr.delicatessences.delicatessences.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageMetadata;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import fr.delicatessences.delicatessences.BuildConfig;
import fr.delicatessences.delicatessences.R;
import fr.delicatessences.delicatessences.model.persistence.SynchronizationHelper;
import fr.delicatessences.delicatessences.utils.ProgressModalDialogHolder;

import static fr.delicatessences.delicatessences.model.persistence.SynchronizationHelper.CUSTOM_METADATA_KEY;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    private static final int TYPE_MASK = 0x3;
    private static final int ID_MASK = 0xFFFFFFFC;
    private int extraId;
    private int extraType;
    private String extraTag;

    private ProgressModalDialogHolder mProgressHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w(LoginActivity.class.getName(), "onCreate.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mProgressHolder = new ProgressModalDialogHolder(this);

        Intent intent = getIntent();
        if (intent != null){
            String action = intent.getAction();
            if (Intent.ACTION_VIEW.equals(action)){
                // deep link
                String data = intent.getDataString();
                int startOfId = data.lastIndexOf("-") + 1;
                int endOfId = data.lastIndexOf(".");
                try{
                    int id = Integer.parseInt(data.substring(startOfId, endOfId));
                    // split the id in two : a element id and a element type
                    extraId = (id & ID_MASK) >>> 2;
                    extraType = id & TYPE_MASK;
                    extraTag = MainActivity.TAG_DEEP_LINK;
                } catch (NumberFormatException e){
                    extraId = 0;
                    extraType = 0;
                    extraTag = MainActivity.TAG_DEFAULT;
                }
            }
            else {
                extraId = 0;
                extraType = 0;
                extraTag = MainActivity.TAG_DEFAULT;
            }
        }


        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            Log.w(LoginActivity.class.getName(), "current user != null.");
            // already signed in: start main activity
            startFlow(currentUser.getUid());
            return;
        } else {
            // not signed in, start sign in flow
            int mode = AppCompatDelegate.MODE_NIGHT_YES;
            AppCompatDelegate.setDefaultNightMode(mode);
            getDelegate().setLocalNightMode(mode);


            AuthUI authUI = AuthUI.getInstance();
            // create the intent
            AuthUI.SignInIntentBuilder builder = authUI.createSignInIntentBuilder();
            builder.setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build()));
            builder.setIsSmartLockEnabled(false);
            builder.setTheme(R.style.LoginTheme);
            builder.setLogo(R.drawable.logo_login);
            intent = builder.build();

            startActivityForResult(intent, RC_SIGN_IN);
        }

    }

    private void startFlow(final String userId){
        Log.w(LoginActivity.class.getName(), "Start flow.");
        int mode = AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(mode);
        getDelegate().setLocalNightMode(mode);
        mProgressHolder.showLoadingDialog(R.string.synchronization);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storage.setMaxDownloadRetryTimeMillis(20000);
        storage.setMaxOperationRetryTimeMillis(10000);
        storage.setMaxUploadRetryTimeMillis(120000);
        if (SynchronizationHelper.oldLocalDatabaseExists()){
            SynchronizationHelper.moveToFinalFilename(userId);
        }

        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected){
            if (SynchronizationHelper.localDatabaseExists(userId)){
                Log.i(LoginActivity.class.getName(), "There is a local database.");
                Task<StorageMetadata> metadataTask = SynchronizationHelper.getDatabaseMetaData(userId);
                metadataTask.addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(final StorageMetadata metadata) {
                        Log.i(LoginActivity.class.getName(), "Metadata ok: compare update times.");
                        final String value = metadata.getCustomMetadata(CUSTOM_METADATA_KEY);
                        final long remoteTimeMillis = value != null ? Long.parseLong(value) : 0L;
                        long localTimeMillis = SynchronizationHelper.getLastUpdateTime(LoginActivity.this, userId);

                        // compare local and remote last update time
                        if (remoteTimeMillis > localTimeMillis){
                            Log.i(LoginActivity.class.getName(), "Remote database is newer than local one: download.");
                            FileDownloadTask downloadTask = SynchronizationHelper.downloadRemoteDatabase(userId);
                            downloadTask.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    // remote base is newer than local one.
                                    // remote update time becomes new local update time.
                                    Log.i(LoginActivity.class.getName(), "Database downloaded: save update time.");
                                    SynchronizationHelper.saveLastUpdateTime(LoginActivity.this, remoteTimeMillis);
                                    startMainActivity();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    int errorCode = ((StorageException) exception).getErrorCode();
                                    switch (errorCode){
                                        case StorageException.ERROR_OBJECT_NOT_FOUND:
                                        case StorageException.ERROR_BUCKET_NOT_FOUND:
                                        case StorageException.ERROR_PROJECT_NOT_FOUND:
                                        case StorageException.ERROR_NOT_AUTHORIZED:
                                        case StorageException.ERROR_NOT_AUTHENTICATED:
                                        case StorageException.ERROR_CANCELED:
                                        case StorageException.ERROR_QUOTA_EXCEEDED:
                                        case StorageException.ERROR_INVALID_CHECKSUM:
                                        case StorageException.ERROR_UNKNOWN:
                                            // should not happen: log it
                                            Crashlytics.log(Log.WARN, "Login", "Error code " + errorCode + " while downloading database.");
                                            break;

                                        case StorageException.ERROR_RETRY_LIMIT_EXCEEDED:
                                            // no internet?
                                            Log.i(LoginActivity.class.getName(), "Failed to download database.");
                                            break;
                                    }
                                    startMainActivity();
                                }
                            });
                        } else {
                            if (localTimeMillis > remoteTimeMillis){
                                // local base is newer than remote one, upload!
                                Log.i(LoginActivity.class.getName(), "Local database is newer than remote one: upload.");
                                SynchronizationHelper.uploadDatabase(LoginActivity.this);
                            }
                            startMainActivity();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int errorCode = ((StorageException) e).getErrorCode();
                        switch (errorCode){
                            case StorageException.ERROR_OBJECT_NOT_FOUND:
                                Log.i(LoginActivity.class.getName(), "No remote database. Upload for the first time.");
                                // a local base but no remote base : upload!
                                SynchronizationHelper.uploadDatabase(LoginActivity.this);
                                break;

                            case StorageException.ERROR_BUCKET_NOT_FOUND:
                            case StorageException.ERROR_PROJECT_NOT_FOUND:
                            case StorageException.ERROR_NOT_AUTHORIZED:
                            case StorageException.ERROR_NOT_AUTHENTICATED:
                            case StorageException.ERROR_CANCELED:
                            case StorageException.ERROR_QUOTA_EXCEEDED:
                            case StorageException.ERROR_INVALID_CHECKSUM:
                            case StorageException.ERROR_UNKNOWN:
                                // should not happen
                                Crashlytics.log(Log.WARN, "Login", "Error code " + errorCode + " while downloading metadata.");
                                break;

                            case StorageException.ERROR_RETRY_LIMIT_EXCEEDED:
                                // no internet?
                                Log.i(LoginActivity.class.getName(), "Failed to get metadata.");
                                break;
                        }
                        startMainActivity();
                    }
                });
            } else {
                Log.i(LoginActivity.class.getName(), "No local database.");
                final File localFile = SynchronizationHelper.getLocalDatabaseFile(userId);
                try {
                    localFile.createNewFile();
                    FileDownloadTask downloadTask = SynchronizationHelper.downloadRemoteDatabase(userId);
                    downloadTask.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // no local base but a remote base.
                            Log.i(LoginActivity.class.getName(), "Database downloaded.");
                            Task<StorageMetadata> metadataTask = SynchronizationHelper.getDatabaseMetaData(userId);
                            metadataTask.addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                                @Override
                                public void onSuccess(StorageMetadata metadata) {
                                    // remote update time becomes new local update time.
                                    Log.i(LoginActivity.class.getName(), "Metadata ok: save update time.");
                                    String value = metadata.getCustomMetadata(SynchronizationHelper.CUSTOM_METADATA_KEY);
                                    long remoteTimeMillis = value != null ? Long.parseLong(value) : 0L;
                                    SynchronizationHelper.saveLastUpdateTime(LoginActivity.this, remoteTimeMillis);
                                    startMainActivity();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    int errorCode = ((StorageException) e).getErrorCode();
                                    switch (errorCode) {
                                        case StorageException.ERROR_OBJECT_NOT_FOUND:
                                        case StorageException.ERROR_BUCKET_NOT_FOUND:
                                        case StorageException.ERROR_PROJECT_NOT_FOUND:
                                        case StorageException.ERROR_NOT_AUTHORIZED:
                                        case StorageException.ERROR_NOT_AUTHENTICATED:
                                        case StorageException.ERROR_CANCELED:
                                        case StorageException.ERROR_QUOTA_EXCEEDED:
                                        case StorageException.ERROR_INVALID_CHECKSUM:
                                        case StorageException.ERROR_UNKNOWN:
                                            // should not happen
                                            Crashlytics.log(Log.WARN, "Login", "Error code " + errorCode + " while downloading metadata (no local db).");
                                            break;

                                        case StorageException.ERROR_RETRY_LIMIT_EXCEEDED:
                                            // lost internet?
                                            Log.i(LoginActivity.class.getName(), "Failed to get metadata.");
                                            break;
                                    }
                                    startMainActivity();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            int errorCode = ((StorageException) exception).getErrorCode();
                            switch (errorCode) {
                                case StorageException.ERROR_OBJECT_NOT_FOUND:
                                    // no remote base: first install
                                    break;

                                case StorageException.ERROR_BUCKET_NOT_FOUND:
                                case StorageException.ERROR_PROJECT_NOT_FOUND:
                                case StorageException.ERROR_NOT_AUTHORIZED:
                                case StorageException.ERROR_NOT_AUTHENTICATED:
                                case StorageException.ERROR_CANCELED:
                                case StorageException.ERROR_QUOTA_EXCEEDED:
                                case StorageException.ERROR_INVALID_CHECKSUM:
                                case StorageException.ERROR_UNKNOWN:
                                    // should not happen
                                    Crashlytics.log(Log.WARN, "Login", "Error code " + errorCode + " while downloading database (no local db).");
                                    break;

                                case StorageException.ERROR_RETRY_LIMIT_EXCEEDED:
                                    // no internet?
                                    break;
                            }
                            startMainActivity();
                        }
                    });
                } catch (IOException e) {
                    Crashlytics.log(Log.WARN, "Login", "Error creating database file..");
                    startMainActivity();
                }
            }
        } else {
            Log.i(LoginActivity.class.getName(), "No internet connection.");
            startMainActivity();
        }
    }


    private void startMainActivity(){
        Log.w(LoginActivity.class.getName(), "Main activity started.");
        startActivity(createIntent());
        finish();
    }


    private Intent createIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_CLASS, extraTag);
        intent.putExtra(MainActivity.EXTRA_ID, extraId);
        intent.putExtra(MainActivity.EXTRA_VIEW_TYPE, extraType);

        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.w(LoginActivity.class.getName(), "onActivityResult " + requestCode + " " + resultCode);

        // sign in ?
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            int viewId = R.id.loginLayout;

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = auth.getCurrentUser();
                startFlow(currentUser.getUid());
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    showSnackbar(viewId, R.string.sign_in_cancelled);
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackbar(viewId, R.string.no_internet_connection);
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackbar(viewId, R.string.unknown_error);
                    return;
                }
            }

            showSnackbar(viewId, R.string.unknown_error);
        }
    }


    private void showSnackbar(int viewId, int messageId){
        Snackbar mySnackbar = Snackbar.make(findViewById(viewId), messageId, Snackbar.LENGTH_SHORT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProgressHolder.dismissDialog();
    }


    @Override
    protected void onResume() {
        super.onResume();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            startFlow(currentUser.getUid());
        }
    }
}

