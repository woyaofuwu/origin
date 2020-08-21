UPDATE tf_b_trade_svc
   SET serv_para1=(SELECT discnt_code
                     FROM tf_b_trade_discnt a
                    WHERE trade_id=TO_NUMBER(:TRADE_ID)
                      AND accept_month=TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
                      AND modify_tag=:MODIFY_TAG
                      AND EXISTS(SELECT 1 FROM td_b_discnt b
                                  WHERE a.discnt_code=b.discnt_code
                                    AND b.discnt_type_code=:DISCNT_TYPE_CODE
                                    AND SYSDATE BETWEEN start_date AND end_date))
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND service_id=:SERVICE_ID
   AND modify_tag=:MODIFY_TAG