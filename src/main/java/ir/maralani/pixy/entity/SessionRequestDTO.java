package ir.maralani.pixy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionRequestDTO {
    private String client_stream;
    private String server_stream;
}
