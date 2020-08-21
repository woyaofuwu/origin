Update TF_F_USER_SALE_GOODS 
   Set remark ='换机修改用户IMEI,老IMEI:'||res_code,RES_CODE=:NEW_IMEI  ,update_time=Sysdate,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID 
 WHERE user_id =to_number(:USER_ID) 
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)   
   And res_type_code ='4'
   And GOODS_STATE = :GOODS_STATE
   And RES_CODE=:OLD_IMEI