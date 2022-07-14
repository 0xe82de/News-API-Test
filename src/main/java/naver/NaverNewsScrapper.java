package naver;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.PriorityQueue;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;

public class NaverNewsScrapper {

    private static final String CLIENT_ID = "";

    private static final String CLIENT_SECRET = "";

    private static final String API_URL = "https://openapi.naver.com/v1/search/news.json";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

    public static void main(String[] args) throws IOException {
        String keyword = "네이버";

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(API_URL);
        urlBuilder.append("?query=").append(encode(keyword, UTF_8));
        urlBuilder.append("&display=100"); // 데이터 개수
        urlBuilder.append("&start=1"); // 시작 위치
        urlBuilder.append("&sort=sim"); // sim: 유사도순, date: 최신순

        URL url = new URL(urlBuilder.toString());
        HttpsURLConnection conn = makeHttpsURLConnection(url);
        String response = parseResponse(conn);

        if (isSuccessApi(conn)) {
            try {
                JSONParser parser = new JSONParser();
                JSONObject totalInfo = (JSONObject) parser.parse(response);
                JSONArray items = (JSONArray) totalInfo.get("items");

                PriorityQueue<NaverNewsDto> naverNewsDtoPQ = new PriorityQueue<>();
                for (Object item : items) {
                    naverNewsDtoPQ.offer(createNaverDto((JSONObject) item));
                }

                for (NaverNewsDto naverNewsDto : naverNewsDtoPQ) {
                    // 데이터 처리
                    System.out.println("naverNewsDto = " + naverNewsDto);
                }

                System.out.println("O, url=" + urlBuilder + ", keyword=" + keyword);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("X, url=" + urlBuilder + ", keyword=" + keyword);
            }
        } else {
            System.out.println("X, url=" + urlBuilder + ", keyword=" + keyword);
        }
    }

    private static NaverNewsDto createNaverDto(JSONObject item) {
        return NaverNewsDto.builder()
                .title((String) item.get("title"))
                .originalLink((String) item.get("originallink"))
                .link((String) item.get("link"))
                .description((String) item.get("description"))
                .publishDate(LocalDateTime.parse((String) item.get("pubDate"), formatter))
                .build();
    }

    private static boolean isSuccessApi(HttpsURLConnection conn) throws IOException {
        int responseCode = conn.getResponseCode();
        return 200 <= responseCode & responseCode <= 300;
    }

    private static String parseResponse(HttpsURLConnection conn) throws IOException {
        BufferedReader br;
        if (isSuccessApi(conn)) {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        // 저장된 데이터를 라인 별로 읽어 StringBuilder 객체에 저장
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        conn.disconnect();
        br.close();

        return sb.toString();
    }

    private static HttpsURLConnection makeHttpsURLConnection(URL url) throws IOException {
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("X-Naver-Client-Id", CLIENT_ID);
        conn.setRequestProperty("X-Naver-Client-Secret", CLIENT_SECRET);
        return conn;
    }
}
