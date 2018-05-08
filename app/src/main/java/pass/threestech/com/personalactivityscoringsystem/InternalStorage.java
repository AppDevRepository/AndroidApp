package pass.threestech.com.personalactivityscoringsystem;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class InternalStorage{

    private InternalStorage() {}

    public static void writeObject(Context context, String key, Object object) throws IOException {
        FileOutputStream fos = context.openFileOutput(key, context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
        fos.close();
    }

    public static Object readObject(Context context, String key) throws IOException,
            ClassNotFoundException {
        String path = context.getFilesDir().getAbsolutePath()+"/"+key;
        FileInputStream fis = context.openFileInput(key);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object object = ois.readObject();
        return object;
    }

    public static void deleteObject(Context context, String key) throws IOException,
            ClassNotFoundException {
        String path = context.getFilesDir().getAbsolutePath()+"/"+key;
        File file = new File(path);
        if(file.exists()){
            file.delete();
        }
    }
}