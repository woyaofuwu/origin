/*TF_B_TRADE_BLACKWHITE SEL_PLATSVC_SVCINFO*/
SELECT
t.service_id,
to_char(t.TRADE_ID) TRADE_ID, 
to_char(t.INST_ID) INST_ID, 
t.ACCEPT_MONTH, 
to_char(t.USER_ID) USER_ID, 
t.USER_TYPE_CODE, 
t.SERV_CODE, 
t.SERIAL_NUMBER, 
t.GROUP_ID, 
t.BIZ_CODE, 
t.BIZ_NAME, 
t.BIZ_DESC, 
to_char(t.BOOK_DATE,'yyyy-mm-dd hh24:mi:ss') BOOK_DATE, 
t.CONTRACT_ID, 
to_char(t.PRICE) PRICE, 
t.BILLING_TYPE, 
t.OPR_SEQ_ID, 
to_char(t.EXPECT_TIME,'yyyy-mm-dd hh24:mi:ss') EXPECT_TIME, 
to_char(t.START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, 
to_char(t.END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, 
t.MODIFY_TAG, 
to_char(t.UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, 
t.UPDATE_STAFF_ID,  
t.UPDATE_DEPART_ID,  
t.REMARK,  
t.RSRV_NUM1,  
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
t.RSRV_TAG3,
t.oper_state
 FROM TF_B_TRADE_BLACKWHITE t
WHERE t.trade_id = :TRADE_ID
AND t.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2)) 
AND t.service_id = :SERVICE_ID
AND (
(t.USER_TYPE_CODE in ('QB','EB') 
AND t.oper_state IN (
SELECT s.param_code FROM td_s_commpara s WHERE s.param_attr = '737' AND s.para_code1 = '1' AND s.subsys_code = 'CSM'
))
OR(t.USER_TYPE_CODE in ('B','W','S','XW') 
AND t.oper_state IN (
SELECT s.param_code FROM td_s_commpara s WHERE s.param_attr = '747' AND s.para_code1 = '1' AND s.subsys_code = 'CSM'
)))
AND t.RSRV_TAG3 IN ('0','1','2','3')