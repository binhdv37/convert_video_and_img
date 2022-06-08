package com.example.convert_img_video.component;

import io.github.techgnious.IVCompressor;
import io.github.techgnious.dto.ResizeResolution;
import io.github.techgnious.dto.VideoFormats;
import org.mp4parser.Box;
import org.mp4parser.IsoFile;
import org.mp4parser.boxes.iso14496.part12.MovieBox;
import org.mp4parser.boxes.iso14496.part12.TrackBox;
import org.mp4parser.boxes.iso14496.part12.TrackHeaderBox;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class MyRunner implements CommandLineRunner {

    private final ExecutorService executorService = Executors.newFixedThreadPool(100);

    private final int NUMBER_OF_VIDEO = 10;

    private final String outputPath = "D:\\";

    @Override
    public void run(String... args) throws Exception {
        extractVideoFile();

//        File file = ResourceUtils.getFile("classpath:video/1080p_video.mp4");
//        FileInputStream is = new FileInputStream(file);
//
//        byte[] in = is.readAllBytes();
//
//        List<Runnable> listTask = new ArrayList<>();
//
//        for (int i = 0; i < NUMBER_OF_VIDEO; i++) {
//            String outputPath = this.outputPath + i + ".mp4";
//            byte[] tmp = in.clone();
//            listTask.add(() -> convertAndSave(tmp, outputPath));
//        }
//
//        System.out.println("- Start convert " + NUMBER_OF_VIDEO + " video");
//
//        // execute
//        listTask.forEach(executorService::execute);
    }

    private void convertAndSave(byte[] file, String outputPath) {
        try {
            IVCompressor compressor = new IVCompressor();
            System.out.println("- Start : " + outputPath + " at " + new Date());
            byte[] out = compressor.reduceVideoSize(file, VideoFormats.MP4, ResizeResolution.R720P);
            Path outPath = Paths.get(outputPath);
            Files.write(outPath, out);
            System.out.println("- Done : " + outputPath + " at " + new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void extractVideoFile() throws IOException {
        File file = ResourceUtils.getFile("classpath:video/1080p_video.mp4");
        FileChannel fc = new FileInputStream(file).getChannel();
        IsoFile isoFile = new IsoFile(fc);
        MovieBox moov = isoFile.getMovieBox();
        for (Box box : moov.getBoxes()) {
            if (box instanceof TrackBox) {
                TrackBox trackBox = (TrackBox) box;
                List<Box> boxes = trackBox.getBoxes();
                for (Box b : boxes) {
                    if (b instanceof TrackHeaderBox) {
                        TrackHeaderBox trackHeaderBox = (TrackHeaderBox) b;
                        System.out.println("Height: " + trackHeaderBox.getHeight());
                        System.out.println("Width: " + trackHeaderBox.getWidth());
                        break;
                    }
                }
                break;
            }
        }
    }


}
