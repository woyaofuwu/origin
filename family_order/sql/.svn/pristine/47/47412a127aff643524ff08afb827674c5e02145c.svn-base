SELECT trade_type_code,
       brand_code,
       product_id,
       trade_attr,
       receipt_info1,
       receipt_info2,
       receipt_info3,
       receipt_info4,
       receipt_info5,
       TO_CHAR(start_date,'YYYY-MM-DD HH:MI:SS'),
       TO_CHAR(end_date,'YYYY-MM-DD HH:MI:SS'),
       eparchy_code,
       remark,
       verify_statue,
       verify_staff_id,
       verify_depart_id,
       verify_time,
       verify_suggestion,
       op_tag
  FROM TF_B_TRADE_RECEIPT
 WHERE (trade_type_code = :TRADE_TYPE_CODE)
   AND (brand_code = :BRAND_CODE)
   AND (product_id = :PRODUCT_ID)
   AND (trade_attr = :TRADE_ATTR)
   AND (eparchy_code = :EPARCHY_CODE)
   AND sysdate BETWEEN start_date AND end_date