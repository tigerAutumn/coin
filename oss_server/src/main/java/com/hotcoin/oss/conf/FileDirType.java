package com.hotcoin.oss.conf;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "file-dir-type")
public class FileDirType  implements InitializingBean {

    private Map<String,Object> dirMaps;

    public static Map<String,Object> fileDirMap;

    public Map<String, Object> getDirMaps() {
        return dirMaps;
    }

    public void setDirMaps(Map<String, Object> dirMaps) {
        this.dirMaps = dirMaps;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        fileDirMap = this.getDirMaps();
    }
}
