UPDATE tf_b_trade
   SET subscribe_state='A',
       finish_date=SYSDATE,
       cancel_tag=:CANCEL_TAG,
       cancel_staff_id= :CANCEL_STAFF_ID,
       cancel_depart_id= :CANCEL_DEPART_ID,
       cancel_city_code= :CANCEL_CITY_CODE,
       cancel_eparchy_code = :CANCEL_EPARCHY_CODE
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND exec_time > SYSDATE