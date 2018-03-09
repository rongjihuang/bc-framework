package cn.bc.spider.http;

import org.apache.http.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.HttpContext;

import java.net.URI;
import java.util.List;

public class PostRedirectStrategy extends LaxRedirectStrategy {
	@Override
	public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
		final URI uri = getLocationURI(request, response, context);
		final String method = request.getRequestLine().getMethod();
		if (method.equalsIgnoreCase(HttpHead.METHOD_NAME)) {
			return new HttpHead(uri);
		} else if (method.equalsIgnoreCase(HttpGet.METHOD_NAME)) {
			return new HttpGet(uri);
		} else {
			final int status = response.getStatusLine().getStatusCode();
			if (status == HttpStatus.SC_TEMPORARY_REDIRECT) {
				System.out.println("-------url=" + uri);
				return copyRequest(request).setUri(uri).build();
			} else {
				return new HttpGet(uri);
			}
		}
	}

	private RequestBuilder copyRequest(HttpRequest request) {
		// 这个方法默认会将原始的 FormParam 的 key 和 value 解码，从而当有已 encode 的中文时就悲哀了
		RequestBuilder copied = RequestBuilder.copy(request);
		// 重新 encode FormParam
		List<NameValuePair> parameters = copied.getParameters();
		parameters.forEach(nameValuePair -> {
			if("hphm".equals(nameValuePair.getName())){
				System.out.println("-------origin: hphm=" + nameValuePair.getValue() + ", class=" + nameValuePair.getClass());
			}
		});

		return copied;
	}
}