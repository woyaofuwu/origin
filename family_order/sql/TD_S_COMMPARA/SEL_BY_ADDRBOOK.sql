SELECT a.addrbook_id para_code1,a.month para_code2,
       decode(a.user_type,'1','集团管理员','2','普通用户','3','自由联系人','其他类型') para_code3,
       a.CUSER_NAME para_code4,a.EUSER_NAME para_code5,a.USER_ALIAS para_code6,
       a.LOGIN_NO para_code7,a.PHONE_NO para_code8,a.SHORT_NO para_code9,
       a.OFFICIAL_NO para_code10,a.HOME_NO para_code11,a.MOBILE_NO para_code12,
       a.PHS_NO para_code13,a.BP_NO para_code14,a.FAX_NO para_code15,
       a.SMS_SEND_NO para_code16,a.PHONE_SEND_NO para_code17,a.EMAIL_NO para_code18,
       a.C_ADDRESS para_code19,a .POST_CODE para_code20,a.QQ_NO para_code21,
       a.MSN_NO para_code22,a.HONORIFIC para_code23,a.FLAG_SEX para_code24,
       a.HEAD_SHIP para_code25,a.FLAG_LEVEL para_code26,a.BIRTHDAY para_code27,
       a.UPDATE_STAFF_ID para_code28,a.UPDATE_DEPART_ID para_code29,
       to_char(a.update_time,'yyyy-mm-dd hh24:mi:ss') para_code30,
       '' update_time,'' start_date,'' end_date,'' eparchy_code,
       '' remark,'' update_staff_id,'' update_depart_id,'' subsys_code,
       0 param_attr,'' param_code,'' param_name
  FROM TF_F_CUST_ADDRBOOK a
 WHERE (a.addrbook_id = :PARA_CODE1 or :PARA_CODE1 IS NULL)
   AND (a.phone_no = :PARA_CODE2 or :PARA_CODE2 IS NULL)
   AND (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL)
   AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
   AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
   AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
   AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
   AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
   AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
   AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)