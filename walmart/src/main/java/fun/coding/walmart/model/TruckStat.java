package fun.coding.walmart.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TruckStat {

    private Integer id;
    private Double temperature;
    private Double humidity;
    private String location;
    private String timestamp;
}
