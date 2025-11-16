package ru.mishelby.walletapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mishelby.walletapi.model.TransferOperationRequest;
import ru.mishelby.walletapi.model.WalletDto;
import ru.mishelby.walletapi.model.DepositOperationRequest;
import ru.mishelby.walletapi.model.WalletOperationResponse;
import ru.mishelby.walletapi.service.WalletService;

import java.util.UUID;

@Tag(name = "Wallet Controller", description = "Управление операциями над кошельками")
@Slf4j
@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @Operation(summary = "Получить текущий баланс кошелька")
    @GetMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Баланс получен успешно!"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос!"),
            @ApiResponse(responseCode = "500", description = "Неизвестная ошибка!")
    })
    public ResponseEntity<WalletDto> getWalletBalance(@PathVariable("uuid") UUID uuid) {
        log.info("[INFO] GET request for getting wallet balance for [{}]", uuid);
        return ResponseEntity.ok(walletService.getBalance(uuid));
    }

    @Operation(summary = "Внести деньги на кошелёк по его ID")
    @PostMapping(path = "/{uuid}/deposit", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пополнение прошло успешно!"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос!"),
            @ApiResponse(responseCode = "500", description = "Неизвестная ошибка!")
    })
    public ResponseEntity<WalletOperationResponse> depositOperation(@PathVariable("uuid") UUID uuid,
                                                                    @RequestBody DepositOperationRequest request) {
        log.info("[INFO] POST deposit request for wallet [{}]", uuid);
        return ResponseEntity.ok(walletService.deposit(uuid, request));
    }

    @Operation(summary = "Перевести деньги на другой кошелёк по его ID")
    @PostMapping(path = "/{uuid}/withdraw", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Перевод выполнен успешно!"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос!"),
            @ApiResponse(responseCode = "500", description = "Неизвестная ошибка!")
    })
    public ResponseEntity<WalletOperationResponse> withdrawOperation(@PathVariable("uuid") UUID uuid,
                                                                     @RequestBody TransferOperationRequest request) {
        log.info("[INFO] POST withdraw request for wallet [{}]", uuid);
        return ResponseEntity.ok(walletService.withdraw(uuid, request));
    }
}
