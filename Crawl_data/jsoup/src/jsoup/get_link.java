package jsoup;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class get_link {
	
	@SuppressWarnings("deprecation")
	public static String getHtmlByUrl(String url){  
        String html = null;  
        HttpClient httpClient = new DefaultHttpClient();//创建httpClient对象  
        HttpGet httpget = new HttpGet(url);//以get方式请求该URL  
        try {  
            HttpResponse responce = httpClient.execute(httpget);//得到responce对象  
            int resStatu = responce.getStatusLine().getStatusCode();//返回码  
            if (resStatu==HttpStatus.SC_OK) {//200正常  其他就不对  
                //获得相应实体  
                HttpEntity entity = responce.getEntity();  
                if (entity!=null) {  
                    html = EntityUtils.toString(entity);//获得html源代码  
                }  
            }  
        } catch (Exception e) {  
            System.out.println("访问【"+url+"】出现异常!");  
            e.printStackTrace();  
        } finally {  
            httpClient.getConnectionManager().shutdown();  
        }  
        return html;  
    }  		
	
	 public static void main(String[] args){
		 	String html = getHtmlByUrl("http://urochester.bncollege.com/webapp/wcs/stores/servlet/CategoryDisplay?categoryId=40000&catalogId=10001&langId=-1&storeId=27055&top=Y/");
		 	if(html!=null&&!"".equals(html)){
		 		Document doc = Jsoup.parse(html);
		 		Elements links_info = doc.select("div.popularPicksContainer>ul.popularPicksListItem>li.quickViewButtonHolder>a>img");
		 		for (Element link : links_info){
		 			String src = link.attr("src");
		 			String title = link.attr("title");
		 			System.out.println(src+","+title);  
		 		}
		 	
		 	}
	        

	    }

}
