package hienlt.app.musicplayer.asynctasks;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v24Frames;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.jaudiotagger.tag.images.Artwork;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hienlt.app.musicplayer.interfaces.IScanMedia;
import hienlt.app.musicplayer.models.Song;
import hienlt.app.musicplayer.provider.SongProvider;
import hienlt.app.musicplayer.ui.activities.ScanActivity;
import hienlt.app.musicplayer.utils.Common;
import hienlt.app.musicplayer.utils.DiskLruImageCache;
import hienlt.app.musicplayer.utils.FileUtils;
import hienlt.app.musicplayer.utils.NumberUtils;
import hienlt.app.musicplayer.utils.StringUtils;

/**
 * Created by hienl_000 on 4/4/2016.
 */
public class ScanMusicAsynctask extends AsyncTask<Void, File, ArrayList<Song>> {
    private static String TAG = "hienlt0610";
    private boolean isCancelled = false;
    private ArrayList<Song> listSong;
    private int numMusicFind = 0;
    private Handler myHandler;
    private ArrayList<String> exceptionFolter;
    private IScanMedia iScanMedia;
    private Context context;

    public ScanMusicAsynctask(Context context, IScanMedia iScanMedia) {

        this.iScanMedia = iScanMedia;
        this.context = context;
    }

    private DiskLruImageCache cache;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listSong = new ArrayList<>();
        myHandler = new Handler();

        cache = DiskLruImageCache.newInstance(Common.ALBUM_CACHE_FOLDER);

    }

    public void cancelTask() {
        isCancelled = true;
    }

    @Override
    protected ArrayList<Song> doInBackground(Void... params) {
        if (isCancelled) return null;

        if (FileUtils.isExternalStorageReadable()) {
            scanDirectory(Environment.getExternalStorageDirectory());
        }
        scanDirectory(Environment.getDataDirectory());

        return listSong;
    }

    @Override
    protected void onPostExecute(final ArrayList<Song> songs) {
        super.onPostExecute(songs);
        ((ScanActivity) context).tvMusicUrl.setText("Đang update...");
        ((ScanActivity) context).tvFile.setText("");
        if (isCancelled) return;
        if (iScanMedia != null) {
            myHandler.post(new Runnable() {
                @Override
                public void run() {
                    SongProvider.getInstance(context).updateListSong(songs);
                    iScanMedia.finallyScanMedia((ArrayList<Song>) songs);
                }
            });
        }
    }

    @Override
    protected void onProgressUpdate(final File... values) {
        super.onProgressUpdate(values);
//        myHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                ((ScanActivity) context).tvFile.setText(values[0].getName());
//            }
//        });
        if (values[0].getName().endsWith(".mp3")) {
            numMusicFind++;
            try {
                MP3File mp3File = (MP3File) AudioFileIO.read(values[0]);
                if (mp3File.getAudioHeader().getTrackLength() > 59) {
                    //Add song to list
                    final Song song = new Song();
                    song.setId(Common.generateUUID(values[0].getAbsolutePath()));
                    song.setParentFolder(values[0].getParent());
                    song.setLocalDataSource(values[0].getAbsolutePath());
                    if (mp3File.hasID3v2Tag()) {
                        ID3v24Tag v2Tag = mp3File.getID3v2TagAsv24();
                        //Tên bài hát
                        song.setTitle(v2Tag.getFirst(ID3v24Frames.FRAME_ID_TITLE));
                        //Album
                        song.setAlbum(v2Tag.getFirst(ID3v24Frames.FRAME_ID_ALBUM));
                        //Ca sĩ
                        song.setArtist(v2Tag.getFirst(ID3v24Frames.FRAME_ID_ARTIST));
                        //Năm xuất bản
                        song.setYear(NumberUtils.parseInt(v2Tag.getFirst(ID3v24Frames.FRAME_ID_YEAR), 0));
                        //Thể loại
                        song.setGenre(v2Tag.getFirst(ID3v24Frames.FRAME_ID_GENRE));

                        //Cache albumArt
                        if (cache != null) {
                            if (!cache.containsKey(song.getAlbum())) {
                                Artwork artwork = v2Tag.getFirstArtwork();
                                if (artwork != null) {
                                    byte[] bytes = artwork.getBinaryData();
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    cache.put(song.getAlbum(), bitmap);
                                }
                            }
                        }
                    }

                    song.setDuration(mp3File.getAudioHeader().getTrackLength() * 1000);
                    song.setBitRate((int) mp3File.getAudioHeader().getBitRateAsNumber());
                    if (TextUtils.isEmpty(song.getTitle())) {
                        String fname = values[0].getName();
                        int pos = fname.lastIndexOf(".");
                        if (pos > 0) {
                            fname = fname.substring(0, pos);
                        }
                        song.setTitle(fname);
                    }

                    if (TextUtils.isEmpty(song.getAlbum()))
                        song.setAlbum("<unknown>");
                    if (TextUtils.isEmpty(song.getArtist()))
                        song.setArtist("<unknown>");

                    song.setSize(values[0].length());
                    listSong.add(song);

                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            iScanMedia.foundMp3(song, values[0].getAbsolutePath());
                        }
                    });
                }
            } catch (CannotReadException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TagException e) {
                e.printStackTrace();
            } catch (ReadOnlyFileException e) {
                e.printStackTrace();
            } catch (InvalidAudioFrameException e) {
                e.printStackTrace();
            }
        } else {
            myHandler.post(new Runnable() {
                @Override
                public void run() {
                    iScanMedia.foundMp3(null, values[0].getAbsolutePath());
                }
            });
        }

    }

    private void scanDirectory(File directory) {
        if (isCancelled) return;
        if (directory != null) {
            File[] listFiles = directory.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File file : listFiles) {
                    if (isCancelled) break;
                    if (file.isDirectory()) {
                        scanDirectory(file);
                    } else {
                        onProgressUpdate(file);
                    }
                }
            }
        }
    }
}
