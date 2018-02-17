package com.github.igorperikov.etcd.discovery.etcd;

import com.coreos.jetcd.kv.GetResponse;
import com.github.igorperikov.etcd.discovery.EtcdTestClusterConfiguration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LeaseExpirationTest extends EtcdTestClusterConfiguration {
    protected static EtcdClient etcdClient2 = EtcdClient.newBuilder()
            .ttlSeconds(1)
            .endpoint(
                    etcdContainer.getContainerIpAddress(),
                    etcdContainer.getMappedPort(2379)
            ).build();

    @Test
    public void leaseShouldExpireOnClosingClient() throws Exception {
        String key = "lease-key";
        etcdClient.put(key, "value").get();
        etcdClient.close();
        GetResponse getResponse = etcdClient2.getAllByPrefix(key).get();
        assertEquals(0, getResponse.getCount());
    }
}
