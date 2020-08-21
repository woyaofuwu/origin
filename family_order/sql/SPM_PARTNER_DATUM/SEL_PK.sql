--IS_CACHE=Y
SELECT corp_no,corp_name,prov_no,prov_name,open_bank,bank_account,payfee_mode,address,post,linkman,telphone,fax,to_char(atterm_time,'yyyy-mm-dd hh24:mi:ss') atterm_time,state,create_staff_id,to_char(create_time,'yyyy-mm-dd hh24:mi:ss') create_time 

  FROM spm_partner_datum

 WHERE PROV_NO = :CORP_NO