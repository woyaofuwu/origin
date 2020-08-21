SELECT to_char(user_id) user_id,acyc_id,to_char(acct_id) acct_id,integrate_item_code,integrate_item,to_char(fee) fee 
  FROM ts_a_usergroupbill
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND acyc_id=:ACYC_ID