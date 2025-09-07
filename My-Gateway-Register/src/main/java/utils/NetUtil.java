package utils;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 网络工具类
 * 提供获取本地IP地址的工具方法
 * 支持按照优先级匹配不同网段的IP地址
 */
public class NetUtil {

    /**
     * 匹配IP地址与前缀数组
     * 根据前缀数组的顺序，返回IP地址匹配的前缀索引
     * 
     * @param ip 要匹配的IP地址
     * @param prefix 前缀数组，如["*", "10", "172", "192", "127"]
     * @return 匹配的前缀在数组中的索引，如果不匹配则返回-1
     */
    private static int matchedIndex(String ip, String[] prefix) {
        for (int i = 0; i < prefix.length; i++) {
            String p = prefix[i];
            if ("*".equals(p)) { // *, assumed to be IP
                // 如果前缀是*，排除私有网段和回环地址
                if (ip.startsWith("127.") ||
                        ip.startsWith("10.") ||
                        ip.startsWith("172.") ||
                        ip.startsWith("192.")) {
                    continue;
                }
                return i;
            } else {
                // 如果IP地址以指定前缀开头，返回索引
                if (ip.startsWith(p)) {
                    return i;
                }
            }
        }
        // 如果没有匹配的前缀，返回-1
        return -1;
    }

    /**
     * 获取本地IP地址
     * 根据指定的IP优先级获取本地IP地址
     * 
     * @param ipPreference IP优先级字符串，格式如"*>10>172>192>127"，表示优先选择公网IP，然后是10网段，依次类推
     * @return 匹配优先级最高的本地IP地址，如果没有匹配则返回127.0.0.1
     */
    public static String getLocalIp(String ipPreference) {
        // 如果没有指定IP优先级，使用默认优先级
        if (ipPreference == null) {
            ipPreference = "*>10>172>192>127";
        }
        // 将IP优先级字符串分割为前缀数组
        String[] prefix = ipPreference.split("[> ]+");
        try {
            // 创建IP地址的正则表达式模式
            Pattern pattern = Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+");
            // 获取所有网络接口
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            String matchedIp = null;
            int matchedIdx = -1;
            // 遍历所有网络接口
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                // 跳过回环接口和虚拟接口
                if (ni.isLoopback() || ni.isVirtual()) {
                    continue;
                }
                // 获取接口的所有IP地址
                Enumeration<InetAddress> en = ni.getInetAddresses();
                // 遍历接口的所有IP地址
                while (en.hasMoreElements()) {
                    InetAddress addr = en.nextElement();
                    // 跳过回环地址、非站点本地地址和任意本地地址
                    if (addr.isLoopbackAddress() ||
                            !addr.isSiteLocalAddress() ||
                            addr.isAnyLocalAddress()) {
                        continue;
                    }
                    // 获取IP地址字符串
                    String ip = addr.getHostAddress();
                    // 使用正则表达式匹配IP地址格式
                    Matcher matcher = pattern.matcher(ip);
                    if (matcher.matches()) {
                        // 获取IP地址匹配的前缀索引
                        int idx = matchedIndex(ip, prefix);
                        if (idx == -1) {
                            continue;
                        }
                        // 如果是第一个匹配的IP地址，或者优先级更高，则更新匹配结果
                        if (matchedIdx == -1) {
                            matchedIdx = idx;
                            matchedIp = ip;
                        } else {
                            if (matchedIdx > idx) {
                                matchedIdx = idx;
                                matchedIp = ip;
                            }
                        }
                    }
                }
            }
            // 如果找到匹配的IP地址，返回它
            if (matchedIp != null)
                return matchedIp;
            // 如果没有找到匹配的IP地址，返回回环地址
            return "127.0.0.1";
        } catch (Exception e) {
            // 如果发生异常，返回回环地址
            return "127.0.0.1";
        }
    }

    /**
     * 获取本地IP地址
     * 使用默认的IP优先级获取本地IP地址
     * 默认优先级为"*>10>172>192>127"，表示优先选择公网IP
     * 
     * @return 匹配优先级最高的本地IP地址，如果没有匹配则返回127.0.0.1
     */
    public static String getLocalIp() {
        return getLocalIp("*>10>172>192>127");
    }

}
