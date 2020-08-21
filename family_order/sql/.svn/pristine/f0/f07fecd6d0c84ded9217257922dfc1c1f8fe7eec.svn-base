UPDATE ti_a_asyc_recv
   SET deal_time=sysdate,deal_tag='1',operate_type='1',operate_id=TO_NUMBER(:OPERATE_ID)
 WHERE acct_id=TO_NUMBER(:ACCT_ID)
   AND user_id=TO_NUMBER(:USER_ID) AND deal_tag='0' AND TRADE_TYPE_CODE=3