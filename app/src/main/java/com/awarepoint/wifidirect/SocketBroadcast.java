package com.awarepoint.wifidirect;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;

/*
 * This class tries to send a broadcast UDP packet over your wifi network to discover the boxee service.
 */

public class SocketBroadcast extends Thread {
    private WifiP2pInfo wifip2pinfo;

    private static final String TAG = "Discovery";
    private static final String REMOTE_KEY = "b0xeeRem0tE!";
    private static final int DISCOVERY_PORT = 7203;

    private static final int TIMEOUT_MS = 500;

    // TODO: Vary the challenge, or it's not much of a challenge :)
    private static final String mChallenge = "myvoice";
    private WifiManager mWifi;

    SocketBroadcast(Context context, WifiP2pInfo wifip2pinfo) {
        try {
            this.wifip2pinfo =  wifip2pinfo;
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo w = wifi.getConnectionInfo();
            Log.d(MainActivity.TAG, w.toString());
            mWifi = wifi;
        }catch(Exception e){
            Log.e("SocketBroadcast", e.getMessage());
            e.printStackTrace();
        }
    }


    Thread thread = new Thread(new Runnable() {
        public void run() {
            try{


                DataOutputStream  dataOutputStream;
                ServerSocket serverSocket = new ServerSocket(DISCOVERY_PORT);
                Socket socket = new Socket();
                try {
                    InetAddress broadcastAddress = wifip2pinfo.groupOwnerAddress;

                    socket.bind(null);
                    socket.connect((new InetSocketAddress(wifip2pinfo.groupOwnerAddress.getHostAddress(), DISCOVERY_PORT)), 500);

                    String toSend = "String to send";
                    //toSend += "\0";

                    dataOutputStream =new DataOutputStream (socket.getOutputStream());
                    dataOutputStream.writeBytes(toSend);
                    dataOutputStream.flush(); // Send off the data


                    Thread.sleep(5000);

                    dataOutputStream.close();
                    socket.close();
                    serverSocket.close();
                }catch(Exception e){
                    Log.e(TAG,e.getMessage());
                    e.printStackTrace();
                }finally {

                    if(serverSocket != null){
                        if(!serverSocket.isClosed()){
                            serverSocket.close();
                        }
                    }
                    if(socket != null){
                        if(socket.isConnected()){
                           socket.close();
                        }
                    }
                }


            } catch (IOException e) {
                Log.e(TAG, "Could not send discovery request", e);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        }
    });

    /**
     * Send a broadcast UDP packet containing a request for boxee services to
     * announce themselves.
     *
     * @throws IOException
     */
    private void sendDiscoveryRequest(DatagramSocket socket, InetAddress broadcastAddress) throws IOException {
        String data = "Hola Mundo!!";
        Log.d(TAG, "Sending data " + data);

        DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(),
                broadcastAddress, DISCOVERY_PORT);
        socket.send(packet);
    }

    /**
     * Calculate the broadcast IP we need to send the packet along. If we send it
     * to 255.255.255.255, it never gets sent. I guess this has something to do
     * with the mobile network not wanting to do broadcast.
     */
    private InetAddress getBroadcastAddress() throws IOException {
        DhcpInfo dhcp = mWifi.getDhcpInfo();
        if (dhcp == null) {
            Log.d(TAG, "Could not get dhcp info");
            return null;
        }

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

    /**
     * Listen on socket for responses, timing out after TIMEOUT_MS
     *
     * @param socket
     *          socket on which the announcement request was sent
     * @throws IOException
     */
    private void listenForResponses(DatagramSocket socket) throws IOException {
        byte[] buf = new byte[1024];
        try {
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String s = new String(packet.getData(), 0, packet.getLength());
                Log.d(TAG, "Received response " + s);
            }
        } catch (SocketTimeoutException e) {
            Log.d(TAG, "Receive timed out");
        }
    }

    /**
     * Calculate the signature we need to send with the request. It is a string
     * containing the hex md5sum of the challenge and REMOTE_KEY.
     *
     * @return signature string
     */
    private String getSignature(String challenge) {
        MessageDigest digest;
        byte[] md5sum = null;
        try {
            digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(challenge.getBytes());
            digest.update(REMOTE_KEY.getBytes());
            md5sum = digest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        StringBuffer hexString = new StringBuffer();
        for (int k = 0; k < md5sum.length; ++k) {
            String s = Integer.toHexString((int) md5sum[k] & 0xFF);
            if (s.length() == 1)
                hexString.append('0');
            hexString.append(s);
        }
        return hexString.toString();
    }


}