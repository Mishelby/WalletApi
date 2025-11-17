package ru.mishelby.walletapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

import java.util.List;
import java.util.UUID;

import static ru.mishelby.walletapi.utils.ApiErrorExample.*;

/**
 * REST-контроллер для управления операциями над кошельками.
 * <p>
 * Поддерживает следующие операции:
 * <ul>
 *     <li>Получение текущего баланса кошелька</li>
 *     <li>Пополнение кошелька (deposit)</li>
 *     <li>Перевод средств на другой кошелёк (withdraw)</li>
 * </ul>
 * <p>
 * Все операции логируются через {@link org.slf4j.Logger}.
 * Использует {@link WalletService} для выполнения бизнес-логики.
 */
@Tag(name = "Wallet Controller", description = "Управление операциями над кошельком")
@Slf4j
@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @Operation(summary = "Получить список всех кошельков")
    @GetMapping(produces =  MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Баланс получен успешно!",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "200 OK", value = WALLET_LIST_200)))
    })
    public ResponseEntity<List<WalletDto>> findAll(@RequestParam(required = false, defaultValue = "0") int page,
                                                   @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("[INFO] GET request for getting all wallets");
        return ResponseEntity.ok(walletService.findAll(page, size));
    }

    /**
     * Получает текущий баланс кошелька по его UUID.
     *
     * @param uuid UUID кошелька
     * @return {@link ResponseEntity} с объектом {@link WalletDto}, содержащим баланс и дату запроса
     */
    @Operation(summary = "Получить текущий баланс кошелька")
    @GetMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Баланс получен успешно!",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "200 OK", value = WALLET_BALANCE_200))),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос!",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "400 Bad Request", value = WALLET_BALANCE_400))),
            @ApiResponse(responseCode = "500", description = "Неизвестная ошибка!",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "500 Internal Server Error", value = INTERNAL_ERROR_EXAMPLE)))
    })
    public ResponseEntity<WalletDto> getWalletBalance(@PathVariable("uuid") UUID uuid) {
        log.info("[INFO] GET request for getting wallet balance for [{}]", uuid);
        return ResponseEntity.ok(walletService.getBalance(uuid));
    }

    /**
     * Пополняет баланс кошелька по его UUID.
     *
     * @param uuid    UUID кошелька
     * @param request объект {@link DepositOperationRequest} с суммой пополнения
     * @return {@link ResponseEntity} с объектом {@link WalletOperationResponse}, содержащим старый и новый баланс
     */
    @Operation(summary = "Внести деньги на кошелёк по его ID")
    @PostMapping(path = "/{uuid}/deposit", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пополнение прошло успешно!",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "200 OK", value = WALLET_OPERATION_200))),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос!",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "400 Bad Request", value = WALLET_OPERATION_400))),
            @ApiResponse(responseCode = "500", description = "Неизвестная ошибка!",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "500 Internal Server Error", value = INTERNAL_ERROR_EXAMPLE)))
    })
    public ResponseEntity<WalletOperationResponse> depositOperation(
            @PathVariable("uuid") UUID uuid,
            @RequestBody @Valid DepositOperationRequest request
    ) {
        log.info("[INFO] POST deposit request for wallet [{}]", uuid);
        return ResponseEntity.ok(walletService.deposit(uuid, request));
    }

    /**
     * Переводит средства с одного кошелька на другой по их UUID.
     *
     * @param uuid    UUID кошелька-отправителя
     * @param request объект {@link TransferOperationRequest} с суммой перевода и UUID получателя
     * @return {@link ResponseEntity} с объектом {@link WalletOperationResponse}, содержащим старый и новый баланс отправителя
     */
    @Operation(summary = "Перевести деньги на другой кошелёк по его ID")
    @PostMapping(path = "/{uuid}/withdraw", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Перевод выполнен успешно!",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "200 OK", value = WALLET_OPERATION_200))),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос!",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "400 Bad Request", value = WALLET_OPERATION_400))),
            @ApiResponse(responseCode = "500", description = "Неизвестная ошибка!",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "500 Internal Server Error", value = INTERNAL_ERROR_EXAMPLE)))
    })
    public ResponseEntity<WalletOperationResponse> withdrawOperation(
            @PathVariable("uuid") UUID uuid,
            @RequestBody @Valid TransferOperationRequest request
    ) {
        log.info("[INFO] POST withdraw request for wallet [{}]", uuid);
        return ResponseEntity.ok(walletService.withdraw(uuid, request));
    }
}
