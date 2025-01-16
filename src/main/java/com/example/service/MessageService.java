package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.entity.Message;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final AccountRepository accountRepository; 

    public MessageService(MessageRepository messageRepository, AccountRepository accountRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }

    public Message createMessage(Message message) throws IllegalArgumentException {
        // Check if the user exists (postedBy must refer to a valid user).
        accountRepository.findById(message.getPostedBy())
            .orElseThrow(() -> new IllegalArgumentException("User not found."));

        // Validate message text.
        if (message.getMessageText() == null || message.getMessageText().isBlank()) {
            throw new IllegalArgumentException("Message text cannot be blank.");
        }

        // Ensure message text does not exceed 255 characters.
        if (message.getMessageText().length() > 255) {
            throw new IllegalArgumentException("Message text cannot exceed 255 characters.");
        }

        // Save the message to the database.
        return messageRepository.save(message);
    }

    public List<Message> getAllMessages() {
        // Retrieves all messages from the database.
        return messageRepository.findAll();  
    }

    public Optional<Message> getMessageById(int messageId) {
        // Retrieves a message by ID from the database.
        return messageRepository.findById(messageId);  
    }

    public int deleteMessageById(int messageId) {
        // Check if the message exists in the database and delete it.
        if (messageRepository.existsById(messageId)) {
            messageRepository.deleteById(messageId);  
            // Return 1 indicating that the message was deleted.
            return 1;  
        }
        // Return 0 indicating that the message was not found.
        return 0;  
    }

    public int updateMessageById(int messageId, String newMessageText) throws IllegalArgumentException {
        // Check if the message exists otherwise throw exception.
        Message existingMessage = messageRepository.findById(messageId)
            .orElseThrow(() -> new IllegalArgumentException("Message not found."));

        // Validate the new message text.
        if (newMessageText == null || newMessageText.isBlank()) {
            throw new IllegalArgumentException("Message text cannot be empty or blank.");
        }

        // Validate length of the message text.
        if (newMessageText.length() > 255) {
            throw new IllegalArgumentException("New message text must be less than 255 characters.");
        }

        // Update and save the new message text.
        existingMessage.setMessageText(newMessageText);
        messageRepository.save(existingMessage); 
    
        // Return 1 indicating that the message was updated.
        return 1;
    }

    public List<Message> getMessagesByAccountId(int accountId) {
        return messageRepository.findByPostedBy(accountId);
    }
}
