INSERT INTO TF_B_TRADE_ATTR(TRADE_ID,ACCEPT_MONTH,USER_ID,INST_TYPE,INST_ID,ATTR_CODE,ATTR_VALUE,START_DATE,END_DATE,MODIFY_TAG,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3)
SELECT :TRADE_ID,TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)),a.USER_ID,'D',a.inst_id ,b.attr_code,b.attr_init_value,a.start_date,to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss') end_date,a.modify_tag,SYSDATE,a.UPDATE_STAFF_ID,a.UPDATE_DEPART_ID,a.REMARK,a.discnt_code,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null
from  tf_b_trade_discnt a, td_b_attr_itema b
WHERE a.trade_id = TO_NUMBER(:TRADE_ID)
  AND a.accept_month = TO_NUMBER(substr(:TRADE_ID,5,2))
  AND a.discnt_code = b.id  AND B.ID_TYPE = 'D'
  AND b.attr_can_null = '0'