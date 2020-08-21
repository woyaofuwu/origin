--IS_CACHE=Y
select b.data_code
from tf_m_staffdataright a, tf_m_roledataright b
where 
a.data_type = :DATA_TYPE
and a.right_attr = 1
and a.right_tag = 1
and a.data_code = b.role_code
and a.staff_id = :TRADE_STAFF_ID
union 
select a.data_code
from tf_m_staffdataright a
where 
a.data_type = :DATA_TYPE
and a.right_attr = 0
and a.right_tag = 1
and a.staff_id =:TRADE_STAFF_ID