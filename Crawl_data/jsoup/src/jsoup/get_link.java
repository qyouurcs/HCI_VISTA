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
        HttpClient httpClient = new DefaultHttpClient();//����httpClient����  
        HttpGet httpget = new HttpGet(url);//��get��ʽ�����URL  
        try {  
            HttpResponse responce = httpClient.execute(httpget);//�õ�responce����  
            int resStatu = responce.getStatusLine().getStatusCode();//������  
            if (resStatu==HttpStatus.SC_OK) {//200����  �����Ͳ���  
                //�����Ӧʵ��  
                HttpEntity entity = responce.getEntity();  
                if (entity!=null) {  
                    html = EntityUtils.toString(entity);//���htmlԴ����  
                }  
            }  
        } catch (Exception e) {  
            System.out.println("���ʡ�"+url+"�������쳣!");  
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
