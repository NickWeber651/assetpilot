package de.nick.assetpilot.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountRepository accountRepository;

    @Test
    void updateAccount_whenAccountExists_returnsUpdatedAccount() throws Exception {
        Account existingAccount = new Account(
                1L,
                "Altes Konto",
                "Alte Bank",
                AccountType.CASH,
                "EUR",
                new BigDecimal("100.00")
        );

        when(accountRepository.findById(1L)).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String requestBody = """
                {
                  "id": 999,
                  "name": "Neues Konto",
                  "provider": "Neue Bank",
                  "type": "SAVINGS",
                  "currency": "USD",
                  "currentBalance": 2500.50
                }
                """;

        mockMvc.perform(put("/api/accounts/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Neues Konto"))
                .andExpect(jsonPath("$.provider").value("Neue Bank"))
                .andExpect(jsonPath("$.type").value("SAVINGS"))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.currentBalance").value(2500.5));

        verify(accountRepository).findById(1L);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void updateAccount_whenAccountDoesNotExist_returnsNotFound() throws Exception {
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        String requestBody = """
                {
                  "name": "Unbekannt",
                  "provider": "Bank",
                  "type": "CASH",
                  "currency": "EUR",
                  "currentBalance": 10.00
                }
                """;

        mockMvc.perform(put("/api/accounts/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void updateAccount_whenNameIsBlank_returnsBadRequest() throws Exception {
        String requestBody = """
                {
                  "name": "",
                  "provider": "Bank",
                  "type": "CASH",
                  "currency": "EUR",
                  "currentBalance": 10.00
                }
                """;

        mockMvc.perform(put("/api/accounts/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(accountRepository, never()).findById(any());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void updateAccount_whenProviderIsMissing_returnsBadRequest() throws Exception {
        String requestBody = """
                {
                  "name": "Konto",
                  "type": "CASH",
                  "currency": "EUR",
                  "currentBalance": 10.00
                }
                """;

        mockMvc.perform(put("/api/accounts/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(accountRepository, never()).findById(any());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void updateAccount_whenCurrentBalanceIsNegative_returnsBadRequest() throws Exception {
        String requestBody = """
                {
                  "name": "Konto",
                  "provider": "Bank",
                  "type": "CASH",
                  "currency": "EUR",
                  "currentBalance": -1.00
                }
                """;

        mockMvc.perform(put("/api/accounts/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(accountRepository, never()).findById(any());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void updateAccount_whenTypeIsInvalid_returnsBadRequest() throws Exception {
        String requestBody = """
                {
                  "name": "Konto",
                  "provider": "Bank",
                  "type": "GIRO",
                  "currency": "EUR",
                  "currentBalance": 10.00
                }
                """;

        mockMvc.perform(put("/api/accounts/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(accountRepository, never()).findById(any());
        verify(accountRepository, never()).save(any(Account.class));
    }
}

