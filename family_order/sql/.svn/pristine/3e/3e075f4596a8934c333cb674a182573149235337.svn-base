Insert Into tf_f_user_attr (PARTITION_ID,  USER_ID,INST_TYPE, INST_ID, ATTR_CODE, ATTR_VALUE, START_DATE, END_DATE, UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK,  RSRV_NUM1)
Select   Mod(TO_NUMBER(:USER_ID),10000) PARTITION_ID , TO_NUMBER(:USER_ID) USER_ID,:INST_TYPE, :INST_ID, :ATTR_CODE, :ATTR_VALUE, to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, 
to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, to_date(:UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, :UPDATE_STAFF_ID, :UPDATE_DEPART_ID, :REMARK, :RSRV_NUM1
From dual 
 WHERE  NOT EXISTS (SELECT 1 FROM tf_f_user_attr
                    WHERE user_id = TO_NUMBER(:USER_ID)
                      AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
                      AND inst_id = :INST_ID 
                      And inst_type=:INST_TYPE
                      And attr_code=:ATTR_CODE 
                      And start_date =to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss')
                      AND end_date > start_date
                      AND end_date > SYSDATE)