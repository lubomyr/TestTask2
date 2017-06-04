package com.probegin.probegin;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.probegin.probegin.adapters.NewsAdapter;
import com.probegin.probegin.databinding.ActivityMainBinding;
import com.probegin.probegin.entities.News;
import com.probegin.probegin.services.NewsListener;
import com.probegin.probegin.services.NewsService;
import com.probegin.probegin.services.NewsServiceImpl;

import java.util.List;

import static com.probegin.probegin.utils.NameSpace.KEY_NEWS;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        NewsAdapter.OnItemClickListener, NewsListener {
    private ActivityMainBinding binding;
    private NewsAdapter newsAdapter;
    private NewsService newsService;
    private boolean permitActions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        newsService = new NewsServiceImpl(this, this);
        bindButtons();
        bindNewsAdapter();
        permitActions = true;
        if (newsService.getCurrentPage() == 0)
            getFirstNewsPage();
    }

    @Override
    public void onClick(View v) {
        if (permitActions) {
            binding.progressBar.setVisibility(View.VISIBLE);
            permitActions = false;
            switch (v.getId()) {
                case R.id.next:
                    newsService.getNextNewsPage();
                    break;
                case R.id.prev:
                    newsService.getPrevNewsPage();
                    break;
            }
        }
    }

    @Override
    public void pageNewsListResult(List<News> pageNewsList) {
        binding.page.setText(String.valueOf(newsService.getCurrentPage()));
        binding.prev.setVisibility((newsService.getCurrentPage() != 1) ? View.VISIBLE : View.INVISIBLE);
        binding.next.setVisibility(!pageNewsList.isEmpty() ? View.VISIBLE : View.INVISIBLE);
        if (!pageNewsList.isEmpty()) {
            newsAdapter.setList(pageNewsList);
            newsAdapter.notifyDataSetChanged();
        }
        binding.progressBar.setVisibility(View.GONE);
        permitActions = true;
    }

    @Override
    public void onItemClick(News item) {
        Intent intent = new Intent(this, ExpandedActivity.class);
        intent.putExtra(KEY_NEWS, item);
        startActivity(intent);
    }

    private void bindButtons() {
        binding.prev.setOnClickListener(this);
        binding.next.setOnClickListener(this);
    }

    private void bindNewsAdapter() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new NewsAdapter(this, R.layout.item_news);
        binding.recyclerView.setAdapter(newsAdapter);
        newsAdapter.setOnItemClickListener(this);
    }

    private void getFirstNewsPage() {
        binding.progressBar.setVisibility(View.VISIBLE);
        newsService.getFirstNewsPage();
    }
}
