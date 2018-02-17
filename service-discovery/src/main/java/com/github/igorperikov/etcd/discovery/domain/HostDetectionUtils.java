package com.github.igorperikov.etcd.discovery.domain;

import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;

class HostDetectionUtils {
    private static final InetUtils inetUtils = new InetUtils(new InetUtilsProperties());

    static String getAddress(boolean preferIpAddress) {
        InetUtils.HostInfo hostInfo = inetUtils.findFirstNonLoopbackHostInfo();
        if (preferIpAddress) {
            return hostInfo.getIpAddress();
        } else {
            return hostInfo.getHostname();
        }
    }
}
