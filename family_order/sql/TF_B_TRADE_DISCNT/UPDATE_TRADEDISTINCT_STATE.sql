UPDATE tf_b_trade_discnt set  MODIFY_TAG = :MODIFY_TAG,end_date= to_date( :END_DATE,'YYYY-MM-DD HH24:MI:SS'),start_date= to_date( :START_DATE,'YYYY-MM-DD HH24:MI:SS'),rsrv_str3= :RSRV_STR3,RSRV_TAG1= :RSRV_TAG1
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=TO_NUMBER(substr(:TRADE_ID,5,2))
   AND user_id=TO_NUMBER(:USER_ID)
   AND discnt_code=:DISCNT_CODE
   AND package_id=:PACKAGE_ID