DELETE FROM ts_a_valuecard_staff
 WHERE staff_id=:STAFF_ID
   AND value_card_type_code=:VALUE_CARD_TYPE_CODE
   AND (:VALUE_CODE is null or value_code=:VALUE_CODE)
   AND in_date=to_char(sysdate,'yyyymmdd')