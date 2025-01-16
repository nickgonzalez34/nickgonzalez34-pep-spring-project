package com.example.service;

import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account registerAccount(Account account) throws IllegalArgumentException {
        // Validate username and ensure it's not null or blank
        if (account.getUsername() == null || account.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank.");
        }
        // Validate password: ensure it's not null and at least 4 characters long
        if (account.getPassword() == null || account.getPassword().length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters.");
        }
        // Check if the username already exists in the database
        if (accountRepository.existsByUsername(account.getUsername())) {
            throw new IllegalArgumentException("Username already exists.");
        }
        // Save account to database
        return accountRepository.save(account);
    }

    public Account login(Account account) throws IllegalArgumentException {
        // Find the account by username in the database
        Account existingAccount = accountRepository.findByUsername(account.getUsername());
        // If no account is found with the given username, throw an exception
        if (existingAccount == null) {
            throw new IllegalArgumentException("Username not found.");
        }
        // If the password doesn't match, throw an exception for invalid credentials
        if (!existingAccount.getPassword().equals(account.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        // Return the account if the login is successful
        return existingAccount;
    }
}
