package com.lanyue.shortlink.project.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkStatsRespDTO {

    private Long pv;

    private Long uv;

    private List<StatsTrendDTO> trend;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatsTrendDTO {

        private String date;

        private Long count;
    }
}
