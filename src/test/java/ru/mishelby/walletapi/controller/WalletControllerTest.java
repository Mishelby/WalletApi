package ru.mishelby.walletapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mishelby.walletapi.model.DepositOperationRequest;
import ru.mishelby.walletapi.model.TransferOperationRequest;
import ru.mishelby.walletapi.model.WalletDto;
import ru.mishelby.walletapi.model.WalletOperationResponse;
import ru.mishelby.walletapi.repository.WalletRepository;
import ru.mishelby.walletapi.service.WalletService;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.mishelby.walletapi.model.enums.OperationType.DEPOSIT;
import static ru.mishelby.walletapi.model.enums.OperationType.WITHDRAW;

@WebMvcTest(WalletController.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WalletService walletService;

    @MockitoBean
    private WalletRepository walletRepository;

    private WalletDto walletDto;

    private static final UUID WALLET_ID = UUID.fromString("12345678-1234-1234-1234-123456789012");
    private static final UUID WALLET_ID_TO = UUID.fromString("12345678-1234-1234-1234-123456789013");
    private static final BigDecimal INITIAL_BALANCE = BigDecimal.ZERO;
    private static final BigDecimal UPDATED_BALANCE = BigDecimal.TEN;
    private static final LocalDateTime TIME = LocalDateTime.of(2025, 4, 20, 20, 59);

    @BeforeEach
    void setUp() {
        walletDto = new WalletDto(INITIAL_BALANCE, TIME);
    }

    private String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Nested
    @DisplayName("GET /wallets/{uuid}")
    class GetWalletBalance {

        @Test
        @DisplayName("Должен вернуть статус 200 и баланс пользователя")
        void getWalletBalance_shouldReturnBalance() throws Exception {
            Mockito.when(walletService.getBalance(WALLET_ID)).thenReturn(walletDto);

            mockMvc.perform(get("/api/v1/wallets/{uuid}", WALLET_ID))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.balance").value(INITIAL_BALANCE))
                    .andExpect(jsonPath("$.requestedAt").exists());
        }
    }

    @Nested
    @DisplayName("POST /wallets/{uuid}/deposit")
    class DepositWallet {

        @Test
        @DisplayName("Должен вернуть статус 200 и новый баланс пользователя")
        void depositOperation_shouldReturnUpdatedBalance() throws Exception {
            var request = new DepositOperationRequest(UPDATED_BALANCE);
            var response = new WalletOperationResponse(DEPOSIT, INITIAL_BALANCE, UPDATED_BALANCE, TIME);

            Mockito.when(walletService.deposit(WALLET_ID, request)).thenReturn(response);

            mockMvc.perform(post("/api/v1/wallets/{uuid}/deposit", WALLET_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.oldBalanceFrom").value(INITIAL_BALANCE))
                    .andExpect(jsonPath("$.newBalanceFrom").value(UPDATED_BALANCE))
                    .andExpect(jsonPath("$.operationType").value(DEPOSIT.toString()))
                    .andExpect(jsonPath("$.operationTime").exists());
        }
    }

    @Nested
    @DisplayName("POST /wallets/{uuid}/withdraw")
    class WithdrawWallet {

        @Test
        @DisplayName("Должен вернуть статус 200 и новый баланс пользователя")
        void withdrawOperation_shouldReturnUpdatedBalance() throws Exception {
            var request = new TransferOperationRequest(WALLET_ID_TO, UPDATED_BALANCE);
            var response = new WalletOperationResponse(WITHDRAW, INITIAL_BALANCE, UPDATED_BALANCE, TIME);

            Mockito.when(walletService.withdraw(WALLET_ID, request)).thenReturn(response);

            mockMvc.perform(post("/api/v1/wallets/{uuid}/withdraw", WALLET_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.oldBalanceFrom").value(INITIAL_BALANCE))
                    .andExpect(jsonPath("$.newBalanceFrom").value(UPDATED_BALANCE))
                    .andExpect(jsonPath("$.operationType").value(WITHDRAW.toString()))
                    .andExpect(jsonPath("$.operationTime").exists());
        }
    }
}
