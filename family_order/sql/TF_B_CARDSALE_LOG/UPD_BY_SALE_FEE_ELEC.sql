UPDATE tf_b_cardsale_log
   SET sale_type_code=:SALE_TYPE_CODE_NEW , rsrv_str2=:FEE_STAFF_ID, 
       rsrv_str3=:FEE_DEPART_ID, rsrv_date1=SYSDATE, remark=remark||'缴费'
 WHERE sale_type_code=:SALE_TYPE_CODE
   AND sale_time = (select sale_time from tf_b_cardsale_log where log_id=TO_NUMBER(:LOG_ID))
   AND rsrv_str5 in (select rsrv_str5 from tf_b_cardsale_log where log_id=TO_NUMBER(:LOG_ID))
   AND start_value=:START_VALUE