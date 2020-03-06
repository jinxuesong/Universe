package com.stephenue.universe.utils;

import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OKManager {
    private File rootFile;//文件的路径
    private File file;//文件
    private long downLoadSize;//下载文件的长度
    private long downLoadSizeBefore = 0;
    private final ThreadPoolExecutor executor;// 线程池
    private boolean isDown = false; //是否已经下载过了（下载后点击暂停） 默认为false
    private String name; //名称
    private String path;// 下载的网址
    private RandomAccessFile raf; // 读取写入IO方法
    private long totalSize = 0;
    private MyThread thread;//线程
    private Handler handler;//Handler 方法
    private OKManager.IProgress progress;// 下载进度方法，内部定义的抽象方法


    /**
     * 构造方法  OKhttp
     * @param path  网络连接路径
     *
     */
    public OKManager(String path, OKManager.IProgress progress) {
        this.path = path;
        this.progress = progress;
        this.handler = new Handler();
        String decodePath = URLDecoder.decode(path);
        this.name = decodePath.substring(path.lastIndexOf("/") + 1);
        rootFile = FileUtils.getRootFile();
        executor = new ThreadPoolExecutor(5, 5, 50, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(3000));
        executor.execute(new MyThread());
    }

    /**
     *  自定义线程
     */
    class MyThread extends Thread {
        @Override
        public void run() {
            super.run();
            downLoadFile();
        }

    }

    /**
     * 这就是下载方法
     */
    private void downLoadFile() {
        try {
            if (file == null) {//判断是否拥有相应的文件
                file = new File(rootFile, name); //很正常的File() 方法
                raf = new RandomAccessFile(file, "rwd");//实例化一下我们的RandomAccessFile()方法
            } else {
                downLoadSize = file.length();// 文件的大小
                if (raf == null) {//判断读取是否为空
                    raf = new RandomAccessFile(file, "rwd");
                }
                raf.seek(downLoadSize);//将文件指针移动到文件downloadSize位置
            }
            totalSize = getContentLength(path);//获取文件的大小
            if (downLoadSize == totalSize) {// 判断是否下载完成
                //已经下载完成
                return;
            }

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(path).
                    addHeader("Range", "bytes=" + downLoadSize + "-" + totalSize).build();
            Response response = client.newCall(request).execute();
            InputStream ins = response.body().byteStream();
            //上面的就是简单的OKHttp连接网络，通过输入流进行写入到本地
            int len = 0;
            byte[] by = new byte[1024];
            long endTime = System.currentTimeMillis();
            while ((len = ins.read(by)) != -1 && isDown) {//如果下载没有出错并且已经开始下载，循环进行以下方法
                raf.write(by, 0, len);
                downLoadSize += len;
                if (System.currentTimeMillis() - endTime > 1000) {
                    final long dlSpeed = (downLoadSize - downLoadSizeBefore) / 1024;
                    final double dd = downLoadSize / (totalSize * 1.0);
                    DecimalFormat format = new DecimalFormat("#0.00");
                    String value = format.format((dd * 100)) + "%";//计算百分比
                    String dlSizebyMB = format.format((double) downLoadSize / 1048576);//已下载大小 MB
                    Log.i("tag", "==================" + value+"========="+downLoadSize+"bytes "+dlSizebyMB+"MB"+"====speed:"+dlSpeed+"kb");
                    handler.post(new Runnable() {//通过Handler发送消息到UI线程，更新
                        @Override
                        public void run() {
                            progress.onProgress((int) (dd * 100));
                        }
                    });
                    endTime = System.currentTimeMillis();
                    downLoadSizeBefore = downLoadSize;
                }
            }
            response.close();//最后要把response关闭
        } catch (Exception e) {
            e.getMessage();
        }

    }

    /**
     * 线程开启方法
     */
    public void start() {
        if (thread == null) {
            thread = new MyThread();
            isDown = true;
            executor.execute(thread);
        }
    }

    /**
     * 线程停止方法
     */
    public void stop() {
        if (thread != null) {
            isDown = false;
            executor.remove(thread);
            thread = null;
        }
    }

    //通过OkhttpClient获取文件的大小
    public long getContentLength(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        long length = response.body().contentLength();
        response.close();
        return length;
    }

    public interface IProgress {
        void onProgress(int progress);
    }

}
