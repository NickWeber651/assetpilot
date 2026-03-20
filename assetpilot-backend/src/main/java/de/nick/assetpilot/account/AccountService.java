package de.nick.assetpilot.account;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Account mit ID " + id + " wurde nicht gefunden"
                ));
    }

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Account updateAccount(Long id, Account account) {
        Account existingAccount = getAccountById(id);

        existingAccount.setName(account.getName());
        existingAccount.setProvider(account.getProvider());
        existingAccount.setType(account.getType());
        existingAccount.setCurrency(account.getCurrency());
        existingAccount.setCurrentBalance(account.getCurrentBalance());

        return accountRepository.save(existingAccount);
    }

    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Account mit ID " + id + " wurde nicht gefunden"
            );
        }

        accountRepository.deleteById(id);
    }
}
