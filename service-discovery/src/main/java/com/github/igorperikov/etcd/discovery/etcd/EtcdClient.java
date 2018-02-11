package com.github.igorperikov.etcd.discovery.etcd;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.KV;
import com.coreos.jetcd.Lease;
import com.coreos.jetcd.Watch;
import com.coreos.jetcd.data.ByteSequence;
import com.coreos.jetcd.kv.GetResponse;
import com.coreos.jetcd.kv.PutResponse;
import com.coreos.jetcd.options.PutOption;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class EtcdClient implements AutoCloseable {
    private final Client etcdClient;
    private final KV kvClient;
    private final Watch watchClient;
    private final Lease leaseClient;

    // TODO: provide builder with scheme providing
    public EtcdClient() {
        this("http://localhost:2379");
    }

    public EtcdClient(String endpoint) {
        this(Collections.singletonList(endpoint));
    }

    public EtcdClient(Collection<String> endpoints) {
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
}
