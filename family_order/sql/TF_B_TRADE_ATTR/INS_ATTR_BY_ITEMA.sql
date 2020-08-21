INSERT INTO TF_B_TRADE_ATTR    
  (TRADE_ID,ACCEPT_MONTH, USER_ID,INST_TYPE,INST_ID,ATTR_CODE,ATTR_VALUE,START_DATE,END_DATE,         
MODIFY_TAG,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,rsrv_num1)
SELECT s.trade_id, s.accept_month,s.user_id,C.ID_TYPE, f_sys_getseqid(:EPARCHY_CODE,'seq_inst_id') inst_id,         
C.ATTR_CODE,C.ATTR_INIT_VALUE,s.START_DATE, s.END_DATE,'0',SYSDATE,s.UPDATE_STAFF_ID, S.UPDATE_DEPART_ID, 
s.REMARK, s.service_id 
  from tf_b_trade_svc s,td_b_attr_itema c
 where s.trade_id = to_number(:TRADE_ID)
   AND s.accept_month = to_number(substr(:TRADE_ID,5,2))
   AND s.user_id = :USER_ID
   AND c.id_type = 'S'
   AND c.ID = s.service_id
   AND C.ATTR_INIT_VALUE IS NOT NULL
   AND C.ATTR_CAN_NULL = '0' 
   AND c.eparchy_code in(:EPARCHY_CODE,'ZZZZ')
   AND SYSDATE BETWEEN C.start_date AND C.end_date
   AND SYSDATE BETWEEN s.start_date AND s.end_date