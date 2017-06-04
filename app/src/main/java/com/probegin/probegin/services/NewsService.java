package com.probegin.probegin.services;

public interface NewsService {

    void getNewsFromServer(int serverPage);

    void getFirstNewsPage();

    void getNextNewsPage();

    void getPrevNewsPage();

    int getCurrentPage();
}
