SELECT trade_type_code,
       brand_code,
       product_id,
       trade_attr,
       receipt_info1,
       receipt_info2,
       receipt_info3,
       receipt_info4,
       receipt_info5,
       to_char(start_date, 'yyyy-MM-dd HH24:mi:ss') start_date,
       to_char(end_date, 'yyyy-MM-dd HH24:mi:ss') end_date,
       eparchy_code,
       remark,
       verify_statue,
       verify_staff_id,
       verify_depart_id,
       verify_time,
       verify_suggestion,
       op_tag
  FROM TF_B_TRADE_RECEIPT
 WHERE (trade_type_code = :TRADE_TYPE_CODE or :TRADE_TYPE_CODE is null)
   AND (brand_code = :BRAND_CODE or :BRAND_CODE is null)
   AND (product_id = :PRODUCT_ID or :PRODUCT_ID is null)
   AND (trade_attr = :TRADE_ATTR or trade_attr IN ('1','Y'))
   AND (eparchy_code = :EPARCHY_CODE or :EPARCHY_CODE is null)
   AND sysdate BETWEEN start_date AND end_date