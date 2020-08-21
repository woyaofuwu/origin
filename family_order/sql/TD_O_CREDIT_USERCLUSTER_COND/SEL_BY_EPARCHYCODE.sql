--IS_CACHE=Y
SELECT user_cluster_id,property_id,comp_method_id,comp_svalue1,comp_svalue2,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,eparchy_code 
  FROM td_o_credit_usercluster_cond
 WHERE eparchy_code=:EPARCHY_CODE