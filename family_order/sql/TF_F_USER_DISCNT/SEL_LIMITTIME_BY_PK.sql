SELECT partition_id,to_char(user_id) user_id,to_char(user_id_a) user_id_a,discnt_code,spec_tag,relation_type_code,to_char( decode(:DATE_TYPE,0,start_date+:ADD_NUM,
                            1,add_months(start_date,:ADD_NUM),
                            2,add_months(start_date,:ADD_NUM*12),
                            10,trunc(start_date)+:ADD_NUM,
                            11,add_months(trunc(start_date,'mm'),:ADD_NUM),
                            12,add_months(trunc(start_date,'yyyy'),:ADD_NUM*12)),'yyyy-mm-dd')  start_date,to_char(sysdate,'yyyy-mm-dd') end_date 
  FROM tf_f_user_discnt
 WHERE partition_id=:PARTITION_ID
   AND user_id=TO_NUMBER(:USER_ID)
   AND discnt_code=:DISCNT_CODE
   AND end_date > sysdate