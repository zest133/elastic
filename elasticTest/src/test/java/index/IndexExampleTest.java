package index;

import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Test;

import admin.ClientExample;
import admin.NodesInfoExample;


import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.index.query.FilterBuilders.*;

public class IndexExampleTest {

	@Test
	public void indexDocTest(){
		Client client = ClientExample.newTransportClient();
		IndexExample.indexDoc(client);
		
		
		
//		QueryBuilder qb = boolQuery()
//			    .must(termQuery("content", "test1"))    
//			    .must(termQuery("content", "test4"))    
//			    .mustNot(termQuery("content", "test2")) 
//			    .should(termQuery("content", "test3")); 
	}
}
