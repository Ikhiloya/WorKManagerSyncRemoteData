package com.ikhiloyaimokhai.workmanagersyncremotedata.workers;

import android.annotation.SuppressLint;
import android.content.Context;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ikhiloyaimokhai.workmanagersyncremotedata.App;
import com.ikhiloyaimokhai.workmanagersyncremotedata.db.dao.BookDao;
import com.ikhiloyaimokhai.workmanagersyncremotedata.db.entity.Book;
import com.ikhiloyaimokhai.workmanagersyncremotedata.service.BookService;
import com.ikhiloyaimokhai.workmanagersyncremotedata.util.AppExecutors;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static com.ikhiloyaimokhai.workmanagersyncremotedata.util.Constants.KEY_OUTPUT_DATA;


public class SyncDataWorker extends Worker {
    private BookService bookService;
    private BookDao mBookDao;
    private AppExecutors appExecutors;
    private static final String BASE_URL = "https://4124ce61-915b-4590-879e-21956799abf9.mock.pstmn.io/";

    private static final String TAG = SyncDataWorker.class.getSimpleName();

    public SyncDataWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        bookService = App.get().getBookService();
        mBookDao = App.get().getBookDao();
        appExecutors = App.get().getExecutors();


    }

    @NonNull
    @Override
    public Result doWork() {

        Context applicationContext = getApplicationContext();
        //simulate slow work
        WorkerUtils.makeStatusNotification("Fetching Data", applicationContext);
        WorkerUtils.sleep();

        try {
            Call<List<Book>> call = bookService.fetchBooks();
            Response<List<Book>> response = call.execute();

            if (response.isSuccessful() && response.body() != null && !response.body().isEmpty() && response.body().size() > 0) {

                String data = WorkerUtils.toJson(response.body());

                Log.i(TAG, "Json String from network " + data);

                Data outputData = new Data.Builder()
                        .putString(KEY_OUTPUT_DATA, data)
                        .build();

                return Result.success(outputData);
            } else {
                return Result.retry();
            }


        } catch (Throwable e) {
            e.printStackTrace();
            // Technically WorkManager will return Result.failure()
            // but it's best to be explicit about it.
            // Thus if there were errors, we're return FAILURE
            Log.e(TAG, "Error fetching data", e);
            return Result.failure();
        }
    }


    @Override
    public void onStopped() {
        super.onStopped();
        Log.i(TAG, "OnStopped called for this worker");
    }
}
