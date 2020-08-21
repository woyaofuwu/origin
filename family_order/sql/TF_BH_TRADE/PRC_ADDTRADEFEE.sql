DECLARE
    iv_trade_id  NUMBER(16) := to_number(:TRADE_ID); 
    iv_adjust_fee NUMBER(11) := to_number(:ADJUST_FEE); 
    iv_eparchy_code CHAR(4) := :EPARCHY_CODE;
    iv_city_code CHAR(4) := :CITY_CODE;
    iv_depart_id CHAR(5) := :DEPART_ID;
    iv_staff_id CHAR(8) := :STAFF_ID;
    iv_trade_type_code NUMBER := :TRADE_TYPE_CODE;
    iv_trade_id_new NUMBER(16) := :TRADE_ID_NEW; 
    iv_remark VARCHAR2(100) := :REMARK;
BEGIN             
    BEGIN
    INSERT INTO tf_bh_trade
    SELECT iv_trade_id_new, iv_trade_id_new, NULL, iv_trade_type_code, '0', 100, subscribe_state, next_deal_tag, product_id, brand_code, user_id, cust_id, acct_id, serial_number, cust_name, SYSDATE, to_number(to_char(SYSDATE,'mm')), iv_staff_id, iv_depart_id, iv_city_code, iv_eparchy_code, NULL, iv_eparchy_code, iv_city_code, '0', SYSDATE, SYSDATE, iv_adjust_fee, 0, 0, NULL, '1', SYSDATE, iv_staff_id, '0', NULL, NULL, NULL, NULL, NULL, '00000000000000000000', trade_id, trade_type_code, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, iv_remark
    FROM tf_bh_trade WHERE trade_id=iv_trade_id; 
    INSERT INTO tf_bh_trade_detail
    SELECT iv_trade_id_new, to_number(to_char(SYSDATE,'mm')), open_date, develop_date, develop_staff_id, develop_depart_id, remove_reason_code, develop_no, user_type_code, user_passwd, assure_cust_id, assure_type_code, assure_date, assure_name, assure_pspt_type_code, assure_pspt_id, assure_contact, cust_passwd, open_limit, pspt_type_code, pspt_id, pspt_addr, pspt_end_date, sex, birthday, nationality_code, local_native_code, population, language_code, folk_code, phone, post_code, post_address, fax_nbr, email, contact, contact_phone, home_address, work_name, work_depart, job, job_type_code, educate_degree_code, religion_code, revenue_level_code, marriage, character_type_code, webuser_id, web_passwd, contact_type_code, community_id, pay_name, pay_mode_code, bank_acct_no, bank_code, contract_no
    FROM tf_bh_trade_detail WHERE trade_id=iv_trade_id;   
    INSERT INTO tf_b_tradefee_paymoney
    SELECT iv_trade_id_new, to_number(to_char(SYSDATE,'mm')), pay_money_code, iv_adjust_fee
    FROM tf_b_tradefee_paymoney WHERE trade_id=iv_trade_id;      
    EXCEPTION
    WHEN OTHERS THEN
    :RESULTINFO:='增加台帐出错：'||substr(sqlerrm,1,180);
    END;   
END;