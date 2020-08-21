--IS_CACHE=Y
SELECT  eparchy_code,para_attr,para_code1,para_code2,para_name,para_value1,para_value2,para_value3,para_value4,para_value5,para_value6,para_value7,para_value8,to_char(para_value9) para_value9,to_char(para_value10) para_value10,to_char(para_value11) para_value11,to_char(para_value12) para_value12,to_char(rdvalue1,'yyyy-mm-dd hh24:mi:ss') rdvalue1,to_char(rdvalue2,'yyyy-mm-dd hh24:mi:ss') rdvalue2,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
from
(
SELECT    /*+INDEX(a PK_TD_M_RES_COMMPARA)*/ a.* from
td_m_res_commpara a
 WHERE eparchy_code=:EPARCHY_CODE
   AND para_attr=:PARA_ATTR
   AND para_code1=:PARA_CODE1
   AND  para_value2=:CODE_STATE_CODE
  AND  (para_value7 is null  OR (  para_value7 is not null   and (para_value5 is not null and  para_value5<=to_char(sysdate,'YYYY-MM-DD HH24:MI:SS'))))
   AND ROWNUM < 50
 order by substr(to_char(sysdate,'yyyymmddhh24:mi:ss'),16,1) 
         + sys.dbms_random.value
)
where ROWNUM <= :ROWNUM
order by para_value9