INSERT INTO tf_o_trashmsguser_other 
             (serial_number,sms_type,cust_type,state_code,
             start_date,end_date,deal_tag,deal_time) 
      VALUES(:SERIAL_NUMBER,:SMS_TYPE,:CUST_TYPE,:STATE_CODE,
             sysdate,to_date(:END_DATE,'YYYY-MM-DD HH24:MI:SS'),:DEAL_TAG,sysdate)