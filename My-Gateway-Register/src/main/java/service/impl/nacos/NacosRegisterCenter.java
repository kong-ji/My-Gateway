package service.impl.nacos;


import cn.hutool.core.bean.BeanUtil;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingMaintainFactory;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.Service;
import com.alibaba.nacos.api.naming.pojo.ServiceInfo;
import com.alibaba.nacos.common.executor.NameThreadFactory;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.Config;
import config.RegisterCenter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import pojo.ServiceDefinition;
import pojo.ServiceInstance;
import service.RegisterCenterListener;
import service.RegisterCenterProcessor;
import utils.NetUtil;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Nacos注册中心实现
 * 将网关自身注册到Nacos
 * 发现并订阅Nacos中的服务变化
 */
@Slf4j
public class NacosRegisterCenter implements RegisterCenterProcessor {

    /**
     * 注册中心配置
     */
    private Config config;

    /**
     * 主要用于维护服务实例信息
     */
    private NamingService namingService;

    /**
     * 主要用于维护服务定义信息
     */
    private NamingMaintainService namingMaintainService;

    /**
     * 监听器
     */
    private RegisterCenterListener listener;

    private final AtomicBoolean init = new AtomicBoolean(false);

    public NacosRegisterCenter(Config config) {
        this.config = config;
        init();
    }

    @SneakyThrows(Exception.class)
    @Override
    public void init() {
        if (!init.compareAndSet(false, true)) return;
        String group = config.getRegisterCenter().getNacos().getGroup();

        Properties properties = buildProperties(config.getRegisterCenter());
        namingService = NamingFactory.createNamingService(properties);
        namingMaintainService = NamingMaintainFactory.createMaintainService(properties);

        // 将网关自己注册到注册中心
        Instance instance = new Instance();
        instance.setInstanceId(NetUtil.getLocalIp() + ":" + config.getPort());
        instance.setIp(NetUtil.getLocalIp());
        instance.setPort(config.getPort());
        namingService.registerInstance(config.getName(), group, instance);
        log.info("gateway instance register: {}", instance);

        // 设置网关服务元数据信息
        Map<String, String> serviceInfo = BeanUtils.describe(new ServiceDefinition(config.getName()));
        namingMaintainService.updateService(config.getName(), group, 0, serviceInfo);
        log.info("gateway service meta register: {}", serviceInfo);
    }

    @Override
    public void subscribeServiceChange(RegisterCenterListener listener) {
        this.listener = listener;

        // 实现心跳逻辑: 创建定时任务，每10秒执行一次订阅服务的操作
        Executors.newScheduledThreadPool(1, new NameThreadFactory("doSubscribeAllServices")).
                scheduleWithFixedDelay(this::doSubscribeAllServices, 0, 10, TimeUnit.SECONDS);
    }

    private Properties buildProperties(RegisterCenter registerCenter) {
        ObjectMapper mapper = new ObjectMapper();
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, registerCenter.getAddress());
        Map map = mapper.convertValue(registerCenter.getNacos(), Map.class);
        if (map == null || map.isEmpty()) return properties;
        properties.putAll(map);
        return properties;
    }

    /**
     * 订阅所有服务
     * 定期检查Nacos中的服务列表，并订阅未订阅的服务
     */
    private void doSubscribeAllServices() {
        try {
            // 获取Nacos分组
            String group = config.getRegisterCenter().getNacos().getGroup();

            // 获取已订阅的服务列表
            Set<String> subscribeServiceSet = namingService.getSubscribeServices().stream()
                    .map(ServiceInfo::getName)
                    .collect(Collectors.toSet());

            // 分页查询服务列表
            int pageNo = 1;
            int pageSize = 100;

            // 获取第一页服务列表
            List<String> serviceList = namingService.getServicesOfServer(pageNo, pageSize, group).getData();

            // 循环处理所有页的服务
            while (CollectionUtils.isNotEmpty(serviceList)) {
                for (String serviceName : serviceList) {
                    // 如果服务已订阅，跳过
                    if (subscribeServiceSet.contains(serviceName)) {
                        continue;
                    }

                    // 创建事件监听器
                    EventListener eventListener = new NacosRegisterListener();
                    // 首次订阅新服务，主动发起一次信号
                    eventListener.onEvent(new NamingEvent(serviceName, null));
                    // 订阅服务变化
                    namingService.subscribe(serviceName, group, eventListener);
                    log.info("subscribe a service, ServiceName: {} Group: {}", serviceName, group);
                }
                // 遍历下一页的服务列表
                serviceList = namingService.getServicesOfServer(++pageNo, pageSize, group).getData();
            }
        } catch (Exception e) { // 任务中捕捉Exception，防止线程池停止
            log.error("subscribe services from nacos occur exception: {}", e.getMessage(), e);
        }
    }
    /**
     * nacos注册中心监听器
     * 实现了nacos的EventListener接口
     * 用于处理nacos的服务实例变化事件
     */
    private class NacosRegisterListener implements EventListener {

        /**
         * 处理nacos事件
         * 当服务实例发生变化时，nacos会调用此方法
         *
         * @param event nacos事件
         */
        @SneakyThrows(NacosException.class)
        @Override
        public void onEvent(Event event) {
            // 如果是命名事件（服务实例变化事件）
            if (event instanceof NamingEvent namingEvent) {

                String serviceName = namingEvent.getServiceName();
                String group = config.getRegisterCenter().getNacos().getGroup();

                // 查询nacos服务定义创建
                Service service = namingMaintainService.queryService(serviceName, group);
                ServiceDefinition serviceDefinition = new ServiceDefinition(service.getName());
                BeanUtil.fillBeanWithMap(service.getMetadata(), serviceDefinition, true);

                // 获取所有服务实例信息
                List<Instance> allInstances = namingService.getAllInstances(serviceName, group);
                Set<ServiceInstance> newInstances = new HashSet<>();

                // 转换nacos实例为网关服务实例
                if (CollectionUtils.isNotEmpty(allInstances)) {
                    for (Instance instance : allInstances) {
                        if (instance == null) continue;

                        // 创建新的服务实例, 添加到实例集合
                        ServiceInstance newInstance = new ServiceInstance();
                        BeanUtil.copyProperties(instance, newInstance);
                        BeanUtil.fillBeanWithMap(instance.getMetadata(), newInstance, true);

                        newInstances.add(newInstance);
                    }
                }

                // 调用我们自己的订阅监听器
                listener.onInstancesChange(serviceDefinition, newInstances);
            }
        }
    }
}

