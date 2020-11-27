package blxt.android.editormd.util;

import com.blxt.quicklog.QLog;

public class FilePathTools {


    /**
     * 路径处理
     * @param pathSrc
     * @return
     */
    public static String getRealPath(String pathSrc){
        String path = "";
        // 处理华为路径
        // /storage/emulated/0/DingTalk/springcloud微服务基础demo_15.md

        String pathRoot = QLog.PATH.SDPath;

        String paths[] = pathSrc.split("/");
        if("emulated".equals(paths[2])){
            paths[2] = "sdcard";
            paths[2] += paths[3];
            paths[3] = "";
            for(String s : paths){
                if(!s.isEmpty()){
                    path += "/" + s;
                }
            }
        }
        return path;
    }
}
