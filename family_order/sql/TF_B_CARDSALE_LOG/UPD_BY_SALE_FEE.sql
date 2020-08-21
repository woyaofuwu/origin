UPDATE tf_b_cardsale_log
   SET sale_type_code=:SALE_TYPE_CODE_NEW , rsrv_str2=:FEE_STAFF_ID, 
       rsrv_str3=:FEE_DEPART_ID, rsrv_date1=SYSDATE, remark=remark||'缴费'
 WHERE log_id=TO_NUMBER(:LOG_ID)
   AND sale_type_code=:SALE_TYPE_CODE