package com.zest.elastic;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;

public class BulkProcessorTest {

	public void process(Client client){
		BulkProcessor bulkProcessor = BulkProcessor.builder(
		        client,  
		        new BulkProcessor.Listener() {

					@Override
					public void afterBulk(long arg0, BulkRequest arg1,
							BulkResponse arg2) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void afterBulk(long arg0, BulkRequest arg1,
							Throwable arg2) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void beforeBulk(long arg0, BulkRequest arg1) {
						// TODO Auto-generated method stub
						
					}
		           
		        })
		        .setBulkActions(10000) 
		        .setBulkSize(new ByteSizeValue(1, ByteSizeUnit.GB)) 
		        .setFlushInterval(TimeValue.timeValueSeconds(5)) 
		        .setConcurrentRequests(1) 
		        .build();
	}
}
