package com.github.igorperikov.etcd.discovery.service;

import com.coreos.jetcd.kv.PutResponse;
import com.github.igorperikov.etcd.discovery.domain.AddressPair;
import com.github.igorperikov.etcd.discovery.domain.ServiceDiscoveryRecord;
import com.github.igorperikov.etcd.discovery.etcd.EtcdClient;
import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Beta
public class ServiceDiscoveryClient {
    private static final String ETCD_NAMESPACE = "service-discovery";
    private static final String DELIMITER = "/";

    private final EtcdClient etcdClient;

    public ServiceDiscoveryClient(EtcdClient etcdClient) {
        this.etcdClient = etcdClient;
    }

    public CompletableFuture<PutResponse> register(@Nonnull ServiceDiscoveryRecord record) {
        String serviceName = record.getServiceName();
        String dc = record.getDatacenter();
        AddressPair address = record.getAddress();

        String key = buildServiceKey(serviceName, dc, address);
        return etcdClient.put(key, address.toString());
    }

    public CompletableFuture<List<AddressPair>> getAddresses(@Nonnull String serviceName, @Nonnull String dc) {
        validateServiceName(serviceName);
        Preconditions.checkArgument(dc != null && !dc.isEmpty(), "datacenter should be not-empty");

        return etcdClient
                .getAllByPrefix(buildServiceKeyWithDc(serviceName, dc))
                .thenApply(getResponse -> {
                    return getResponse.getKvs()
                            .stream()
                            .map(keyValue -> AddressPair.fromString(keyValue.getValue().toStringUtf8()))
                            .collect(Collectors.toList());
                });
    }

    public CompletableFuture<List<AddressPair>> getAddressesAnyDc(@Nonnull String serviceName) {
        validateServiceName(serviceName);

        return etcdClient.getAllByPrefix(buildServiceKeyWithoutDc(serviceName))
                .thenApply(getResponse -> {
                    return getResponse.getKvs()
                            .stream()
                            .map(keyValue -> AddressPair.fromString(keyValue.getValue().toStringUtf8()))
                            .collect(Collectors.toList());
                });
    }

    private void validateServiceName(String serviceName) throws IllegalArgumentException {
        Preconditions.checkArgument(serviceName != null && !serviceName.isEmpty(), "serviceName should be not-empty");
    }

    private String buildServiceKeyWithDc(String serviceName, String dc) {
        return buildServiceKeyWithoutDc(serviceName) + dc + DELIMITER;
    }

    private String buildServiceKeyWithoutDc(String serviceName) {
        return ETCD_NAMESPACE + DELIMITER + serviceName + DELIMITER;
    }

    private String buildServiceKey(String serviceName, String dc, AddressPair address) {
        return buildServiceKeyWithDc(serviceName, dc) + address.getAddress() + DELIMITER + address.getPort();
    }
}
