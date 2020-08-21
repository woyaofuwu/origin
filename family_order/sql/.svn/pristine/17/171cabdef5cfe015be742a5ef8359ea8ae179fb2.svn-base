--IS_CACHE=Y
SELECT RELATION_TYPE_CODE || '0' || ROLE_CODE_B  paracode
,ROLE_A ||' ' ||ROLE_B paraname
 FROM TD_S_RELATION_ROLE  WHERE RELATION_TYPE_CODE in
       (select para1
          from td_o_credit_commpara
         where paracode like 'REFRESH_RELATIONB_%')