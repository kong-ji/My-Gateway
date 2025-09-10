package algorithm;

// 导入Java集合相关类
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 一致性哈希算法实现
 * 用于在分布式环境中将请求均匀地分配到多个节点
 *
 */
public class ConsistentHashing {

    /**
     * 虚拟节点数量
     * 每个实际节点在哈希环上的虚拟节点数量
     *
     */
    private final int virtualNodeNum;

    /**
     * 哈希环
     *
     */
    private final SortedMap<Integer, String> hashCircle = new TreeMap<>();

    /**
     * 构造函数
     * 初始化一致性哈希环，添加所有节点
     * 
     * @param nodes 节点列表
     * @param virtualNodeNum 虚拟节点数量
     */
    public ConsistentHashing(List<String> nodes, int virtualNodeNum) {
        this.virtualNodeNum = virtualNodeNum;
        // 添加所有节点到哈希环
        for (String node : nodes) {
            addNode(node);
        }
    }

    /**
     * 添加节点
     * 将一个节点及其虚拟节点添加到哈希环上
     * 
     * @param node 节点名称
     */
    public void addNode(String node) {
        // 为每个实际节点创建多个虚拟节点
        for (int i = 0; i < virtualNodeNum; i++) {
            // 构造虚拟节点名称
            String virtualNode = node + "&&VN" + i;
            // 计算虚拟节点的哈希值，并添加到哈希环
            hashCircle.put(getHash(virtualNode), node);
        }
    }

    /**
     * 获取节点
     * 根据键查找应该路由到的节点
     * 
     * @param key 键，如请求的ID或客户端IP
     * @return 节点名称，如果哈希环为空则返回null
     */
    public String getNode(String key) {
        // 如果哈希环为空，返回null
        if (hashCircle.isEmpty()) {
            return null;
        }
        // 计算键的哈希值
        int hash = getHash(key);
        // 查找哈希环上大于等于该哈希值的所有节点
        SortedMap<Integer, String> tailMap = hashCircle.tailMap(hash);
        // 如果没有大于等于该哈希值的节点，则返回哈希环上的第一个节点（环形结构）
        Integer nodeHash = tailMap.isEmpty() ? hashCircle.firstKey() : tailMap.firstKey();
        // 返回节点名称
        return hashCircle.get(nodeHash);
    }

    /**
     * 计算哈希值
     * 使用FNV1_32_HASH算法计算字符串的哈希值
     * 
     * @param str 要计算哈希值的字符串
     * @return 32位整型哈希值
     */
    private int getHash(String str) {
        final int p = 16777619; // FNV_PRIME
        int hash = (int) 2166136261L; // FNV_OFFSET_BASIS
        // 计算FNV1哈希
        for (int i = 0; i < str.length(); i++) {
            hash = (hash ^ str.charAt(i)) * p;
        }
        // 混淆哈希值，提高分布均匀性
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        // 确保哈希值为正数
        if (hash < 0) {
            hash = Math.abs(hash);
        }
        return hash;
    }

}