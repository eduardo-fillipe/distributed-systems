package br.ufs.dcomp.eduard6.disciplines.ds.chat;

import br.ufs.dcomp.eduard6.disciplines.ds.rabbit.RabbitHelper;
import com.google.gson.Gson;

import java.io.Closeable;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatEndpoint implements Closeable {
    private static final String BROADCAST_FUNOUT_NAME = "chat-broadcast";

    private final Logger logger = Logger.getLogger(ChatEndpoint.class.getName());
    private final Scanner scanner = new Scanner(System.in);
    private final String name;
    private final Gson gson = new Gson();

    public ChatEndpoint() throws IOException {
        logger.log(Level.INFO, "Iniciando EndPoint de Chat...");
        this.name = readName();
        if (name != null) {
            configureRabbitQueues();
            new Thread(new EndPointListener()).start();
            new EndPointSender(this).run();
        } else {
            logger.log(Level.INFO, "Nome não cadastrado... Saindo...");
        }
    }

    private String readName() {
        String newName;
        System.out.println("Digite o seu nome: ");
        while ((newName = scanner.nextLine()).isBlank());
        newName = '@' + newName;
        return newName;
    }

    private void configureRabbitQueues() throws IOException {
        RabbitHelper.getInstance().createFunout(BROADCAST_FUNOUT_NAME);
        RabbitHelper.getInstance().createQueue(name);
        RabbitHelper.getInstance().bindQueueToFunout(name, BROADCAST_FUNOUT_NAME);
    }

    @Override
    public void close() throws IOException {
        scanner.close();
        RabbitHelper.getInstance().close();
    }

    class EndPointListener implements Runnable {

        private void processMessage(Date date, MessagePackage pkt) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            if (!name.equals(pkt.getFrom())) {
                System.out.printf("MENSAGEM DE [%s] EM [%s]: %s%n", pkt.getFrom(),
                        date == null ? null :sdf.format(date), pkt.getMessage());
            }
        }

        @Override
        public void run() {
            try {
                RabbitHelper.getInstance().listenQueue(name, (consumerTag, message) ->

                    processMessage((Date) message.getProperties().getHeaders().get("send-timestamp"),
                            gson.fromJson(new String(message.getBody()), MessagePackage.class))

                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class EndPointSender implements Runnable {
        private final Logger endPointSenderLogger = Logger.getLogger(EndPointSender.class.getName());
        private final ChatEndpoint chatEndpoint;

        public EndPointSender(ChatEndpoint chatEndpoint) {
            this.chatEndpoint = chatEndpoint;
        }

        @Override
        public void run() {
            endPointSenderLogger.log(Level.INFO, "Iniciando Sender...");
            String message = "";
            endPointSenderLogger.log(Level.INFO, "Iniciando coleta e envio de mensagens...");

            do {
                try {
                    System.out.println("Digite a mensagem à ser enviada: ");
                    while ((message = scanner.nextLine()).isBlank());
                    sendMessage(toMessagePackage(message));
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error sending the message!", e);
                }
            } while (!"SAIR".equals(message));
            endPointSenderLogger.log(Level.INFO, "Sender encerrado.");
        }

        private MessagePackage toMessagePackage(String stringMessage) {
            MessagePackage messagePackage = new MessagePackage();
            messagePackage.setFrom(this.chatEndpoint.name);
            if (stringMessage.startsWith("@")) {
                int messageBegin = stringMessage.indexOf(" ");
                if (messageBegin == -1) throw new IllegalArgumentException("Message can not be empty!");
                messagePackage.setTo(stringMessage.substring(0, messageBegin));
                messagePackage.setMessage(stringMessage.substring(messageBegin + 1));
            } else {
                messagePackage.setMessage(stringMessage);
                messagePackage.setTo("@ALL");
            }

            return messagePackage;
        }

        private void sendMessage(MessagePackage pkt) throws IOException {
            if ("@ALL".equals(pkt.getTo())) {
                RabbitHelper.getInstance().funoutMessage(BROADCAST_FUNOUT_NAME, pkt);
            } else {
                RabbitHelper.getInstance().enqueueMessage(pkt.getTo(), pkt);
            }
        }
    }
}
