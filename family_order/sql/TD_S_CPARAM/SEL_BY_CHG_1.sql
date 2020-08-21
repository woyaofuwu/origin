SELECT count(1) recordcount
from
   ( select a.*,b.company_address,b.group_contact_phone,b.cust_name,rownum tmpnum
     from tf_fh_cust_groupmember a,tf_fh_cust_group b,td_m_staff c,td_m_depart d
     where a.group_id=b.group_id and b.manager_staff_id=c.staff_id
     and c.depart_id = d.depart_id
     and a.backmonth = '200509' and b.backmonth = '200509'
     and a.destroy_date between to_date(:PARAM0,'yyyy-mm-dd hh24:mi:ss')
     and to_date(:PARAM1,'yyyy-mm-dd hh24:mi:ss') and a.rsrv_1='2'
     and (a.group_id=:PARAM2 or :PARAM2 is null)
     and (b.manager_staff_id=:PARAM3 or :PARAM3 is null)
     and ( d.depart_frame like (
         select t.depart_frame from td_m_depart t where t.depart_id=:PARAM4)||'%'
     or :PARAM4 is null )
) tmptable