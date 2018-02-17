package com.github.igorperikov.etcd.discovery.domain;

import com.google.common.annotations.Beta;

@Beta
public class ServiceDiscoveryRecord {
    private final String serviceName;
    private final String datacenter;
    private final AddressPair address;

    private ServiceDiscoveryRecord(String serviceName, String datacenter, AddressPair address) {
        this.serviceName = serviceName;
        this.datacenter = datacenter;
        this.address = address;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getDatacenter() {
        return datacenter;
    }

    public AddressPair getAddress() {
        return address;
    }

    public static class Builder {
        private String serviceName;
        private String datacenter = "default-dc";
        private int port = 80;
        private boolean preferIpAddress = true;

        private Builder() {
        }

        public ServiceDiscoveryRecord build() {
            return new ServiceDiscoveryRecord(
                    serviceName,
                    datacenter,
                    AddressPair.of(HostDetectionUtils.getAddress(preferIpAddress), port)
            );
        }

        public Builder serviceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public Builder datacenter(String datacenter) {
            this.datacenter = datacenter;
            return this;
        }

        public Builder preferIpAddress(boolean preferIpAddress) {
            this.preferIpAddress = preferIpAddress;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }
    }
}
