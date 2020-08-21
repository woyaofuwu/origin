SELECT count(*) recordcount
 FROM tf_f_user a ,tf_f_cust_group b
 WHERE a.cust_id = b.cust_id
AND b.remove_tag = '0'
AND ( a.product_id = 8000 or a.product_id = 8010 )
AND a.partition_id = MOD(user_id,10000)
AND (b.group_id = :PARAM0 or :PARAM0 IS NULL)
AND (a.serial_number = :PARAM1 or :PARAM1 IS NULL)
AND (b.manager_staff_id=:PARAM2 or :PARAM2 IS NULL)
AND EXISTS
     ( SELECT 1
       FROM  td_m_staff c,td_m_depart d
       WHERE b.manager_staff_id = c.staff_id
       and   c.depart_id = d.depart_id
       and d.depart_frame like (
           select t.depart_frame from td_m_depart t where t.depart_id= :PARAM3)||'%'
      )