package util;



import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import loader.ConfigLoader;

import java.io.IOException;
import java.io.InputStream;

/**
 * 配置工具类
 * 提供从YAML文件加载配置的工具方法
 * 使用Jackson库解析YAML文件
 * 支持从指定前缀加载配置
 */
public class ConfigUtil {

    /**
     * Jackson对象映射器
     * 用于解析YAML文件
     * 配置为使用YAMLFactory
     */
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    /**
     * 静态初始化块
     * 配置对象映射器的行为
     */
    static {
        // 配置反序列化时忽略未知属性
        // 这样当YAML文件中有类中不存在的属性时，不会抛出异常
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 从YAML文件加载配置
     * 支持从指定前缀加载配置
     * 
     * @param filePath 配置文件路径，相对于类路径
     * @param clazz 配置类型
     * @param prefix 配置前缀，使用点号分隔，如"grace.gateway"
     * @param <T> 配置类型参数
     * @return 加载的配置对象
     * @throws RuntimeException 如果加载过程中发生IO异常
     */
    public static <T> T loadConfigFromYaml(String filePath, Class<T> clazz, String prefix) {
        try (InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream(filePath)) {
            // 如果文件不存在，返回null
            if (inputStream == null) return null;
            // 解析YAML文件为对象节点
            ObjectNode rootNode = (ObjectNode) mapper.readTree(inputStream);
            // 获取指定前缀的子节点
            ObjectNode subNode = getSubNode(rootNode, prefix);
            // 将子节点转换为指定类型的对象
            return mapper.treeToValue(subNode, clazz);
        } catch (IOException e) {
            // 如果发生IO异常，包装为RuntimeException并抛出
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取指定前缀的子节点
     * 支持多级前缀，如"grace.gateway"
     * 
     * @param node 根节点
     * @param prefix 前缀，使用点号分隔
     * @return 子节点，如果前缀不存在则返回null
     */
    private static ObjectNode getSubNode(ObjectNode node, String prefix) {
        // 如果前缀为空，直接返回根节点
        if (prefix == null || prefix.isEmpty()) return node;
        // 按点号分隔前缀
        String[] keys = prefix.split("\\.");
        // 逐级获取子节点
        for (String key : keys) {
            // 如果节点为空或不存在，返回null
            if (node == null || node.isMissingNode() || node.isNull()) {
                return null;
            }
            // 获取子节点
            node = (ObjectNode) node.get(key);
        }
        return node;
    }

}
