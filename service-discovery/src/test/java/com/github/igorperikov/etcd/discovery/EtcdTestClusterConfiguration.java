package com.github.igorperikov.etcd.discovery;

import com.github.igorperikov.etcd.discovery.etcd.EtcdClient;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.testcontainers.containers.GenericContainer;

public class EtcdTestClusterConfiguration {
    @ClassRule
    public static GenericContainer etcdContainer =
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

    protected static EtcdClient etcdClient;

    @BeforeClass
    public static void initClient() {
        etcdClient = EtcdClient.newBuilder()
                .ttlSeconds(1)
                .endpoint(
                        etcdContainer.getContainerIpAddress(),
                        etcdContainer.getMappedPort(2379)
                ).build();
    }
}
