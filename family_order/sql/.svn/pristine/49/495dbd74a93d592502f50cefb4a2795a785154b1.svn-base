SELECT DECODE(res_no,'2','智能网帐户','BOSS帐户') res_no,eparchy_code,city_code,
to_char(oper_date,'yyyy-mm-dd hh24:mi:ss') oper_date,nvl(sum(rsrv_num1),0) rsrv_num1,nvl(sum(rsrv_num2),0) rsrv_num2,nvl(sum(rsrv_num3),0) 

rsrv_num3,nvl(sum(to_number(rsrv_str1)),0) rsrv_str1,nvl(sum(to_number(rsrv_str2)),0) rsrv_str2,nvl(sum(to_number

(rsrv_str3)),0) rsrv_str3,nvl(sum(to_number(rsrv_str4)),0) rsrv_str4,nvl(sum(to_number(rsrv_str5)),0) rsrv_str5,rsrv_str6 
  FROM tf_b_res_middle
 WHERE res_type_code='0'
   AND (:RES_NO is null or res_no = :RES_NO)
   AND (:EPARCHY_CODE is null or eparchy_code=:EPARCHY_CODE)
   AND (:CITY_CODE is null or city_code=:CITY_CODE)
   AND oper_date>=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')
   AND oper_date<=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')
   AND (:RES_NO_S is null or rsrv_str6=:RES_NO_S)
   AND (:RSRV_STR7 is null or rsrv_str7=:RSRV_STR7)
  group by res_no,eparchy_code,oper_date,city_code,rsrv_str6