select t.RSRV_NUM2,t.INST_ID,t.user_id  
from TF_F_USER_RES t
where t.res_type_code= '4'
and t.rsrv_tag1 = 'J'
and sysdate between t.start_date and t.end_date
and to_number(t.rsrv_num2)>0