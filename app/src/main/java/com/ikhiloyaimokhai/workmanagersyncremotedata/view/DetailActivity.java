package com.ikhiloyaimokhai.workmanagersyncremotedata.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.Log;

import com.ikhiloyaimokhai.workmanagersyncremotedata.App;
import com.ikhiloyaimokhai.workmanagersyncremotedata.R;
import com.ikhiloyaimokhai.workmanagersyncremotedata.db.entity.Book;
import com.ikhiloyaimokhai.workmanagersyncremotedata.factory.ViewModelFactory;
import com.ikhiloyaimokhai.workmanagersyncremotedata.repository.BookRepository;
import com.ikhiloyaimokhai.workmanagersyncremotedata.service.BookService;
import com.ikhiloyaimokhai.workmanagersyncremotedata.util.AppExecutors;
import com.ikhiloyaimokhai.workmanagersyncremotedata.viewmodels.RemoteSyncViewModel;

import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private RemoteSyncViewModel mRemoteSyncViewModel;
    private static final String TAG = DetailActivity.class.getSimpleName();
    private BookRepository mRepository;
    private BookService bookService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        bookService = App.get().getBookService();
        mRepository = new BookRepository(getApplication(), DetailActivity.this, bookService, new AppExecutors());
        ViewModelFactory factory = new ViewModelFactory(mRepository);
        mRemoteSyncViewModel = ViewModelProviders.of(this, factory).get(RemoteSyncViewModel.class);


        List<Book> books = mRemoteSyncViewModel.getOutputData();
        Log.i(TAG, "Books: " + books.toString());

    }
}
