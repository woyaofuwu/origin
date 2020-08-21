UPDATE tf_f_user_purchase 
  SET process_tag='1',end_date=sysdate,remark='特殊终止'
    WHERE user_id = TO_NUMBER(:USER_ID)
        AND trade_id+0 = TO_NUMBER(:TRADE_ID)