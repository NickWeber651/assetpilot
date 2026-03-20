package de.nick.assetpilot.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @Test
    void updateAccount_whenAccountExists_returnsUpdatedAccount() throws Exception {

        when(accountService.updateAccount(any(Long.class), any(Account.class))).thenReturn(new Account(
                1L,
                "Neues Konto",
                "Neue Bank",
                AccountType.SAVINGS,
                "USD",
                new BigDecimal("2500.50")
        ));

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

        verify(accountService).updateAccount(any(Long.class), any(Account.class));
    }

    @Test
    void updateAccount_whenAccountDoesNotExist_returnsNotFound() throws Exception {
        when(accountService.updateAccount(any(Long.class), any(Account.class)))
                .thenThrow(new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Account mit ID 999 wurde nicht gefunden"
                ));

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

        verify(accountService).updateAccount(any(Long.class), any(Account.class));
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

        verify(accountService, never()).updateAccount(any(Long.class), any(Account.class));
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

        verify(accountService, never()).updateAccount(any(Long.class), any(Account.class));
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

        verify(accountService, never()).updateAccount(any(Long.class), any(Account.class));
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

        verify(accountService, never()).updateAccount(any(Long.class), any(Account.class));
    }

    @Test
    void deleteAccount_whenAccountExists_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/accounts/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(accountService).deleteAccount(1L);
    }

    @Test
    void deleteAccount_whenAccountDoesNotExist_returnsNotFound() throws Exception {
        doThrow(new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND,
                "Account mit ID 999 wurde nicht gefunden"
        )).when(accountService).deleteAccount(999L);

        mockMvc.perform(delete("/api/accounts/{id}", 999L))
                .andExpect(status().isNotFound());

        verify(accountService).deleteAccount(999L);
    }
}

