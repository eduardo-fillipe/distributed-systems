package br.ufs.dcomp.eduard6.disciplines.ds.chat;

import java.io.IOException;

public class StartChatEndpoint {
    public static void main(String[] args) throws IOException {
        ChatEndpoint chatEndpoint = new ChatEndpoint();
        chatEndpoint.close();
    }
}
