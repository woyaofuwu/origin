--IS_CACHE=Y
SELECT PRODUCT_ID
  FROM td_b_defproduct_phone
 WHERE product_id = :PRODUCT_ID
   AND SUBSTRB(:PHONECODE_S,1,LENGTHB(phonecode_s)) >= phonecode_s
   AND SUBSTRB(:PHONECODE_S,1,LENGTHB(phonecode_e)) <= phonecode_e
   AND sysdate between start_date and end_date