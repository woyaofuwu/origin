select to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_Date,to_char(a.end_Date,'yyyy-mm-dd hh24:mi:ss') end_Date, a.relation_trade_id, b.subsys_code,b.param_attr,b.param_code,b.param_name,b.para_code1,b.para_code2,b.para_code3,b.para_code4,b.para_code5,b.para_code6,b.para_code7,b.para_code8,b.para_code9,b.para_code10 
from tf_f_user_sale_active a,td_s_commpara b
where a.product_id=b.param_code
and a.package_id=b.para_code1
and b.subsys_code='CSM'
and b.param_attr=1143

and a.partition_id=mod(:USER_ID,10000)
and a.user_id=:USER_ID
and a.accept_date>=to_date(b.para_code4,'yyyy-mm-dd hh24:mi:ss')
and a.end_date>sysdate
and a.process_tag='0'
and sysdate between b.start_date and b.end_date