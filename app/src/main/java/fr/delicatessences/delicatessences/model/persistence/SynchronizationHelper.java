package fr.delicatessences.delicatessences.model.persistence;


import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResult;
import com.google.android.gms.auth.api.credentials.IdentityProviders;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import fr.delicatessences.delicatessences.utils.FileUtils;

public class SynchronizationHelper {


    private static final SynchronizationHelper INSTANCE = new SynchronizationHelper();

    public static final String BASE_DB_PATH = "/data/data/fr.delicatessences.delicatessences/databases/";

    private static final String OLD_DB_NAME = "delicatessences.db";

    private static final String OLD_DB_PATH = BASE_DB_PATH + OLD_DB_NAME;

    private static final String BAK_DB_PATH = BASE_DB_PATH + "backup.db";

    private static final String LAST_UPDATE_PREF_BASE_NAME = "LastUpdateTime_";

    private static final String SYNCHRO_PREFERENCES = "SynchronizationPreferences";

    public static final String CUSTOM_METADATA_KEY = "lastUpdateTimeMillis";



    public static boolean oldLocalDatabaseExists(){
        File oldLocalDbFile = getOldDatabaseFile();
        return oldLocalDbFile.exists();
    }


    public static boolean localDatabaseExists(String userId){
        File file = getLocalDatabaseFile(userId);
        return file != null && file.exists();
    }


    public static File getOldDatabaseFile(){
        return new File(OLD_DB_PATH);
    }

    public static File getLocalDatabaseFile(String userId){
        String databaseName = getLocalDatabaseName(userId);
        return new File(databaseName);
    }

    public static void moveToFinalFilename(String userId){
        File oldLocalDbFile = getOldDatabaseFile();
        if (oldLocalDbFile.exists()){
            File dbFileBackup = new File(BAK_DB_PATH);
            File localDbFile = getLocalDatabaseFile(userId);
            if (localDbFile == null){
                throw new IllegalStateException("moveToFinalFilename - unable to determine path of db file");
            }
            FileUtils.copy(oldLocalDbFile, dbFileBackup);
            FileUtils.copy(oldLocalDbFile, localDbFile);
            oldLocalDbFile.delete();
        }

    }


    private static String getUserId(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        }

