package utils;

import io.netty.channel.epoll.Epoll;

/**
 * 系统工具类
 * 提供与操作系统相关的工具方法
 * 主要用于检测操作系统类型和特定功能的可用性
 * 
 * 此工具类在网关启动时用于决定使用哪种网络IO模型：
 * - 在Linux系统上，如果Epoll可用，则使用Epoll模型
 * - 在其他系统上，使用NIO模型
 */
public class SystemUtil {
	
    /**
     * 操作系统名称
     * 从系统属性中获取，如"Linux"、"Windows 10"等
     */
    public static final String OS_NAME = System.getProperty("os.name");

    /**
     * 是否为Linux平台的标志
     * 在类加载时初始化
     */
    private static boolean isLinuxPlatform = false;

    /**
     * 是否为Windows平台的标志
     * 在类加载时初始化
     */
    private static boolean isWindowsPlatform = false;

    /**
     * 静态初始化块
     * 在类加载时检测操作系统类型
     */
    static {
        // 检测是否为Linux系统
        if (OS_NAME != null && OS_NAME.toLowerCase().contains("linux")) {
            isLinuxPlatform = true;
        }

        // 检测是否为Windows系统
        if (OS_NAME != null && OS_NAME.toLowerCase().contains("windows")) {
            isWindowsPlatform = true;
        }
    }

    /**
     * 检查当前系统是否为Windows平台
     * 
     * @return 如果当前系统是Windows，则返回true；否则返回false
     */
    public static boolean isWindowsPlatform() {
        return isWindowsPlatform;
    }

    /**
     * 检查当前系统是否为Linux平台
     * 
     * @return 如果当前系统是Linux，则返回true；否则返回false
     */
    public static boolean isLinuxPlatform() {
        return isLinuxPlatform;
    }

    /**
     * 检查是否可以使用Epoll
     * Epoll是Linux系统上的高性能IO多路复用机制
     * 在Linux系统上且Epoll可用时返回true
     * 
     * @return 如果可以使用Epoll，则返回true；否则返回false
     */
    public static boolean useEpoll() {
        return isLinuxPlatform() && Epoll.isAvailable();
    }

}
