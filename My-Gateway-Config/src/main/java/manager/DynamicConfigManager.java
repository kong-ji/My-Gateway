package manager;

import pojo.RouteDefinition;
import pojo.ServiceDefinition;
import pojo.ServiceInstance;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态配置管理，缓存从配置中心拉取下来的配置
 */
public class DynamicConfigManager {

    /*********   单例   *********/
    private static final DynamicConfigManager INSTANCE = new DynamicConfigManager();

    private DynamicConfigManager() {}

    public DynamicConfigManager getInstance() {
        return INSTANCE;
    }

    // 路由id对应的路由
    private final ConcurrentHashMap<String /* 路由id */, RouteDefinition> routeIdRouteMap = new ConcurrentHashMap<>();

    // 服务对应的路由
    private final ConcurrentHashMap<String /* 服务名 */, RouteDefinition> serviceNameRouteMap = new ConcurrentHashMap<>();

    // 服务
    private final ConcurrentHashMap<String /* 服务名 */, ServiceDefinition> serviceDefinitionMap = new ConcurrentHashMap<>();

    // 服务对应的实例
    private final ConcurrentHashMap<String /* 服务名 */, ConcurrentHashMap<String /* 实例id */, ServiceInstance>> serviceInstanceMap = new ConcurrentHashMap<>();


    /*********   路由   *********/
    public void updateRouteByRouteId(String id, RouteDefinition routeDefinition) {
        routeIdRouteMap.put(id, routeDefinition);
    }

    public void updateRoutes(Collection<RouteDefinition> routes) {
        updateRoutes(routes, false);
    }

    /**
     * 批量更新路由
     * 可选择是否清除现有路由
     *
     * @param routes 路由定义集合
     * @param clear 是否清除现有路由
     */
    public void updateRoutes(Collection<RouteDefinition> routes, boolean clear) {
        if (routes == null || routes.isEmpty()) return;

        if (clear) {
            routeIdRouteMap.clear();
            serviceNameRouteMap.clear();
        }

        for (RouteDefinition routeDefinition : routes) {
            if (routeDefinition == null) continue;
            routeIdRouteMap.put(routeDefinition.getId(), routeDefinition);
            serviceNameRouteMap.put(routeDefinition.getServiceName(), routeDefinition);
        }
    }

    public RouteDefinition getRouteById(String id) {
        return routeIdRouteMap.get(id);
    }

    public RouteDefinition getRouteByServiceName(String serviceName) {
        return serviceNameRouteMap.get(serviceName);
    }

    /*********   服务   *********/
    public void updateServiceByName(String name, ServiceDefinition serviceDefinition) {
        serviceDefinitionMap.put(name, serviceDefinition);
    }
    public void updateServices(Collection<ServiceDefinition> services) {
        updateServices(services, false);
    }

    /**
     * 批量更新服务
     * 可选择是否清除现有服务
     *
     * @param services 服务定义集合
     * @param clear 是否清除现有服务
     */
    public void updateServices(Collection<ServiceDefinition> services, boolean clear) {
        if (services == null || services.isEmpty()) return;
        if (clear) {
            serviceDefinitionMap.clear();
        }
        for (ServiceDefinition service : services) {
            if (service == null) continue;
            serviceDefinitionMap.put(service.getServiceName(), service);
        }
    }
    public ServiceDefinition getServiceByName(String name) {
        return serviceDefinitionMap.get(name);
    }

    /*********   实例   *********/
    public void addServiceInstance(String serviceName, ServiceInstance instance) {
        serviceInstanceMap.computeIfAbsent(serviceName, k -> new ConcurrentHashMap<>()).put(instance.getInstanceId(), instance);
    }

    public void removeServiceInstance(String serviceName, ServiceInstance instance) {
        serviceInstanceMap.compute(serviceName, (k, v) -> {
            if (v == null || v.get(instance.getInstanceId()) == null) return v;
            v.remove(instance.getInstanceId());
            return v;
        });
    }

    public Map<String, ServiceInstance> getInstancesByServiceName(String serviceName) {
        return serviceInstanceMap.get(serviceName);
    }
}
