package memcached;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Server {
    public final String hostName;
    public final int port;
    public final String serverId;
    private Socket socket;
    private BufferedInputStream inputStream;
    private BufferedOutputStream outputStream;

    private boolean started;

    public Server(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
        this.serverId = String.format("%s:%d", this.hostName, this.port);
        this.started = false;
    }

    public Server(String serverId) {
        var splitted = serverId.split(":");
        this.hostName = splitted[0];
        this.port = Integer.parseInt(splitted[1]);
        this.serverId = serverId;
        this.started = false;
    }

    public boolean start() {
        if (!this.started) {

            try {
                this.socket = new Socket(this.hostName, this.port);
                this.inputStream = new BufferedInputStream(socket.getInputStream());
                this.outputStream = new BufferedOutputStream(socket.getOutputStream());
                MemcachedClient._logger.info(String.format("Server %s connected", this.serverId));
                this.started = true;
                return true;
            } catch (IOException e) {
                MemcachedClient._logger
                        .severe(String.format("Server %s not started: %s", this.serverId, e.getMessage()));
                this.started = false;
                this.socket = null;
                this.outputStream = null;
                this.inputStream = null;
            }
        } else {
            MemcachedClient._logger.info(String.format("Server %s already connected", this.serverId));
        }
        return this.started;
    }

    public boolean isStarted() {
        return this.started;
    }

    public void stop() {
        try {
            this.socket.close();
        } catch (IOException e) {
        }
        try {
            this.inputStream.close();
        } catch (IOException e) {
        }
        try {
            this.outputStream.close();
        } catch (IOException e) {
        }
    }

}