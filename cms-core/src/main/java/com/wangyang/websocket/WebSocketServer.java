package com.wangyang.websocket;

import com.alibaba.fastjson.JSONObject;

import com.wangyang.common.BaseResponse;
import com.wangyang.common.exception.ObjectException;
import com.wangyang.pojo.authorize.UserDetailDTO;
import com.wangyang.util.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangyang
 * @date 2021/4/29
 */
@Slf4j
@Component
@ServerEndpoint("/ws/{token}")
public class WebSocketServer {
    private Session session; //客户端会话
    private UserDetailDTO userDetailDTO;
//    private static final Map<String, WebSocketSession> users  =  new HashMap<String, WebSocketSession>();  //Map来存储WebSocketSession，key用USER_ID 即在线用户列表
//    private static final String USER_ID = "WEBSOCKET_USERID";   //对应监听器从的key

    private static TokenProvider tokenProvider;

    @Autowired
    public  void setTokenProvider(TokenProvider tokenProvider) {
        WebSocketServer.tokenProvider = tokenProvider;
    }

    //存放每个客户端的连接会话
    public static ConcurrentHashMap<String,WebSocketServer> clients = new ConcurrentHashMap<>();

    //开启连接
    //存入连接回话中
    @OnOpen
    public void onOpen(Session session, @PathParam( "token") String token){
        this.session = session;
        boolean validateToken = tokenProvider.validateToken(token);
        if(!validateToken){
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            throw new ObjectException("ws 失败");
        }
        UserDetailDTO userDetailDTO = tokenProvider.getAuthentication(token);
        if(clients.containsKey(userDetailDTO.getUsername())){
            if(clients.get(userDetailDTO.getUsername()).session.isOpen()){
                try {
                    clients.get(userDetailDTO.getUsername()).session.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        sendMessageToUsers(userDetailDTO.getUsername()+"上线了！");
        System.out.println("当前的uuid为："+userDetailDTO.getUsername());
        this.userDetailDTO=userDetailDTO;
        clients.put(userDetailDTO.getUsername(),this);
    }

    //发送消息
    @OnMessage
    public void OnMessage(String msg, @PathParam( "token") String token){
        System.out.println("当前的uuid为："+userDetailDTO.getUsername());
        System.out.println("收到消息: "+msg);
    }

    //关闭连接
    @OnClose
    public void onClose(@PathParam( "token") String token){
        System.out.println("当前的uuid为,关闭socket连接 "+userDetailDTO.getUsername());
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendMessageToUsers(userDetailDTO.getUsername()+"下线了！");
        clients.remove(token);
    }
    //发生异常的情况
    @OnError
    public void onError(Throwable error) {
        error.printStackTrace();
    }

    public void sendMessageToUser(String fromUser ,String toUser, String message) {
        for (String id : clients.keySet()) {
            if (id.equals(toUser)) {
                try {
                    if (clients.get(id).session.isOpen()) {
                        clients.get(id).session.getBasicRemote().sendText(message);
                        System.out.println(clients.get(fromUser).userDetailDTO.getUsername()+" send to"+
                                clients.get(toUser).userDetailDTO.getUsername()+" "+message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
    public void sendMessageToUser(String toUser, BaseResponse response){
        sendMessageToUser(toUser, JSONObject.toJSON(response).toString());
    }
    public void sendMessageToUser(String toUser, String message) {
        for (String id : clients.keySet()) {
            if (id.equals(toUser)) {
                try {
                    if (clients.get(id).session.isOpen()) {
                        clients.get(id).session.getBasicRemote().sendText(message);
                        log.debug("send to user {} msg: {}",clients.get(id).userDetailDTO.getUsername(),message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
    public void sendMessageToUsers(String message) {
        for (String userId : clients.keySet()) {
            try {
                if (clients.get(userId).session.isOpen()) {
                    clients.get(userId).session.getBasicRemote().sendText(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


//
//    /**
//     * 连接成功时候，会触发页面上onopen方法
//     */
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//
//        System.out.println("成功建立websocket连接!");
//        String userId = (String) session.getAttributes().get(USER_ID);
//        users.put(userId,session);
//        System.out.println("当前线上用户数量:"+users.size());
//
//        //这块会实现自己业务，比如，当用户登录后，会把离线消息推送给用户
//        //TextMessage returnMessage = new TextMessage("成功建立socket连接，你将收到的离线");
//        //session.sendMessage(returnMessage);
//    }
//
//    /**
//     * 关闭连接时触发
//     */
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
//        log.debug("关闭websocket连接");
//        String userId= (String) session.getAttributes().get(USER_ID);
//        System.out.println("用户"+userId+"已退出！");
//        users.remove(userId);
//        System.out.println("剩余在线用户"+users.size());
//    }
//
//    /**
//     * js调用websocket.send时候，会调用该方法
//     */
//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//
//        super.handleTextMessage(session, message);
//
//        /**
//         * 收到消息，自定义处理机制，实现业务
//         */
//        System.out.println("服务器收到消息："+message);
//
//        if(message.getPayload().startsWith("#anyone#")){ //单发某人
//
//            sendMessageToUser((String)session.getAttributes().get(USER_ID), new TextMessage("服务器单发：" +message.getPayload())) ;
//
//        }else if(message.getPayload().startsWith("#everyone#")){
//
//            sendMessageToUsers(new TextMessage("服务器群发：" +message.getPayload()));
//
//        }else{
//            sendMessageToUsers(new TextMessage("服务器群发：" +message.getPayload()));
//
//        }
//
//    }
//
//    @Override
//    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
//        if(session.isOpen()){
//            session.close();
//        }
//        log.debug("传输出现异常，关闭websocket连接... ");
//        String userId= (String) session.getAttributes().get(USER_ID);
//        users.remove(userId);
//    }
//
//    @Override
//    public boolean supportsPartialMessages() {
//
//        return false;
//    }
//
//
//    /**
//     * 给某个用户发送消息
//     *
//     * @param userId
//     * @param message
//     */
//    public void sendMessageToUser(String userId, TextMessage message) {
//        for (String id : users.keySet()) {
//            if (id.equals(userId)) {
//                try {
//                    if (users.get(id).isOpen()) {
//                        users.get(id).sendMessage(message);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                break;
//            }
//        }
//    }

    /**
     * 给所有在线用户发送消息
     *
     * @param message
     */


}


