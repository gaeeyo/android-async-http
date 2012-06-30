
package com.loopj.android.http;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
 


public class StreamHttpResponseHandler extends AsyncHttpResponseHandler {

    void sendResponseMessage(HttpResponse response) {
        StatusLine status = response.getStatusLine();
        String responseBody = null;
        try {
            HttpEntity entity = response.getEntity();
            if(entity != null) {
            	InputStream is = entity.getContent();
            	try {
            		boolean handled = onReceive(status.getStatusCode(), is);
					if (!handled) {
						ByteArrayOutputStream os = new ByteArrayOutputStream();
						BufferedInputStream in = new BufferedInputStream(is);
						int data;
						while ((data = in.read()) != -1) {
							os.write(data);
						}
					    responseBody = os.toString("UTF-8");
					}
            	} finally {
            		is.close();
            	}
            }
            if(status.getStatusCode() >= 300) {
            	
                sendFailureMessage(new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()), responseBody);
            } else {
                sendSuccessMessage(responseBody);
            }
        } catch(IOException e) {
            sendFailureMessage(e, (String) null);
        } catch(OutOfMemoryError e) {
        	sendFailureMessage(e, (String) null);
        } catch(Exception e) {
        	sendFailureMessage(e, (String) null);;
        }
    }
    
    /**
     * Response の IntputStream へのアクセスが可能になった時点で呼ばれる
     * @param statusCode
     * @param is
     * @return InputStreamを利用した場合はtrue(内容は破棄される)
     *         false を返した場合は AsyncHttpResponseHandler 同様 String に変換して onSuccess や onFailure が呼ばれる
     * @throws Exception
     */
    protected boolean onReceive(int statusCode, InputStream is) throws Exception {
    	return false;
    }
}