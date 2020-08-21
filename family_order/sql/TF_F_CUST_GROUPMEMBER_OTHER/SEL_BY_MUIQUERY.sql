SELECT to_char(a.cust_id) cust_id,a.group_id,a.vpmn_id,to_char(a.member_cust_id) member_cust_id,a.cust_name,a.pspt_id,a.pspt_addr,a.age,a.pspt_post_code,a.vip_type_code,a.class_id,a.short_number,a.member_kind,a.pay_for_way,a.pay_limit,a.write_off_flag,a.write_off_limit,a.cust_manager_id,a.chkoff_id,a.important_flag,a.member_belong,a.member_status,a.sex,to_char(a.birthday,'yyyy-mm-dd') birthday,a.work_name,a.work_depart,a.job,a.marrige,
a.educate_degree_code,a.revenue_level_code,a.contact_phone,a.fax_nbr,a.email,a.company_address,a.bill_address,a.post_code,a.vip_no,a.owe_level,a.alarm_status,a.in_mode_code,a.pay_mode_code,a.bank_account_no,to_char(a.in_date,'yyyy-mm-dd') in_date,
a.in_type,a.consume,to_char(a.create_date,'yyyy-mm-dd') create_date,to_char(a.destroy_date,'yyyy-mm-dd') destroy_date,a.remove_tag,a.consume_habit,to_char(a.score_value) score_value,to_char(a.credit_value) credit_value,a.cust_manager_appr,a.brand_code,a.register_name,a.register_pspt_id,f_sys_getcodename('class_id',class_id,'9',null) vip_tag,a.duty,a.technical_post,a.folk_code,
a.political,a.religion_code,a.home_phone,a.other_contact_mode,to_char(a.become_vip_date,'yyyy-mm-dd') become_vip_date,a.family_card_flag,a.specical_demand,a.value_book_number,a.communion_habit,a.remark,
to_char(a.open_date,'yyyy-mm-dd') open_date,a.is_high_value,a.score_level,a.vipcard_post_way,to_char(a.update_time,'yyyy-mm-dd') update_time,a.update_staff_id,a.update_depart_id,f_sys_getcodename('staff_id',cust_manager_id,null,null) prevaluec2,
a.prevaluec1,a.prevaluec3,a.prevaluec4,a.rsrv_1,a.rsrv_2,to_char(a.prevalued1,'yyyy-mm-dd') prevalued1,
to_char(a.prevalued2,'yyyy-mm-dd') prevalued2,to_char(a.prevaluen1) prevaluen1,to_char(a.prevaluen2) prevaluen2,to_char(a.prevaluen3) prevaluen3,a.open_limit,a.develop_depart_id,a.develop_staff_id,a.pspt_type_code,to_char(a.pspt_end_date,'yyyy-mm-dd') pspt_end_date,to_char(a.user_id) user_id,a.nationality_code,a.local_native_code,a.population,a.language_code,
a.post_address,a.contact,a.home_address,a.character_type_code,a.webuser_id,a.web_passwd,a.contact_type_code,a.community_id,a.serial_number
 FROM (  SELECT a.*,rownum tmpnum FROM tf_f_cust_groupmember_other a  where a.remove_tag = '0'
 AND (a.cust_manager_id=:CUST_MANAGER_ID OR :CUST_MANAGER_ID IS NULL)
 AND (a.vip_tag=:VIP_TAG OR :VIP_TAG IS NULL)
 AND (a.serial_number=:SERIAL_NUMBER OR :SERIAL_NUMBER IS NULL)
 AND (a.cust_name like'%'||:CUST_NAME||'%' OR :CUST_NAME IS NULL)
 AND (a.group_id like '%'||:GROUP_ID||'%' OR :GROUP_ID IS NULL)
     and (a.member_kind = :MEMBER_KIND or :MEMBER_KIND is null)
 AND (a.vpmn_id=:VPMN_GROUP_ID OR :VPMN_GROUP_ID IS NULL)
     AND (decode(a.vpmn_id,null,'0','','0','1') =:PREVALUE1 OR :PREVALUE1 IS NULL)
 AND (a.score_value>=:SCORE_VALUE or :SCORE_VALUE is null )
 AND (a.score_level=:RSRV_2 OR :RSRV_2 IS NULL)
 AND (a.rsrv_2=:PREVALUE2 OR :PREVALUE2 IS NULL)
 AND (a.prevaluec1=:CONTRACT_NO OR :CONTRACT_NO IS NULL)
 AND (a.credit_value>=:BASIC_CREDIT_VALUE OR :BASIC_CREDIT_VALUE IS NULL)
 AND (a.class_id=:CLASS_ID OR :CLASS_ID IS NULL)
 AND (a.prevaluen3>=:PRODUCT_NUM or :PRODUCT_NUM is null)
 AND (a.open_date>=TO_DATE(:BEGIN_DATE , 'YYYY-MM-DD HH24:MI:SS') OR :BEGIN_DATE is null)
 AND (a.open_date<=TO_DATE(:END_DATE , 'YYYY-MM-DD HH24:MI:SS') or :END_DATE is null)
 AND exists ( select 1  from td_m_staff b,td_m_depart c  WHERE a.cust_manager_id=b.staff_id
  AND b.depart_id=c.depart_id  AND c.depart_frame LIKE
	(select t.depart_frame from td_m_depart t where t.depart_id=:DEPART_ID)||'%'
         )
 AND ( exists ( SELECT 1 FROM tf_f_groupmember_product p
     WHERE p.serial_number=a.serial_number AND p.product_id=:PRODUCT_NAME
      AND p.serial_number LIKE '%'||:SERIAL_NUMBER||'%'
     )  OR :PRODUCT_NAME IS NULL
 ) ) a WHERE tmpnum >= :X_REMARK1 AND (:X_REMARK2 is null OR tmpnum<=:X_REMARK2)