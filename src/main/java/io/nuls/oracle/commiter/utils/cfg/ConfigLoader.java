package io.nuls.oracle.commiter.utils.cfg;

import io.nuls.core.parse.JSONUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public class ConfigLoader {
    public static Map<String, Object> load(String fileName) throws IOException {

        String filePath = getFilePath(fileName);
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            throw new FileNotFoundException(filePath);
        }
        String json = TxtFileReader.read(filePath);
        return JSONUtils.json2map(json);
    }

    private static String getFilePath(String name) {
        String userDir = System.getProperty("user.dir");
        String filePath = userDir + "/" + name;
        File file = new File(filePath);
        if (file.exists() && !file.isDirectory()) {
            return filePath;
        }
        //处理在idea中启动的情况
        return ConfigLoader.class.getClassLoader().getResource(name).getPath();
    }
}
