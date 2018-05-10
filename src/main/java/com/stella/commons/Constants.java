package com.stella.commons;

public interface Constants {
    long EXCEPTION_HTTP_CODE = -9L;
    String SHORT_DATE_FORMAT = "yyyy-MM-dd";

    interface Incoming {
        interface UrlPaths {
            interface Query {
                String GET_PAYMENTS = "/query/payments";
                String GET_LAST_BLOCK = "/query/last-block" ;
            }
        };
    }

    interface WebWallet {
        interface UrlPaths {
            interface Notification {
                String CRYPTO_DEPOSIT = "/notification/crypto-deposit";
            }
        };
    }

    interface BeanNames {
        String LITECOIN_SERVICE = "litecoinService";
        String BITCOIN_SERVICE = "bitcoinService";
        String ETHEREUM_SERVICE = "ethereumService";
        String RIPPLE_SERVICE = "rippleService";
        String QUARTZ_DB_INITIALIZER = "quartzDbInitializer";
    }
}
