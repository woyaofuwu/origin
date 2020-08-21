/*TF_B_TRADE_GRP_MERCH_MEB SEL_PLATSVC_SVCINFO*/
SELECT t.SERVICE_ID,
to_char(t.TRADE_ID) TRADE_ID, 
t.ACCEPT_MONTH, 
to_char(t.USER_ID) USER_ID, 
t.SERIAL_NUMBER, 
to_char(t.EC_USER_ID) EC_USER_ID, 
t.EC_SERIAL_NUMBER, 
t.PRODUCT_ORDER_ID, 
t.PRODUCT_OFFER_ID, 
to_char(t.START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, 
to_char(t.END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, 
t.MODIFY_TAG, 
to_char(t.UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, 
t.UPDATE_STAFF_ID, 
t.UPDATE_DEPART_ID, 
t.REMARK, RSRV_NUM1, 
t.RSRV_NUM2, 
t.RSRV_NUM3, 
to_char(t.RSRV_NUM4) RSRV_NUM4, 
to_char(t.RSRV_NUM5) RSRV_NUM5, 
t.RSRV_STR1, 
t.RSRV_STR2, 
t.RSRV_STR3, 
t.RSRV_STR4, 
t.RSRV_STR5, 
to_char(t.RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, 
to_char(t.RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, 
to_char(t.RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, 
t.RSRV_TAG1, 
t.RSRV_TAG2, 
t.RSRV_TAG3
 FROM tf_b_trade_grp_merch_meb t
WHERE t.trade_id = :TRADE_ID 
AND t.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2)) 
AND t.modify_tag IN (
SELECT s.param_code FROM td_s_commpara s WHERE s.param_attr = '757' AND s.para_code1 = '1' AND s.subsys_code ='CSM'
)
AND t.RSRV_TAG3 IN ('0','1','2')