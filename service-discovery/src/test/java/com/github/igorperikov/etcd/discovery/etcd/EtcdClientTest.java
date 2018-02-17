package com.github.igorperikov.etcd.discovery.etcd;

import com.coreos.jetcd.kv.GetResponse;
import com.github.igorperikov.etcd.discovery.EtcdTestClusterConfiguration;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;

public class EtcdClientTest extends EtcdTestClusterConfiguration {
    @Test
    public void shouldCorrectlyReturnStoredValue() throws Exception {
        String key = "etcd-key";
        String storedValue = "42";

        CompletableFuture<GetResponse> getCF = etcdClient
                .put(key, storedValue)
                .thenCompose(putResponse -> etcdClient.getAllByPrefix(key));
        String retrievedValue = getCF.get().getKvs().get(0).getValue().toStringUtf8();

        assertEquals(storedValue, retrievedValue);
    }

    @Test
    public void prefixQueryShouldReturnAllAvailableValues() throws Exception {
        String prefix = "etcd-prefix";
        String key1 = prefix + "1";
        String key2 = prefix + "2";
        String key3 = prefix + "3";
        String value = "42";

        GetResponse getResponse = CompletableFuture.allOf(
                etcdClient.put(key1, value),
                etcdClient.put(key2, value),
                etcdClient.put(key3, value)
        ).thenCompose(putResponse -> etcdClient.getAllByPrefix(prefix)).get();

        assertEquals(3, getResponse.getCount());
    }

    @Test
    public void leaseQueriesShouldExpireAfterClosingClient() {

    }
}
