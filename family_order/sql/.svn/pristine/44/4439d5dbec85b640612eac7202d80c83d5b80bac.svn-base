UPDATE tf_b_trade_discnt
   SET start_date=TO_DATE(:NEW_DATE, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=:ACCEPT_MONTH
   AND id=TO_NUMBER(:ID)
   AND id_type=:ID_TYPE
   AND discnt_code=:DISCNT_CODE
   AND modify_tag=:MODIFY_TAG