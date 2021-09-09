package fun.coding.walmart.service;

import fun.coding.walmart.model.TruckStat;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TruckStatsReader {

    private final String readUrl = "https://whispering-temple-65255.herokuapp.com/xmlTelemetry";
    private final Queue<TruckStat> cache = new ArrayDeque<>();

    private final TruckStatsConversionService conversionService;
    private final FileStorageService fileStorageService;

    public TruckStatsReader(TruckStatsConversionService conversionService,
            FileStorageService fileStorageService) {
        this.conversionService = conversionService;
        this.fileStorageService = fileStorageService;
    }


    /**
     * Job runs every minute.
     *
     * @throws IOException
     */
    @Scheduled(fixedDelay = 2 * 1000)
    public void fetchAndStoreInCache() throws IOException {
        log.info("Running fetchAndStoreInCache job at time: "+LocalDateTime.now());
        TruckStat truckStats = fetchStats();
        cache.add(truckStats);
    }

    /**
     * Job runs every 5 mins.
     * @return
     * @throws IOException
     */
    @Scheduled(fixedDelay = 10 * 1000)
    public void storeStats() throws IOException {
        if (cache.size() < 5) {
            return;
        }

        List<TruckStat> truckStats = new ArrayList<>();
        for (int i=0; i<5; i++) {
            TruckStat truckStat = cache.remove();
            truckStats.add(truckStat);
        }

        fileStorageService.store(truckStats);
    }

    private TruckStat fetchStats() throws IOException {
        String responseXml = getResponse();
        TruckStat truckStats = conversionService.fromXml(responseXml);
        truckStats.setTimestamp(LocalDateTime.now().toString());

        log.info("FetchedRecord: " + truckStats);

        return truckStats;
    }

    private String getResponse() throws IOException {
        String responseXml = "";

        HttpGet request = new HttpGet(readUrl);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = httpClient.execute(request)) {

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // return it as a String
                responseXml = EntityUtils.toString(entity);
            }
        }

        return responseXml;
    }

}
