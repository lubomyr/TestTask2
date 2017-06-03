package com.probegin.probegin.services;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.probegin.probegin.entities.News;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static com.probegin.probegin.utils.NameSpace.DOMAIN;

public class NewsServiceImpl implements NewsService {
    private int serverPage;
    private int localPage;
    private ListIterator serverPageNewsIterator;
    private Context context;
    private NewsListener newsListener;
    private final int maxItems = 3;
    private List<News> result;
    private String action;
    private int count;
    private int iteratorPos;
    private final String KEY_FIRST = "first";
    private final String KEY_NEXT = "next";
    private final String KEY_PREV = "prev";
    private final String KEY_PAGE = "page";
    private final String KEY_TITLE = "title";
    private final String KEY_SUMMARY = "summary";
    private final String KEY_ACTIONS = "actions";
    private final String KEY_IMAGE = "image";

    public NewsServiceImpl(Context context, NewsListener newsListener) {
        this.context = context;
        this.newsListener = newsListener;
        result = new ArrayList<>();
    }

    public void getNewsFromServer(int serverPage) {
        String url = DOMAIN + "/news/?lcp_page0=" + serverPage;
        final List<News> result = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest req = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String data) {
                        Document doc = Jsoup.parse(data);
                        Elements pageEl = doc.getElementsByClass(KEY_PAGE);
                        Element pageListEl = pageEl.get(0);
                        int size = pageListEl.getElementsByClass(KEY_TITLE).size();
                        for (int i = 0; i < size; i++) {
                            Element titleEl = pageListEl.getElementsByClass(KEY_TITLE).get(i);
                            Element summaryEl = pageListEl.getElementsByClass(KEY_SUMMARY).get(i);
                            Element actionsEl = pageListEl.getElementsByClass(KEY_ACTIONS).get(i);
                            Element imageEl = pageListEl.getElementsByClass(KEY_IMAGE).get(i);
                            String title = titleEl.text();
                            String summary = summaryEl.text();
                            String actions = actionsEl.text();
                            String link = imageEl.children().tagName("a").attr("href");
                            String image = imageEl.children().get(0).childNode(0).attr("src");
                            News news = new News(title, summary, actions, link, image);
                            result.add(news);
                        }
                        serverPageNewsList.clear();
                        serverPageNewsList.addAll(result);
                        setupLocalNewsPage();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // Handle error
                    }
                }
        );
        queue.add(req);
    }


    @Override
    public void getFirstNewsPage() {
        serverPage = 1;
        localPage = 1;
        count = 0;
        action = KEY_FIRST;
        result.clear();
        getNewsFromServer(serverPage);
    }

    @Override
    public void getNextNewsPage() {
        count = 0;
        action = KEY_NEXT;
        result.clear();
        while (serverPageNewsIterator.hasNext() && count < maxItems) {
            News news = (News) serverPageNewsIterator.next();
            result.add(news);
            count++;
        }
        if (result.isEmpty()) {
            getNewsFromServer(serverPage + 1);
        } else
            setupLocalNewsPage();
    }

    @Override
    public void getPrevNewsPage() {
        count = 0;
        action = KEY_PREV;
        result.clear();
        iteratorPos = serverPageNewsIterator.nextIndex();
        if (iteratorPos > maxItems) {
            for (int i = 0; i < iteratorPos; i++) {
                serverPageNewsIterator.previous();
            }
            setupLocalNewsPage();
        } else {
            getNewsFromServer(serverPage - 1);
        }
    }

    @Override
    public int getCurrentPage() {
        return localPage;
    }

    private void setupLocalNewsPage() {
        switch (action) {
            case KEY_FIRST: {
                serverPageNewsIterator = serverPageNewsList.listIterator();
                while (serverPageNewsIterator.hasNext() && count < maxItems) {
                    News news = (News) serverPageNewsIterator.next();
                    result.add(news);
                    count++;
                }
            }
            break;

            case KEY_NEXT: {
                if (result.isEmpty()) {
                    serverPageNewsIterator = serverPageNewsList.listIterator();
                    while (serverPageNewsIterator.hasNext() && count < maxItems) {
                        News news = (News) serverPageNewsIterator.next();
                        result.add(news);
                        count++;
                    }
                    if (!result.isEmpty())
                        serverPage++;
                }
                if (!result.isEmpty())
                    localPage++;
            }
            break;

            case KEY_PREV: {
                if (iteratorPos <= maxItems) {
                    if (!serverPageNewsList.isEmpty())
                        serverPage--;
                    serverPageNewsIterator = serverPageNewsList.listIterator();
                    for (int i = 0; i < serverPageNewsList.size() - maxItems; i++)
                        serverPageNewsIterator.next();
                }
                while (serverPageNewsIterator.hasNext() && count < maxItems) {
                    News news = (News) serverPageNewsIterator.next();
                    result.add(news);
                    count++;
                }
                if (!result.isEmpty())
                    localPage--;
            }
            break;
        }

        newsListener.pageNewsListResult(result);
    }
}
