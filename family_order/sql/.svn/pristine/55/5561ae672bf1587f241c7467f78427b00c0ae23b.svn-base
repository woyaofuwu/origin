UPDATE tf_b_resorder_contract
   SET start_res_no=:START_RES_NO,end_res_no=:END_RES_NO,depart_id=:DEPART_ID,finish_time=DECODE(:TIME_TAG,'1',SYSDATE,NULL),
       factory_code=:FACTORY_CODE,back_tag=:BACK_TAG_NEW,rsrv_num1=rsrv_num1-TO_NUMBER(:RSRV_NUM1),fee_time=sysdate,
       rsrv_num2=DECODE(trunc(fee_time),trunc(sysdate),rsrv_num2+TO_NUMBER(:RSRV_NUM1),TO_NUMBER(:RSRV_NUM1))
 WHERE contract_id=:CONTRACT_ID
   AND batch_id=TO_NUMBER(:BATCH_ID)
   AND res_type_code=:RES_TYPE_CODE
   AND back_tag=:BACK_TAG