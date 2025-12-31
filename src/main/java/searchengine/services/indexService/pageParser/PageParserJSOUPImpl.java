package searchengine.services.indexService.pageParser;

import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import searchengine.config.UserAgent;
import searchengine.dto.indexing.PageParsed;
import searchengine.services.PoolService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class PageParserJSOUPImpl implements PageParseable{

    private final PoolService poolService;

    @Override
    public PageParsed getParsedPage(String url) throws IOException {

            PageParsed pageParsed = new PageParsed();
            Document doc;
            Connection.Response response;
            int code;

            response = Jsoup.connect(url)
                    .userAgent(generateUserAgent())
                    .referrer("https://www.google.com")
                    .ignoreHttpErrors(true)
                    .followRedirects(true)
                    .timeout(60000)
                    .execute();
            try {
                Thread.sleep(generateRandomRangeDelay()); // задержка между запросами
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            code = response.statusCode();

            if (code == 200) {
                doc = response.parse();
            } else {
                doc = new Document(url);
            }
            pageParsed.setDoc(doc);
            pageParsed.setCode(code);
            return pageParsed;
    }

    private String generateUserAgent() {
        List<UserAgent> userAgents = poolService.getUserAgentList().getUserAgents();
        Map<Integer, UserAgent> nameMap = new LinkedHashMap<>();
        String name = "";

        int number = 1;
        for (UserAgent usr : userAgents) {
            nameMap.put(number, usr);
            number++;
        }

        int randomNumber = 1 + (int) (Math.random() * nameMap.size());
        name = nameMap.get(randomNumber).getName();
        return name;
    }

    private long generateRandomRangeDelay() {
        long beginningOfRange = 500;
        return (long) (beginningOfRange + (Math.random() * 4500));
    }
}
