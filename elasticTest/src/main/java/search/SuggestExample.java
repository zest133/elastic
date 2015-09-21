package search;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class SuggestExample {
    public static void contextSuggest(Client client) {
        String index = "test";

        //delete index if exists
        if (client.admin().indices().prepareExists(index).execute().actionGet().isExists())
            client.admin().indices().prepareDelete(index).execute().actionGet();

        
        /**
         * PUT services/_mapping/service
{
    "service": {
        "properties": {
            "name": {
                "type" : "string"
            },
            "tag": {
                "type" : "string"
            },
            "suggest_field": {
                "type": "completion",
                "context": {
                    "color": { 
                        "type": "category",
                        "path": "color_field",
                        "default": ["red", "green", "blue"]
                    },
                    "location": { 
                        "type": "geo",
                        "precision": "5m",
                        "neighbors": true,
                        "default": "u33"
                    }
                }
            }
        }
    }
}  아래 것들은 대략 머 이런식이겠지?

         */
        try {
            client.admin().indices().prepareCreate(index).addMapping("doc",
                jsonBuilder().startObject()
                    .startObject("doc")
                        .startObject("properties")
                            .startObject("suggestions")
                                .field("type", "completion")//prefix query 대신에 이걸 사용해라. 존나 빠르다. 
                                .startObject("context")
                                    .startObject("filter_category")
                                        .field("type", "category")
                                    .endObject()
                                .endObject()
                            .endObject()
                        .endObject()
                    .endObject()
                .endObject())
                .execute()
                .actionGet();

            client.admin().cluster().prepareHealth(index).setWaitForYellowStatus().execute().actionGet();

            client.prepareIndex(index, "doc")
                .setSource(
                    jsonBuilder().startObject()
                        .startObject("suggestions")
                            .array("input", "apple the fruit")
                            .startObject("context")
                                .array("filter_category", "fruit")
                            .endObject()
                        .endObject()
                    .endObject()
                )
                .setRefresh(true)
                .execute()
                .actionGet();

            client.prepareIndex(index, "doc")
                .setSource(
                    jsonBuilder().startObject()
                        .startObject("suggestions")
                            .array("input", "apple the company")
                            .startObject("context")
                                .field("filter_category", "company")
                            .endObject()
                        .endObject()
                    .endObject()
                )
                .setRefresh(true)
                .execute()
                .actionGet();

            //filter by fruit
            CompletionSuggestionBuilder suggestionBuilder = new CompletionSuggestionBuilder("1")
                .field("suggestions")
                .text("a")
                .addContextField("filter_category", "fruit");

            SuggestResponse response = client.prepareSuggest(index).addSuggestion(suggestionBuilder).get();
            for (Suggest.Suggestion.Entry entry: response.getSuggest().getSuggestion("1").getEntries()) {
                System.out.println("Fruit suggestions for: " + entry.getText());
                for (CompletionSuggestion.Entry.Option option: ((CompletionSuggestion.Entry)entry).getOptions()) {
                    System.out.println(option.getText());
                }
            }

            //filter by company
            suggestionBuilder = new CompletionSuggestionBuilder("1")
                .field("suggestions")
                .text("a")
                .addContextField("filter_category", "company");

            response = client.prepareSuggest(index).addSuggestion(suggestionBuilder).get();
            for (Suggest.Suggestion.Entry entry: response.getSuggest().getSuggestion("1").getEntries()) {
                System.out.println("Company suggestions for: " + entry.getText());
                for (CompletionSuggestion.Entry.Option option: ((CompletionSuggestion.Entry)entry).getOptions()) {
                    System.out.println(option.getText());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
