package com.probegin.probegin;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;

import com.bumptech.glide.Glide;
import com.probegin.probegin.databinding.ActivityExpandedBinding;
import com.probegin.probegin.entities.News;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import static com.probegin.probegin.utils.NameSpace.KEY_NEWS;

public class ExpandedActivity extends AppCompatActivity {
    private ActivityExpandedBinding binding;
    private News news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_expanded);

        news = getData();
        bindViews();
        getExpandedInfo();
    }

    private News getData() {
        Intent intent = getIntent();
        return (News) intent.getSerializableExtra(KEY_NEWS);
    }

    private void bindViews() {
        Glide.with(this).load(news.getImage()).into(binding.image);
        //binding.summary.setText(news.getSummary());
    }

    private void getExpandedInfo() {
        binding.progressBar.setVisibility(View.VISIBLE);
        getExpandedInfoFromServer(news.getUrl());
    }

    private void getExpandedInfoFromServer(String url) {
        ServerTask task = new ServerTask();
        task.url = url;
        task.execute();
    }

    private void parseData(Document doc) {
        Element mainEl = doc.getElementsByTag("main").get(0);
        Element section0El = mainEl.getElementsByTag("section").get(0);
        Element section1El = mainEl.getElementsByTag("section").get(1);
        String titleHtml = removeImgBlock(section0El.html());
        String expHtml = removeImgBlock(section1El.html());
        binding.summary.setText(Html.fromHtml(titleHtml));
        binding.expanded.setText(Html.fromHtml(expHtml));
    }

    private String removeImgBlock(String input) {
        final String imgTag = "<img";
        if (input.contains(imgTag)) {
            int startPos = input.indexOf(imgTag);
            int endPos = input.indexOf(">", startPos);
            String start = input.substring(0, startPos);
            String end = input.substring(endPos + 1, input.length());
            input = start + end;
        }
        return input;
    }

    private class ServerTask extends AsyncTask<Void, Void, Void> {
        String url;
        Document doc = null;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                doc = Jsoup.connect(url).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            binding.progressBar.setVisibility(View.GONE);
            parseData(doc);
        }
    }
}
