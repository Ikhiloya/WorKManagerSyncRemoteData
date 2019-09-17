package com.ikhiloyaimokhai.workmanagersyncremotedata.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.work.WorkInfo;

import com.ikhiloyaimokhai.workmanagersyncremotedata.App;
import com.ikhiloyaimokhai.workmanagersyncremotedata.R;
import com.ikhiloyaimokhai.workmanagersyncremotedata.factory.ViewModelFactory;
import com.ikhiloyaimokhai.workmanagersyncremotedata.repository.BookRepository;
import com.ikhiloyaimokhai.workmanagersyncremotedata.service.BookService;
import com.ikhiloyaimokhai.workmanagersyncremotedata.util.AppExecutors;
import com.ikhiloyaimokhai.workmanagersyncremotedata.viewmodels.RemoteSyncViewModel;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private RemoteSyncViewModel mRemoteSyncViewModel;
    private ProgressBar mProgressBar;
    private Button mCancelButton;
    private Button mOutputButton;
    private Button mGetBookButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGetBookButton = findViewById(R.id.getBookBtn);
        mOutputButton = findViewById(R.id.outputButton);
        mCancelButton = findViewById(R.id.cancelButton);
        mProgressBar = findViewById(R.id.progressBar);


        // Get the ViewModel
        BookService bookService = App.get().getBookService();
        BookRepository mRepository = new BookRepository(getApplication(), MainActivity.this, bookService, new AppExecutors());
        ViewModelFactory factory = new ViewModelFactory(mRepository);
        mRemoteSyncViewModel = ViewModelProviders.of(this, factory).get(RemoteSyncViewModel.class);


        mGetBookButton.setOnClickListener(view -> mRemoteSyncViewModel.fetchData());


        // Show work info, goes inside onCreate()
        mRemoteSyncViewModel.getOutputWorkInfo().observe(this, listOfWorkInfo -> {

            // If there are no matching work info, do nothing
            if (listOfWorkInfo == null || listOfWorkInfo.isEmpty()) {
                return;
            }

            // We only care about the first output status.
            // Every continuation has only one worker tagged TAG_SYNC_DATA
            WorkInfo workInfo = listOfWorkInfo.get(0);
            Log.i(TAG, "WorkState: " + workInfo.getState());
            if (workInfo.getState() == WorkInfo.State.ENQUEUED) {
                showWorkFinished();

                // If there is an output file show "See Output" button
                mRemoteSyncViewModel.setOutputData(App.get().getOutputString());
                mOutputButton.setVisibility(View.VISIBLE);
            } else {
                showWorkInProgress();
            }
        });

        mCancelButton.setOnClickListener(view -> mRemoteSyncViewModel.cancelWork());

        mOutputButton.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, DetailActivity.class));
        });


    }

    /**
     * Shows and hides views for when the Activity is processing an image
     */
    private void showWorkInProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
        mCancelButton.setVisibility(View.VISIBLE);
        mGetBookButton.setVisibility(View.GONE);
        mOutputButton.setVisibility(View.GONE);
    }

    /**
     * Shows and hides views for when the Activity is done processing an image
     */
    private void showWorkFinished() {
        mProgressBar.setVisibility(View.GONE);
        mCancelButton.setVisibility(View.GONE);
        mGetBookButton.setVisibility(View.VISIBLE);
        mOutputButton.setVisibility(View.VISIBLE);
    }
}
