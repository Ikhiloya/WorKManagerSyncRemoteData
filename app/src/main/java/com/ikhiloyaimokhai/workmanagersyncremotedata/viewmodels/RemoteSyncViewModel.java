package com.ikhiloyaimokhai.workmanagersyncremotedata.viewmodels;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.ikhiloyaimokhai.workmanagersyncremotedata.db.entity.Book;
import com.ikhiloyaimokhai.workmanagersyncremotedata.repository.BookRepository;
import com.ikhiloyaimokhai.workmanagersyncremotedata.util.Resource;
import com.ikhiloyaimokhai.workmanagersyncremotedata.workers.SyncDataWorker;
import com.ikhiloyaimokhai.workmanagersyncremotedata.workers.WorkerUtils;

import java.util.ArrayList;
import java.util.List;

import static com.ikhiloyaimokhai.workmanagersyncremotedata.util.Constants.SYNC_DATA_WORK_NAME;
import static com.ikhiloyaimokhai.workmanagersyncremotedata.util.Constants.TAG_SYNC_DATA;


public class RemoteSyncViewModel extends AndroidViewModel {
    private BookRepository mRepository;
    private WorkManager mWorkManager;
    // New instance variable for the WorkInfo
    private LiveData<List<WorkInfo>> mSavedWorkInfo;

    private List<Book> books = new ArrayList<>();

    public RemoteSyncViewModel(BookRepository mRepository) {
        super(mRepository.getApplication());
        this.mRepository = mRepository;
        mWorkManager = WorkManager.getInstance(mRepository.getApplication());
        mSavedWorkInfo = mWorkManager.getWorkInfosByTagLiveData(TAG_SYNC_DATA);

    }


    public LiveData<Resource<List<Book>>> loadBooks() {
        return mRepository.loadBooks();
    }


    public void fetchData() {

        // Create Network constraint
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();


        OneTimeWorkRequest syncDataWork =
                new OneTimeWorkRequest.Builder(SyncDataWorker.class)
                        .addTag(TAG_SYNC_DATA)
                        .setConstraints(constraints)
                        .build();

        mWorkManager.enqueueUniqueWork(SYNC_DATA_WORK_NAME, ExistingWorkPolicy.REPLACE, syncDataWork);

    }


    public void setOutputData(String outputData) {
        books = WorkerUtils.fromJson(outputData);
    }


    public List<Book> getOutputData() {
        return books;
    }


    public LiveData<List<WorkInfo>> getOutputWorkInfo() {
        return mSavedWorkInfo;
    }

    /**
     * Cancel work using the work's unique name
     */
    public void cancelWork() {
        Log.i("VIEWMODEL", "Cancelling work");
        mWorkManager.cancelUniqueWork(SYNC_DATA_WORK_NAME);
    }


}
