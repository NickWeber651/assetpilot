package de.nick.assetpilot.account;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void getAllAccounts_returnsAllFromRepository() {
        Account account = new Account(
                1L,
                "Cash",
                "Hausbank",
                AccountType.CASH,
                "EUR",
                new BigDecimal("100.00")
        );
        when(accountRepository.findAll()).thenReturn(List.of(account));

        List<Account> result = accountService.getAllAccounts();

        assertEquals(1, result.size());
        assertEquals("Cash", result.getFirst().getName());
        verify(accountRepository).findAll();
    }

    @Test
    void getAccountById_whenMissing_throwsNotFound() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> accountService.getAccountById(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Account mit ID 99 wurde nicht gefunden"));
    }

    @Test
    void updateAccount_whenExisting_updatesFieldsAndSaves() {
        Account existing = new Account(
                1L,
                "Alt",
                "Bank A",
                AccountType.CASH,
                "EUR",
                new BigDecimal("10.00")
        );
        Account updateRequest = new Account(
                999L,
                "Neu",
                "Bank B",
                AccountType.SAVINGS,
                "USD",
                new BigDecimal("2500.50")
        );

        when(accountRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account result = accountService.updateAccount(1L, updateRequest);

        assertEquals(1L, result.getId());
        assertEquals("Neu", result.getName());
        assertEquals("Bank B", result.getProvider());
        assertEquals(AccountType.SAVINGS, result.getType());
        assertEquals("USD", result.getCurrency());
        assertEquals(new BigDecimal("2500.50"), result.getCurrentBalance());
        verify(accountRepository).findById(1L);
        verify(accountRepository).save(existing);
    }

    @Test
    void deleteAccount_whenMissing_throwsNotFoundAndDoesNotDelete() {
        when(accountRepository.existsById(123L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> accountService.deleteAccount(123L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Account mit ID 123 wurde nicht gefunden"));
        verify(accountRepository).existsById(123L);
        verify(accountRepository, never()).deleteById(123L);
    }
}

