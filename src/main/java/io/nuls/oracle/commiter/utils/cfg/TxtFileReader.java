package io.nuls.oracle.commiter.utils.cfg;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Niels
 */
public class TxtFileReader {
    public static String read(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new RuntimeException("文件路径不正确");
        }
        StringBuilder ss = new StringBuilder();
        InputStreamReader read = null;
        try {
            read = new InputStreamReader(
                    new FileInputStream(file), "utf-8");//考虑到编码格式
            BufferedReader bufferedReader = new BufferedReader(read);

            String lineTxt;
            while (true) {
                if (!((lineTxt = bufferedReader.readLine()) != null)) {
                    break;
                }
                ss.append(lineTxt);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                read.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ss.toString();
    }
}
