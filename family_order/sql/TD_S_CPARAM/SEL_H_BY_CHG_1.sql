SELECT count(1) recordcount
from tf_fh_cust_groupmember a
WHERE a.backmonth = :PARAM5
and a.destroy_date between to_date(:PARAM0,'yyyy-mm-dd hh24:mi:ss')
and to_date(:PARAM1,'yyyy-mm-dd hh24:mi:ss')
and (a.group_id=:PARAM2 or :PARAM2 is null)
and (b.cust_manager_id=:PARAM3 or :PARAM3 is null)
and a.remove_tag='2'
and
    ( exists
    ( select 1
    from td_m_staff c,td_m_depart d
     where cust_manager_id=c.staff_id
     and c.depart_id = d.depart_id
     and d.depart_frame like (
         select t.depart_frame from td_m_depart t where t.depart_id=:PARAM4)||'%'
     )
     or :PARAM4 is null )