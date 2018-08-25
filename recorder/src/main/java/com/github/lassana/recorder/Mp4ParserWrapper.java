package com.github.lassana.recorder;

import android.util.Log;

import org.mp4parser.Container;
import org.mp4parser.muxer.Movie;
import org.mp4parser.muxer.Track;
import org.mp4parser.muxer.builder.DefaultMp4Builder;
import org.mp4parser.muxer.container.mp4.MovieCreator;
import org.mp4parser.muxer.tracks.AppendTrack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.List;

/**
 * @author Nikolai Doronin {@literal <lassana.nd@gmail.com>}
 * @since 8/25/13
 */
public final class Mp4ParserWrapper {

    public static final String TAG = "Mp4ParserWrapper";

    public static final int FILE_BUFFER_SIZE = 1024;

    private Mp4ParserWrapper() {
    }

    /**
     * Appends mp4 audios/videos: {@code anotherFileName} to {@code mainFileName}.
     *
     * @param mainFileName The first file path.
     * @param anotherFileName The second file path.
     *
     * @return true if the operation was made successfully.
     */
    public static boolean append(String mainFileName, String anotherFileName) {
        try {
            File targetFile = new File(mainFileName);
            File anotherFile = new File(anotherFileName);
            if (targetFile.exists() && targetFile.length() > 0) {
                String tmpFileName = mainFileName + ".tmp";
                append(mainFileName, anotherFileName, tmpFileName);
                copyFile(tmpFileName, mainFileName);
                return anotherFile.delete() && new File(tmpFileName).delete();
            } else {
                if (!targetFile.exists()) {
                    if (!targetFile.getParentFile().exists()) {
                        if (!targetFile.getParentFile().mkdirs()) {
                            return false;
                        }
                    }
                    if (!targetFile.createNewFile()) {
                        return false;
                    }
                }
                copyFile(anotherFileName, mainFileName);
                return anotherFile.delete();
            }
        } catch (Exception e) {
            Log.e(TAG, "Appending two mp4 files failed with exception", e);
            return false;
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "Appending two mp4 files failed with out of memory exception", e);
            return false;
        }
    }


    private static void copyFile(final String from, final String destination)
            throws IOException {
        FileInputStream in = new FileInputStream(from);
        FileOutputStream out = new FileOutputStream(destination);
        copy(in, out);
        in.close();
        out.close();
    }

    private static void copy(FileInputStream in, FileOutputStream out) throws IOException {
        byte[] buf = new byte[FILE_BUFFER_SIZE];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
    }

    public static void append(
            final String firstFile,
            final String secondFile,
            final String newFile) throws IOException {
        final Movie movieA = MovieCreator.build(secondFile);
        final Movie movieB = MovieCreator.build(firstFile);

        final Movie finalMovie = new Movie();

        final List<Track> movieOneTracks = movieA.getTracks();
        final List<Track> movieTwoTracks = movieB.getTracks();

        for (int i = 0; i < movieOneTracks.size() || i < movieTwoTracks.size(); ++i) {
            finalMovie.addTrack(new AppendTrack(movieTwoTracks.get(i), movieOneTracks.get(i)));
        }

        final Container container = new DefaultMp4Builder().build(finalMovie);

        final FileOutputStream fos = new FileOutputStream(new File(String.format(newFile)));
        final WritableByteChannel bb = Channels.newChannel(fos);
        container.writeContainer(bb);
        fos.close();
    }

}
