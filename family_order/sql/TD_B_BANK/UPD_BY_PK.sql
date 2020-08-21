UPDATE td_b_bank
   SET bank=:BANK,city_code=:CITY_CODE,super_bank_code=:SUPER_BANK_CODE,bank_inner_code=:BANK_INNER_CODE,contact=:CONTACT,contact_phone=:CONTACT_PHONE,remark=:REMARK,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,update_time=SYSDATE 
 WHERE bank_code=:BANK_CODE AND eparchy_code=:EPARCHY_CODE