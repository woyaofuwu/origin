UPDATE tf_f_othertradefee
   SET pay_mode_code=:PAY_MODE_CODE,pay_tag=:PAY_TAG,remark=remark||:TRADE_STAFF_ID||:REMARK,trade_time=TO_DATE(:TRADE_TIME, 'YYYY-MM-DD HH24:MI:SS'),trade_staff_id=:TRADE_STAFF_ID,trade_depart_id=:TRADE_DEPART_ID,eparchy_code=:EPARCHY_CODE,city_code=:CITY_CODE  
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND contract_no=:CONTRACT_NO
   AND feeitem_code=:FEEITEM_CODE