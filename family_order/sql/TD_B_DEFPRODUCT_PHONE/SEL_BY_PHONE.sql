--IS_CACHE=Y
SELECT product_id,force_tag,gift_discnt_code
  FROM td_b_defproduct_phone
 WHERE SUBSTRB(:PHONECODE_S,1,LENGTHB(phonecode_s)) >= phonecode_s
   AND SUBSTRB(:PHONECODE_S,1,LENGTHB(phonecode_e)) <= phonecode_e
   AND sysdate between start_date and end_date