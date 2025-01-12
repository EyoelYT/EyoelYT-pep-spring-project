package com.example.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Message;
import com.example.repository.MessageRepository;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Message createMessage(Message message) {
        Message newMessage = new Message();
        newMessage.setMessageText(message.getMessageText());
        newMessage.setPostedBy(message.getPostedBy());
        newMessage.setTimePostedEpoch(message.getTimePostedEpoch());
        return messageRepository.save(newMessage);
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public List<Message> getAllMessagesById(String accountId) {
        int id = Integer.parseInt(accountId);
        return messageRepository.findAllByPostedBy(id);
    }

    public Message getMessageById(String messageId) {
        int id = Integer.parseInt(messageId);
        return messageRepository.findByMessageId(id).orElse(null);
    }

    @Transactional
    public int deleteMessageById(String messageId){
        int id = Integer.parseInt(messageId);
        return messageRepository.deleteByMessageId(id);
    }

    public boolean updateMessageByMessageId(String messageId, String messageText) {
        int id = Integer.parseInt(messageId);
        Message message = messageRepository.getById(id);
        if (message != null) {
            Message updatedMessage = message;
            updatedMessage.setMessageText(messageText);
            messageRepository.save(updatedMessage);
            return true;
        }
        return false;
    }

}
