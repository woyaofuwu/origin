SELECT COUNT(1) ORDERNUM2
                 FROM TF_B_TRADE A
                WHERE A.USER_ID = :USER_ID
                  AND A.TRADE_TYPE_CODE = '110'