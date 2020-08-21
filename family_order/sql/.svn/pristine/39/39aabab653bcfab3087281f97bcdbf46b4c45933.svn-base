--IS_CACHE=Y
SELECT TRADE_TYPE_CODE, IN_MODE_CODE, VIP_CLASS_ID, PRODUCT_ID, PACKAGE_ID, ELEMENT_TYPE_CODE, ELEMENT_ID, to_char(CAMPN_ID) CAMPN_ID, FEE_MODE, FEE_TYPE_CODE, to_char(FEE) FEE, to_char(LIMIT_MONEY) LIMIT_MONEY, PAY_MODE, IN_DEPOSIT_CODE, OUT_DEPOSIT_CODE, to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, EPARCHY_CODE, to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, RSRV_STR7, RSRV_STR8, RSRV_STR9, RSRV_STR10, TRADE_FEE_TYPE, RULE_BIZ_KIND_CODE
  from TD_B_PRODUCT_TRADEFEE t
 where (t.TRADE_TYPE_CODE = :TRADE_TYPE_CODE Or TRADE_TYPE_CODE ='-1')    
   And (t.in_mode_code=:IN_MODE_CODE Or  in_mode_code Is Null )       
   And (t.vip_class_id=:VIP_CLASS_ID Or  vip_class_id Is Null )   
   And (t.ELEMENT_TYPE_CODE = :ELEMENT_TYPE_CODE  Or ELEMENT_TYPE_CODE Is Null)
   and (t.PRODUCT_ID = :PRODUCT_ID or t.PRODUCT_ID = '-1')
   and (t.PACKAGE_ID = :PACKAGE_ID or t.PACKAGE_ID = '-1')
   and (t.CAMPN_ID = :CAMPN_ID or t.CAMPN_ID = '-1')
   and (t.ELEMENT_ID = :ELEMENT_ID or t.ELEMENT_ID = '-1')
   and (t.EPARCHY_CODE = :EPARCHY_CODE or t.EPARCHY_CODE = 'ZZZZ')
   and t.TRADE_FEE_TYPE =:TRADE_FEE_TYPE
  and sysdate between t.START_DATE and t.END_DATE
  and  not exists 
(select 1 from td_s_commpara tt where tt.subsys_code='CSM' and tt.param_attr=88
   and tt.eparchy_code= :EPARCHY_CODE  and tt.param_code= :TRADE_TYPE_CODE  
   and sysdate between tt.START_DATE and tt.END_DATE
 )