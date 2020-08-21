SELECT to_char(a.trade_id) trade_id,to_number(ACCEPT_MONTH) ACCEPT_MONTH,a.user_id,a.sale_project_id,a.sale_package_id,a.element_type_code,a.element_id,a.start_date,a.end_date,b.min_number para_code1,b.max_number para_code2,b.package_name para_code3, para_code4,para_code5, para_code6, para_code7, para_code8,modify_tag
  FROM tf_b_trade_saleproject_sub a,td_b_sale_package b
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND user_id = TO_NUMBER(:USER_ID)
   AND modify_tag = '0'
   AND SYSDATE < a.end_date
   AND a.sale_project_id = :SALE_PROJECT_ID
   AND a.sale_package_id = :SALE_PACKAGE_ID
   AND a.sale_project_id = b.sale_project_id
   AND a.sale_package_id = b.sale_package_id
   AND SYSDATE < b.end_date
UNION ALL
SELECT to_char(0) trade_id,to_number(0) ACCEPT_MONTH,c.user_id,c.sale_project_id,c.sale_package_id,c.element_type_code,c.element_id,c.start_date,c.end_date,d.min_number para_code1,d.max_number para_code2,d.package_name para_code3,'' para_code4,'' para_code5,'' para_code6,'' para_code7,'' para_code8,'' modify_tag
  FROM tf_f_user_saleproject_sub c,td_b_sale_package d
 WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id = TO_NUMBER(:USER_ID)
   AND SYSDATE < c.end_date
   AND c.sale_project_id = :SALE_PROJECT_ID
   AND c.sale_package_id = :SALE_PACKAGE_ID
   AND c.sale_project_id = d.sale_project_id
   AND c.sale_package_id = d.sale_package_id
   AND SYSDATE < d.end_date
   AND NOT EXISTS(SELECT 1 FROM tf_b_trade_saleproject_sub
                   WHERE trade_id = TO_NUMBER(:TRADE_ID)
                     AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
                     AND c.user_id = user_id
                     AND sale_project_id = :SALE_PROJECT_ID
                     AND sale_package_id = :SALE_PACKAGE_ID
                     AND c.sale_project_id = sale_project_id
                     AND c.sale_package_id = sale_package_id
                     AND c.element_id = element_id
                     AND c.element_type_code = element_type_code
                     AND c.start_date = start_date
                     AND modify_tag = '1')