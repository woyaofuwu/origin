SELECT COUNT(1) recordcount
FROM   DUAL
WHERE  (SELECT SUM(COUNTS)
        FROM   (SELECT COUNT(*) COUNTS
                 FROM   TF_F_USER_SVC
                 WHERE  USER_ID = :USER_ID
                 and    partition_id = mod(to_number(:USER_ID), 10000)
                 AND    END_DATE > SYSDATE
                 and    start_date < sysdate
                 AND    SERVICE_ID IN (SELECT PARA_CODE1
                                       FROM   TD_S_COMMPARA
                                       WHERE  SUBSYS_CODE = 'CSM'
                                       AND    PARAM_ATTR = 2001
                                       AND    PARAM_CODE = :PARAM_CODE)
                 UNION ALL
                 SELECT COUNT(*) COUNTS
                 FROM   TF_B_TRADE_SVC
                 WHERE  TRADE_ID = :TRADE_ID
                 and    accept_month = to_number(substr(:TRADE_ID, 5,2))
                 and    end_date > sysdate
                 AND    decode(modify_tag, '4', '0','5','1', modify_tag) = 0
                 AND    SERVICE_ID IN (SELECT PARA_CODE1
                                       FROM   TD_S_COMMPARA
                                       WHERE  SUBSYS_CODE = 'CSM'
                                       AND    PARAM_ATTR = 2001
                                       AND    PARAM_CODE = :PARAM_CODE)
                 UNION ALL
                 SELECT -COUNT(*) COUNTS
                 FROM   TF_B_TRADE_SVC
                 WHERE  TRADE_ID = :TRADE_ID
                 and    accept_month = to_number(substr(:TRADE_ID, 5,2))
                 and    start_date < sysdate
                 AND    decode(modify_tag, '4', '0','5','1', modify_tag) = 1
                 AND    SERVICE_ID IN (SELECT PARA_CODE1
                                       FROM   TD_S_COMMPARA
                                       WHERE  SUBSYS_CODE = 'CSM'
                                       AND    PARAM_ATTR = 2001
                                       AND    PARAM_CODE = :PARAM_CODE))) > :NUM