package org.jeejeejango.web;

import lombok.extern.slf4j.Slf4j;
import org.jeejeejango.client.NoSuchUserException;
import org.jeejeejango.client.UserClient;
import org.jeejeejango.entity.Account;
import org.jeejeejango.entity.User;
import org.jeejeejango.exception.AccountUserException;
import org.jeejeejango.repository.AccountRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.util.StringUtils.hasText;

@RestController
@RequestMapping("/api/v1/accounts")
@Slf4j
public class AccountRestController {

    private AccountRepository accountRepository;

    private UserClient userClient;


    public AccountRestController(AccountRepository accountRepository,
                                 UserClient userClient) {
        this.accountRepository = accountRepository;
        this.userClient = userClient;
    }


    @GetMapping
    public ResponseEntity<Collection<Account>> getAllAccounts(Pageable pageable) {
        if (log.isInfoEnabled()) {
            log.info("find all account, pageable {}", pageable);
        }
        return new ResponseEntity<>(accountRepository.findAll(pageable).getContent(), HttpStatus.OK);
    }


    @GetMapping("/search")
    public ResponseEntity<Collection<Account>> findAccountsByName(@RequestParam(required = false) String firstName,
                                                                  @RequestParam(required = false) String lastName,
                                                                  @RequestParam(required = false) String email,
                                                                  @RequestParam(required = false) String accountName,
                                                                  Pageable pageable) {

        boolean searchByUsers = false;
        List<Long> userIds = Collections.emptyList();
        if (hasText(firstName) || hasText(lastName) || hasText(email)) {
            searchByUsers = true;
            userIds = userClient.searchUsers(firstName, lastName, email);
        }

        if (log.isInfoEnabled()) {
            log.info("search account {}, pageable {}", accountName, pageable);
        }

        return ResponseEntity.ok(searchByUsers
            ? (hasText(accountName)
                ? accountRepository.findByUserIdInAndAccountNameContainingIgnoreCase(userIds, accountName, pageable)
                : accountRepository.findByUserIdIn(userIds, pageable))
            : (hasText(accountName)
                ? accountRepository.findByAccountNameContainingIgnoreCase(accountName, pageable)
                : accountRepository.findAll(pageable).getContent()));
    }


    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        Optional<Account> account = accountRepository.findById(id);
        if (log.isInfoEnabled()) {
            log.info("account id {} found {}", id, account.isPresent());
        }
        return account.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<?> addAccount(@RequestBody @Valid Account account) {
        account.setId(null);
        if (hasText(account.getAccountName())) {
            userClient.validateUser(account.getUserId());
        } else {
            User user = userClient.getUserById(account.getUserId());
            if (user == null) {
                throw new NoSuchUserException("User is invalid");
            }
            account.setAccountName(user.getFirstName() + " " + user.getLastName() + " Account");
        }

        account = accountRepository.save(account);
        if (log.isInfoEnabled()) {
            log.info("add account by id {}", account.getId());
        }
        return ResponseEntity.ok(account);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateAccount(@PathVariable("id") Long id, @RequestBody @Valid Account account) {
        if (!id.equals(account.getId())) {
            if (log.isWarnEnabled()) {
                log.warn("update account id does not match", id);
            }
            return ResponseEntity.badRequest().build();
        }
        if (log.isInfoEnabled()) {
            log.info("update account by id {}", id);
        }

        if (!account.getUserId().equals(account.getOriginalUserId())) {
            throw new AccountUserException("Account's user cannot be changed");
        }

        accountRepository.save(account);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable("id") Long id) {
        if (log.isInfoEnabled()) {
            log.info("delete account by id {}", id);
        }
        accountRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }


}
