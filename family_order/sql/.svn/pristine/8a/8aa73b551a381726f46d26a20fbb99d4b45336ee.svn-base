SELECT MAX(e.info_value) MAX_LEVEL
       FROM TF_B_TRADE_PLATSVC_ATTR e
       WHERE e.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
       AND e.user_id = TO_NUMBER(:USER_ID)
       AND e.service_id = TO_NUMBER(:SERVICE_ID)
       AND e.info_code = '302'