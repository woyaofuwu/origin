UPDATE ts_a_valuecard_staff
   SET total_num=TO_NUMBER(:TOTAL_NUM),oper_staff_id=:OPER_STAFF_ID,oper_time=sysdate,oper_depart_id=:OPER_DEPART_ID,remark=:REMARK  
 WHERE staff_id=:STAFF_ID
   AND value_card_type_code=:VALUE_CARD_TYPE_CODE
   AND (:VALUE_CODE is null or value_code=:VALUE_CODE)
   AND in_date=to_char(sysdate,'yyyymmdd')