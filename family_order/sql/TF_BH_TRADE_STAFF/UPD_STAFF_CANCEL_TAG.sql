UPDATE tf_bh_trade_staff
   SET cancel_tag=:CANCEL_TAG,cancel_date=TO_DATE(:CANCEL_DATE, 'YYYY-MM-DD HH24:MI:SS'),cancel_staff_id=:CANCEL_STAFF_ID,cancel_depart_id=:CANCEL_DEPART_ID,cancel_city_code=:CANCEL_CITY_CODE,cancel_eparchy_code=:CANCEL_EPARCHY_CODE  
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND cancel_tag = '0'