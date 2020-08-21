--IS_CACHE=Y
SELECT discnt_code paracode,discnt_name paraname
FROM td_b_discnt
WHERE SYSDATE BETWEEN START_DATE AND END_DATE AND
      (:TRADE_EPARCHY_CODE is null or :TRADE_EPARCHY_CODE is not null)
       AND tag_set like '1%'