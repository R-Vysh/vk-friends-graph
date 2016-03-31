package kyiv.rvysh.vkfriends.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kyiv.rvysh.vkfriends.App;

// TODO Replace with Jersey in VkService
public class HttpRequestUtils {

	private static Logger LOGGER = LoggerFactory.getLogger(App.class);

	private HttpRequestUtils() {
	}

	public static String sendGet(String host, String path)
			throws URISyntaxException, ClientProtocolException, IOException {
		return sendGet(host, path, null, null);
	}

	public static String sendGet(String host, String path, Map<String, Object> parameters)
			throws URISyntaxException, ClientProtocolException, IOException {
		return sendGet(host, path, parameters, null);
	}

	public static String sendGet(String host, String path, Map<String, Object> parameters, Map<String, String> headers)
			throws URISyntaxException, ClientProtocolException, IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		try {
			URI uri = createUri(host, path, parameters);
			HttpGet request = new HttpGet(uri);
			addHeaders(request, headers);
			HttpResponse response = client.execute(request);
			LOGGER.debug("Response Code : " + response.getStatusLine().getStatusCode());
			return responseToString(response);
		} finally {
			client.close();
		}
	}

	public static String sendPost(String host, String path)
			throws URISyntaxException, ClientProtocolException, IOException {
		return sendPost(host, path, null, null);
	}

	public static String sendPost(String host, String path, Map<String, Object> parameters)
			throws URISyntaxException, ClientProtocolException, IOException {
		return sendGet(host, path, parameters, null);
	}

	public static String sendPost(String host, String path, Map<String, Object> parameters, Map<String, String> headers)
			throws URISyntaxException, ClientProtocolException, IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		try {
			URI uri = createUri(host, path, parameters);
			HttpPost request = new HttpPost(uri);
			addHeaders(request, headers);
			HttpResponse response = client.execute(request);
			LOGGER.debug("Response Code : " + response.getStatusLine().getStatusCode());
			return responseToString(response);
		} finally {
			client.close();
		}
	}

	private static URI createUri(String host, String path, Map<String, Object> parameters) throws URISyntaxException {
		URIBuilder uriBuilder = new URIBuilder().setScheme("http").setHost(host).setPath(path);
		if (parameters != null) {
			for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
				uriBuilder.addParameter(parameter.getKey(), parameter.getValue().toString());
			}
		}
		return uriBuilder.build();
	}

	private static void addHeaders(HttpRequestBase request, Map<String, String> headers) {
		if (headers != null) {
			for (Map.Entry<String, String> header : headers.entrySet()) {
				request.addHeader(header.getKey(), header.getValue());
			}
		}
	}
	
	private static String responseToString(HttpResponse response) throws UnsupportedOperationException, IOException {
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
	}
}
