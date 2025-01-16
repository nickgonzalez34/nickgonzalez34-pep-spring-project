package com.example.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@RestController
public class SocialMediaController {
    private final AccountService accountService;
    private final MessageService messageService;

    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    @PostMapping("/register")
    public ResponseEntity<Account> registerAccount(@RequestBody Account account) {
        try {
            // Attempt to register the new account.
            Account newAccount = accountService.registerAccount(account);
            // If registration is successful, return the account with status 200 OK.
            return ResponseEntity.ok(newAccount);
        } catch (IllegalArgumentException e) {
            // Get the message from the thrown exception.
            String message = e.getMessage();
            // If the username already exists, return status 409 CONFLICT.
            if (message.equals("Username already exists.")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            }
        }
        // If there is a validation issue for any other reason, return status 400 BAD REQUEST.
        return ResponseEntity.badRequest().body(null);
    }

    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Account account) {
        try {
            // Verify login.
            Account authenticatedAccount = accountService.login(account);
            // If login is successful, return the account with status 200 OK.
            return ResponseEntity.ok(authenticatedAccount);
        } catch (IllegalArgumentException e) {
            // If login fails due to invalid username or password, return status 401 Unauthorized.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        try {
            // Attempt to create and save the message via the service.
            Message createdMessage = messageService.createMessage(message);
            // If successful, return the created message with a 200 OK status.
            return ResponseEntity.ok(createdMessage);
        } catch (IllegalArgumentException e) {
            // If any validation fails, return a 400 BAD REQUEST status.
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        // Retrieve all messages from the service.
        List<Message> messages = messageService.getAllMessages();
        // Return the list of messages with a 200 OK status.
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/messages/{message_id}")
    public ResponseEntity<Message> getMessageById(@PathVariable("message_id") int messageId) {
        // Retrieve the message by ID.
        Optional<Message> message = messageService.getMessageById(messageId);

        if (message.isPresent()) {
            // If the message is found, return it with a 200 OK status.
            return ResponseEntity.ok(message.get());
        } else {
            // If the message is not found, return an empty body with a 200 OK status.
            return ResponseEntity.ok().build();
        }
    }

    @DeleteMapping("/messages/{message_id}")
    public ResponseEntity<Integer> deleteMessageById(@PathVariable("message_id") int messageId) {
        // Call the service to delete the message and get the number of rows affected.
        int rowsAffected = messageService.deleteMessageById(messageId);

        // If the message was deleted, return the number of rows affected (1), otherwise return an empty body.
        if (rowsAffected == 1) {
            return ResponseEntity.ok(rowsAffected);  
        } else {
            return ResponseEntity.ok().build();  
        }
    }

    @PatchMapping("/messages/{message_id}")
    public ResponseEntity<Integer> updateMessageById(@PathVariable("message_id") int messageId, 
                                                 @RequestBody Message message) {
        try {
        // Call the service to update the message and get the number of rows affected.
        int rowsAffected = messageService.updateMessageById(messageId, message.getMessageText());

        if (rowsAffected == 1) {
            // If the message was successfully updated, return 1.
            return ResponseEntity.ok(rowsAffected);
        } 
        } catch (IllegalArgumentException e) {
            // If the message fails validation return a 400 BAD REQUEST status.
            return ResponseEntity.badRequest().body(null);
        }
        // Return a 400 BAD REQUEST status for any other failure.
        return ResponseEntity.badRequest().body(null);
    }

    @GetMapping("/accounts/{account_id}/messages")
    public ResponseEntity<List<Message>> getMessagesByAccountId(@PathVariable("account_id") int accountId) {
        // Retrieve all messages by Account Id.
        List<Message> messages = messageService.getMessagesByAccountId(accountId);
        
        // Return the list of messages (empty if no messages).
        return ResponseEntity.ok(messages);
    }
}
