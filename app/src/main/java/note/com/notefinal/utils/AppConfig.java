package note.com.notefinal.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Иван on 03.11.2015.
 */
public class AppConfig {
    public static int getDbVersion(Context ctx) {
        int res = 1;
        AssetManager assetManager = ctx.getAssets();
        try {
            InputStream in = assetManager.open("app.properties");
            Properties properties = new Properties();
            properties.load(in);
            res = Integer.parseInt(properties.getProperty("note_final.db.version"));
        } catch (IOException e) {
            LogUtils.log(AppConfig.class, "Get file list error");
        }
        return res;
    }
}
