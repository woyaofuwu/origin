INSERT INTO TI_B_USER_MPUTE
      (SYNC_SEQUENCE, SYNC_DAY, MODIFY_TAG, TRADE_ID, PARTITION_ID, USER_ID,
       MPUTE_MONTH_FEE, START_DATE, END_DATE, UPDATE_TIME)
      SELECT DISTINCT :SYNC_SEQUENCE, :SYNC_DAY, '0', :TRADE_ID,
                      MOD(T.USER_ID, 10000), T.USER_ID, 0, SYSDATE, SYSDATE + 1,
                      SYSDATE
        FROM (SELECT A.USER_ID
                 FROM TI_B_USER_DISCNT A
                WHERE A.SYNC_SEQUENCE = :SYNC_SEQUENCE
                  AND A.SYNC_DAY = :SYNC_DAY
               UNION
               SELECT B.USER_ID
                 FROM TI_B_USER_INFOCHANGE B
                WHERE B.SYNC_SEQUENCE = :SYNC_SEQUENCE
                  AND B.SYNC_DAY = :SYNC_DAY
               UNION
               SELECT C.USER_ID
                 FROM TI_B_USER_ACCTDAY C
                WHERE C.SYNC_SEQUENCE = :SYNC_SEQUENCE
                  AND C.SYNC_DAY = :SYNC_DAY
               UNION
               SELECT D.USER_ID_B USER_ID
                 FROM TI_B_RELATION_UU D
                WHERE D.SYNC_SEQUENCE = :SYNC_SEQUENCE
                  AND D.SYNC_DAY = :SYNC_DAY
               UNION
               SELECT E.USER_ID
                 FROM TI_B_USER_SHARE E
                WHERE E.SYNC_SEQUENCE = :SYNC_SEQUENCE
                  AND E.SYNC_DAY = :SYNC_DAY) T