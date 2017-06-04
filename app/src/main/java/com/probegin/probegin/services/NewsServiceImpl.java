package com.probegin.probegin.services;

import android.content.Context;
import android.os.AsyncTask;

import com.probegin.probegin.entities.News;
import com.probegin.probegin.utils.TextUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
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
    private String action;
    private int count;
    private int iteratorPos;
    private final int maxItems = 3;
    private final String KEY_FIRST = "first";
    private final String KEY_NEXT = "next";
    private final String KEY_PREV = "prev";

    public NewsServiceImpl(Context context, NewsListener newsListener) {
        this.context = context;
        this.newsListener = newsListener;
        serverPageNewsList = new ArrayList<>();
        localPageNewsList = new ArrayList<>();
    }

    private void getNewsFromServer(int serverPage) {
        ServerTask task = new ServerTask();
        task.serverPage = serverPage;
        task.execute();
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
        setupLocalNewsPage();
    }


    @Override
    public void getFirstNewsPage() {
        serverPage = 1;
        localPage = 1;
        count = 0;
        action = KEY_FIRST;
        localPageNewsList.clear();
        getNewsFromServer(serverPage);
    }


    @Override
    public void getNextNewsPage() {
        count = 0;
        action = KEY_NEXT;
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
        action = KEY_PREV;
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
            case KEY_FIRST: {
                serverPageNewsIterator = serverPageNewsList.listIterator();
                while (serverPageNewsIterator.hasNext() && (count < maxItems)) {
                    News news = (News) serverPageNewsIterator.next();
                    localPageNewsList.add(news);
                    count++;
                }
            }
            break;

            case KEY_NEXT: {
                if (localPageNewsList.isEmpty()) {
                    serverPageNewsIterator = serverPageNewsList.listIterator();
                    while (serverPageNewsIterator.hasNext() && count < maxItems) {
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

            case KEY_PREV: {
                if (iteratorPos <= maxItems) {
                    if (!serverPageNewsList.isEmpty())
                        serverPage--;
                    serverPageNewsIterator = serverPageNewsList.listIterator();
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

    private class ServerTask extends AsyncTask<Void, Void, Void> {
        int serverPage;
        Document doc = null;

        @Override
        protected Void doInBackground(Void... params) {
            serverPageNewsList.clear();
            try {
                String url = DOMAIN + "/news/?lcp_page0=" + serverPage;
                doc = Jsoup.connect(url).get();
            } catch (IOException e) {
                e.printStackTrace();
                TextUtils.showMessage(context, e.getMessage());
                setupLocalNewsPage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            parseData(doc);
        }
    }
}