        return null;
    }



    public static FileDownloadTask downloadRemoteDatabase(String userId){
        File file = getLocalDatabaseFile(userId);

        StorageReference databaseReference = getDatabaseReference(userId);
        return databaseReference.getFile(file);
    }


    public static void uploadDatabase(Context context){
        String userId = getUserId();
        if (userId != null){
            FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
            long lastUpdateTime = getLastUpdateTime(context, userId);

            Bundle extras = new Bundle();
            extras.putLong(UploadJobService.LAST_UPDATE_TIME_EXTRA, lastUpdateTime);
            extras.putString(UploadJobService.USER_ID_EXTRA, userId);
            String tag = UploadJobService.UPLOAD_JOB_TAG + userId;

            Job job = dispatcher.newJobBuilder()
                    .setService(UploadJobService.class)
                    .setTag(tag)
                    .setRecurring(false)
                    .setLifetime(Lifetime.FOREVER)
                    .setTrigger(Trigger.executionWindow(10, 30))
                    .setReplaceCurrent(true)
                    .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                    .setConstraints(
                            Constraint.ON_ANY_NETWORK
                    )
                    .setExtras(extras)
                    .build();

            dispatcher.mustSchedule(job);
        } else {
            FirebaseCrash.report(new IllegalStateException("SynchronizationHelper#uploadDatabase - current user is null, cannot upload."));
        }
    }


    public static void cancelUploadJob(Context context){
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        int result = dispatcher.cancel(UploadJobService.UPLOAD_JOB_TAG);
        Log.i(SynchronizationHelper.class.getName(), "Cancel upload returned " + result);
    }

    public static Task<StorageMetadata> getDatabaseMetaData(String userId){
        StorageReference databaseReference = getDatabaseReference(userId);
        return databaseReference.getMetadata();
    }


    public static long getLastUpdateTime(Context context, String userId){
        if (userId != null){
            SharedPreferences sharedPreferences = context.getSharedPreferences(SYNCHRO_PREFERENCES, Context.MODE_PRIVATE);
            String key = LAST_UPDATE_PREF_BASE_NAME + userId;
            return sharedPreferences.getLong(key, 0);
        }

        return 0;
    }



    public static void saveLastUpdateTime(Context context,  long timeMillis){
        String userId = getUserId();
        if (userId != null){
            SharedPreferences sharedPreferences = context.getSharedPreferences(SYNCHRO_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String key = LAST_UPDATE_PREF_BASE_NAME + userId;
            editor.putLong(key, timeMillis);

            editor.commit();
        } else {
            FirebaseCrash.report(new IllegalStateException("SynchronizationHelper#saveLastUpdateTime - current user is null, cannot save update time."));
        }
    }


    @NonNull
    public static StorageReference getDatabaseReference(@NonNull String userId){
        if (userId == null){
            throw new IllegalStateException("getDatabaseReference - current user UID is null");
        }
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        return storageRef.child(userId + ".db");
    }

    @NonNull
    public static String getLocalDatabaseName(@NonNull String userId) {
        if (userId != null){
            return BASE_DB_PATH + userId + ".db";
        }

        return OLD_DB_NAME;
    }

    @NonNull
    public static String getLocalDatabaseName() {
        String userId = getUserId();
        return getLocalDatabaseName(userId);
    }

    public static Task<Void> deleteRemoteDatabase(String userId){
        StorageReference databaseReference = getDatabaseReference(userId);
        return databaseReference.delete();
    }

    public static void deleteLocalDatabase(String userId){
        File file = getLocalDatabaseFile(userId);
        file.delete();
    }

    public static void reautheticate(GoogleApiClient credentialsClient, CredentialRequest credentialRequest){
        Auth.CredentialsApi.request(credentialsClient, credentialRequest).setResultCallback(
                new ResultCallback<CredentialRequestResult>() {
                    @Override
                    public void onResult(CredentialRequestResult credentialRequestResult) {
                        if (credentialRequestResult.getStatus().isSuccess()) {
                            // See "Handle successful credential requests"
                            Credential credential = credentialRequestResult.getCredential();
                            System.out.println("name : "  + credential.getName());
                            System.out.println("password : "  + credential.getPassword());
                            onCredentialRetrieved(credential);
                            Log.i(SynchronizationHelper.class.getName(), "success credential");
                        } else {
                            // See "Handle unsuccessful and incomplete credential requests"
                            //resolveResult(credentialRequestResult.getStatus());
                            Log.i(SynchronizationHelper.class.getName(), "failure credential");
                        }
                    }
                });


    }


    private static void onCredentialRetrieved(Credential credential) {
        String accountType = credential.getAccountType();
        if (accountType == null) {
            // Sign the user in with information from the Credential.
            //signInWithPassword(credential.getId(), credential.getPassword());
        } else if (accountType.equals(IdentityProviders.GOOGLE)) {
            Log.i(SynchronizationHelper.class.getName(), "google credential");
            // The user has previously signed in with Google Sign-In. Silently
            // sign in the user with the same ID.
            // See https://developers.google.com/identity/sign-in/android/
            /*GoogleSignInOptions gso =
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestEmail()
                            .build();
            GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .setAccountName(credential.getId())
                    .build();
            OptionalPendingResult<GoogleSignInResult> opr =
                    Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);*/
            //
        }
    }

    private void resolveResult(Status status) {
        if (status.getStatusCode() == CommonStatusCodes.RESOLUTION_REQUIRED) {
            // Prompt the user to choose a saved credential; do not show the hint
            // selector.
           /* try {
                //status.startResolutionForResult(this, 0);
            } catch (IntentSender.SendIntentException e) {
                Log.e(SynchronizationHelper.class.getName(), "STATUS: Failed to send resolution.", e);
            }*/
        } else {
            // The user must create an account or sign in manually.
            Log.e(SynchronizationHelper.class.getName(), "STATUS: Unsuccessful credential request.");
        }
    }
}
