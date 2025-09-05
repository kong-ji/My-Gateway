package config.lib.config;

import lombok.Getter;

@Getter
public enum ConfigCenter {

    NACOS("nacos"),
    ZOOKEEPER("zookeeper");

    private final String des;

    ConfigCenter(String des) {
        this.des = des;
    }

}
