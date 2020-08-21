UPDATE ti_a_asyc_recv
   SET spay_fee=TO_NUMBER(:SPAY_FEE),
      all_money=TO_NUMBER(:ALL_MONEY),
      all_new_balance=TO_NUMBER(:ALL_NEW_BALANCE),
      allbowe_fee=TO_NUMBER(:ALLBOWE_FEE),
      allrowe_fee=TO_NUMBER(:ALLROWE_FEE),
      recv_fee=TO_NUMBER(:RECV_FEE),
      operate_id=TO_NUMBER(:OPERATE_ID),
      operate_type=:OPERATE_TYPE,deal_time=SYSDATE,
      deal_tag=:DEAL_TAG,
      result_code=:RESULT_CODE,
      result_info=:RESULT_INFO,
      round_tag=:ROUND_TAG,
      user_id=TO_NUMBER(:USER_ID)  
 WHERE asycrecv_id=TO_NUMBER(:ASYCRECV_ID)