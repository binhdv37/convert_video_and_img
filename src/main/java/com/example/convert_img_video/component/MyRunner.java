package com.example.convert_img_video.component;

import io.github.techgnious.IVCompressor;
import io.github.techgnious.dto.ResizeResolution;
import io.github.techgnious.dto.VideoFormats;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class MyRunner implements CommandLineRunner {

    private final ExecutorService executorService = Executors.newFixedThreadPool(100);

    private final IVCompressor compressor = new IVCompressor();

    private final int NUMBER_OF_VIDEO = 2;

    private final String inputFilePath = "D:\\a\\1080p_video.mp4";
    private final String outputPath = "D:\\";

    @Override
    public void run(String... args) throws Exception {
        Path path = Paths.get(inputFilePath);

        byte[] in = Files.readAllBytes(path);

        List<Runnable> listTask = new ArrayList<>();

        for (int i = 0; i < NUMBER_OF_VIDEO; i++) {
            String outputPath = this.outputPath + i + ".mp4";
            byte[] tmp = in.clone();
            listTask.add(() -> convertAndSave(tmp, outputPath));
        }

        System.out.println("- Start convert " + NUMBER_OF_VIDEO + " video");

        // execute
        listTask.forEach(executorService::execute);
    }

    private void convertAndSave(byte[] file, String outputPath) {
        try {
            System.out.println("- Start : " + outputPath);
            byte[] out = compressor.reduceVideoSize(file, VideoFormats.MP4, ResizeResolution.R720P);
            Path outPath = Paths.get(outputPath);
            Files.write(outPath, out);
            System.out.println("- Done : " + outputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
