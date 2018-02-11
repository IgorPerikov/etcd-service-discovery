package com.github.igorperikov.etcd.discovery.etcd;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.KV;
import com.coreos.jetcd.Lease;
import com.coreos.jetcd.Watch;
import com.coreos.jetcd.data.ByteSequence;
import com.coreos.jetcd.kv.GetResponse;
import com.coreos.jetcd.kv.PutResponse;
import com.coreos.jetcd.options.PutOption;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class EtcdClient implements AutoCloseable {
    private final Client etcdClient;
    private final KV kvClient;
    private final Watch watchClient;
    private final Lease leaseClient;

    private EtcdClient(Collection<String> endpoints) {
        etcdClient = Client.builder().endpoints(endpoints).build();
        kvClient = etcdClient.getKVClient();
        watchClient = etcdClient.getWatchClient();
        leaseClient = etcdClient.getLeaseClient();
    }

    public CompletableFuture<PutResponse> put(String key, String value) {
        ByteSequence keyByteSequence = ByteSequence.fromString(key);
        ByteSequence valueByteSequence = ByteSequence.fromString(value);
        return kvClient.put(
                keyByteSequence,
                valueByteSequence,
                PutOption.newBuilder().build()
        );
    }

    public CompletableFuture<GetResponse> get(String key) {
        ByteSequence keyByteSequence = ByteSequence.fromString(key);
        return kvClient.get(keyByteSequence);
    }

    @Override
    public void close() throws Exception {
        etcdClient.close();
        kvClient.close();
        watchClient.close();
        leaseClient.close();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String scheme = "http";
        private Set<String> endpoints = new HashSet<>();

        private Builder() {
        }

        public EtcdClient build() throws IllegalStateException {
            if (endpoints.isEmpty()) {
                throw new IllegalStateException("Provide at least one endpoint!");
            }
            return new EtcdClient(
                    endpoints.stream()
                            .map(endpoint -> scheme + "://" + endpoint)
                            .collect(Collectors.toList())
            );
        }

        public Builder withScheme(String scheme) {
            this.scheme = scheme;
            return this;
        }

        public Builder withEndpoint(
                @Nonnull String address,
                int port
        ) {
            this.endpoints.add(address + ":" + port);
            return this;
        }
    }
}
