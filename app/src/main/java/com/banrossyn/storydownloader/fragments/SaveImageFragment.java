package com.banrossyn.storydownloader.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.banrossyn.storydownloader.FullViewActivity;
import com.banrossyn.storydownloader.adapter.FileListAdapter;
import com.banrossyn.storydownloader.databinding.FragmentDownloadBinding;
import com.banrossyn.storydownloader.model.DownloadedFileModel;
import com.banrossyn.storydownloader.util.Utils;
import com.banrossyn.storydownloader.interfaces.FileListClickInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class SaveImageFragment extends Fragment implements FileListClickInterface {

    FragmentDownloadBinding binding;
    Activity activity;
    private ArrayList<File> fileArrayList;
    private ArrayList<DownloadedFileModel> statusModelArrayList = new ArrayList<>();

    public SaveImageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDownloadBinding.inflate(getLayoutInflater());
        activity = getActivity();

        // initialize views
        initViews();

        return binding.getRoot();
    }

    private void initViews() {

        getAllFiles();

        binding.swipeRefresh.setOnRefreshListener(() -> {

            statusModelArrayList.clear();
            fileArrayList.clear();

            new Handler(Looper.getMainLooper()).postDelayed(this::getAllFiles, 100);


        });


    }

    private void getAllFiles() {

        DownloadedFileModel downloadedFileModel;
        fileArrayList = new ArrayList<>();

        File[] files = new File(Utils.getStoreVideoExternalStorage(activity, Utils.DOWNLOAD_DIRECTORY)).listFiles();

        if (files != null) {

            Arrays.sort(files, (a, b) -> {
                if (a.lastModified() < b.lastModified())
                    return 1;
                if (a.lastModified() > b.lastModified())
                    return -1;
                return 0;
            });

            for (int i = 0; i < files.length; i++) {

                File file = files[i];

                if (files[i].getName().contains("webp") || files[i].getName().contains("jpg") || files[i].getName().contains("jpeg") || files[i].getName().contains("png")) {


                    fileArrayList.add(file);

                    downloadedFileModel = new DownloadedFileModel(file.getName(),
                            Uri.fromFile(file),
                            file.getPath(),
                            file.getName());
                    statusModelArrayList.add(downloadedFileModel);
                }


            }


            if (statusModelArrayList != null && statusModelArrayList.isEmpty()) {
                binding.tvNoResult.setVisibility(View.VISIBLE);
            } else {
                binding.tvNoResult.setVisibility(View.GONE);
            }


            FileListAdapter fileListAdapter = new FileListAdapter(activity, statusModelArrayList, this);

            binding.rvDownload.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));

            binding.rvDownload.setAdapter(fileListAdapter);

        }

        binding.swipeRefresh.setRefreshing(false);
    }

    @Override
    public void getPosition(int position, File file) {
        Intent inNext = new Intent(activity, FullViewActivity.class);
        inNext.putExtra("ImageDataFile", statusModelArrayList);
        inNext.putExtra("Position", position);
        startActivity(inNext);
    }

}