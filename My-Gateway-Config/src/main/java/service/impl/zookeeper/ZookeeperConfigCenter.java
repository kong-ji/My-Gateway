package service.impl.zookeeper;


import com.alibaba.nacos.api.NacosFactory;
import config.ConfigCenter;
import service.ConfigCenterProcessor;
import service.RoutesChangeListener;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

// TODO Zookeeper配置中心实现
public class ZookeeperConfigCenter implements ConfigCenterProcessor {

    /**
     * 配置
     */
    private ConfigCenter configCenter;

    /**
     * 初始化状态标志
     * 使用AtomicBoolean保证线程安全
     * 防止重复初始化
     */
    private final AtomicBoolean init = new AtomicBoolean(false);


    @Override
    public void init(ConfigCenter configCenter) {
        // 如果配置中心未启用或已经初始化，则直接返回
        if (!configCenter.isEnabled() || !init.compareAndSet(false, true)) {
            return;
        }
        this.configCenter = configCenter;
        //创建Zookeeper配置服务实例...
    }

    @Override
    public void subscribeRoutesChange(RoutesChangeListener listener) {
        if (!configCenter.isEnabled() || !init.get()) {
            return;
        }
        //
    }
}
