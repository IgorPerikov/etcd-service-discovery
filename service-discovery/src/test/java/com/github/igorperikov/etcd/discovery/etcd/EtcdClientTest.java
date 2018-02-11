package com.github.igorperikov.etcd.discovery.etcd;

import com.coreos.jetcd.kv.GetResponse;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;

public class EtcdClientTest {
    @ClassRule
    public static GenericContainer etcd =
            new GenericContainer("gcr.io/etcd-development/etcd:latest")
                    .withExposedPorts(2379, 2380)
                    .withCommand(
                            "/usr/local/bin/etcd " +
                                    "--name node1 " +
                                    "--listen-peer-urls http://0.0.0.0:2380 " +
                                    "--listen-client-urls http://0.0.0.0:2379 " +
                                    "--advertise-client-urls http://0.0.0.0:2379 " +
                                    "--initial-cluster node1=http://0.0.0.0:2380 " +
                                    "--initial-advertise-peer-urls http://0.0.0.0:2380"
                    );

    private static EtcdClient etcdClient;

    @BeforeClass
    public static void initClient() {
        etcdClient = EtcdClient.newBuilder()
                .withEndpoint(
                        etcd.getContainerIpAddress(),
                        etcd.getMappedPort(2379)
                ).build();
    }

    @Test
    public void shouldCorrectlyReturnStoredValue() throws Exception {
        String key = "key";
        String storedValue = "42";

        CompletableFuture<GetResponse> getCF = etcdClient
                .put(key, storedValue)
                .thenCompose(putResponse -> etcdClient.get(key));
        String retrievedValue = getCF.get().getKvs().get(0).getValue().toStringUtf8();
        assertEquals(storedValue, retrievedValue);
    }
}
