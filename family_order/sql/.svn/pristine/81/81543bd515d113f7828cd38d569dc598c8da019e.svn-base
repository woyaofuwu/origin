UPDATE tf_f_customer
   SET  cust_name=:CUST_NAME,pspt_type_code=:PSPT_TYPE_CODE,pspt_id=:PSPT_ID,open_limit=:OPEN_LIMIT,
        eparchy_code=:EPARCHY_CODE,city_code=:CITY_CODE,cust_passwd=:CUST_PASSWD,
        simple_spell=:SIMPLE_SPELL,is_real_name=:IS_REAL_NAME,city_code_a=:CITY_CODE_A,
        update_time=sysdate,update_staff_id=:TRADE_STAFF_ID,update_depart_id=:TRADE_DEPART_ID,
        develop_depart_id=:DEVELOP_DEPART_ID,develop_staff_id=:DEVELOP_STAFF_ID,remark=:REMARK  
 WHERE cust_id = TO_NUMBER(:CUST_ID)
   AND partition_id = MOD(TO_NUMBER(:CUST_ID),10000)