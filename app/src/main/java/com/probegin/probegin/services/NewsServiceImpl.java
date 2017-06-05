package com.probegin.probegin.services;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.probegin.probegin.entities.News;
import com.probegin.probegin.utils.TextUtils;

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
    private List<News> serverPageNewsList;
    private List<News> localPageNewsList;
    private ListIterator serverPageNewsIterator;
    private Context context;
    private NewsListener newsListener;
    private int count;
    private int iteratorPos;
    private final int maxItems = 3;

    private enum Action {FIRST, NEXT, PREV}

    ;
    private Action action;

    public NewsServiceImpl(Context context, NewsListener newsListener) {
        this.context = context;
        this.newsListener = newsListener;
        serverPageNewsList = new ArrayList<>();
        localPageNewsList = new ArrayList<>();
    }

    public void getNewsFromServer(int serverPage) {
        String url = DOMAIN + "/news/?lcp_page0=" + serverPage;
        RequestQueue queue = Volley.newRequestQueue(context);
        serverPageNewsList.clear();

        StringRequest req = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String data) {
                        Document doc = Jsoup.parse(data);
                        parseData(doc);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        TextUtils.showMessage(context, volleyError.getMessage());
                        setupLocalNewsPage();
                    }
                }
        );
        queue.add(req);
    }

    private void parseData(Document doc) {
        List<News> items = new ArrayList<>();
        final String KEY_PAGE = "page";
        final String KEY_TITLE = "title";
        final String KEY_SUMMARY = "summary";
        final String KEY_ACTIONS = "actions";
        final String KEY_IMAGE = "image";
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
            items.add(news);
        }
        serverPageNewsList.addAll(items);
        serverPageNewsIterator = serverPageNewsList.listIterator();
        setupLocalNewsPage();
    }


    @Override
    public void getFirstNewsPage() {
        serverPage = 1;
        localPage = 1;
        count = 0;
        action = Action.FIRST;
        localPageNewsList.clear();
        getNewsFromServer(serverPage);
    }

    @Override
    public void getNextNewsPage() {
        count = 0;
        action = Action.NEXT;
        localPageNewsList.clear();
        while (serverPageNewsIterator.hasNext() && (count < maxItems)) {
            News news = (News) serverPageNewsIterator.next();
            localPageNewsList.add(news);
            count++;
        }
        if (localPageNewsList.isEmpty()) {
            getNewsFromServer(serverPage + 1);
        } else
            setupLocalNewsPage();
    }

    @Override
    public void getPrevNewsPage() {
        count = 0;
        action = Action.PREV;
        localPageNewsList.clear();
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
            case FIRST: {
                while (serverPageNewsIterator.hasNext() && (count < maxItems)) {
                    News news = (News) serverPageNewsIterator.next();
                    localPageNewsList.add(news);
                    count++;
                }
            }
            break;

            case NEXT: {
                if (localPageNewsList.isEmpty()) {
                    while (serverPageNewsIterator.hasNext() && (count < maxItems)) {
                        News news = (News) serverPageNewsIterator.next();
                        localPageNewsList.add(news);
                        count++;
                    }
                    if (!localPageNewsList.isEmpty())
                        serverPage++;
                }
                if (!localPageNewsList.isEmpty())
                    localPage++;
            }
            break;

            case PREV: {
                if (iteratorPos <= maxItems) {
                    if (!serverPageNewsList.isEmpty())
                        serverPage--;
                    for (int i = 0; i < serverPageNewsList.size() - maxItems; i++)
                        serverPageNewsIterator.next();
                }
                while (serverPageNewsIterator.hasNext() && (count < maxItems)) {
                    News news = (News) serverPageNewsIterator.next();
                    localPageNewsList.add(news);
                    count++;
                }
                if (!localPageNewsList.isEmpty())
                    localPage--;
            }
            break;
        }

        newsListener.pageNewsListResult(localPageNewsList);
    }
}
