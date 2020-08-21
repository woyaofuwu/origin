INSERT INTO  TF_B_TRADE_RELATION(TRADE_ID,ACCEPT_MONTH,USER_ID_A,SERIAL_NUMBER_A,USER_ID_B, --1
SERIAL_NUMBER_B,RELATION_TYPE_CODE,ROLE_TYPE_CODE,ROLE_CODE_A,ROLE_CODE_B,
ORDERNO,SHORT_CODE,INST_ID,START_DATE,END_DATE,MODIFY_TAG,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,
RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5, RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5, RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3)
SELECT TO_NUMBER(:TRADE_ID),TO_NUMBER(SUBSTR(:TRADE_ID,5,2)), user_id_a,serial_number_a,user_id_b,
serial_number_b,relation_type_code,ROLE_TYPE_CODE,role_code_a,role_code_b,
orderno,short_code,f_sys_getseqid('ZZZZ','seq_inst_id') ,start_date,end_date,'0',UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK, 
RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5, RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5, RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2, RSRV_TAG3
  FROM tf_b_trade_relation_uu_bak   a
 WHERE trade_id = TO_NUMBER(:RSRV_STR1)
   AND accept_month = TO_NUMBER(SUBSTR(:RSRV_STR1,5,2))
   AND user_id_b = TO_NUMBER(:USER_ID)
   AND EXISTS (SELECT 1 FROM td_s_commpara a
                where a.subsys_code ='CSM'
                and a.param_attr = '6017'
                And  a.param_code='1'
                and a.para_code1 = relation_type_code   
                And ( substr(a.eparchy_code,3)=substr(a.trade_id,0,2)  Or  eparchy_code='ZZZZ')
                and a.end_date > sysdate
                )
   AND end_date > SYSDATE