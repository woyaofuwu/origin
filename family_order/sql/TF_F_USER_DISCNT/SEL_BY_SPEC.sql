SELECT partition_id,to_char(user_id) user_id,to_char(user_id_a) user_id_a,discnt_code,spec_tag,relation_type_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_discnt a
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND end_date>sysdate
   AND exists (select 1 from td_s_commpara 
                where param_attr=96
                  AND subsys_code='CSM'
                  AND sysdate between start_date and end_date
                  AND eparchy_code=:TRADE_EPARCHY_CODE
                  AND to_number(PARAM_CODE)=a.discnt_code)
  AND (:TRADE_STAFF_ID='SUPERUSR' OR EXISTS (SELECT 1 FROM tf_m_staffdataright C,tf_m_roledataright D
                                                WHERE C.staff_id=:TRADE_STAFF_ID
                                                  AND C.data_code=D.role_code
                                                  AND C.right_attr='1'   --权限属性：1-数据角色权限
                                                  AND C.right_tag='1'    --权限标志：1-有效
                                                  AND D.data_type='3'    --数据类型：3-优惠权限
                                                  AND D.data_code=TO_CHAR(a.discnt_code)))
ORDER BY discnt_code