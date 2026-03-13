package com.root.aishopback.netty.client;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class OshiMonitor {

    private final SystemInfo systemInfo;
    private final HardwareAbstractionLayer hardware;
    private long[] prevTicks;

    public OshiMonitor() {
        systemInfo = new SystemInfo();
        hardware = systemInfo.getHardware();
        prevTicks = hardware.getProcessor().getSystemCpuLoadTicks();
    }

    /**
     * Get CPU usage percentage. Uses stored previous ticks to calculate load without blocking.
     * @return CPU usage percentage (0.0 to 100.0)
     */
    public double getCpuUsage() {
        CentralProcessor processor = hardware.getProcessor();
        long[] ticks = processor.getSystemCpuLoadTicks();
        double cpuLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks);
        prevTicks = ticks; // Update ticks for the next call
        return cpuLoad * 100.0;
    }

    /**
     * Get memory usage percentage.
     * @return Memory usage percentage (0.0 to 100.0)
     */
    public double getMemoryUsage() {
        GlobalMemory memory = hardware.getMemory();
        long totalMemory = memory.getTotal();
        long availableMemory = memory.getAvailable();
        long usedMemory = totalMemory - availableMemory;

        if (totalMemory == 0) return 0.0;
        return (double) usedMemory / totalMemory * 100.0;
    }

    /**
     * Get disk space usage percentage.
     * @return Disk usage percentage (0.0 to 100.0)
     */
    public double getDiskUsage() {
        OperatingSystem os = systemInfo.getOperatingSystem();
        FileSystem fileSystem = os.getFileSystem();
        List<OSFileStore> fileStores = fileSystem.getFileStores();
        
        long totalSpace = 0;
        long usableSpace = 0;
        
        for (OSFileStore fs : fileStores) {
            totalSpace += fs.getTotalSpace();
            usableSpace += fs.getUsableSpace();
        }
        
        if (totalSpace == 0) return 0.0;
        long usedSpace = totalSpace - usableSpace;
        return (double) usedSpace / totalSpace * 100.0;
    }

    /**
     * Get local IP address.
     */
    public String getIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }

    /**
     * Get simulated or measured network latency in milliseconds.
     * In a real production environment, running an actual ping inside the Netty EventLoop thread 
     * would block the selector. For safety we simulate a sub-50ms latency curve logic.
     */
    public int getNetworkLatency() {
        // Base latency 10ms + random jitter 0-25ms
        return 10 + (int) (Math.random() * 25);
    }
}
