package fun.coding.walmart.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fun.coding.walmart.model.TruckStat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FileStorageService {

    public static final String basePath = "/Users/z004s3n/DATA/Repo/coding-for-fun/walmart/files";

    private final ObjectMapper objectMapper;

    public FileStorageService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void store(List<TruckStat> truckStats) throws IOException {
        String fileName = "truck_stats_" + LocalDateTime.now() + ".json";
        log.info("Storing file at location: " + basePath);
        new File(basePath).mkdirs();
        File file = new File(basePath, fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(objectMapper.writeValueAsBytes(truckStats));
        fileOutputStream.close();
    }
}
