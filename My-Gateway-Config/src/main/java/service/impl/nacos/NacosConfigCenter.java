package service.impl.nacos;


import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.ConfigCenter;
import lombok.Data;
import lombok.SneakyThrows;
import service.ConfigCenterProcessor;
import service.RoutesChangeListener;

import java.util.Map;
import java.util.Properties;

/**
 * Nacos配置中心实现类
 */
@Data
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
     * 初始化Nacos配置中心连接
     * 创建与Nacos的连接，并初始化ConfigService
     *
     * @param configCenter 配置中心配置信息
     */
    @SneakyThrows
    @Override
    public void init(ConfigCenter configCenter) {
        this.configCenter = configCenter;
        Properties properties = buildProperties(configCenter);
        this.configService = NacosFactory.createConfigService(properties);

    }

    /**
     * 订阅路由规则变更
     * @param listener 路由变更监听器
     */
    @Override
    public void subscribeRoutesChange(RoutesChangeListener listener) {

    }

    /**
     * 构建Nacos属性
     * 将ConfigCenter中的配置转换为Nacos所需的Properties对象
     *
     * @param configCenter 配置中心配置
     * @return Nacos配置属性
     * */
    private Properties buildProperties(ConfigCenter configCenter) {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, configCenter.getConfigAddress());
        ObjectMapper mapper = new ObjectMapper();
        properties.putAll(mapper.convertValue(configCenter.getNacosConfig(), Map.class));
        return properties;
    }
}
