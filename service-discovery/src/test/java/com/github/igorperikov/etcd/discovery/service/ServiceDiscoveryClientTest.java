package com.github.igorperikov.etcd.discovery.service;

import com.github.igorperikov.etcd.discovery.EtcdTestClusterConfiguration;
import com.github.igorperikov.etcd.discovery.domain.AddressPair;
import com.github.igorperikov.etcd.discovery.domain.ServiceDiscoveryRecord;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ServiceDiscoveryClientTest extends EtcdTestClusterConfiguration {
    private final ServiceDiscoveryClient serviceDiscoveryClient = new ServiceDiscoveryClient(etcdClient);

    private final String serviceName = "test-service";
    private final String anotherServiceName = "another-test-service";

    @Before
    public void init() throws Exception {
        ServiceDiscoveryRecord record1 = ServiceDiscoveryRecord.newBuilder()
                .serviceName(serviceName)
                .datacenter("dc-1")
                .port(1)
                .build();
        ServiceDiscoveryRecord record2 = ServiceDiscoveryRecord.newBuilder()
                .serviceName(serviceName)
                .datacenter("dc-2")
                .port(2)
                .build();
        ServiceDiscoveryRecord record3 = ServiceDiscoveryRecord.newBuilder()
                .serviceName(serviceName)
                .port(3)
                .build();
        ServiceDiscoveryRecord record4 = ServiceDiscoveryRecord.newBuilder()
                .serviceName(anotherServiceName)
                .port(4)
                .build();
        ServiceDiscoveryRecord record5 = ServiceDiscoveryRecord.newBuilder()
                .serviceName(anotherServiceName)
                .port(5)
                .build();

        CompletableFuture.allOf(
                serviceDiscoveryClient.register(record1),
                serviceDiscoveryClient.register(record2),
                serviceDiscoveryClient.register(record3),
                serviceDiscoveryClient.register(record4),
                serviceDiscoveryClient.register(record5)
        ).get();
    }

    @Test
    public void shouldCorrectlyReturnAnyDcAddresses() throws Exception {
        List<AddressPair> addresses = serviceDiscoveryClient.getAddressesAnyDc(serviceName).get();
        Assert.assertEquals(3, addresses.size());
    }

    @Test
    public void shouldCorrectlyReturnConcreteDcAddresses() throws Exception {
        List<AddressPair> addresses = serviceDiscoveryClient.getAddresses(serviceName, "dc-2").get();
        Assert.assertEquals(1, addresses.size());
    }
}
