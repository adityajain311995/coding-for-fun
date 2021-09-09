package fun.coding.walmart.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fun.coding.walmart.model.TruckStat;
import fun.coding.walmart.service.FileStorageService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/truck_stats")
public class TruckStatController {

    private final ObjectMapper objectMapper;

    public TruckStatController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @GetMapping("/aggregate")
    public String aggregate() throws IOException {
        String basePath = FileStorageService.basePath;
        File basePathDir = new File(basePath);

        if (!basePathDir.exists()) {
            return "There is not sufficient data present for past 10 mins";
        }

        List<String> fileNames = Arrays.stream(basePathDir.list())
                .sorted(Comparator.reverseOrder())
                .limit(2)
                .collect(Collectors.toList());
        if (fileNames.size() < 2) {
            return "There is not sufficient data present for past 10 mins";
        }

        List<TruckStat> allTruckStats = new ArrayList<>();
        for (String fileName : fileNames) {
            File file = new File(basePath, "/" + fileName);
            List<TruckStat> truckStats = objectMapper.readValue(file, new TypeReference<List<TruckStat>>() {});
            allTruckStats.addAll(truckStats);
        }

        List<String> timeStampsOfTrucksHavingTempMoreThan45 = allTruckStats.stream()
                .filter(truckStat -> truckStat.getTemperature() >= 45.0)
                .map(truckStat -> truckStat.getTimestamp())
                .collect(Collectors.toList());

        return "Timestamps of trucks in last 10 mins having temperature <= 45 are: \n "
                + timeStampsOfTrucksHavingTempMoreThan45;
    }
}
