SELECT /*+ index(tf_f_user_discnt,PK_TF_F_USER_DISCNT)*/ partition_id,to_char(user_id) user_id,to_char(user_id_a) user_id_a,discnt_code,spec_tag,relation_type_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
FROM tf_f_user_discnt WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
AND user_id=TO_NUMBER(:USER_ID)
AND spec_tag='2'
AND relation_type_code='20'
AND discnt_code IN (1285,1286,1391,687,688,689)
AND start_date < sysdate
AND to_char(end_date,'yyyymm') = to_char(SYSDATE,'yyyymm')
AND end_date = (SELECT /*+ index(tf_f_user_discnt,PK_TF_F_USER_DISCNT)*/ MAX(end_date) FROM  tf_f_user_discnt
               WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
               AND user_id=TO_NUMBER(:USER_ID)
               AND spec_tag='2'
               AND relation_type_code='20'
               AND discnt_code IN (1285,1286,1391,687,688,689)
               AND to_char(end_date,'yyyymm') = to_char(SYSDATE,'yyyymm')
               )
AND ROWNUM < 2