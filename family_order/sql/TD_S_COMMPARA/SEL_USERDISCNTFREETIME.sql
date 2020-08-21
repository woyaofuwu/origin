Select b.* From tf_f_user_discnt a, td_s_commpara b
Where a.user_id=:USER_ID
And b.subsys_code='CSM'
And b.param_attr=3206
And a.discnt_code=b.param_code
And a.end_date>:START_DATE
And a.start_date<=:END_DATE
And b.end_date>Sysdate
And (b.eparchy_code=:EPARCHY_CODE Or b.eparchy_code='ZZZZ')