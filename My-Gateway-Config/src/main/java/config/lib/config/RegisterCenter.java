package config.lib.config;

import lombok.Getter;

@Getter
public enum RegisterCenter {

    NACOS("nacos"),
    ZOOKEEPER("zookeeper");

    private final String des;

    RegisterCenter(String des) {
        this.des = des;
    }

}
