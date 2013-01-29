package libs;

import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wujinliang
 * @since 2012-02-04
 */
public class WS {
    private static Logger logger = LoggerFactory.getLogger(WS.class);

    public static void end(HttpClient httpClient) {
        try {
            if (httpClient != null) httpClient.getConnectionManager().shutdown();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    public static void end(HttpClient httpClient, HttpRequestBase httpRequestBase) {
        try {
            if (httpRequestBase != null) httpRequestBase.abort();
            end(httpClient);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static WSRequest url(String url) {
        return new WSRequest(new DefaultHttpClient(), url);
    }

    public static class WSRequest {
        public String url;
        private int soTimeout;// ms
        private int conTimeout;// ms
        private int retry;// 重试次数
        private int maxRetry = 5;// 最大重试次数
        public Map<String, String> headers = new HashMap<String, String>();
        public Map<String, Object> parameters = new HashMap<String, Object>();
        private HttpClient httpClient;

        private WSRequest(HttpClient httpClient, String url) {
            this.httpClient = httpClient == null ? new DefaultHttpClient() : httpClient;
            this.url = url;
        }

        public WSRequest soTimeout(int to) {
            this.soTimeout = to;
            return this;
        }

        public WSRequest conTimeout(int to) {
            this.conTimeout = to;
            return this;
        }

        public WSRequest maxRetry(int maxRetry) {
            this.maxRetry = maxRetry;
            return this;
        }

        public WSRequest setHeader(String name, String value) {
            this.headers.put(name, value);
            return this;
        }

        public WSRequest setParameter(String name, String value) {
            this.parameters.put(name, value);
            return this;
        }

        public WSResponse get() {
            return get(true);
        }

        public WSResponse get(boolean useProxy) {
            return this.executeRequest(new HttpGet(this.url), useProxy);
        }

        private WSResponse executeRequest(HttpRequestBase httpMethod) {
            return executeRequest(httpMethod, true);
        }

        private WSResponse executeRequest(HttpRequestBase httpMethod, boolean useProxy) {
            if (retry > maxRetry) {
                end(httpClient, httpMethod);
                throw new RuntimeException("重试次数超过了"+maxRetry+"次");
            }
            HttpParams httpParameters = httpClient.getParams();
            if (conTimeout > 0) HttpConnectionParams.setConnectionTimeout(httpParameters, conTimeout);
            if (soTimeout > 0) HttpConnectionParams.setSoTimeout(httpParameters, soTimeout);
            InputStream is = null;
            Proxy.ProxyEntry proxyEntry = null;//Proxy.getRandomProxy();
            try {
                if (this.headers != null) {
                    for (String key : headers.keySet()) {
                        httpMethod.addHeader(key, headers.get(key) + "");
                    }
                }
                if (this.parameters != null) {
                    HttpParams params = new BasicHttpParams();
                    httpMethod.setParams(params);
                    for (String key : parameters.keySet()) {
                        params.setParameter(key, parameters.get(key));
                    }
                }
                if (proxyEntry != null && useProxy) {
                    logger.info("using proxy:{} to get url: {}", proxyEntry.host + ":" + proxyEntry.port, url);
                    if (proxyEntry.host != null && !"localhost".equals(proxyEntry.host)) {// 若代理是本机ip, 则不使用代理访问
                        HttpHost proxy = new HttpHost(proxyEntry.host, proxyEntry.port);
                        httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
                    }
                }

                HttpResponse response = httpClient.execute(httpMethod);
                HttpEntity entity = response.getEntity();
                WSResponse wsResponse = new WSResponse();
                if (entity != null) {
                    is = entity.getContent();
                    int statusCode = response.getStatusLine().getStatusCode();
                    wsResponse.status = statusCode;
                    wsResponse.headers = response.getAllHeaders();
                    logger.info("get url:{} response status code: {}", url, statusCode);
                    if (statusCode == HttpStatus.SC_OK) {
                        Charset charset = getContentCharSet(entity);
                        byte[] bytes = IOUtils.toByteArray(is);

                        // 猜测字符集
                        if (charset == null) {
                            CharsetDetector detector = new CharsetDetector();
                            detector.setText(bytes);
                            CharsetMatch match = detector.detect();
                            String charsetName = match.getName();
                            if (charsetName != null) {
                                try {
                                    charset = Charset.forName(charsetName);
                                } catch (Exception e) {
                                    logger.warn("Detected character set " + charsetName + " not supported");
                                }
                            }
                        }
                        wsResponse.body = new String(bytes, charset == null ? "UTF-8" : charset.name());
                        wsResponse.contentType = getHeader(entity.getContentType(), "text/html");
                        wsResponse.contentLength = entity.getContentLength();
                    }
                }
                return wsResponse;
            } catch (Exception e) {
                Proxy.removeProxy(proxyEntry);
                retry++;
                logger.error("出现异常,正在重试第" + retry + "次", e);
                end(httpClient, httpMethod);
                this.httpClient = new DefaultHttpClient();
                return get(useProxy);
            } finally {
                IOUtils.closeQuietly(is);
                end(httpClient);
            }
        }

        private static String getHeader(Header header, String defaultValue) {
            return header == null || header.getValue() == null ? defaultValue : header.getValue();
        }

        private static Charset getContentCharSet(final HttpEntity entity) throws ParseException {
            if (entity == null) {
                throw new IllegalArgumentException("HTTP entity may not be null");
            }
            String charset = null;
            if (entity.getContentType() != null) {
                HeaderElement values[] = entity.getContentType().getElements();
                if (values.length > 0) {
                    NameValuePair param = values[0].getParameterByName("charset");
                    if (param != null) {
                        charset = param.getValue();
                    }
                }
            }
            try {
                return charset != null ? Charset.forName(charset) : null;
            } catch (Exception e) {
                logger.warn("get charset {} error", charset);
                return null;
            }
        }
    }

    public static class WSResponse {
        public int status;
        public String contentType;
        public String body;
        public long contentLength;
        public Header[] headers;
    }
}
