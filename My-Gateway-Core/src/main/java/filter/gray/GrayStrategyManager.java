package filter.gray;

import filter.gray.strategy.GrayStrategy;
import filter.gray.strategy.ThresholdGrayStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * 灰度策略管理器
 * 负责加载和管理所有灰度策略实现
 * 使用Java SPI机制自动发现和加载灰度策略
 */
@Slf4j
public class GrayStrategyManager {

    /**
     * 策略映射表
     * 键为策略标识，值为策略实例
     * 存储所有已加载的灰度策略
     */
    private static final Map<String, GrayStrategy> strategyMap = new HashMap<>();

    /**
     * 静态初始化块
     * 在类加载时执行，使用SPI机制加载所有灰度策略实现
     */
    static {
        // 使用Java SPI机制加载所有GrayStrategy的实现类
        ServiceLoader<GrayStrategy> serviceLoader = ServiceLoader.load(GrayStrategy.class);
        // 遍历所有实现类，将它们添加到策略映射表中
        for (GrayStrategy strategy : serviceLoader) {
            strategyMap.put(strategy.mark(), strategy);
            log.info("load gray strategy success: {}", strategy);
        }
    }

    /**
     * 获取灰度策略
     * 根据策略名称获取对应的灰度策略实例
     * 如果指定名称的策略不存在，则返回默认的阈值灰度策略
     * 
     * @param name 策略名称
     * @return 灰度策略实例，如果不存在则返回ThresholdGrayStrategy实例
     */
    public static GrayStrategy getStrategy(String name) {
        // 从策略映射表中获取指定名称的策略
        GrayStrategy strategy = strategyMap.get(name);
        // 如果策略不存在，则使用默认的阈值灰度策略
        if (strategy == null)
            strategy = new ThresholdGrayStrategy();
        return strategy;
    }

}
