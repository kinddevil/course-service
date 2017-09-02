package com.microservices.support.filters;

import com.google.common.io.CharStreams;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.http.*;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.Host;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class RouteFilter extends ZuulFilter {

  private static Logger log = LoggerFactory.getLogger(RouteFilter.class);

  private final Timer connectionManagerTimer = new Timer(
      "SimpleHostRoutingFilter.connectionManagerTimer", true);

  private boolean sslHostnameValidationEnabled;
  private boolean forceOriginalQueryStringEncoding;

  private ProxyRequestHelper helper;
  private Host hostProperties;
  private PoolingHttpClientConnectionManager connectionManager;
  private CloseableHttpClient httpClient;

  @Override
  public String filterType() {
    return "route";
  }

  @Override
  public int filterOrder() {
    return 2;
  }

  @Override
  public boolean shouldFilter() {
    return true;
  }

  @Override
  public Object run() {
    RequestContext ctx = RequestContext.getCurrentContext();
    String url = ctx.getRequest().getRequestURL().toString();
    HttpServletRequest request = ctx.getRequest();
    log.info(String.format("routeFilter... %s request to %s", request.getMethod(), request.getRequestURL().toString()));

    /*RequestContext ctx = RequestContext.getCurrentContext();
    HttpServletRequest request = ctx.getRequest();
    String url = request.getRequestURL().toString();
    if (url.matches(".*//*usermodel/.*")) {
      MultiValueMap<String, String> headers = this.helper
        .buildZuulRequestHeaders(request);
      log.info(headers.toString());
      MultiValueMap<String, String> params = this.helper
        .buildZuulRequestQueryParams(request);
      String verb = getVerb(request);
      InputStream requestEntity = getRequestBody(request);
      String uri = request.getRequestURI().replaceFirst("/users/[^/$]*", "/users/2000231397");
      uri = uri.replaceFirst("/profiles/[^/$]*", "/profiles/2000231397");
      uri = uri.replaceFirst("/usermodel", "");
      log.info(String.format("in route filter, uri is %s", uri));
      try {
        CloseableHttpResponse response = forward(this.httpClient, verb, uri, request,
            headers, params, requestEntity);
        setResponse(response);
      } catch (Exception ex) {
      }
    }*/

    return null;
  }

  public RouteFilter(ProxyRequestHelper helper, ZuulProperties properties) {
    this.helper = helper;
    this.hostProperties = properties.getHost();
    this.sslHostnameValidationEnabled = properties.isSslHostnameValidationEnabled();
    this.forceOriginalQueryStringEncoding = true;
  }

  @PostConstruct
  private void initialize() {
    this.httpClient = newClient();
    this.connectionManagerTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        if (RouteFilter.this.connectionManager == null) {
          return;
        }
        RouteFilter.this.connectionManager.closeExpiredConnections();
      }
    }, 30000, 5000);
  }

  private InputStream getRequestBody(HttpServletRequest request) {
    InputStream requestEntity = null;
    try {
      requestEntity = request.getInputStream();
    }
    catch (IOException ex) {
      // no requestBody is ok.
    }
    return requestEntity;
  }

  protected CloseableHttpClient newClient() {
    final RequestConfig requestConfig = RequestConfig.custom()
      .setSocketTimeout(1000)
      .setConnectTimeout(1000)
      .setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();

    HttpClientBuilder httpClientBuilder = HttpClients.custom();
    if (!this.sslHostnameValidationEnabled) {
      httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
    }
    return httpClientBuilder.setConnectionManager(newConnectionManager())
      .disableContentCompression()
      .useSystemProperties().setDefaultRequestConfig(requestConfig)
      .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
      .setRedirectStrategy(new RedirectStrategy() {
        @Override
        public boolean isRedirected(HttpRequest request,
                                    HttpResponse response, HttpContext context)
        throws ProtocolException {
        return false;
        }

        @Override
        public HttpUriRequest getRedirect(HttpRequest request,
                                          HttpResponse response, HttpContext context)
        throws ProtocolException {
        return null;
        }
      }).build();
  }

  protected PoolingHttpClientConnectionManager newConnectionManager() {
    try {
      final SSLContext sslContext = SSLContext.getInstance("SSL");
      sslContext.init(null, new TrustManager[] { new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates,
            String s) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates,
            String s) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
          return null;
        }
      } }, new SecureRandom());

      RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder
        .<ConnectionSocketFactory> create()
        .register("http", PlainConnectionSocketFactory.INSTANCE);
      if (this.sslHostnameValidationEnabled) {
        registryBuilder.register("https",
            new SSLConnectionSocketFactory(sslContext));
      }
      else {
        registryBuilder.register("https", new SSLConnectionSocketFactory(
              sslContext, NoopHostnameVerifier.INSTANCE));
      }
      final Registry<ConnectionSocketFactory> registry = registryBuilder.build();

      this.connectionManager = new PoolingHttpClientConnectionManager(registry, null, null, null,
          1000, TimeUnit.MILLISECONDS);
      this.connectionManager
        .setMaxTotal(this.hostProperties.getMaxTotalConnections());
      this.connectionManager.setDefaultMaxPerRoute(
          this.hostProperties.getMaxPerRouteConnections());
      return this.connectionManager;
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private CloseableHttpResponse forward(CloseableHttpClient httpclient, String verb,
                                        String uri, HttpServletRequest request, MultiValueMap<String, String> headers,
                                        MultiValueMap<String, String> params, InputStream requestEntity)
    throws Exception {
    URL host = RequestContext.getCurrentContext().getRouteHost();
    HttpHost httpHost = getHttpHost(host);
    uri = StringUtils.cleanPath((host.getPath() + uri).replaceAll("/{2,}", "/"));
    int contentLength = request.getContentLength();

    ContentType contentType = null;

    if (request.getContentType() != null) {
      contentType = ContentType.parse(request.getContentType());
    }

    InputStreamEntity entity = new InputStreamEntity(requestEntity, contentLength, contentType);

    HttpRequest httpRequest = buildHttpRequest(verb, uri, entity, headers, params, request);
    try {
      CloseableHttpResponse zuulResponse = forwardRequest(httpclient, httpHost,
          httpRequest);
      return zuulResponse;
    }
    finally {
      // When HttpClient instance is no longer needed,
      // shut down the connection manager to ensure
      // immediate deallocation of all system resources
      // httpclient.getConnectionManager().shutdown();
    }
    }

  private HttpHost getHttpHost(URL host) {
    HttpHost httpHost = new HttpHost(host.getHost(), host.getPort(),
        host.getProtocol());
    return httpHost;
  }

  protected HttpRequest buildHttpRequest(String verb, String uri,
                                         InputStreamEntity entity, MultiValueMap<String, String> headers,
                                         MultiValueMap<String, String> params, HttpServletRequest request) {
    HttpRequest httpRequest;
    String uriWithQueryString = uri + (this.forceOriginalQueryStringEncoding
        ? getEncodedQueryString(request) : this.helper.getQueryString(params));

    switch (verb.toUpperCase()) {
      case "POST":
        HttpPost httpPost = new HttpPost(uriWithQueryString);
        httpRequest = httpPost;
        httpPost.setEntity(entity);
        break;
      case "PUT":
        HttpPut httpPut = new HttpPut(uriWithQueryString);
        httpRequest = httpPut;
        httpPut.setEntity(entity);
        break;
      case "PATCH":
        HttpPatch httpPatch = new HttpPatch(uriWithQueryString);
        httpRequest = httpPatch;
        httpPatch.setEntity(entity);
        break;
      case "DELETE":
        BasicHttpEntityEnclosingRequest entityRequest = new BasicHttpEntityEnclosingRequest(
            verb, uriWithQueryString);
        httpRequest = entityRequest;
        entityRequest.setEntity(entity);
        break;
      default:
        httpRequest = new BasicHttpRequest(verb, uriWithQueryString);
    }

    httpRequest.setHeaders(convertHeaders(headers));
    return httpRequest;
  }

  private String getEncodedQueryString(HttpServletRequest request) {
    String query = request.getQueryString();
    return (query != null) ? "?" + query : "";
  }

  private Header[] convertHeaders(MultiValueMap<String, String> headers) {
    List<Header> list = new ArrayList<>();
    for (String name : headers.keySet()) {
      for (String value : headers.get(name)) {
        list.add(new BasicHeader(name, value));
      }
    }
    return list.toArray(new BasicHeader[0]);
  }

  private CloseableHttpResponse forwardRequest(CloseableHttpClient httpclient,
                                               HttpHost httpHost, HttpRequest httpRequest) throws IOException {
    return httpclient.execute(httpHost, httpRequest);
  }

  private String getVerb(HttpServletRequest request) {
    String sMethod = request.getMethod();
    return sMethod.toUpperCase();
  }

  private void setResponse(HttpResponse response) throws IOException {
    InputStream responseDataStream = response.getEntity().getContent();
    final String responseData;
    try {
      responseData = CharStreams.toString(new InputStreamReader(responseDataStream));
      log.info(String.format("in route filter, response is %s", responseData));
      RequestContext.getCurrentContext().setResponseBody(responseData);
      responseDataStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    RequestContext.getCurrentContext().set("zuulResponse", response);
    this.helper.setResponse(response.getStatusLine().getStatusCode(),
        response.getEntity() == null ? null : response.getEntity().getContent(),
        revertHeaders(response.getAllHeaders()));
  }

  private MultiValueMap<String, String> revertHeaders(Header[] headers) {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
    for (Header header : headers) {
      String name = header.getName();
      if (!map.containsKey(name)) {
        map.put(name, new ArrayList<String>());
      }
      map.get(name).add(header.getValue());
    }
    return map;
  }
}
