SELECT count(1) recordcount
 FROM tf_f_cust_groupmember c, tf_f_cust_group d ,td_m_staff b,td_m_depart a
  where c.group_id = d.group_id AND d.manager_staff_id = b.staff_id
  and b.depart_id = a.depart_id
  AND c.remove_tag = '0' AND d.remove_tag = '0'
 and a.depart_frame like (
   select t.depart_frame from td_m_depart t where t.depart_id=:PARAM0)||'%'
 and (d.manager_staff_id =:PARAM1 or :PARAM1 is null)
 and (c.serial_number =:PARAM2 or :PARAM2 is null)
 and (c.group_id =:PARAM3 or :PARAM3 is null)
 and (d.cust_name =:PARAM4 or :PARAM4 is null)
 and (c.vip_tag =:PARAM5 or :PARAM5 is null)
 and (c.open_date>=TO_DATE(:PARAM6 , 'YYYY-MM-DD HH24:MI:SS') or :PARAM6 is null)
 and (c.open_date<=TO_DATE(:PARAM7 , 'YYYY-MM-DD HH24:MI:SS') or :PARAM7 is null)
 AND  c.vpmn_group_id IS not NULL