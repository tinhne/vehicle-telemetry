package com.vehicletelemetry.dto;
import lombok.*;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TelemetryHistoryResponse {
    private String             vehicleId;
    private List<TelemetryDTO> records;
    private long  totalElements;
    private int   totalPages;
    private int   currentPage;
    private int   pageSize;
}
