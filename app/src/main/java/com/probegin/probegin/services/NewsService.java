package com.probegin.probegin.services;

public interface NewsService {

    void getFirstNewsPage();

    void getNextNewsPage();

    void getPrevNewsPage();

    int getCurrentPage();
}
