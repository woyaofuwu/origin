select a.user_id,a.inst_id,a.discnt_code,a.start_date,a.end_date,
b.inst_type,b.attr_code,b.attr_value,b.element_id,b.start_date,b.end_date
from TF_F_USER_DISCNT a,TF_F_USER_ATTR b 
where a.inst_id=b.rela_inst_id 
and a.user_id=b.user_id
and b.user_id=:USER_ID
and a.DISCNT_CODE=:ELEMENT_ID
and( b.ELEMENT_ID=:ELEMENT_ID or b.ELEMENT_ID='')
and to_date(:DATE,'YYYY-MM-DD HH24:MI:SS') between a.start_date and a.end_date
and to_date(:DATE,'YYYY-MM-DD HH24:MI:SS') between b.start_date and b.end_date