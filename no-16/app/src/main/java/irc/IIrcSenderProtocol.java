package irc;

import java.io.IOException;

public interface IIrcSenderProtocol {

    void setNickName(String nickName) throws IOException;

    void joinChannel(String channel) throws IOException;

    void sendRawMessage(String message) throws IOException;

    void printMessage(String message) throws IOException;

}