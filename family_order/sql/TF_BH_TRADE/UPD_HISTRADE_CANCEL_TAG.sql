UPDATE /*+INDEX(a,IDX_TF_BH_TRADE)*/ tf_bh_trade a
   SET a.cancel_tag=:CANCEL_TAG,a.cancel_date=TO_DATE(:CANCEL_DATE, 'YYYY-MM-DD HH24:MI:SS'),a.cancel_staff_id=:CANCEL_STAFF_ID,a.cancel_depart_id=:CANCEL_DEPART_ID,a.cancel_city_code=:CANCEL_CITY_CODE,a.cancel_eparchy_code=:CANCEL_EPARCHY_CODE  
 WHERE a.trade_id = TO_NUMBER(:TRADE_ID)
   AND a.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND a.cancel_tag = '0'