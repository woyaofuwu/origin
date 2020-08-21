SELECT COUNT(*) recordcount
  FROM tf_b_trade_svc a
    WHERE trade_id=TO_NUMBER(:TRADE_ID)
      AND accept_month=TO_NUMBER(:ACCEPT_MONTH)
        AND service_id=:SERVICE_ID
          AND (modify_tag=:MODIFY_TAG OR :MODIFY_TAG = '*')
           AND EXISTS(SELECT 1 FROM td_s_commpara b
              WHERE subsys_code='CSM'
                AND param_attr=2001
                 AND param_code=:PARAM_CODE
                  AND b.para_code1=a.serv_para1
                   AND (eparchy_code = :EPARCHY_CODE OR eparchy_code='ZZZZ')
                     AND SYSDATE BETWEEN start_date AND end_date)