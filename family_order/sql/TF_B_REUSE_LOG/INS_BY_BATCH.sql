INSERT INTO tf_b_reuse_log
(
log_id,         serial_number, back_type_code,
eparchy_code,   sim_card_no,   moffice_id,
code_type_code, destory_time,  remove_tag,
city_code_o,    stock_id_o,    user_id,
back_time,      back_staff_id, back_depart_id,
remark,         rsrv_tag1,     rsrv_tag2,
rsrv_tag3,      rsrv_date1,    rsrv_date2,
rsrv_date3,     rsrv_str1,     rsrv_str2,
rsrv_str3
)
SELECT
:LOG_ID, SUBSTR(a.imsi,1,15),'1',
:EPARCHY_CODE,NULL,NULL,
NULL,NULL,NULL,
NULL,NULL,NULL,
sysdate,:BACK_STAFF_ID,:BACK_DEPART_ID,
'[IMSI批量回收]',NULL,NULL,
NULL,NULL,NULL,
NULL,NULL,NULL,
a.imsi 
FROM tf_r_simcard_idle a,tf_r_simcardorder b 
WHERE
a.sim_card_no=b.res_no AND b.back_tag='0' AND b.rsrv_date1<sysdate AND b.sim_state_code='1' AND b.eparchy_code=:EPARCHY_CODE AND (:CITY_CODE is null OR b.city_code=:CITY_CODE) AND (:STOCK_ID is null OR b.stock_id=:STOCK_ID)