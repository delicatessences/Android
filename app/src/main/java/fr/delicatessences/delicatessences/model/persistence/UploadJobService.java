package fr.delicatessences.delicatessences.model.persistence;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import fr.delicatessences.delicatessences.model.DatabaseHelper;

public class UploadJobService extends JobService {

    public static final String UPLOAD_JOB_TAG = "upload-";

    public static final String LAST_UPDATE_TIME_EXTRA = "LastUpdateTime";
    public static final String USER_ID_EXTRA = "UserID";


    @Override
    public boolean onStartJob(final JobParameters job) {
        // get local latest update time
        Bundle extras = job.getExtras();
        final long localUpdateTime = extras.getLong(LAST_UPDATE_TIME_EXTRA, 0);
        if (localUpdateTime == 0){
            Crashlytics.log(Log.WARN, "Synchronization", "Invalid local update time.");
            jobFinished(job, false);
        }
        final String userId = extras.getString(USER_ID_EXTRA);
        if (userId == null){
            Crashlytics.log(Log.WARN, "Synchronization", "User ID is null.");
            jobFinished(job, false);
        }

        // get remote update time
        final StorageReference databaseReference = SynchronizationHelper.getDatabaseReference(userId);
        Task<StorageMetadata> metadataTask = databaseReference.getMetadata();
        metadataTask.addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata metadata) {
                // Metadata could be downloaded from the server, compare remote and local update times.
                final String value = metadata.getCustomMetadata(SynchronizationHelper.CUSTOM_METADATA_KEY);
                final long remoteUpdateTime = value != null ? Long.parseLong(value) : 0;

                if (localUpdateTime > remoteUpdateTime) {
                    // local update is newer than remote update time : upldoad!
                    upload(job, databaseReference, localUpdateTime, userId);
                } else {
                    // remote update is newer or equals to local update time : no need to upload.
                    jobFinished(job, false);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // an error occurred while downloading metadata
                int errorCode = ((StorageException) e).getErrorCode();
                switch (errorCode){
                    case StorageException.ERROR_OBJECT_NOT_FOUND:
                        // remote file does not exist yet, upload!
                        upload(job, databaseReference, localUpdateTime, userId);
                        break;

                    case StorageException.ERROR_BUCKET_NOT_FOUND:
                    case StorageException.ERROR_PROJECT_NOT_FOUND:
                    case StorageException.ERROR_NOT_AUTHORIZED:
                    case StorageException.ERROR_NOT_AUTHENTICATED:
                    case StorageException.ERROR_CANCELED:
                    case StorageException.ERROR_QUOTA_EXCEEDED:
                        // should not happen
                        jobFinished(job, false);
                        Crashlytics.log(Log.WARN, "Synchronization", "Error code " + errorCode + " while downloading metadata.");
                        break;

                    case StorageException.ERROR_RETRY_LIMIT_EXCEEDED:
                    case StorageException.ERROR_INVALID_CHECKSUM:
                    case StorageException.ERROR_UNKNOWN:
                        // something went wrong: try again later
                        Log.i(UploadJobService.class.getName(), "Failed to get metadata, retrying later.");
                        jobFinished(job, true);
                        break;
                }
            }
        });

        return true;
    }

    private void upload(final JobParameters job, final StorageReference databaseReference, final long updateTime, final String userId){

        final File databaseFile = SynchronizationHelper.getLocalDatabaseFile(userId);
        if (databaseFile.exists()){
            final Uri file = Uri.fromFile(databaseFile);

            // start uploading...
            UploadTask uploadTask = databaseReference.putFile(file);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // upload finished with success: update metadata accordingly
                    String value = Long.toString(updateTime);
                    StorageMetadata metadata = new StorageMetadata.Builder()
                            .setCustomMetadata(SynchronizationHelper.CUSTOM_METADATA_KEY, value)
                            .build();

                    databaseReference.updateMetadata(metadata)
                            .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                                @Override
                                public void onSuccess(StorageMetadata storageMetadata) {
                                    Log.i(UploadJobService.class.getName(), "Upload successful.");
                                    jobFinished(job, false);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // an error occurred while updating metadata
                                    int errorCode = ((StorageException) e).getErrorCode();
                                    switch (errorCode){
                                        case StorageException.ERROR_OBJECT_NOT_FOUND:
                                        case StorageException.ERROR_BUCKET_NOT_FOUND:
                                        case StorageException.ERROR_PROJECT_NOT_FOUND:
                                        case StorageException.ERROR_NOT_AUTHORIZED:
                                        case StorageException.ERROR_NOT_AUTHENTICATED:
                                        case StorageException.ERROR_QUOTA_EXCEEDED:
                                        case StorageException.ERROR_CANCELED:
                                            // should not happen
                                            jobFinished(job, false);
                                            Crashlytics.log(Log.WARN, "Synchronization", "Error code " + errorCode + " while updating metadata.");
                                            break;

                                        case StorageException.ERROR_RETRY_LIMIT_EXCEEDED:
                                        case StorageException.ERROR_INVALID_CHECKSUM:
                                        case StorageException.ERROR_UNKNOWN:
                                            // something went wrong: try again later
                                            Log.i(UploadJobService.class.getName(), "Failed to update metadata, retrying later.");
                                            jobFinished(job, true);
                                            break;
                                    }
                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // an error occurred while uploading
                    int errorCode = ((StorageException) e).getErrorCode();
                    switch (errorCode){
                        case StorageException.ERROR_OBJECT_NOT_FOUND:
                        case StorageException.ERROR_BUCKET_NOT_FOUND:
                        case StorageException.ERROR_PROJECT_NOT_FOUND:
                        case StorageException.ERROR_NOT_AUTHORIZED:
                        case StorageException.ERROR_NOT_AUTHENTICATED:
                        case StorageException.ERROR_QUOTA_EXCEEDED:
                        case StorageException.ERROR_CANCELED:
                            // should not happen: log it
                            Crashlytics.log(Log.WARN, "Synchronization", "Error code " + errorCode + " while updating database.");
                            jobFinished(job, false);
                            break;

                        case StorageException.ERROR_RETRY_LIMIT_EXCEEDED:
                        case StorageException.ERROR_INVALID_CHECKSUM:
                        case StorageException.ERROR_UNKNOWN:
                            // something went wrong: try again later
                            Log.i(UploadJobService.class.getName(), "Failed to upload database, retrying later.");
                            jobFinished(job, true);
                            break;
                    }
                }
            });
        } else {
            Crashlytics.log(Log.WARN, "Synchronization", "Databse file does not exist.");
        }

    }


    @Override
    public boolean onStopJob(JobParameters job) {
        return true;
    }
}