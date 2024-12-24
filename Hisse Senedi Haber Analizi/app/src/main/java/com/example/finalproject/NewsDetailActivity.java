package com.example.finalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;

public class NewsDetailActivity extends AppCompatActivity {

    private ImageView articleImageView;
    private TextView titleTextView;
    private TextView snippetTextView;
    private TextView publishDateTextView;
    private TextView sourceTextView;
    private TextView sentimentTextView;
    private View sentimentIndicatorView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        articleImageView = findViewById(R.id.newsImage);
        titleTextView = findViewById(R.id.newsTitle);
        snippetTextView = findViewById(R.id.newsDescription);
        publishDateTextView = findViewById(R.id.newsPublishDate);
        sourceTextView = findViewById(R.id.newsSourceName);
        sentimentTextView = findViewById(R.id.sentimentTextView);
        sentimentIndicatorView = findViewById(R.id.sentimentIndicatorView);

        // Gelen Intent'ten article nesnesini al
        Article article = (Article) getIntent().getSerializableExtra("news");

        if (article != null) {
            titleTextView.setText(article.getTitle());
            snippetTextView.setText(article.getSnippet());
            publishDateTextView.setText(AdapterListNews.getDateTime(article.getPublishedAt()));
            sourceTextView.setText(article.getSource());
            Picasso.get().load(article.getImageUrl()).into(articleImageView);
            updateSentimentIndicator(sentimentIndicatorView, sentimentTextView, article.getSentimentScore());
        }
    }

    private void updateSentimentIndicator(View sentimentIndicatorView, TextView sentimentTextView, double sentimentValue) {
        if (sentimentValue >= 0.5) {
            sentimentIndicatorView.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_shape_green));
            sentimentTextView.setText("Positive");
        } else if (sentimentValue <= -0.5) {
            sentimentIndicatorView.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_shape_red));
            sentimentTextView.setText("Negative");
        } else {
            sentimentIndicatorView.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_shape_gray));
            sentimentTextView.setText("Neutral");
        }
    }
}
