SELECT count(1) recordcount
 FROM tf_fh_group_order a
 where
   ( exists
       ( select 1
         from tf_f_groupmember_product c
         WHERE a.group_id=c.group_id
          AND (c.serial_number=:PARAM3 or :PARAM3 IS NULL)
       )
     or :PARAM3 IS NULL
    )
   AND (a.contract_id like '%'||:PARAM1||'%' or :PARAM1 IS NULL)
   AND (a.prevaluec1 in (
		  select product_name from td_b_grp_product
		  start with product_name = :PARAM2
		  connect by prior product_name = brand_code
		)  or :PARAM2 IS NULL)
   AND (a.in_date>=TO_DATE(:PARAM4 , 'YYYY-MM-DD HH24:MI:SS') or :PARAM4 IS NULL)
   AND (a.finish_date>=TO_DATE(:PARAM5 , 'YYYY-MM-DD HH24:MI:SS') or :PARAM5 IS NULL)
   AND a.backmonth=:PARAM8
        and exists
         ( select 1
	   from tf_f_cust_group b,td_m_staff d,td_m_depart e
	   where b.manager_staff_id = d.staff_id
	   and d.depart_id = e.depart_id
	   and a.group_id = b.group_id
	   and b.remove_tag = '0'
	   and ( b.manager_staff_id = :PARAM6 OR :PARAM6 IS NULL)
           and (b.group_id like '%'||:PARAM0||'%' or :PARAM0 IS NULL)
	   and e.depart_frame like (
               select t.depart_frame from td_m_depart t where t.depart_id= :PARAM7)||'%'
	  )