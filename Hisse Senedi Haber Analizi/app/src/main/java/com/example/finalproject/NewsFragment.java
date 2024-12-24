package com.example.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.databinding.FragmentNewsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment {

    private static final String TAG = "NewsFragment";
    private FragmentNewsBinding binding;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private AdapterListNews adapterListNews;
    private List<Article> positiveArticles;
    private List<Article> negativeArticles;
    private List<Article> neutralArticles;
    private List<Article> allArticles;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNewsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        positiveArticles = new ArrayList<>();
        negativeArticles = new ArrayList<>();
        neutralArticles = new ArrayList<>();
        allArticles = new ArrayList<>();
        adapterListNews = new AdapterListNews(allArticles, getContext());
        recyclerView.setAdapter(adapterListNews);

        // Kategori kartlarına onClickListener ekleyin
        binding.cardBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryClicked(v);
            }
        });

        binding.cardEntertainment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryClicked(v);
            }
        });

        binding.cardHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryClicked(v);
            }
        });

        binding.cardAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryClicked(v);
            }
        });

        binding.ivToolbarCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        db.collection("news")
                .orderBy("publishedat", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Article article = document.toObject(Article.class);
                                double sentimentValue = article.getSentimentScore();
                                allArticles.add(article);
                                if (sentimentValue >= 0.5) {
                                    positiveArticles.add(article);
                                } else if (sentimentValue <= -0.5) {
                                    negativeArticles.add(article);
                                } else {
                                    neutralArticles.add(article);
                                }

                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            adapterListNews.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void categoryClicked(View view) {
        List<Article> filteredArticles = new ArrayList<>();
        switch (view.getId()) {
            case R.id.cardBusiness:
                filteredArticles.addAll(positiveArticles);
                break;
            case R.id.cardEntertainment:
                filteredArticles.addAll(negativeArticles);
                break;
            case R.id.cardHealth:
                filteredArticles.addAll(neutralArticles);
                break;
            case R.id.cardAll:
                filteredArticles.addAll(positiveArticles);
                filteredArticles.addAll(negativeArticles);
                filteredArticles.addAll(neutralArticles);
                break;
            default:
                filteredArticles.addAll(allArticles);
                break;
        }
        adapterListNews.updateArticleList(filteredArticles);
    }

    private void signOut() {
        mAuth.signOut();
        // Clear shared preferences
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("UserPrefs", getActivity().MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(getActivity(), SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the activity stack
        startActivity(intent);
        getActivity().finish();
    }
}
