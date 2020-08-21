select count(1) recordcount
FROM tf_f_cust_contract a,tf_f_cust_group b
 WHERE a.cust_id=b.cust_id
   AND (contract_id like '%'||:PARAM0||'%' or :PARAM0 is null)
   AND contract_name like '%'||:PARAM5||'%'
   AND (substr(rsrv_str2,0,6) = :PARAM6 or :PARAM6 is null)
   AND (contract_start_date>=to_date(:PARAM7,'YYYY-MM-DD HH:MI:SS') or :PARAM7 is null)
   AND (contract_end_date>=to_date(:PARAM8,'YYYY-MM-DD HH:MI:SS')  or :PARAM8 is null)
   AND b.remove_tag = '0'
   AND ( b.manager_staff_id = :PARAM3 OR :PARAM3 IS NULL)
   AND b.group_id like '%'||:PARAM1||'%'
   AND a.contract_id in
         ( select c.contract_id
	   from td_m_staff d,td_m_depart e,tf_f_group_order c
	   where b.manager_staff_id = d.staff_id and b.group_id=c.group_id
	   and d.depart_id = e.depart_id
	   and ( c.prevaluec1 in
                 ( select product_name from td_b_grp_product
		   start with product_name = :PARAM2
		   connect by prior product_name = brand_code
		  ) or :PARAM2 is null )
	   and e.depart_frame like (
               select t.depart_frame from td_m_depart t where t.depart_id= :PARAM4)||'%')