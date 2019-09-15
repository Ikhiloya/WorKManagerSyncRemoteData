package com.ikhiloyaimokhai.workmanagersyncremotedata.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.work.Data;
import androidx.work.WorkInfo;

import com.ikhiloyaimokhai.workmanagersyncremotedata.App;
import com.ikhiloyaimokhai.workmanagersyncremotedata.R;
import com.ikhiloyaimokhai.workmanagersyncremotedata.db.entity.Book;
import com.ikhiloyaimokhai.workmanagersyncremotedata.factory.ViewModelFactory;
import com.ikhiloyaimokhai.workmanagersyncremotedata.repository.BookRepository;
import com.ikhiloyaimokhai.workmanagersyncremotedata.service.BookService;
import com.ikhiloyaimokhai.workmanagersyncremotedata.util.AppExecutors;
import com.ikhiloyaimokhai.workmanagersyncremotedata.util.Constants;
import com.ikhiloyaimokhai.workmanagersyncremotedata.viewmodels.RemoteSyncViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private RemoteSyncViewModel mRemoteSyncViewModel;
    private BookService bookService;
    private BookRepository mRepository;
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


        TextView tv1 = findViewById(R.id.tv1);
        TextView tv2 = findViewById(R.id.tv2);
        TextView tv3 = findViewById(R.id.tv3);


        // Get the ViewModel
        bookService = App.get().getBookService();
        mRepository = new BookRepository(getApplication(), MainActivity.this, bookService, new AppExecutors());
        ViewModelFactory factory = new ViewModelFactory(mRepository);
        mRemoteSyncViewModel = ViewModelProviders.of(this, factory).get(RemoteSyncViewModel.class);


//        mGetBookButton.setOnClickListener(view -> {
//                    tv1.setText("");
//                    tv2.setText("");
//                    tv3.setText("");
//                    mRemoteSyncViewModel.fetchData();
//                }
//        );
        mRemoteSyncViewModel.fetchData();


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
                Data outputData = workInfo.getOutputData();
//
                String outputString = outputData.getString(Constants.KEY_OUTPUT_DATA);
                System.out.println("+++++++++++++++++STRING" + outputString);
//
//                // If there is an output file show "See File" button
//                if (!TextUtils.isEmpty(outputString)) {
                    mRemoteSyncViewModel.setOutputData(App.get().getOutputString());
                    mOutputButton.setVisibility(View.VISIBLE);
//                }
            } else {
                showWorkInProgress();

            }


//            boolean finished = workInfo.getState().isFinished();
////            Log.i(TAG, "isFinished: " + finished);
//            Log.i(TAG, "WorkState: " + workInfo.getState());
//            if (!finished) {
//                showWorkInProgress();
//            } else {
//                showWorkFinished();
//                Data outputData = workInfo.getOutputData();
////
//                String outputString = outputData.getString(Constants.KEY_OUTPUT_DATA);
////
////                // If there is an output file show "See File" button
//                if (!TextUtils.isEmpty(outputString)) {
//                    mRemoteSyncViewModel.setOutputData(outputString);
//                    mOutputButton.setVisibility(View.VISIBLE);
//                }
//            }
        });

        mCancelButton.setOnClickListener(view -> mRemoteSyncViewModel.cancelWork());

        mOutputButton.setOnClickListener(view -> {
            List<Book> books = mRemoteSyncViewModel.getOutputData();
            tv1.setText(
                    new StringBuilder()
                            .append("Id- ")
                            .append(books.get(0).getId())
                            .append(" Title- ")
                            .append(books.get(0).getTitle())
                            .append(" Genre- ")
                            .append(books.get(0).getGenre())
                            .append(" Author- ")
                            .append(books.get(0).getAuthor())
                            .toString());

            tv2.setText(
                    new StringBuilder()
                            .append("Id- ")
                            .append(books.get(1).getId())
                            .append(" Title- ")
                            .append(books.get(1).getTitle())
                            .append(" Genre- ")
                            .append(books.get(1).getGenre())
                            .append(" Author- ")
                            .append(books.get(1).getAuthor())
                            .toString());


            tv3.setText(
                    new StringBuilder()
                            .append("Id- ")
                            .append(books.get(2).getId())
                            .append(" Title- ")
                            .append(books.get(2).getTitle())
                            .append(" Genre- ")
                            .append(books.get(2).getGenre())
                            .append(" Author- ")
                            .append(books.get(2).getAuthor())
                            .toString());
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
