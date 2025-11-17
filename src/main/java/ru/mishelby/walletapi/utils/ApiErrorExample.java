package ru.mishelby.walletapi.utils;

public final class ApiErrorExample {

    public static final String WALLET_BALANCE_200 = """
            {
              "walletID": "1234-56789-1234-58789-5325321664"
              "balance": 1250.75,
              "requestedAt": "2025-11-17T10:15:30"
            }
            """;

    public static final String WALLET_LIST_200 = """
            [
                {
                    "walletID": "11111111-1111-1111-1111-111111111111",
                    "balance": 4431,
                    "requestedAt": "2025-11-17 11:04:02"
                },
                {
                    "walletID": "22222222-2222-2222-2222-222222222222",
                    "balance": 1268,
                    "requestedAt": "2025-11-17 11:04:02"
                },
                {
                    "walletID": "33333333-3333-3333-3333-333333333333",
                    "balance": 184,
                    "requestedAt": "2025-11-17 11:04:02"
                }
            ]
            """;

    public static final String WALLET_BALANCE_400 = """
            {
              "title": "Bad Request",
              "status": 400,
              "details": "Invalid wallet UUID format",
              "instance": "/api/v1/wallets/invalid-uuid",
              "localDateTime": "2025-11-17T10:20:00",
              "fieldError": []
            }
            """;

    public static final String WALLET_OPERATION_200 = """
            {
              "operationType": "DEPOSIT",
              "oldBalanceFrom": 1000.00,
              "newBalanceFrom": 1500.00,
              "operationTime": "2025-11-17T10:25:00"
            }
            """;

    public static final String WALLET_OPERATION_400 = """
            {
              "title": "Validation Error",
              "status": 400,
              "details": "Request validation failed",
              "instance": "/api/v1/wallets/123e4567-e89b-12d3-a456-426614174000/deposit",
              "localDateTime": "2025-11-17T10:30:00",
              "fieldError": [
                {
                  "field": "amount",
                  "message": "Amount must be greater than zero"
                }
              ]
            }
            """;
    public static final String INTERNAL_ERROR_EXAMPLE = """
            {
              "title": "Internal Server Error",
              "status": 500,
              "details": "Unexpected error occurred",
              "instance": "/api/v1/wallets/123/deposit",
              "localDateTime": "2025-11-16T21:10:00"
            }
            """;

    private ApiErrorExample() {
    }
}
