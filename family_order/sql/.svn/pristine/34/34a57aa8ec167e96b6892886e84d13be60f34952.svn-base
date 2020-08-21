SELECT TO_CHAR(TRADE_ID) TRADE_ID,DISCNT_CODE,INST_ID,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID
  FROM tf_b_trade_discnt a
 WHERE trade_id = TO_NUMBER(:TRADE_ID) and accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
    AND modify_tag = trim(:MODIFY_TAG)  and exists (SELECT 1
          FROM td_b_dtype_discnt b
         WHERE a.discnt_code = b.discnt_code
           AND trim(b.discnt_type_code) = trim(:DISCNT_TYPE_CODE)
           AND SYSDATE BETWEEN start_date AND end_date)