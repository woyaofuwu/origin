--IS_CACHE=Y
select a.staff_id from td_m_staff a,td_m_depart b,td_m_departkind c
where a.depart_id=b.depart_id 
and b.depart_kind_code = c.depart_kind_code
and b.validflag='0'
and c.code_type_code ='0'
and a.staff_id= :STAFF_ID