package com.example.se2_exploding_kittens.Network;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class IPUtil {

    public static short getSubnetMaskCidr()  {
        try{
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements())
            {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (networkInterface.isLoopback())
                    continue;

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses())
                {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    // If broadcast address is null, this is a mobile network interface.
                    if (broadcast == null)
                        continue;
                    return interfaceAddress.getNetworkPrefixLength();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static InetAddress getDeviceIP()  {
        try{
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements())
            {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (networkInterface.isLoopback())
                    continue;

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses())
                {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    // If broadcast address is null, this is a mobile network interface.
                    if (broadcast == null)
                        continue;
                    return interfaceAddress.getAddress();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static InetAddress getLocalBroadcastAddress()  {
        try{
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements())
            {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (networkInterface.isLoopback())
                    continue;

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses())
                {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    // If broadcast address is null, this is a mobile network interface.
                    if (broadcast == null)
                        continue;
                    return broadcast;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }
}
