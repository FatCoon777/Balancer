package ru;

public class Context {
    private final static ThreadLocal<String> ip = new ThreadLocal<>();

    public static void setIp(String newIp) {
        ip.set(newIp);
    }

    public static String getIp() {
        return ip.get();
    }
}
