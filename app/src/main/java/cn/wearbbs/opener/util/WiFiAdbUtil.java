package cn.wearbbs.opener.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class WiFiAdbUtil {
    /**
     * @return 获取ip地址
     */
    public static String getIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIp = intf
                        .getInetAddresses(); enumIp.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIp.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {

                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static boolean isMobile(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            return false;
        }
        if(activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        } else {
            return false;
        }
    }
    public static int execRootCmdCilent(String cmd) {
        int result = -1;
        DataOutputStream dos = null;
        try {
            Process p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            p.waitFor();
            result = p.exitValue();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    // 判断机器Android是否已经root，即是否获取root权限
    public static boolean haveRoot() {
        boolean haveRoot = false;
        int ret = execRootCmdCilent("echo test"); // 通过执行测试命令来检测
        if (ret != -1) {
            haveRoot = true;
        }
        return haveRoot;
    }

    /**
     * 打开adb连接
     */
    public static void openAdb() {
        execShell("setprop service.adb.tcp.port 5555");//  Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c", "setprop service.adb.tcp.port 5555"});
        execShell("stop adbd");
        execShell("start adbd");
        execShell("exit");
    }

    /**
     * 断开adb连接
     */
    public static void closeAdb() {
        execShell("stop adbd");
        execShell("exit");
    }

    /**
     * 是否已打开adb连接
     */
    public static boolean isOpen() {
        String tmp = execRootCmd("getprop service.adb.tcp.port");
        execRootCmd("exit");
        return tmp.matches("^[1-9]\\\\d*\\$") && !tmp.contains("-1");
    }
    /**
     * 在su文件中写入命令
     *
     * @param str
     */
    public static void execShell(String str) {
        try {
            // 权限设置
            Process p = Runtime.getRuntime().exec("su");
            // 获取输出流
            OutputStream outputStream = p.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(
                    outputStream);
            // 将命令写入
            dataOutputStream.writeBytes(str);
            // 提交命令
            dataOutputStream.flush();
            // 关闭流操作
            dataOutputStream.close();
            outputStream.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    // 执行命令并且输出结果
    public static String execRootCmd(String cmd) {
        String result = "";
        DataOutputStream dos = null;
        DataInputStream dis = null;

        try {
            Process p = Runtime.getRuntime().exec("su");// 经过Root处理的android系统即有su命令
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            String line = null;
            while ((line = dis.readLine()) != null) {
                result += line;
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

}
