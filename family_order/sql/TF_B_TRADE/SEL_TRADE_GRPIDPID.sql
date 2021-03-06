select distinct p.user_id_a,tp.trade_id,tp.product_offer_id,tp.merch_spec_code,t.order_id,                    
 tp.product_spec_code, p.product_id,t.product_id mproduct_id, tp.user_id, tp.group_id,tp.PRODUCT_ORDER_ID		        
 from tf_b_trade_product p, tf_b_trade t,tf_b_trade_grp_merchp tp                       		
 where p.trade_id = t.trade_id                                                          		
   and tp.trade_id=t.trade_id                                                           		
   and t.RSRV_STR10 = '1'                                                               		
   and p.user_id_a != '-1'                                                              		
   and t.brand_code = 'BOSG'                                                            		
   and t.product_id= :PRODUCT_ID                                                        		
   and tp.group_id = :GROUP_ID                                                        		