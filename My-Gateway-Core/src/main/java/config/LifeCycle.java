package config;

/**
 * 生命周期管理接口
 */
public interface LifeCycle {

    /**
     * 启动组件
     * 初始化资源，准备接收请求
     */
    void start();

    /**
     * 关闭组件
     * 释放资源，停止接收新请求
     */
    void shutdown();

    /**
     * 检查组件是否已启动
     * @return 组件是否已启动
     */
    boolean isStarted();

}
