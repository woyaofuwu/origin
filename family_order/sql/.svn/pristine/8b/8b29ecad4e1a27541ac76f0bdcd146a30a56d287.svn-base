UPDATE tf_f_user_otherserv
  SET process_tag='1',end_date=sysdate,remark='特殊终止'  
    WHERE user_id=TO_NUMBER(:USER_ID)
      AND partition_id=mod(TO_NUMBER(:USER_ID),10000)
        AND rsrv_str2=to_char(:TRADE_ID)