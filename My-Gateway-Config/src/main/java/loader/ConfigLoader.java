package loader;


import config.Config;
import util.ConfigUtil;

import static constant.ConfigConstant.CONFIG_PATH;
import static constant.ConfigConstant.CONFIG_PREFIX;

/**
 * 配置加载
 */
public class ConfigLoader {

    public static Config load(String[] args) {
        return ConfigUtil.loadConfigFromYaml(CONFIG_PATH, Config.class, CONFIG_PREFIX);
    }

}
