--IS_CACHE=Y
SELECT product_id,org_domain,cop_id,biz_code,discnt_code,discnt_name,discnt_mode,force_tag,rela_discnt_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM td_b_siservice_discnt
 WHERE product_id=:PRODUCT_ID
   AND (org_domain=:ORG_DOMAIN OR :ORG_DOMAIN = '*')
   AND (cop_id=:COP_ID OR :COP_ID = '-1')
   AND (biz_code=:BIZ_CODE OR :BIZ_CODE = '*')
   AND sysdate BETWEEN start_date AND end_date