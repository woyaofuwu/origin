insert into tf_f_acct_discnt
select :ACCT_ID,para_code1,sysdate,TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') 
from td_s_commpara where param_attr='1252' and subsys_code='CSM'