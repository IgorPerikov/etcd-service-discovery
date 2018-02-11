package com.github.igorperikov.etcd.discovery.utility;

public class AddressPair extends Pair<String, Integer> {
    /**
     * this method consistent with the result of {@link #toString()} method
     * @param address string in format address:port
     */
    public static AddressPair fromString(String address) throws IllegalArgumentException {
        try {
            String[] parts = address.split(":");
            return of(parts[0], Integer.valueOf(parts[1]));
        } catch (Exception e) {
            throw new IllegalArgumentException("Wrong address parameter format, expecting 'address:port'", e);
        }
    }

    public static AddressPair of(String address, Integer port) {
        return new AddressPair(address, port);
    }

    private AddressPair(String address, Integer port) {
        super(address, port);
    }

    public String getAddress() {
        return getFirst();
    }

    public Integer getPort() {
        return getSecond();
    }

    /**
     * this method consistent with the expecting argument of {@link #fromString(String)} method
     * @return
     */
    @Override
    public String toString() {
        return getAddress() + ":" + getPort();
    }
}
