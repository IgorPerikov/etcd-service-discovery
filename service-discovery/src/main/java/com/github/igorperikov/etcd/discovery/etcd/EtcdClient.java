package com.github.igorperikov.etcd.discovery.etcd;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.KV;
import com.coreos.jetcd.Lease;
import com.coreos.jetcd.data.ByteSequence;
import com.coreos.jetcd.kv.GetResponse;
import com.coreos.jetcd.kv.PutResponse;
import com.coreos.jetcd.options.GetOption;
import com.coreos.jetcd.options.PutOption;
import com.google.common.annotations.Beta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Beta
public class EtcdClient implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(EtcdClient.class);

    private final Client etcdClient;
    private final KV kvClient;
    private final Lease leaseClient;
    private final long ttl;

    private Long leaseId;

    private EtcdClient(Collection<String> endpoints, long ttl) {
        etcdClient = Client.builder().endpoints(endpoints).build();
        kvClient = etcdClient.getKVClient();
        leaseClient = etcdClient.getLeaseClient();
        this.ttl = ttl;
    }

    public CompletableFuture<PutResponse> put(String key, String value) {
        return leaseClient
                .grant(ttl)
                .thenCompose(leaseGrantResponse -> {
                    leaseId = leaseGrantResponse.getID();
                    leaseClient.keepAlive(leaseId);
                    ByteSequence keyByteSequence = ByteSequence.fromString(key);
                    ByteSequence valueByteSequence = ByteSequence.fromString(value);
                    return kvClient.put(
                            keyByteSequence,
                            valueByteSequence,
                            PutOption.newBuilder()
                                    .withLeaseId(leaseId)
                                    .build()
                    );
                });
    }

    public CompletableFuture<GetResponse> getAllByPrefix(String prefix) {
        ByteSequence prefixByteSequence = ByteSequence.fromString(prefix);
        GetOption prefixOption = GetOption.newBuilder()
                .withPrefix(prefixByteSequence)
                .build();
        return kvClient.get(prefixByteSequence, prefixOption);
    }

    @Override
    public void close() throws ExecutionException, InterruptedException {
        if (leaseId != null) {
            log.info("Revoking lease with id={}", leaseId);
            leaseClient.revoke(leaseId).get();
        }
        etcdClient.close();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String scheme = "http";
        private Set<String> endpoints = new HashSet<>();
        private long ttl = 5;

        private Builder() {
        }

        public EtcdClient build() throws IllegalStateException {
            if (endpoints.isEmpty()) {
                throw new IllegalStateException("Provide at least one endpoint!");
            }
            return new EtcdClient(
                    endpoints.stream()
                            .map(endpoint -> scheme + "://" + endpoint)
                            .collect(Collectors.toList()),
                    ttl
            );
        }

        public Builder scheme(String scheme) {
            this.scheme = scheme;
            return this;
        }

        public Builder endpoint(
                @Nonnull String address,
                int port
        ) {
            this.endpoints.add(address + ":" + port);
            return this;
        }

        public Builder ttlSeconds(long ttlSeconds) {
            this.ttl = ttlSeconds;
            return this;
        }
    }
}
