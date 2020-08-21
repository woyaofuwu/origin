SELECT /*+use_nl(b)*/a.partition_id,to_char(a.user_id) user_id,a.biz_code,a.pre_charge,to_char(a.max_item_pre_day) max_item_pre_day,to_char(a.max_item_pre_mon) max_item_pre_mon,a.is_text_ecgn,a.default_ecgn_lang,a.text_ecgn_en,a.text_ecgn_zh,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,a.rsrv_num1,a.rsrv_num2,a.rsrv_num3,to_char(a.rsrv_num4) rsrv_num4,to_char(a.rsrv_num5) rsrv_num5,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,to_char(a.rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(a.rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(a.rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3 
  FROM tf_f_user_grpmbmp_info a,tf_f_cust_group b,tf_f_user c
 WHERE b.group_id=:GROUP_ID
   AND b.remove_tag = '0'
   AND b.cust_id=c.cust_id
   AND c.remove_tag = '0'
   AND c.user_id=a.user_id
   AND mod(c.user_id,10000)=a.partition_id
   AND a.end_date>SYSDATE
   AND c.product_id IN (SELECT param_code FROM td_s_commpara
                         WHERE param_attr=:PARAM_ATTR
                           AND param_code<>:PRODUCT_ID
                           AND para_code5=:PARA_CODE5
                           AND (eparchy_code = :EPARCHY_CODE OR :EPARCHY_CODE IS NULL)
                           AND end_date>SYSDATE)