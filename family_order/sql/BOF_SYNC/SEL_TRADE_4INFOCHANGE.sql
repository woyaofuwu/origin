SELECT B.USER_ID, MOD(B.USER_ID, 10000) PARTITION_ID
                FROM TF_B_TRADE_PRODUCT B
               WHERE B.TRADE_ID = :TRADE_ID
                 AND B.ACCEPT_MONTH = :ACCEPT_MONTH
                 AND B.MAIN_TAG = '1'
              UNION 
              SELECT C.USER_ID, MOD(C.USER_ID, 10000)
                FROM TF_B_TRADE_RES C
               WHERE C.TRADE_ID = :TRADE_ID
                 AND C.ACCEPT_MONTH = :ACCEPT_MONTH
                 AND C.RES_TYPE_CODE IN ('0', '1')