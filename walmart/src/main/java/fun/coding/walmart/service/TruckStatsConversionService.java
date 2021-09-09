package fun.coding.walmart.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fun.coding.walmart.model.TruckStat;
import org.springframework.stereotype.Service;

@Service
public class TruckStatsConversionService {

    private final XmlMapper xmlMapper;
    private final ObjectMapper objectMapper;

    public TruckStatsConversionService(XmlMapper xmlMapper, ObjectMapper objectMapper) {
        this.xmlMapper = xmlMapper;
        this.objectMapper = objectMapper;
    }

    public TruckStat fromXml(String xmlString) throws JsonProcessingException {
       return xmlMapper.readValue(xmlString, TruckStat.class);
    }

    public String toJson(TruckStat truckReading) throws JsonProcessingException {
        return objectMapper.writeValueAsString(truckReading);
    }

}
