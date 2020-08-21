UPDATE tf_b_trade_svc
   SET serv_para1=(SELECT discnt_code
                     FROM td_b_product_discnt f
                    WHERE product_id = :PRODUCT_ID
                      AND force_tag = '1'
                      AND SYSDATE BETWEEN start_date AND end_date
                      AND EXISTS(SELECT 1 FROM td_b_discnt
                                  WHERE f.discnt_code=discnt_code
                                    AND discnt_type_code=:DISCNT_TYPE_CODE
                                    AND SYSDATE BETWEEN start_date AND end_date)
                   UNION               
                   SELECT discnt_code
                     FROM td_b_product_discnt a
                    WHERE product_id = :PRODUCT_ID
                      AND forcegroup_tag='1'
                      AND force_tag != '1'
                      AND SYSDATE BETWEEN start_date AND end_date
                      AND exists (select 1 from tf_f_user_discnt
                                   where partition_id=mod(TO_NUMBER(:USER_ID),10000)
                                     and user_id=TO_NUMBER(:USER_ID)
                                     and discnt_code=a.discnt_code
                                     and end_date=(select max(end_date) from tf_f_user_discnt c
                                                    where partition_id=mod(TO_NUMBER(:USER_ID),10000)
                                                      and user_id=TO_NUMBER(:USER_ID)
                                                      and exists (select 1 from td_b_product_discnt
                                                                   where product_id = :PRODUCT_ID
                                                                     AND forcegroup_tag='1'
                                                                     AND SYSDATE BETWEEN start_date AND end_date
                                                                     AND discnt_code=c.discnt_code)))
                      AND EXISTS(SELECT 1 FROM td_b_discnt
                                  WHERE a.discnt_code=discnt_code
                                    AND discnt_type_code=:DISCNT_TYPE_CODE
                                    AND SYSDATE BETWEEN start_date AND end_date))
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND service_id=:SERVICE_ID
   AND modify_tag=:MODIFY_TAG