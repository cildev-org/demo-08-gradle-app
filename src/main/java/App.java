import com.sun.net.httpserver.HttpServer;
import org.apache.commons.text.StringSubstitutor;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.Executors;

// Lab app for CVE-2022-42889 (Text4Shell). /render runs the default
// StringSubstitutor interpolator on ?s=; before commons-text 1.10.0 its url
// lookup is on, so ${url:UTF-8:http://127.0.0.1:8080/echo?x=T} makes the app
// fetch itself and the output becomes the token T. /echo mirrors ?x=.
// /version reports the commons-text version from its pom.properties.
public class App {
  static String q(URI u, String k) {
    String rq = u.getRawQuery();
    if (rq == null) return "";
    for (String p : rq.split("&")) {
      int i = p.indexOf('=');
      if (i > 0 && p.substring(0, i).equals(k))
        return URLDecoder.decode(p.substring(i + 1), StandardCharsets.UTF_8);
    }
    return "";
  }
  static void send(com.sun.net.httpserver.HttpExchange ex, int code, String body) throws IOException {
    byte[] b = body.getBytes(StandardCharsets.UTF_8);
    ex.sendResponseHeaders(code, b.length);
    try (OutputStream o = ex.getResponseBody()) { o.write(b); }
  }
  static String version() {
    try (InputStream in = App.class.getResourceAsStream(
        "/META-INF/maven/org.apache.commons/commons-text/pom.properties")) {
      if (in == null) return "unknown";
      Properties p = new Properties();
      p.load(in);
      return p.getProperty("version", "unknown");
    } catch (IOException e) { return "unknown"; }
  }
  public static void main(String[] a) throws Exception {
    HttpServer s = HttpServer.create(new InetSocketAddress("0.0.0.0", 8080), 0);
    // A thread pool, not the serial default: /render fetches the app itself.
    s.setExecutor(Executors.newFixedThreadPool(8));
    s.createContext("/render", ex -> send(ex, 200,
        StringSubstitutor.createInterpolator().replace(q(ex.getRequestURI(), "s"))));
    s.createContext("/echo", ex -> send(ex, 200, q(ex.getRequestURI(), "x")));
    s.createContext("/version", ex -> send(ex, 200, version()));
    s.start();
    System.out.println("up");
  }
}
