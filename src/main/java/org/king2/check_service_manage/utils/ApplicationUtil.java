package org.king2.check_service_manage.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 描述:一些小工具方法
 * @author 刘梓江
 * @Date 2021/4/2 14:17
 */
public class ApplicationUtil {

    private static  final Logger logger= LoggerFactory.getLogger(ApplicationUtil.class);

    /**
     * 打印获取日志信息
     * @param t
     * @return
     */
    public static String getExceptionMessage(Throwable t) {
        StringWriter stringWriter= new StringWriter();
        PrintWriter writer= new PrintWriter(stringWriter);
        t.printStackTrace(writer);
        StringBuffer buffer= stringWriter.getBuffer();
        return buffer.toString();
    }

    /**
     * 校验ip是否正确
     * @param ip
     * @return
     */
    public static boolean isIp(String ip) {
        StringBuilder regex = new StringBuilder();
        regex.append("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.");
        regex.append("((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.");
        regex.append("((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.");
        regex.append("((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
        Pattern pattern = Pattern.compile(regex.toString());
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }

    /**
     * 校验端口是否正确
     * @param port
     * @return
     */
    public static boolean isPort(String port){
        Pattern pattern = Pattern.compile("([0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-4]\\d{4}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])");
        Matcher matcher = pattern.matcher(port);
        return matcher.matches();
    }

    /**
     * 获取本机外网ip
     * @return
     */
    public static String getHostInternetIp() {
        String ip = "";
        String chinaz = "http://ip.chinaz.com";

        StringBuilder inputLine = new StringBuilder();
        String read = "";
        URL url = null;
        HttpURLConnection urlConnection = null;
        BufferedReader in = null;
        try {
            url = new URL(chinaz);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            while ((read = in.readLine()) != null) {
                inputLine.append(read + "\r\n");
            }
            Pattern p = Pattern.compile("\\<dd class\\=\"fz24\">(.*?)\\<\\/dd>");
            Matcher m = p.matcher(inputLine.toString());
            if (m.find()) {
                String ipstr = m.group(1);
                ip = ipstr;
            }
        } catch (Exception e) {
            logger.warn(ApplicationUtil.getExceptionMessage(e));
        }finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.warn(ApplicationUtil.getExceptionMessage(e));
                }
            }
        }

        //二次尝试获取公网IP
        if (StringUtils.isEmpty(ip)) {
            try {
                URL getInternetIpUrl = new URL("http://checkip.amazonaws.com");
                in = new BufferedReader(new InputStreamReader(getInternetIpUrl.openStream()));
                ip = in.readLine(); //you get the IP as a String
                logger.info("二次获取公网IP："+ip);
            } catch (IOException e) {
                logger.warn(ApplicationUtil.getExceptionMessage(e));
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        logger.warn(ApplicationUtil.getExceptionMessage(e));
                    }
                }
            }
        }
        return ip;
    }


    /**
     * 获取本机内网ip
     * @return
     */
    public static String getHostIntranetIp(){
        String ipInfo="";
        try{
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()){
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()){
                    InetAddress ip = (InetAddress) addresses.nextElement();
                    if (ip != null
                            && ip instanceof Inet4Address
                            && !ip.isLoopbackAddress() //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                            && ip.getHostAddress().indexOf(":")==-1){
                        ipInfo=ip.getHostAddress();
                    }
                }
            }
        }catch(Exception e){
            logger.warn(ApplicationUtil.getExceptionMessage(e));
        }
        return ipInfo;
    }


    /**
     * 判断主机端口
     * @param hostName
     * @param port
     * @return boolean - true/false
     */
    public static boolean isSocketHostPortAlive(String hostName, String port) {
        boolean isAlive = false;
        // 超时设置，单位毫秒
        int timeout = 1000;
        try {
            // 创建一个套接字
            SocketAddress socketAddress = new InetSocketAddress(hostName, Integer.parseInt(port));
            Socket socket = new Socket();
            socket.connect(socketAddress, timeout);
            socket.close();
            isAlive = true;

        } catch (SocketTimeoutException exception) {
            logger.warn("SocketTimeoutException " + hostName + ":" + port + ". " + exception.getMessage());
        } catch (IOException exception) {
            logger.warn("IOException - Unable to connect to " + hostName + ":" + port + ". " + exception.getMessage());
        }catch (Exception exception){
            logger.warn(ApplicationUtil.getExceptionMessage(exception));
        }
        return isAlive;
    }

    /**
     * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址;
     */
    public  static String getHostIpAddress(HttpServletRequest request) {
        // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
        String ipAddress = null;
        //ipAddress = request.getRemoteAddr();
        ipAddress = request.getHeader("X-Real-IP");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("x-forwarded-for");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inet.getHostAddress();
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) { //"***.***.***.***".length() = 15
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }


    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url
     *            发送请求的 URL
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url,String publicKey) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("publicKey", publicKey);

            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());

            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.warn("发送 POST 请求出现异常！", e);
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }

}
