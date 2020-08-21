SELECT vip_manager_id,vip_manager_name,
       f_sys_getcodename('depart_id',parent_depart_id,'','') city_code,
       duty,f_sys_getcodename('local_native',local_native,'','') local_native,
       decode(a.sex,'M','男','F','女') sex,
       to_char(a.birthday,'yyyy-mm-dd') birthday,a.user_pid,school,
       f_sys_getcodename('educate_degree',educate_grade,'','') educate_grade,
       speciality,home_address,to_char(work_date,'yyyy-mm-dd') work_date,
       polity,c.depart_name depart,work_bound,link_phone,a.serial_number,
       fax,a.email,home_phone,
       trait,hobby,train_record,work_story,appraise_tag,a.remark,
       a.update_staff_id,a.update_depart_id,
       to_char(a.update_time,'yyyy-mm-dd') update_time,
       to_char(prevaluen1) prevaluen1,to_char(prevaluen2) prevaluen2,
       to_char(prevaluen3) prevaluen3,
       decode(d.right_class,'1','客户经理','2','行业经理','3','部门领导','客户经理') prevaluec1,
       f_sys_getcodename('depart_id',c.depart_id,'','') prevaluec2,
       prevaluec3,prevaluec4,to_char(prevalued1,'yyyy-mm-dd') prevalued1,
       to_char(prevalued2,'yyyy-mm-dd') prevalued2,
       to_char(prevalued3,'yyyy-mm-dd') prevalued3,1 x_tag
 FROM tf_f_managerstaff a,td_m_staff b,td_m_depart c,tf_m_staffdataright d
 WHERE a.vip_manager_id=b.staff_id 
 and b.depart_id = c.depart_id and b.staff_id = d.staff_id
 and d.data_code = 'SYS_CMS_CUSTINFOQUERY' 
 ORDER BY c.depart_id,d.right_class DESC