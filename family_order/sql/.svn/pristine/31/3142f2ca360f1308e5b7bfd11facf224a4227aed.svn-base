UPDATE ti_o_interfacesms
   SET deal_time=sysdate,deal_state=:DEAL_STATE  
 WHERE sms_notice_id=TO_NUMBER(:SMS_NOTICE_ID)
   AND deal_state=:DEAL_STATE