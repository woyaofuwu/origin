INSERT INTO tf_mh_stafftempdataright(staff_id,data_code,data_type,right_class,oper_special,accredit_time,accredit_staff_id,use_tag,times,used_times,start_date,end_date,remark,rsvalue1,rsvalue2,update_time,update_staff_id,update_depart_id)
 SELECT staff_id,data_code,data_type,right_class,oper_special,accredit_time,accredit_staff_id,use_tag,times,used_times,start_date,end_date,remark||'[时间失效搬入]',rsvalue1,:RSVALUE2,SYSDATE,:UPDATE_STAFF_ID,:UPDATE_DEPART_ID
FROM tf_m_stafftempdataright
WHERE staff_id=:STAFF_ID
   AND use_tag='1'
   AND end_date<SYSDATE