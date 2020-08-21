UPDATE TI_BI_MO_MON_OTHER
   SET deal_tag='2',finish_time=TO_DATE(:FINISH_TIME, 'YYYY-MM-DD HH24:MI:SS'),result_code=decode(:MODIFY_TAG,'0',60,'1',70),result_info=:RESULT_INFO 
 WHERE msisdn=:SERIAL_NUMBER
   AND sms_type = :SMS_TYPE
   AND deal_tag='0'