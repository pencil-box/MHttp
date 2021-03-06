package im.wangchao.mhttp;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;
import okhttp3.internal.Util;

/**
 * <p>Description  : FileResponseHandler.</p>
 * <p/>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 15/10/18.</p>
 * <p>Time         : 下午2:39.</p>
 */
public class FileCallbackHandler extends AbsCallbackHandler<File> {
    final private File file;
    final private static int BUFFER_SIZE = 4096;

    public FileCallbackHandler(Context context){
        this.file = getTempFile(context);
    }

    public FileCallbackHandler(File file){
        this.file = file;
    }

    protected File getFile(){
        return file;
    }

    @Override protected void onSuccess(File file, OkResponse response){

    }

    @Override protected void onFailure(OkResponse response, Throwable throwable) {

    }

    @Override protected File backgroundParser(OkResponse response) throws IOException{
        writeFile(response.response(), file);
        return file;
    }

    @Override public String accept() {
        return Accept.ACCEPT_FILE;
    }

    private File getTempFile(Context context){
        try {
            return File.createTempFile("temp", "_handled", context.getCacheDir());
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * write file , send progress message
     */
    protected void writeFile(Response response, File file) throws IOException {
        if (file == null){
            throw new IllegalArgumentException("File == null");
        }
        InputStream instream = response.body().byteStream();
        long contentLength = response.body().contentLength();
        FileOutputStream buffer = new FileOutputStream(file);
        if (instream != null) {
            try {
                byte[] tmp = new byte[BUFFER_SIZE];
                int l, count = 0;
                while ((l = instream.read(tmp)) != -1 && !Thread.currentThread().isInterrupted()) {
                    count += l;
                    buffer.write(tmp, 0, l);

                    sendProgressMessage(count, (int) contentLength);
                }
            } finally {
                Util.closeQuietly(instream);
                buffer.flush();
                Util.closeQuietly(buffer);
            }
        }
    }
}
