INSERT INTO  TF_B_TRADE_DISCNT(TRADE_ID,ACCEPT_MONTH,USER_ID,USER_ID_A,PACKAGE_ID, --1
PRODUCT_ID,DISCNT_CODE,SPEC_TAG,RELATION_TYPE_CODE,INST_ID,
CAMPN_ID,START_DATE,END_DATE,MODIFY_TAG,UPDATE_TIME,
UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,
RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,
RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,
RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3)
SELECT :TRADE_ID,to_number(substr(:TRADE_ID,5,2)), user_id,user_id_a,package_id,
product_id,discnt_code,spec_tag,relation_type_code,substr(trade_id,0,2)||substr(f_sys_getseqid('ZZZZ','seq_inst_id'),3) inst_id, 
CAMPN_ID,START_DATE,end_date,'0',UPDATE_TIME, 
UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2, 
RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2, 
RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,
RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3
  FROM tf_b_trade_discnt_bak
 WHERE trade_id = TO_NUMBER(:RSRV_STR1)
   AND accept_month = TO_NUMBER(SUBSTR(:RSRV_STR1,5,2))
   AND user_id = TO_NUMBER(:USER_ID)
   AND discnt_code not in (950,1111,1237,1238,1239)   
   AND ( relation_type_code is null or
        EXISTS (SELECT 1 FROM td_s_commpara a
                where a.subsys_code ='CSM'
                and a.param_attr = 6017    
                And a.param_code='1'
                and a.para_code1 = relation_type_code
                and a.end_date > sysdate
                ) 
           )
   AND end_date > SYSDATE