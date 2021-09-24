package com.bytecode.websockets.springboot.websocketsspringboot.handler;

import com.bytecode.websockets.springboot.websocketsspringboot.model.Message;
import com.bytecode.websockets.springboot.websocketsspringboot.model.MessageRecevided;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class IntegracionHandler extends TextWebSocketHandler {
    private Log logger = LogFactory.getLog(getClass());

    @Autowired
    @Qualifier("ids")
    public List<String> ids;

    private static ArrayList<WebSocketSession> sessions = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //Creamos el ID de sesion
        String id = UUID.randomUUID().toString().replace("-","").substring(0, 5);
        logger.info("HANDLE AFTER CONNECTION " + id);
        session.getAttributes().put("idSesion", id);
        ids.add(id);

        //Le mandamos el Id de Su sesion
        Message message = new Message();

        message.setType("SESION_ID");
        message.setBody(id);

        session.sendMessage(new TextMessage(message.toString()));
        sessions.add(session);

        //Le avisamos a todos los usuarios menos a el de que hay un nuevo usuario
        sessions
                .stream().filter(webSocketSession -> !webSocketSession.getAttributes().get("idSesion").toString().equalsIgnoreCase(id))
                .forEach(webSocketSession -> {
            Message messageNewUser = new Message();
            messageNewUser.setBody(id);
            messageNewUser.setType("NEW_USER");
            try {
                webSocketSession.sendMessage(new TextMessage(messageNewUser.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions = (ArrayList<WebSocketSession>) sessions.stream()
                            .filter(webSocketSession -> !webSocketSession.getAttributes().get("idSesion").toString().equalsIgnoreCase(session.getAttributes().get("idSesion").toString()))
                            .collect(Collectors.toList());

        //Le avisamos a todos los usuarios menos a el de que un usuario se salio
        sessions
                .stream().filter(webSocketSession -> !webSocketSession.getAttributes().get("idSesion").toString().equalsIgnoreCase(session.getAttributes().get("idSesion").toString()))
                .forEach(webSocketSession -> {
                    Message messageNewUser = new Message();
                    messageNewUser.setBody(session.getAttributes().get("idSesion").toString());
                    messageNewUser.setType("CLOSE_USER");
                    try {
                        webSocketSession.sendMessage(new TextMessage(messageNewUser.toString()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        logger.info("HANDLE MESSAGE");
        logger.info("MESSAGE= " + message.toString());

        ObjectMapper objectMapper = new ObjectMapper();
        MessageRecevided messageRecevided = objectMapper.readValue(message.getPayload().toString(), MessageRecevided.class);

        //Send
        String idSend = messageRecevided.getId();

        //Intercambio
        messageRecevided.setId(session.getAttributes().get("idSesion").toString());

        //Le mandamos el Id de Su sesion
        Message messageTo = new Message();

        messageTo.setType("NEW_MESSAGE");
        messageTo.setBody(messageRecevided);

        sendMessageToUser(idSend, new TextMessage(messageTo.toString()));
    }

    public void sendMessageToUser(String idSesion, TextMessage message) {
        for (WebSocketSession session: sessions) {
            if (session.getAttributes().get("idSesion").toString().equalsIgnoreCase(idSesion)) {
                try {
                    if (session.isOpen()) {
                        session.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
