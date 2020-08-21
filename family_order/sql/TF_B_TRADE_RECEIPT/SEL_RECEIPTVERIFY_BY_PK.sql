SELECT trade_type_code,
       brand_code,
       product_id,
       trade_attr,
       receipt_info1,
       receipt_info2,
       receipt_info3,
       receipt_info4,
       receipt_info5,
       TO_CHAR(start_date, 'YYYY-MM-DD HH24:MI:SS') start_date,
       TO_CHAR(end_date, 'YYYY-MM-DD HH24:MI:SS') end_date,
       eparchy_code,
       remark,
       update_staff_id,
       update_depart_id,
       update_time,
       verify_statue,
       op_tag,
       verify_suggestion
  FROM TF_B_TRADE_RECEIPT
 WHERE 1=1 
   AND (verify_statue = :ERIFY_STATUE or :ERIFY_STATUE is null)
   AND trade_attr = :TRADE_ATTR 
   AND trade_attr IN ('1','2','Y')
   AND sysdate BETWEEN start_date AND end_date