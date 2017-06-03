package com.probegin.probegin.services;

import android.content.Context;
import android.widget.ProgressBar;

import com.probegin.probegin.entities.News;

import java.util.ArrayList;
import java.util.List;

public interface NewsService {
    List<News> serverPageNewsList = new ArrayList<>();

    void getNewsFromServer(int serverPage);

    void getFirstNewsPage();

    void getNextNewsPage();

    void getPrevNewsPage();

    int getCurrentPage();
}
