package com.example.hci_vista_low;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ItemList {
	private List<Item> items;

	public ItemList() {
		items = new ArrayList<Item>();
	}
	public boolean parser(String json_result) {
		try {
			JSONArray multiResults = new JSONArray(json_result);
			System.out.println(multiResults.length() + "\n");
			for (int j = 0; j < multiResults.length(); j++) {
				JSONObject jsonObject = multiResults.getJSONObject(j);
				String title = jsonObject.getString("title");
				String desc = jsonObject.getString("details");
				float price = (float)jsonObject.getDouble("price");
				String path = jsonObject.getString("url");
				System.out.println("title:" + title);
				System.out.println("desc: " + desc);
				System.out.println("price: " + price);
				System.out.println("path: " + path);
				items.add(new Item(title, price, desc, path));
				/*
				 * formated_results[i] += desc + '\n'; formated_results[i] +=
				 * price + '\n'; formated_results[i] += path + '\n';
				 */
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public void displayList(){
		
	}

}
