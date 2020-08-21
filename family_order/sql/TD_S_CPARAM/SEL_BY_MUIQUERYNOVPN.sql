SELECT a.*,rownum tmpnum
FROM tf_f_cust_groupmember_other a
where a.remove_tag='0'
 and exists
 AND (a.group_id=:PARAM0 OR :PARAM0 IS NULL)
 AND (a.cust_name like '%'||:PARAM1||'%' OR :PARAM1 IS NULL)
 AND (a.serial_number=:PARAM2 OR :PARAM2 IS NULL)
 AND (a.open_date >= to_date(:PARAM3,'yyyy-mm-dd') or :PARAM3 IS NULL)
 AND (a.open_date <= to_date(:PARAM4,'yyyy-mm-dd') OR :PARAM4 IS NULL)
 AND (a.manager_staff_id=:PARAM5 OR :PARAM5 IS NULL)
 and exists
   ( select 1
    from td_m_staff c,td_m_depart d
    WHERE a.cust_manager_id = c.staff_id
    and c.depart_id = d.depart_id
    and d.depart_frame like (
       select t.depart_frame from td_m_depart t where t.depart_id=:PARAM6)||'%'
  )
 and a.vpmn_id is NULL