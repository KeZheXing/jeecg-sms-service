package org.jeecg.modules.airag.app.entity.request;

import lombok.Data;

@Data
public class MCPortStatusRequest {

    private String port;

    private Integer active;

    private Integer inserted;

    private Integer slotActive;

    private Integer sig;

    private String st;
}
