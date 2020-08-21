UPDATE tf_b_resdaystat_log
   SET oper_date_str=TO_CHAR(SYSDATE,'YYYYMMDD'),oper_time=SYSDATE,oper_staff_id=:OPER_STAFF_ID,para_value9=para_value9+TO_NUMBER(:PARA_VALUE9)  
 WHERE res_type_code=:RES_TYPE_CODE
   AND para_value2=:CARD_TYPE_CODE
   AND depart_id||''=:DEPART_ID
   AND staff_id=:STAFF_ID
   AND (:RSRV_TAG1 IS NULL OR rsrv_tag1=:RSRV_TAG1)
   AND (:RSRV_TAG2 IS NULL OR rsrv_tag2=:RSRV_TAG2)
   AND para_value1=:PARA_VALUE1