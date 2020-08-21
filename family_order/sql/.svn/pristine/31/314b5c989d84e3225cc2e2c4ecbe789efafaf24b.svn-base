update  TF_B_TRADE_GRP_MERCHP_DISCNT b   
 set MODIFY_TAG = :MODIFY_TAG,end_date=to_date(:END_DATE,'YYYY-MM-DD HH24:MI:SS') ,start_date=to_date(:START_DATE,'YYYY-MM-DD HH24:MI:SS'),rsrv_str1= :RSRV_STR1,rsrv_str2= :RSRV_STR2,IS_NEED_PF= :IS_NEED_PF
  where b.trade_id=TO_NUMBER(:TRADE_ID)
  and  b.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
  and  b.user_id=:USER_ID
  and b.product_discnt_code=:PRODUCT_DISCNT_CODE