package de.nick.assetpilot.account;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountRepository accountRepository;

    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @GetMapping("/{id}")
    public Account getAccountById(@PathVariable Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Account mit ID " + id + " wurde nicht gefunden"
                ));
    }

    @PostMapping
    public Account createAccount(@Valid @RequestBody Account account) {
        return accountRepository.save(account);
    }

    @PutMapping("/{id}")
    public Account updateAccount(@PathVariable Long id, @Valid @RequestBody Account account) {
        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Account mit ID " + id + " wurde nicht gefunden"
                ));

        existingAccount.setName(account.getName());
        existingAccount.setProvider(account.getProvider());
        existingAccount.setType(account.getType());
        existingAccount.setCurrency(account.getCurrency());
        existingAccount.setCurrentBalance(account.getCurrentBalance());

        return accountRepository.save(existingAccount);
    }
}
