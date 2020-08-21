select partition_id,to_char(user_id) user_id,product_id,package_id,to_char(user_id_a) user_id_a,inst_id,discnt_code,spec_tag,
relation_type_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
from ucr_crm1.tf_f_user_discnt t where t.user_id=:USER_ID and t.user_id_a=:USER_ID_A and t.product_id=:PRODUCT_ID
and t.package_id=:PACKAGE_ID and t.discnt_code in('1285','1286','1391') and to_char(t.start_date,'yyyymm')=:SYSMONTH
union
select partition_id,to_char(user_id) user_id,product_id,package_id,to_char(user_id_a) user_id_a,inst_id,discnt_code,spec_tag,
relation_type_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
from ucr_crm1.tf_f_user_discnt t where t.user_id=:USER_ID and t.user_id_a=:USER_ID_A and t.product_id=:PRODUCT_ID
and t.package_id=:PACKAGE_ID and t.discnt_code in('1285','1286','1391') and to_char(t.end_date,'yyyymm')=:SYSMONTH