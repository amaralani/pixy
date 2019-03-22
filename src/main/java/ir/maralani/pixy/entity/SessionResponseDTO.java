package ir.maralani.pixy.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponseDTO {
    private Map<String, Integer> client_statistics;
    private Map<String, Integer> server_statistics;
}
