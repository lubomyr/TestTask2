package com.probegin.probegin;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.probegin.probegin.databinding.ActivityExpandedBinding;
import com.probegin.probegin.entities.News;

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
    }

    private News getData() {
        Intent intent = getIntent();
        return (News) intent.getSerializableExtra(KEY_NEWS);
    }

    private void bindViews() {
        Glide.with(this).load(news.getImage()).into(binding.image);
        binding.summary.setText(news.getSummary());
    }
}
