package com.probegin.probegin.services;

import com.probegin.probegin.entities.News;

import java.util.List;

public interface NewsListener {
    void pageNewsListResult(List<News> pageNewsList);
}
