package service.impl.nacos;


import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.ConfigCenter;
import config.lib.config.NacosConfig;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import service.ConfigCenterProcessor;
import service.RoutesChangeListener;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Nacos配置中心实现类
 */
@Slf4j
@Builder
public class NacosConfigCenter implements ConfigCenterProcessor {

    /**
     * 网关的配置中心信息
     */
    private ConfigCenter configCenter;

    /**
     * Nacos依赖包 提供的与配置中心进行交互的接口
     */
    private ConfigService configService;

    /**
     * 初始化状态标志
     * 使用AtomicBoolean保证线程安全
     * 防止重复初始化
     */
    private final AtomicBoolean init = new AtomicBoolean(false);

    /**
     * 初始化Nacos配置中心连接
     * 创建与Nacos的连接，并初始化ConfigService
     *
     * @param configCenter 配置中心配置信息
     */
    @SneakyThrows(NacosException.class)
    @Override
    public void init(ConfigCenter configCenter) {
        // 如果配置中心未启用或已经初始化，则直接返回
        if (!configCenter.isEnabled() || !init.compareAndSet(false, true)) {
            return;
        }
        this.configCenter = configCenter;
        // 创建Nacos配置服务实例
        Properties properties = buildProperties(configCenter);
        this.configService = NacosFactory.createConfigService(properties);

    }

    /**
     * 订阅路由规则变更
     * @param listener 路由变更监听器
     */
    @SneakyThrows(NacosException.class)
    @Override
    public void subscribeRoutesChange(RoutesChangeListener listener) {
        // 如果配置中心未启用或未初始化，则直接返回
        if (!configCenter.isEnabled() || !init.get()) {
            return;
        }
        // 获取Nacos特定配置
        NacosConfig nacos = configCenter.getNacosConfig();
        String configJson = configService.getConfig(nacos.getDataId(), nacos.getGroup(), nacos.getTimeout());
        log.info("config from nacos: {}", configJson);

    }

    /**
     * 构建Nacos属性
     * 将ConfigCenter中的配置转换为Nacos所需的Properties对象
     *
     * @param configCenter 配置中心配置
     * @return Nacos配置属性
     * */
    private Properties buildProperties(ConfigCenter configCenter) {
        ObjectMapper mapper = new ObjectMapper();
        Properties properties = new Properties();
        // 设置Nacos服务器地址
        properties.put(PropertyKeyConst.SERVER_ADDR, configCenter.getConfigAddress());
        // 将NacosConfig中的所有属性转换为Map并添加到Properties中
        Map map = mapper.convertValue(configCenter.getNacosConfig(), Map.class);
        if (map == null || map.isEmpty()) return properties;
        properties.putAll(map);
        return properties;
    }
}
