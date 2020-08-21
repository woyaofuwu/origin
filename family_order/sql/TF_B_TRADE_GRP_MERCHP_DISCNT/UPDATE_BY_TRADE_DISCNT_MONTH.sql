update TF_B_TRADE_GRP_MERCHP_DISCNT b set  rsrv_tag1=:RSRV_TAG1
where  b.trade_id=TO_NUMBER(:TRADE_ID)
 and  b.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
  and b.product_discnt_code=:PRODUCT_DISCNT_CODE
  and b.user_id=:USER_ID